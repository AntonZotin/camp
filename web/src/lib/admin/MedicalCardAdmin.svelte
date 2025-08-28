<script lang="ts">
	import { onMount } from 'svelte';
	import type {Child, MedicalCard} from "$lib/models";
	import { fade, fly } from 'svelte/transition';
	import { Loader, Plus, Trash2, Edit, AlertCircle, FileText } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';

	import type { UserSession } from '$lib/stores/userStore';
	import {toast} from "svelte-sonner";

	export let user: UserSession;

	let cards: MedicalCard[] = [];
	let loading = true;
	let loadingChildren = false;
	let error = '';
	let showModal = false;
	let editCard: MedicalCard | null = null;
	let cardForm = { childId: 0, healthInfo: '', chronicDiseases: '', allergies: '', vaccinations: '', notes: '' };
	let children: Child[] = [];

	async function loadCards() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/medical-cards`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok)
				error = 'Ошибка загрузки медкарт';
			else
				cards = await res.json();
		} finally {
			loading = false;
		}
	}

	async function loadChildren() {
		loadingChildren = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/children`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (res.ok) children = await res.json();
		} finally {
			loadingChildren = false;
		}
	}

	function openModal(card: MedicalCard | null = null) {
		showModal = true;
		editCard = card;
		if (card) {
			cardForm = {
				childId: card.child?.id || 0,
				healthInfo: card.healthInfo || '',
				chronicDiseases: card.chronicDiseases || '',
				allergies: card.allergies || '',
				vaccinations: card.vaccinations || '',
				notes: card.notes || ''
			};
		} else {
			cardForm = { childId: 0, healthInfo: '', chronicDiseases: '', allergies: '', vaccinations: '', notes: '' };
		}
	}

	function closeModal() {
		showModal = false;
		editCard = null;
	}

	async function saveCard() {
		const method = editCard ? 'PUT' : 'POST';
		const url = editCard ? `${PUBLIC_API_URL}/api/medical-cards/${editCard.id}` : `${PUBLIC_API_URL}/api/medical-cards`;
		const body = JSON.stringify({
			child: children.find(c => c.id == cardForm.childId),
			healthInfo: cardForm.healthInfo,
			chronicDiseases: cardForm.chronicDiseases,
			allergies: cardForm.allergies,
			vaccinations: cardForm.vaccinations,
			notes: cardForm.notes
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
			await loadCards();
			closeModal();
			toast.success('Карта успешно сохранена');
		} else {
			const error = await response.text();
			toast.error(`Ошибка: ${error}`);
		}
	}

	async function deleteCard(id: number) {
		if (!confirm('Удалить медкарту?')) return;
		const response = await fetch(`${PUBLIC_API_URL}/api/medical-cards/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user.accessToken}` }
		});
		if (response.ok) {
			await loadCards();
			toast.success('Карта успешно удалена');
		} else {
			const error = await response.text();
			toast.error(`Ошибка: ${error}`);
		}
	}

	onMount(() => {
		loadCards();
		loadChildren();
	});
</script>

<div class="medical-card-admin">
	<div class="header">
		<h2>
			<FileText size={24} />
			<span>Управление медицинскими картами</span>
		</h2>
		<button class="add-btn" on:click={() => openModal()}>
			<Plus size={18} />
			<span>Добавить медкарту</span>
		</button>
	</div>

	{#if loading || loadingChildren}
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
		<div class="card-table">
			<table>
				<thead>
					<tr>
						<th>ID</th>
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
							<td>{c.id}</td>
							<td>{c.child?.fullName}</td>
							<td>{c.healthInfo}</td>
							<td>{c.chronicDiseases}</td>
							<td>{c.allergies}</td>
							<td>{c.vaccinations}</td>
							<td>{c.notes}</td>
							<td>
								<button class="icon-btn edit" title="Редактировать" on:click={() => openModal(c)}>
									<Edit size={16} />
								</button>
								<button class="icon-btn delete" title="Удалить" on:click={() => deleteCard(c.id)}>
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
		<h3>{editCard ? 'Редактировать медкарту' : 'Добавить медкарту'}</h3>
		<form on:submit|preventDefault={saveCard}>
			<div class="form-group">
				<label for="childId">Ребёнок</label>
				<select id="childId" bind:value={cardForm.childId} required>
					<option value="" disabled>Выберите ребёнка</option>
					{#each children as ch}
						<option value={ch.id}>{ch.fullName}</option>
					{/each}
				</select>
			</div>

			<div class="form-group">
				<label for="healthInfo">Здоровье</label>
				<textarea id="healthInfo" bind:value={cardForm.healthInfo} rows="2" placeholder="Общая информация о здоровье..."></textarea>
			</div>

			<div class="form-group">
				<label for="chronicDiseases">Хронические заболевания</label>
				<textarea id="chronicDiseases" bind:value={cardForm.chronicDiseases} rows="2" placeholder="Хронические заболевания..."></textarea>
			</div>

			<div class="form-group">
				<label for="allergies">Аллергии</label>
				<textarea id="allergies" bind:value={cardForm.allergies} rows="2" placeholder="Аллергии..."></textarea>
			</div>

			<div class="form-group">
				<label for="vaccinations">Прививки</label>
				<textarea id="vaccinations" bind:value={cardForm.vaccinations} rows="2" placeholder="Информация о прививках..."></textarea>
			</div>

			<div class="form-group">
				<label for="notes">Примечания</label>
				<textarea id="notes" bind:value={cardForm.notes} rows="2" placeholder="Дополнительные примечания..."></textarea>
			</div>

			<div class="modal-actions">
				<button type="submit" class="save-btn">Сохранить</button>
				<button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
			</div>
		</form>
	</div>
{/if}

<style>
	.medical-card-admin {
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

	.card-table {
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

	.form-group {
		margin-bottom: 1rem;
	}

	.form-group label {
		display: block;
		margin-bottom: 0.5rem;
		font-weight: 500;
		color: var(--text-primary);
	}

	.form-group select, .form-group textarea {
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

	.form-group select:focus, .form-group textarea:focus {
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

		.modal {
			min-width: 300px;
			margin: 1rem;
		}
	}
</style> 