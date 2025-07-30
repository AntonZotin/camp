<script lang="ts">
	import { Loader, Plus, Trash2, Edit, AlertCircle } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	export let user;
	export let theme: 'light' | 'dark' = 'light';

	let menus = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editMenu = null;
	let menuForm = { date: '', sessionId: '', breakfast: '', lunch: '', dinner: '', notes: '' };
	let sessions = [];
	let loadingSessions = false;

	async function loadMenus() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/menus`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки меню');
			menus = await res.json();
		} catch (e) {
			error = e.message || 'Ошибка';
		} finally {
			loading = false;
		}
	}

	async function loadSessions() {
		loadingSessions = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/sessions`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (res.ok) sessions = await res.json();
		} finally {
			loadingSessions = false;
		}
	}

	function openModal(menu = null) {
		showModal = true;
		editMenu = menu;
		if (menu) {
			menuForm = {
				date: menu.date,
				sessionId: menu.session?.id || '',
				breakfast: menu.breakfast || '',
				lunch: menu.lunch || '',
				dinner: menu.dinner || '',
				notes: menu.notes || ''
			};
		} else {
			menuForm = { date: '', sessionId: '', breakfast: '', lunch: '', dinner: '', notes: '' };
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
				Authorization: `Bearer ${user?.accessToken}`
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
			headers: { Authorization: `Bearer ${user?.accessToken}` }
		});
		await loadMenus();
	}

	import { onMount } from 'svelte';
	onMount(() => { loadMenus(); loadSessions(); });
</script>

<div class="menu-admin" data-theme={theme}>
	<h2>Меню питания</h2>
	{#if loading}
		<div class="loader spin"><Loader size={24}/> Загрузка...</div>
	{:else if error}
		<div class="error"><AlertCircle size={20}/> {error}</div>
	{:else}
		<button class="add-btn" on:click={() => openModal()}><Plus size={18}/> Новое меню</button>
		<table class="menu-table">
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
							<button class="icon-btn blue" title="Редактировать" on:click={() => openModal(m)}><Edit size={16}/></button>
							<button class="icon-btn" title="Удалить" on:click={() => deleteMenu(m.id)}><Trash2 size={16}/></button>
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	{/if}
	{#if showModal}
		<button type="button" class="modal-backdrop" on:click={closeModal} aria-label="Закрыть модальное окно"></button>
		<section class="modal" on:click|stopPropagation tabindex="0" role="dialog" aria-modal="true" on:keydown={e => { if (e.key === 'Escape') closeModal(); }}>
			<h3>{editMenu ? 'Редактировать меню' : 'Новое меню'}</h3>
			<form on:submit|preventDefault={saveMenu}>
				<label>Дата<input type="date" bind:value={menuForm.date} required /></label>
				<label>Смена
					<select bind:value={menuForm.sessionId} required>
						<option value="" disabled>Выберите смену</option>
						{#each sessions as s}
							<option value={s.id}>{s.name}</option>
						{/each}
					</select>
				</label>
				<label>Завтрак<textarea bind:value={menuForm.breakfast} rows="2"></textarea></label>
				<label>Обед<textarea bind:value={menuForm.lunch} rows="2"></textarea></label>
				<label>Ужин<textarea bind:value={menuForm.dinner} rows="2"></textarea></label>
				<label>Примечания<textarea bind:value={menuForm.notes} rows="2"></textarea></label>
				<div class="modal-actions">
					<button type="submit" class="save-btn">Сохранить</button>
					<button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
				</div>
			</form>
		</section>
	{/if}
</div>

<style>
.menu-admin {
	padding: 1rem;
	background: var(--bg-primary);
	border-radius: var(--radius);
	border: 1px solid var(--border);
	max-width: 1100px;
	margin: 0 auto;
}

.menu-admin h2 {
	font-size: 1.5rem;
	color: var(--primary);
	margin-bottom: 2rem;
	display: flex;
	align-items: center;
	gap: 0.75rem;
}

.add-btn {
	background: var(--primary);
	color: white;
	border: none;
	border-radius: var(--radius);
	padding: 0.75rem 1.5rem;
	font-size: 0.9rem;
	font-weight: 500;
	margin-bottom: 1.5rem;
	cursor: pointer;
	display: flex;
	align-items: center;
	gap: 0.5rem;
	transition: var(--transition);
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
	width: 100%;
	border-collapse: collapse;
	background: var(--bg-primary);
	border-radius: var(--radius);
	overflow: hidden;
	border: 1px solid var(--border);
}

.menu-table th {
	background: var(--bg-secondary);
	color: var(--text-primary);
	font-weight: 600;
	padding: 1rem;
	text-align: left;
	border-bottom: 1px solid var(--border);
}

.menu-table td {
	padding: 1rem;
	border-bottom: 1px solid var(--border);
	color: var(--text-primary);
}

.menu-table tr:hover {
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

.icon-btn.blue {
	color: var(--primary);
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
	min-width: 500px;
	max-width: 90vw;
	max-height: 90vh;
	overflow-y: auto;
}

.modal h3 {
	margin-bottom: 1.5rem;
	font-size: 1.5rem;
	color: var(--primary);
}

.modal label {
	display: block;
	margin-bottom: 1rem;
	font-weight: 500;
	color: var(--text-primary);
}

.modal input, .modal select, .modal textarea {
	width: 100%;
	padding: 0.75rem;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	background: var(--bg-primary);
	color: var(--text-primary);
	font-size: 0.9rem;
	transition: var(--transition);
	margin-top: 0.5rem;
}

.modal input:focus, .modal select:focus, .modal textarea:focus {
	border-color: var(--primary);
	box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
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
	.menu-admin {
		padding: 1rem;
		margin: 0;
	}

	.menu-table {
		font-size: 0.8rem;
	}

	.menu-table th, .menu-table td {
		padding: 0.5rem;
	}

	.modal {
		min-width: 300px;
		margin: 1rem;
	}
}
</style> 