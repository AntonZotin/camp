<script lang="ts">
	import { onMount } from 'svelte';
	import type {CampSession, Menu} from "$lib/models";
	import { fade, fly } from 'svelte/transition';
	import { Loader, Plus, Trash2, Edit, AlertCircle, Utensils } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';

	export let user: UserSession;

  	let menus: Menu[] = [];
	let loading = true;
	let error = '';
	let showModal = false;
  	let editMenu: Menu | null = null;
	type MenuForm = {
	  date: string;
	  sessionId: number | null;
	  breakfast: string;
	  lunch: string;
	  dinner: string;
	  notes: string;
	};
	let menuForm: MenuForm = { date: '', sessionId: null, breakfast: '', lunch: '', dinner: '', notes: '' };
  	let sessions: CampSession[] = [];
	let loadingSessions = false;

	async function loadMenus() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/menus`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok)
				error = 'Ошибка загрузки меню';
			else
				menus = await res.json();
		} finally {
			loading = false;
		}
	}

	async function loadSessions() {
		loadingSessions = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/sessions`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (res.ok) sessions = await res.json();
		} finally {
			loadingSessions = false;
		}
	}

	function openModal(menu: Menu | null = null) {
		showModal = true;
		editMenu = menu;
		if (menu) {
			menuForm = {
				date: menu.date,
				sessionId: menu.session.id || null,
				breakfast: menu.breakfast || '',
				lunch: menu.lunch || '',
				dinner: menu.dinner || '',
				notes: menu.notes || ''
			};
		} else {
			menuForm = { date: '', sessionId: null, breakfast: '', lunch: '', dinner: '', notes: '' };
		}
	}

	function closeModal() {
		showModal = false;
		editMenu = null;
	}

	async function saveMenu() {
		const method = editMenu ? 'PUT' : 'POST';
		const url = editMenu ? `${PUBLIC_API_URL}/api/menus/${editMenu.id}` : `${PUBLIC_API_URL}/api/menus`;
		const body = JSON.stringify({
			date: menuForm.date,
			session: sessions.find(s => s.id == menuForm.sessionId),
			breakfast: menuForm.breakfast,
			lunch: menuForm.lunch,
			dinner: menuForm.dinner,
			notes: menuForm.notes
		});
		await fetch(url, {
			method,
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${user.accessToken}`
			},
			body
		});
		await loadMenus();
		closeModal();
	}

	async function deleteMenu(id: number) {
		if (!confirm('Удалить меню?')) return;
		await fetch(`${PUBLIC_API_URL}/api/menus/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user.accessToken}` }
		});
		await loadMenus();
	}

	onMount(() => { loadMenus(); loadSessions(); });
</script>

<div class="menu-admin">
	<div class="header">
		<h2>
			<Utensils size={24} />
			<span>Управление меню питания</span>
		</h2>
		<button class="add-btn" on:click={() => openModal()}>
			<Plus size={18} />
			<span>Добавить меню</span>
		</button>
	</div>

	{#if loading || loadingSessions}
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
		<div class="menu-table">
			<table>
				<thead>
					<tr>
						<th>Дата</th>
						<th>Смена</th>
						<th>Завтрак</th>
						<th>Обед</th>
						<th>Ужин</th>
						<th>Примечания</th>
						<th>Действия</th>
					</tr>
				</thead>
				<tbody>
					{#each menus as m}
						<tr>
							<td>{m.date}</td>
							<td>{m.session?.name}</td>
							<td>{m.breakfast}</td>
							<td>{m.lunch}</td>
							<td>{m.dinner}</td>
							<td>{m.notes}</td>
							<td>
								<button class="icon-btn edit" title="Редактировать" on:click={() => openModal(m)}>
									<Edit size={16} />
								</button>
								<button class="icon-btn delete" title="Удалить" on:click={() => deleteMenu(m.id)}>
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
		<h3>{editMenu ? 'Редактировать меню' : 'Добавить меню'}</h3>
		<form on:submit|preventDefault={saveMenu}>
			<div class="form-row">
				<div class="form-group">
					<label for="date">Дата</label>
					<input id="date" type="date" bind:value={menuForm.date} required />
				</div>
				<div class="form-group">
					<label for="sessionId">Смена</label>
					<select id="sessionId" bind:value={menuForm.sessionId} required>
						<option value="" disabled>Выберите смену</option>
						{#each sessions as s}
							<option value={s.id}>{s.name}</option>
						{/each}
					</select>
				</div>
			</div>

			<div class="form-group">
				<label for="breakfast">Завтрак</label>
				<textarea id="breakfast" bind:value={menuForm.breakfast} rows="2" placeholder="Меню завтрака..."></textarea>
			</div>

			<div class="form-group">
				<label for="lunch">Обед</label>
				<textarea id="lunch" bind:value={menuForm.lunch} rows="2" placeholder="Меню обеда..."></textarea>
			</div>

			<div class="form-group">
				<label for="dinner">Ужин</label>
				<textarea id="dinner" bind:value={menuForm.dinner} rows="2" placeholder="Меню ужина..."></textarea>
			</div>

			<div class="form-group">
				<label for="notes">Примечания</label>
				<textarea id="notes" bind:value={menuForm.notes} rows="2" placeholder="Дополнительные примечания..."></textarea>
			</div>

			<div class="modal-actions">
				<button type="submit" class="save-btn">Сохранить</button>
				<button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
			</div>
		</form>
	</div>
{/if}

<style>
	.menu-admin {
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

	.menu-table {
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