<script lang="ts">
	import { Loader, Plus, Trash2, Edit, AlertCircle } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	export let user;
	export let theme: 'light' | 'dark' = 'light';

	let payments = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editPayment = null;
	let paymentForm = { parentId: '', voucherId: '', amount: '', date: '', status: 'pending', method: '', comment: '' };
	let parents = [];
	let vouchers = [];
	let loadingParents = false;
	let loadingVouchers = false;
	const statuses = ['paid', 'pending', 'cancelled'];
	const methods = ['card', 'cash', 'other'];

	async function loadPayments() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/payments`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки оплат');
			payments = await res.json();
		} catch (e) {
			error = e.message || 'Ошибка';
		} finally {
			loading = false;
		}
	}

	async function loadParents() {
		loadingParents = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/admin/users`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (res.ok) parents = (await res.json()).filter(u => u.role === 'PARENT');
		} finally {
			loadingParents = false;
		}
	}

	async function loadVouchers() {
		loadingVouchers = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/vouchers`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (res.ok) vouchers = await res.json();
		} finally {
			loadingVouchers = false;
		}
	}

	function openModal(payment = null) {
		showModal = true;
		editPayment = payment;
		if (payment) {
			paymentForm = {
				parentId: payment.parent?.id || '',
				voucherId: payment.voucher?.id || '',
				amount: payment.amount || '',
				date: payment.date ? payment.date.slice(0, 16) : '',
				status: payment.status || 'pending',
				method: payment.method || '',
				comment: payment.comment || ''
			};
		} else {
			paymentForm = { parentId: '', voucherId: '', amount: '', date: '', status: 'pending', method: '', comment: '' };
		}
	}
	function closeModal() {
		showModal = false;
		editPayment = null;
	}
	async function savePayment() {
		const method = editPayment ? 'PUT' : 'POST';
		const url = editPayment ? `${PUBLIC_API_URL}/api/payments/${editPayment.id}` : `${PUBLIC_API_URL}/api/payments`;
		const body = JSON.stringify({
			parent: parents.find(p => p.id == paymentForm.parentId),
			voucher: vouchers.find(v => v.id == paymentForm.voucherId),
			amount: parseFloat(paymentForm.amount),
			date: paymentForm.date,
			status: paymentForm.status,
			method: paymentForm.method,
			comment: paymentForm.comment
		});
		await fetch(url, {
			method,
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${user?.accessToken}`
			},
			body
		});
		await loadPayments();
		closeModal();
	}
	async function deletePayment(id: number) {
		if (!confirm('Удалить оплату?')) return;
		await fetch(`${PUBLIC_API_URL}/api/payments/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user?.accessToken}` }
		});
		await loadPayments();
	}

	import { onMount } from 'svelte';
	onMount(() => { loadPayments(); loadParents(); loadVouchers(); });
</script>

<div class="payment-admin" data-theme={theme}>
	<h2>Оплаты</h2>
	{#if loading}
		<div class="loader spin"><Loader size={24}/> Загрузка...</div>
	{:else if error}
		<div class="error"><AlertCircle size={20}/> {error}</div>
	{:else}
		<button class="add-btn" on:click={() => openModal()}><Plus size={18}/> Новая оплата</button>
		<table class="payment-table">
			<thead>
				<tr>
					<th>Родитель</th>
					<th>Путёвка</th>
					<th>Сумма</th>
					<th>Дата</th>
					<th>Статус</th>
					<th>Способ</th>
					<th>Комментарий</th>
					<th>Действия</th>
				</tr>
			</thead>
			<tbody>
				{#each payments as p}
					<tr>
						<td>{p.parent?.username}</td>
						<td>{p.voucher?.id}</td>
						<td>{p.amount}</td>
						<td>{p.date}</td>
						<td>{p.status}</td>
						<td>{p.method}</td>
						<td>{p.comment}</td>
						<td>
							<button class="icon-btn blue" title="Редактировать" on:click={() => openModal(p)}><Edit size={16}/></button>
							<button class="icon-btn" title="Удалить" on:click={() => deletePayment(p.id)}><Trash2 size={16}/></button>
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	{/if}
	{#if showModal}
		<button type="button" class="modal-backdrop" on:click={closeModal} aria-label="Закрыть модальное окно"></button>
		<section class="modal" on:click|stopPropagation tabindex="0" role="dialog" aria-modal="true" on:keydown={e => { if (e.key === 'Escape') closeModal(); }}>
			<h3>{editPayment ? 'Редактировать оплату' : 'Новая оплата'}</h3>
			<form on:submit|preventDefault={savePayment}>
				<label>Родитель
					<select bind:value={paymentForm.parentId} required>
						<option value="" disabled>Выберите родителя</option>
						{#each parents as p}
							<option value={p.id}>{p.username}</option>
						{/each}
					</select>
				</label>
				<label>Путёвка
					<select bind:value={paymentForm.voucherId} required>
						<option value="" disabled>Выберите путёвку</option>
						{#each vouchers as v}
							<option value={v.id}>#{v.id}</option>
						{/each}
					</select>
				</label>
				<label>Сумма<input type="number" min="0" step="0.01" bind:value={paymentForm.amount} required /></label>
				<label>Дата<input type="datetime-local" bind:value={paymentForm.date} required /></label>
				<label>Статус
					<select bind:value={paymentForm.status} required>
						{#each statuses as s}
							<option value={s}>{s}</option>
						{/each}
					</select>
				</label>
				<label>Способ
					<select bind:value={paymentForm.method} required>
						{#each methods as m}
							<option value={m}>{m}</option>
						{/each}
					</select>
				</label>
				<label>Комментарий<textarea bind:value={paymentForm.comment} rows="2"></textarea></label>
				<div class="modal-actions">
					<button type="submit" class="save-btn">Сохранить</button>
					<button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
				</div>
			</form>
		</section>
	{/if}
</div>

<style>
.payment-admin {
	padding: 2rem 1.5rem;
	background: var(--color-card);
	border-radius: 18px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	max-width: 1100px;
	margin: 2rem auto 0 auto;
}
.payment-admin[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-card: #23272f;
}
.payment-admin[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-card: #fff;
}
.payment-admin h2 {
	font-size: 1.4rem;
	color: var(--color-primary, #2d8cff);
	margin-bottom: 2rem;
}
.add-btn {
	background: var(--color-primary, #2d8cff);
	color: #fff;
	border: none;
	border-radius: 8px;
	padding: 0.5rem 1.2rem;
	font-size: 1rem;
	margin-bottom: 1.2rem;
	cursor: pointer;
	display: flex;
	align-items: center;
	gap: 0.5rem;
	transition: background 0.18s;
}
.add-btn:hover {
	background: var(--color-accent, #ffb347);
	color: #222;
}
.payment-table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 1rem;
}
.payment-table th, .payment-table td {
	padding: 0.7rem 0.5rem;
	text-align: left;
	border-bottom: 1px solid #e0e0e0;
}
.payment-table th {
	color: var(--color-primary, #2d8cff);
	font-size: 1.05rem;
}
.icon-btn {
	background: none;
	border: none;
	color: #e74c3c;
	cursor: pointer;
	padding: 0.2rem 0.5rem;
	border-radius: 6px;
	transition: background 0.18s;
	display: flex;
	align-items: center;
}
.icon-btn.blue {
	color: #2d8cff;
}
.icon-btn:hover {
	background: #ffeaea;
}
.loader, .error {
	text-align: center;
	margin-top: 2.5rem;
	font-size: 1.1rem;
	color: #888;
	display: flex;
	align-items: center;
	gap: 0.5rem;
	justify-content: center;
}
.error { color: #e74c3c; }
.modal-backdrop {
	position: fixed;
	top: 0; left: 0; right: 0; bottom: 0;
	background: rgba(0,0,0,0.18);
	z-index: 1000;
}
.modal {
	position: fixed;
	top: 50%; left: 50%;
	transform: translate(-50%, -50%);
	background: var(--color-card);
	padding: 2rem 2.2rem;
	border-radius: 16px;
	box-shadow: 0 8px 32px rgba(45,140,255,0.13);
	z-index: 1001;
	min-width: 320px;
	max-width: 95vw;
}
.modal h3 {
	margin-bottom: 1.2rem;
	font-size: 1.15rem;
	color: var(--color-primary, #2d8cff);
}
.modal label {
	display: block;
	margin-bottom: 0.7rem;
	font-size: 1rem;
}
.modal input, .modal textarea, .modal select {
	width: 100%;
	padding: 0.5rem;
	border-radius: 7px;
	border: 1px solid #d0d7e2;
	margin-top: 0.2rem;
	margin-bottom: 0.7rem;
	font-size: 1rem;
	background: var(--color-bg);
	color: var(--color-text);
}
.modal-actions {
	display: flex;
	gap: 1.2rem;
	justify-content: flex-end;
	margin-top: 1.2rem;
}
.save-btn {
	background: var(--color-primary, #2d8cff);
	color: #fff;
	border: none;
	border-radius: 8px;
	padding: 0.5rem 1.2rem;
	font-size: 1rem;
	cursor: pointer;
	transition: background 0.18s;
}
.save-btn:hover {
	background: var(--color-accent, #ffb347);
	color: #222;
}
.cancel-btn {
	background: #e0e0e0;
	color: #222;
	border: none;
	border-radius: 8px;
	padding: 0.5rem 1.2rem;
	font-size: 1rem;
	cursor: pointer;
	transition: background 0.18s;
}
.cancel-btn:hover {
	background: #f8d7da;
	color: #c0392b;
}
.spin { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }
</style> 