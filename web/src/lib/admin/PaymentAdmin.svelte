<script lang="ts">
	import {fade, fly} from 'svelte/transition';
	import {AlertCircle, ArrowUpDown, CreditCard, Edit, Loader, Plus, Trash2} from 'lucide-svelte';
	import {PUBLIC_API_URL} from '$env/static/public';
	import type {UserSession} from '$lib/stores/userStore';
	import {onMount} from 'svelte';
	import type {MedicalVisit, Payment, User, Voucher} from "$lib/models";
	import {toast} from "svelte-sonner";
	import SearchBox from "$lib/components/SearchBox.svelte";

	export let user: UserSession;

	let payments: Payment[] = [];
	let filteredPayments: Payment[] = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editPayment: Payment | null = null;
	interface PaymentForm {
		parentId: number | null;
		voucherId: number | null;
		amount: number;
		date: string;
		status: 'paid' | 'pending' | 'cancelled';
		method: 'credit_card' | 'cash' | 'bank_transfer';
		comment: string;
	}
	let paymentForm: PaymentForm = { parentId: null, voucherId: null, amount: 0, date: '', status: 'pending', method: 'credit_card', comment: '' };
	let parents: User[] = [];
	let vouchers: Voucher[] = [];
	let loadingParents = false;
	let loadingVouchers = false;
    let searchQuery = '';
    let sortField: keyof Payment = 'date';
    let sortDirection: 'asc' | 'desc' = 'asc';
	const statuses = ['paid', 'pending', 'failed'];
	const methods = ['credit_card', 'cash', 'bank_transfer'];

	async function loadPayments() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/payments`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok)
				error = 'Ошибка загрузки оплат';
			else {
				payments = await res.json();
				filterAndSortPayments();
			}
		} finally {
			loading = false;
		}
	}

	async function loadParents() {
		loadingParents = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/admin/users`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (res.ok) parents = (await res.json() as User[]).filter(u => u.role === 'PARENT');
		} finally {
			loadingParents = false;
		}
	}

	async function loadVouchers() {
		loadingVouchers = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/vouchers`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (res.ok) vouchers = await res.json();
		} finally {
			loadingVouchers = false;
		}
	}

    function filterAndSortPayments() {
        filteredPayments = payments.filter(payment =>
            payment.parent.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
            payment.voucher.status.toLowerCase().includes(searchQuery.toLowerCase()) ||
            payment.date.toLowerCase().includes(searchQuery) ||
            payment.status.toLowerCase().includes(searchQuery) ||
            payment.method.toLowerCase().includes(searchQuery)
        );

        filteredPayments.sort((a, b) => {
            let valueA = a[sortField];
            let valueB = b[sortField];

            if (typeof valueA === 'string') valueA = valueA.toLowerCase();
            if (typeof valueB === 'string') valueB = valueB.toLowerCase();

            if (valueA < valueB) return sortDirection === 'asc' ? -1 : 1;
            if (valueA > valueB) return sortDirection === 'asc' ? 1 : -1;
            return 0;
        });
    }

    function sortBy(field: keyof Payment) {
        if (sortField === field) {
            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDirection = 'asc';
        }
        filterAndSortPayments();
    }

	function openModal(payment: Payment | null = null) {
		showModal = true;
		editPayment = payment;
		if (payment) {
			paymentForm = {
				parentId: payment.parent.id || null,
				voucherId: payment.voucher.id || null,
				amount: payment.amount,
				date: payment.date,
				status: payment.status,
				method: payment.method,
				comment: payment.comment || ''
			};
		} else {
			paymentForm = { parentId: null, voucherId: null, amount: 0, date: '', status: 'pending', method: 'credit_card', comment: '' };
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
			amount: paymentForm.amount,
			date: paymentForm.date,
			status: paymentForm.status,
			method: paymentForm.method,
			comment: paymentForm.comment
		});
		const response = await fetch(url, {
			method,
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${user.accessToken}`
			},
			body
		});
		if (response.ok) {
			await loadPayments();
			closeModal();
			toast.success('Платёж успешно сохранён');
		} else {
			const error = await response.text();
			toast.error(`Ошибка: ${error}`);
		}
	}

	async function deletePayment(id: number) {
		if (!confirm('Удалить оплату?')) return;
		const response = await fetch(`${PUBLIC_API_URL}/api/payments/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user.accessToken}` }
		});
		if (response.ok) {
			await loadPayments();
			toast.success('Платёж успешно удалён');
		} else {
			const error = await response.text();
			toast.error(`Ошибка: ${error}`);
		}
	}

    $: if (searchQuery || sortField) {
        filterAndSortPayments();
    }

	onMount(() => { loadPayments(); loadParents(); loadVouchers(); });
</script>

<div class="payment-admin">
	<div class="header">
		<h2>
			<CreditCard size={24} />
			<span>Управление оплатами</span>
		</h2>
		<button class="add-btn" on:click={() => openModal()}>
			<Plus size={18} />
			<span>Добавить оплату</span>
		</button>
	</div>

    <SearchBox
        bind:value={searchQuery}
        placeholder="Поиск по всем полям..."
    />

	{#if loading || loadingParents || loadingVouchers}
		<div class="loader">
			<Loader size={24} />
			<span>Загрузка...</span>
		</div>
	{:else if error}
		<div class="error">
			<AlertCircle size={20} />
			<span>{error}</span>
		</div>
	{:else}
		<div class="payment-table">
			<table>
				<thead>
					<tr>
						<th on:click={() => sortBy('id')} class:active={sortField==='id'}>
							<span>ID <ArrowUpDown size={14}/></span>
						</th>
						<th>Родитель</th>
						<th>Путёвка</th>
						<th on:click={() => sortBy('amount')} class:active={sortField==='amount'}>
							<span>Сумма <ArrowUpDown size={14}/></span>
						</th>
						<th on:click={() => sortBy('date')} class:active={sortField==='date'}>
							<span>Дата <ArrowUpDown size={14}/></span>
						</th>
						<th on:click={() => sortBy('status')} class:active={sortField==='status'}>
							<span>Статус <ArrowUpDown size={14}/></span>
						</th>
						<th on:click={() => sortBy('method')} class:active={sortField==='method'}>
							<span>Способ <ArrowUpDown size={14}/></span>
						</th>
						<th on:click={() => sortBy('comment')} class:active={sortField==='comment'}>
							<span>Комментарий <ArrowUpDown size={14}/></span>
						</th>
						<th>Действия</th>
					</tr>
				</thead>
				<tbody>
					{#each filteredPayments as p}
						<tr>
							<td>{p.id}</td>
							<td>{p.parent?.username}</td>
							<td>{p.voucher?.id}</td>
							<td>{p.amount}</td>
							<td>{p.date}</td>
							<td>{p.status}</td>
							<td>{p.method}</td>
							<td>{p.comment}</td>
							<td>
								<button class="icon-btn edit" title="Редактировать" on:click={() => openModal(p)}>
									<Edit size={16} />
								</button>
								<button class="icon-btn delete" title="Удалить" on:click={() => deletePayment(p.id)}>
									<Trash2 size={16} />
								</button>
							</td>
						</tr>
					{/each}
				</tbody>
			</table>
		</div>
	{/if}
</div>

{#if showModal}
	<!-- svelte-ignore a11y_click_events_have_key_events, a11y_no_static_element_interactions -->
	<div class="modal-backdrop" out:fade={{ duration: 250 }} on:click={closeModal}></div>
	<div class="modal" in:fly={{ y: 30 }}>
		<h3>{editPayment ? 'Редактировать оплату' : 'Добавить оплату'}</h3>
		<form on:submit|preventDefault={savePayment}>
			<div class="form-row">
				<div class="form-group">
					<label for="parentId">Родитель</label>
					<select id="parentId" bind:value={paymentForm.parentId} required>
						<option value="" disabled>Выберите родителя</option>
						{#each parents as p}
							<option value={p.id}>{p.username}</option>
						{/each}
					</select>
				</div>
				<div class="form-group">
					<label for="voucherId">Путёвка</label>
					<select id="voucherId" bind:value={paymentForm.voucherId} required>
						<option value="" disabled>Выберите путёвку</option>
						{#each vouchers as v}
							<option value={v.id}>#{v.id}</option>
						{/each}
					</select>
				</div>
			</div>

			<div class="form-row">
				<div class="form-group">
					<label for="amount">Сумма</label>
					<input id="amount" type="number" min="0" step="0.01" bind:value={paymentForm.amount} required />
				</div>
				<div class="form-group">
					<label for="date">Дата</label>
					<input id="date" type="datetime-local" bind:value={paymentForm.date} required />
				</div>
			</div>

			<div class="form-row">
				<div class="form-group">
					<label for="status">Статус</label>
					<select id="status" bind:value={paymentForm.status} required>
						{#each statuses as s}
							<option value={s}>{s}</option>
						{/each}
					</select>
				</div>
				<div class="form-group">
					<label for="method">Способ</label>
					<select id="method" bind:value={paymentForm.method} required>
						{#each methods as m}
							<option value={m}>{m}</option>
						{/each}
					</select>
				</div>
			</div>

			<div class="form-group">
				<label for="comment">Комментарий</label>
				<textarea id="comment" bind:value={paymentForm.comment} rows="2" placeholder="Дополнительная информация..."></textarea>
			</div>

			<div class="modal-actions">
				<button type="submit" class="save-btn">Сохранить</button>
				<button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
			</div>
		</form>
	</div>
{/if}

<style>
	.payment-admin {
		padding: 1rem;
	}

	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 2rem;
	}

	.header h2 {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		font-size: 1.5rem;
		color: var(--primary);
		margin: 0;
	}

	.add-btn {
		background: var(--primary);
		color: white;
		border: none;
		border-radius: var(--radius);
		padding: 0.75rem 1.5rem;
		font-size: 0.9rem;
		font-weight: 500;
		cursor: pointer;
		transition: var(--transition);
		display: flex;
		align-items: center;
		gap: 0.5rem;
	}

	.add-btn:hover {
		background: var(--primary-dark);
		transform: translateY(-2px);
	}

	.loader, .error {
		text-align: center;
		margin: 2rem 0;
		font-size: 1rem;
		color: var(--text-secondary);
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 0.5rem;
	}

	.error {
		color: var(--error);
	}

	.payment-table {
		overflow-x: auto;
	}

    table {
        width: 100%;
        border-collapse: collapse;
        background: var(--bg-primary);
        border-radius: var(--radius);
        overflow: hidden;
        border: 1px solid var(--border);
    }

    th {
        background: var(--bg-secondary);
        color: var(--text-primary);
        font-weight: 600;
        padding: 1rem;
        text-align: left;
        border-bottom: 1px solid var(--border);
        cursor: pointer;
        user-select: none;
        transition: var(--transition);
    }

    th:hover {
        background: var(--bg-hover);
    }

    th.active {
        background: var(--primary-light);
        color: var(--primary);
    }

    th span {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    td {
        padding: 1rem;
        border-bottom: 1px solid var(--border);
        color: var(--text-primary);
    }

    tr:hover {
        background: var(--bg-hover);
    }

	.icon-btn {
		background: none;
		border: none;
		cursor: pointer;
		padding: 0.25rem;
		border-radius: var(--radius);
		transition: var(--transition);
		display: inline-flex;
		align-items: center;
		margin-right: 0.5rem;
	}

	.icon-btn.edit {
		color: var(--primary);
	}

	.icon-btn.delete {
		color: var(--error);
	}

	.icon-btn:hover {
		background: var(--bg-hover);
	}

	.modal-backdrop {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		z-index: 1000;
	}

	.modal {
		position: fixed;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		background: var(--bg-primary);
		padding: 2rem;
		border-radius: var(--radius);
		box-shadow: var(--shadow);
		z-index: 1001;
		min-width: 600px;
		max-width: 90vw;
		max-height: 90vh;
		overflow-y: auto;
	}

	.modal h3 {
		margin-bottom: 1.5rem;
		font-size: 1.5rem;
		color: var(--primary);
	}

	.form-row {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 1rem;
		margin-bottom: 1rem;
	}

	.form-group {
		margin-bottom: 1rem;
	}

	.form-group label {
		display: block;
		margin-bottom: 0.5rem;
		font-weight: 500;
		color: var(--text-primary);
	}

	.form-group input, .form-group select, .form-group textarea {
		width: 100%;
		padding: 0.75rem;
		border: 1px solid var(--border);
		border-radius: var(--radius);
		background: var(--bg-primary);
		color: var(--text-primary);
		font-size: 0.9rem;
		transition: var(--transition);
    	box-sizing: border-box;
	}

	.form-group input:focus, .form-group select:focus, .form-group textarea:focus {
		border-color: var(--primary);
		box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
	}

	.form-group textarea {
		resize: vertical;
		min-height: 80px;
	}

	.modal-actions {
		display: flex;
		gap: 1rem;
		justify-content: flex-end;
		margin-top: 1.5rem;
	}

	.save-btn, .cancel-btn {
		padding: 0.75rem 1.5rem;
		border-radius: var(--radius);
		font-weight: 500;
		text-decoration: none;
		transition: var(--transition);
		border: none;
		cursor: pointer;
		font-size: 0.9rem;
	}

	.save-btn {
		background: var(--primary);
		color: white;
	}

	.save-btn:hover {
		background: var(--primary-dark);
	}

	.cancel-btn {
		background: transparent;
		color: var(--text-primary);
		border: 1px solid var(--border);
	}

	.cancel-btn:hover {
		background: var(--bg-hover);
	}

	@media (max-width: 768px) {
		.header {
			flex-direction: column;
			gap: 1rem;
			align-items: stretch;
		}

		.form-row {
			grid-template-columns: 1fr;
		}

		.modal {
			min-width: 300px;
			margin: 1rem;
		}
	}
</style> 