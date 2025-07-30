<script lang="ts">
	import { fade, fly } from 'svelte/transition';
	import { Calendar, Loader, AlertCircle, Plus, Edit, Trash2, Clock, Users } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';
	export let user: UserSession;

	let sessions: any[] = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editSession: any = null;
	let sessionForm = { 
		name: '', 
		startDate: '', 
		endDate: '', 
		description: '',
		maxChildren: 50,
		price: 0
	};

	async function loadSessions() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/sessions`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки смен');
			sessions = await res.json();
		} catch (e) {
			error = (e as Error).message || 'Ошибка';
		} finally {
			loading = false;
		}
	}

	function openModal(sessionData = null) {
		showModal = true;
		editSession = sessionData;
		if (sessionData) {
			sessionForm = {
				name: sessionData.name || '',
				startDate: sessionData.startDate || '',
				endDate: sessionData.endDate || '',
				description: sessionData.description || '',
				maxChildren: sessionData.maxChildren || 50,
				price: sessionData.price || 0
			};
		} else {
			sessionForm = {
				name: '',
				startDate: '',
				endDate: '',
				description: '',
				maxChildren: 50,
				price: 0
			};
		}
	}

	function closeModal() {
		showModal = false;
		editSession = null;
	}

	async function saveSession() {
		const method = editSession ? 'PUT' : 'POST';
		const url = editSession ? `${PUBLIC_API_URL}/api/sessions/${editSession.id}` : `${PUBLIC_API_URL}/api/sessions`;
		const body = JSON.stringify(sessionForm);
		await fetch(url, {
			method,
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${user.accessToken}`
			},
			body
		});
		await loadSessions();
		closeModal();
	}

	async function deleteSession(id: number) {
		if (!confirm('Удалить смену?')) return;
		await fetch(`${PUBLIC_API_URL}/api/sessions/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user.accessToken}` }
		});
		await loadSessions();
	}

	import { onMount } from 'svelte';
	onMount(() => { loadSessions(); });
</script>

<div class="sessions-admin">
	<div class="header">
		<h2>
			<Calendar size={24} />
			<span>Управление сменами</span>
		</h2>
		<button class="add-btn" on:click={() => openModal()}>
			<Plus size={18} />
			<span>Добавить смену</span>
		</button>
	</div>

	{#if loading}
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
		<div class="sessions-table">
			<table>
				<thead>
					<tr>
						<th>ID</th>
						<th>Название</th>
						<th>Даты</th>
						<th>Макс. детей</th>
						<th>Цена</th>
						<th>Описание</th>
						<th>Действия</th>
					</tr>
				</thead>
				<tbody>
					{#each sessions as s}
						<tr>
							<td>{s.id}</td>
							<td>{s.name}</td>
							<td>
								<div class="dates">
									<div class="date-item">
										<Clock size={14} />
										<span>Начало: {s.startDate}</span>
									</div>
									<div class="date-item">
										<Calendar size={14} />
										<span>Конец: {s.endDate}</span>
									</div>
								</div>
							</td>
							<td>
								<div class="capacity">
									<Users size={14} />
									<span>{s.maxChildren || 'Не указано'}</span>
								</div>
							</td>
							<td>{s.price ? `${s.price} ₽` : 'Не указана'}</td>
							<td>
								<div class="description-cell">
									{s.description || '-'}
								</div>
							</td>
							<td>
								<button class="icon-btn edit" title="Редактировать" on:click={() => openModal(s)}>
									<Edit size={16} />
								</button>
								<button class="icon-btn delete" title="Удалить" on:click={() => deleteSession(s.id)}>
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
	<div class="modal-backdrop" on:click={closeModal}></div>
	<div class="modal" in:fly={{ y: 30 }}>
		<h3>{editSession ? 'Редактировать смену' : 'Добавить смену'}</h3>
		<form on:submit|preventDefault={saveSession}>
			<div class="form-row">
				<div class="form-group">
					<label for="name">Название смены</label>
					<input id="name" bind:value={sessionForm.name} required />
				</div>
				<div class="form-group">
					<label for="maxChildren">Максимум детей</label>
					<input id="maxChildren" type="number" bind:value={sessionForm.maxChildren} min="1" max="200" />
				</div>
			</div>
			
			<div class="form-row">
				<div class="form-group">
					<label for="startDate">Дата начала</label>
					<input id="startDate" type="date" bind:value={sessionForm.startDate} required />
				</div>
				<div class="form-group">
					<label for="endDate">Дата окончания</label>
					<input id="endDate" type="date" bind:value={sessionForm.endDate} required />
				</div>
			</div>

			<div class="form-group">
				<label for="price">Стоимость (₽)</label>
				<input id="price" type="number" bind:value={sessionForm.price} min="0" step="100" />
			</div>

			<div class="form-group">
				<label for="description">Описание</label>
				<textarea id="description" bind:value={sessionForm.description} rows="4" placeholder="Описание смены, программы, особенности..."></textarea>
			</div>

			<div class="modal-actions">
				<button type="submit" class="save-btn">Сохранить</button>
				<button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
			</div>
		</form>
	</div>
{/if}

<style>
	.sessions-admin {
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

	.sessions-table {
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

	.dates {
		display: flex;
		flex-direction: column;
		gap: 0.25rem;
	}

	.date-item {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		font-size: 0.9rem;
		color: var(--text-secondary);
	}

	.capacity {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		color: var(--primary);
		font-weight: 500;
	}

	.description-cell {
		max-width: 200px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
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

	.form-group input, .form-group textarea {
		width: 100%;
		padding: 0.75rem;
		border: 1px solid var(--border);
		border-radius: var(--radius);
		background: var(--bg-primary);
		color: var(--text-primary);
		font-size: 0.9rem;
		transition: var(--transition);
	}

	.form-group input:focus, .form-group textarea:focus {
		border-color: var(--primary);
		box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
	}

	.form-group textarea {
		resize: vertical;
		min-height: 100px;
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