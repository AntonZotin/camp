<script lang="ts">
	import { Loader, Plus, Trash2, Edit, AlertCircle } from 'lucide-svelte';
	export let user;
	export let theme: 'light' | 'dark' = 'light';

	let cards = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editCard = null;
	let cardForm = { childId: '', healthInfo: '', chronicDiseases: '', allergies: '', vaccinations: '', notes: '' };
	let children = [];
	let loadingChildren = false;

	async function loadCards() {
		loading = true;
		error = '';
		try {
			const res = await fetch('/api/medical-cards', {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки медкарт');
			cards = await res.json();
		} catch (e) {
			error = e.message || 'Ошибка';
		} finally {
			loading = false;
		}
	}

	async function loadChildren() {
		loadingChildren = true;
		try {
			const res = await fetch('/api/children', {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (res.ok) children = await res.json();
		} finally {
			loadingChildren = false;
		}
	}

	function openModal(card = null) {
		showModal = true;
		editCard = card;
		if (card) {
			cardForm = {
				childId: card.child?.id || '',
				healthInfo: card.healthInfo || '',
				chronicDiseases: card.chronicDiseases || '',
				allergies: card.allergies || '',
				vaccinations: card.vaccinations || '',
				notes: card.notes || ''
			};
		} else {
			cardForm = { childId: '', healthInfo: '', chronicDiseases: '', allergies: '', vaccinations: '', notes: '' };
		}
	}
	function closeModal() {
		showModal = false;
		editCard = null;
	}
	async function saveCard() {
		const method = editCard ? 'PUT' : 'POST';
		const url = editCard ? `/api/medical-cards/${editCard.id}` : '/api/medical-cards';
		const body = JSON.stringify({
			child: children.find(c => c.id == cardForm.childId),
			healthInfo: cardForm.healthInfo,
			chronicDiseases: cardForm.chronicDiseases,
			allergies: cardForm.allergies,
			vaccinations: cardForm.vaccinations,
			notes: cardForm.notes
		});
		await fetch(url, {
			method,
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${user?.accessToken}`
			},
			body
		});
		await loadCards();
		closeModal();
	}
	async function deleteCard(id: number) {
		if (!confirm('Удалить медкарту?')) return;
		await fetch(`/api/medical-cards/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user?.accessToken}` }
		});
		await loadCards();
	}

	import { onMount } from 'svelte';
	onMount(() => { loadCards(); loadChildren(); });
</script>

<div class="medical-card-admin" data-theme={theme}>
	<h2>Медицинские карты</h2>
	{#if loading}
		<div class="loader"><Loader size={24} class="spin"/> Загрузка...</div>
	{:else if error}
		<div class="error"><AlertCircle size={20}/> {error}</div>
	{:else}
		<button class="add-btn" on:click={() => openModal()}><Plus size={18}/> Новая медкарта</button>
		<table class="card-table">
			<thead>
				<tr>
					<th>Ребёнок</th>
					<th>Здоровье</th>
					<th>Хронические</th>
					<th>Аллергии</th>
					<th>Прививки</th>
					<th>Примечания</th>
					<th>Действия</th>
				</tr>
			</thead>
			<tbody>
				{#each cards as c}
					<tr>
						<td>{c.child?.name}</td>
						<td>{c.healthInfo}</td>
						<td>{c.chronicDiseases}</td>
						<td>{c.allergies}</td>
						<td>{c.vaccinations}</td>
						<td>{c.notes}</td>
						<td>
							<button class="icon-btn blue" title="Редактировать" on:click={() => openModal(c)}><Edit size={16}/></button>
							<button class="icon-btn" title="Удалить" on:click={() => deleteCard(c.id)}><Trash2 size={16}/></button>
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	{/if}
	{#if showModal}
		<div class="modal-backdrop" on:click={closeModal}></div>
		<div class="modal" on:click|stopPropagation>
			<h3>{editCard ? 'Редактировать медкарту' : 'Новая медкарта'}</h3>
			<form on:submit|preventDefault={saveCard}>
				<label>Ребёнок
					<select bind:value={cardForm.childId} required>
						<option value="" disabled>Выберите ребёнка</option>
						{#each children as ch}
							<option value={ch.id}>{ch.name}</option>
						{/each}
					</select>
				</label>
				<label>Здоровье<textarea bind:value={cardForm.healthInfo} rows="2" /></label>
				<label>Хронические<textarea bind:value={cardForm.chronicDiseases} rows="2" /></label>
				<label>Аллергии<textarea bind:value={cardForm.allergies} rows="2" /></label>
				<label>Прививки<textarea bind:value={cardForm.vaccinations} rows="2" /></label>
				<label>Примечания<textarea bind:value={cardForm.notes} rows="2" /></label>
				<div class="modal-actions">
					<button type="submit" class="save-btn">Сохранить</button>
					<button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
				</div>
			</form>
		</div>
	{/if}
</div>

<style>
.medical-card-admin {
	padding: 2rem 1.5rem;
	background: var(--color-card);
	border-radius: 18px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	max-width: 1100px;
	margin: 2rem auto 0 auto;
}
.medical-card-admin[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-card: #23272f;
}
.medical-card-admin[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-card: #fff;
}
.medical-card-admin h2 {
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
.card-table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 1rem;
}
.card-table th, .card-table td {
	padding: 0.7rem 0.5rem;
	text-align: left;
	border-bottom: 1px solid #e0e0e0;
}
.card-table th {
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