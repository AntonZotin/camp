<script lang="ts">
	import { themeStore } from '$lib/stores/themeStore';
	import { userStore } from '$lib/stores/userStore';
	import { onMount } from 'svelte';
	import { Users, Trash2, Shield, Loader, AlertCircle, UserCog, Download } from 'lucide-svelte';
	import { get } from 'svelte/store';
	import { AdminExport } from '$lib';
	import { MenuAdmin } from '$lib';
	import { MedicalCardAdmin } from '$lib';
	import { MedicalVisitAdmin } from '$lib';
	import { NotificationAdmin } from '$lib';
	import { PaymentAdmin } from '$lib';
	import { EmployeeAdmin } from '$lib';
	import { ActivityLogAdmin } from '$lib';
	import { PUBLIC_API_URL } from '$env/static/public';

	let theme: 'light' | 'dark' = 'light';
	const unsubTheme = themeStore.subscribe((t) => (theme = t));
	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	let tab: 'users' | 'sessions' | 'menu' | 'medical-cards' | 'medical-visits' | 'notifications' | 'payments' | 'employees' | 'activity-logs' | 'export' = 'users';

	// Users
	let users = [];
	let loadingUsers = true;
	let errorUsers = '';
	let roles = ['ADMIN', 'PARENT', 'EMPLOYEE', 'USER'];

	async function loadUsers() {
		loadingUsers = true;
		errorUsers = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/admin/users`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки пользователей');
			users = await res.json();
		} catch (e) {
			errorUsers = e.message || 'Ошибка';
		} finally {
			loadingUsers = false;
		}
	}

	async function deleteUser(id: number) {
		if (!confirm('Удалить пользователя?')) return;
		await fetch(`${PUBLIC_API_URL}/api/admin/users/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user?.accessToken}` }
		});
		await loadUsers();
	}

	async function changeRole(id: number, role: string) {
		await fetch(`${PUBLIC_API_URL}/api/admin/users/${id}/role?role=${role}`, {
			method: 'PUT',
			headers: { Authorization: `Bearer ${user?.accessToken}` }
		});
		await loadUsers();
	}

	// Смены (CampSessions)
	let sessions = [];
	let loadingSessions = true;
	let errorSessions = '';
	let showSessionModal = false;
	let editSession = null;
	let sessionForm = { name: '', startDate: '', endDate: '', description: '' };

	async function loadSessions() {
		loadingSessions = true;
		errorSessions = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/sessions`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки смен');
			sessions = await res.json();
		} catch (e) {
			errorSessions = e.message || 'Ошибка';
		} finally {
			loadingSessions = false;
		}
	}

	function openSessionModal(session = null) {
		showSessionModal = true;
		editSession = session;
		if (session) {
			sessionForm = { ...session };
		} else {
			sessionForm = { name: '', startDate: '', endDate: '', description: '' };
		}
	}
	function closeSessionModal() {
		showSessionModal = false;
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
				Authorization: `Bearer ${user?.accessToken}`
			},
			body
		});
		await loadSessions();
		closeSessionModal();
	}
	async function deleteSession(id: number) {
		if (!confirm('Удалить смену?')) return;
		await fetch(`${PUBLIC_API_URL}/api/sessions/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user?.accessToken}` }
		});
		await loadSessions();
	}

	onMount(() => { loadUsers(); loadSessions(); return () => { unsubTheme(); unsubUser(); }; });
</script>

<div class="admin-page" data-theme={theme}>
	<h1><Shield size={28}/> Админ-панель</h1>
	<div class="tabs">
		<button class:active={tab==='users'} on:click={() => tab='users'}><Users size={18}/> Пользователи</button>
		<button class:active={tab==='sessions'} on:click={() => tab='sessions'}>Смены</button>
		<button class:active={tab==='menu'} on:click={() => tab='menu'}>Меню</button>
		<button class:active={tab==='medical-cards'} on:click={() => tab='medical-cards'}>Медкарты</button>
		<button class:active={tab==='medical-visits'} on:click={() => tab='medical-visits'}>Медосмотры</button>
		<button class:active={tab==='notifications'} on:click={() => tab='notifications'}>Уведомления</button>
		<button class:active={tab==='payments'} on:click={() => tab='payments'}>Оплаты</button>
		<button class:active={tab==='employees'} on:click={() => tab='employees'}>Сотрудники</button>
		<button class:active={tab==='activity-logs'} on:click={() => tab='activity-logs'}>Журнал активности</button>
		<button class:active={tab==='export'} on:click={() => tab='export'}><Download size={18}/> Экспорт</button>
	</div>

	{#if tab === 'users'}
		<div class="tab-content">
			{#if loadingUsers}
				<div class="loader spin"><Loader size={24}/> Загрузка...</div>
			{:else if errorUsers}
				<div class="error"><AlertCircle size={20}/> {errorUsers}</div>
			{:else}
				<table class="users-table">
					<thead>
						<tr>
							<th>ID</th>
							<th>Логин</th>
							<th>Email</th>
							<th>Роль</th>
							<th>Действия</th>
						</tr>
					</thead>
					<tbody>
						{#each users as u}
							<tr>
								<td>{u.id}</td>
								<td>{u.username}</td>
								<td>{u.email}</td>
								<td>
									<select bind:value={u.role} on:change={(e) => changeRole(u.id, e.target.value)}>
										{#each roles as r}
											<option value={r}>{r}</option>
										{/each}
									</select>
								</td>
								<td>
									<button class="icon-btn" title="Удалить" on:click={() => deleteUser(u.id)}><Trash2 size={18}/></button>
								</td>
							</tr>
						{/each}
					</tbody>
				</table>
			{/if}
		</div>
	{/if}
	{#if tab === 'sessions'}
		<div class="tab-content">
			{#if loadingSessions}
				<div class="loader spin"><Loader size={24}/> Загрузка...</div>
			{:else if errorSessions}
				<div class="error"><AlertCircle size={20}/> {errorSessions}</div>
			{:else}
				<button class="add-btn" on:click={() => openSessionModal()}>+ Новая смена</button>
				<table class="sessions-table">
					<thead>
						<tr>
							<th>ID</th>
							<th>Название</th>
							<th>Даты</th>
							<th>Описание</th>
							<th>Действия</th>
						</tr>
					</thead>
					<tbody>
						{#each sessions as s}
							<tr>
								<td>{s.id}</td>
								<td>{s.name}</td>
								<td>{s.startDate} — {s.endDate}</td>
								<td>{s.description}</td>
								<td>
									<button class="icon-btn blue" title="Редактировать" on:click={() => openSessionModal(s)}><UserCog size={18}/></button>
									<button class="icon-btn" title="Удалить" on:click={() => deleteSession(s.id)}><Trash2 size={18}/></button>
								</td>
							</tr>
						{/each}
					</tbody>
				</table>
			{/if}
			{#if showSessionModal}
				<button type="button" class="modal-backdrop" on:click={closeSessionModal} aria-label="Закрыть модальное окно"></button>
				<section class="modal" on:click|stopPropagation tabindex="0" role="dialog" aria-modal="true" on:keydown={e => { if (e.key === 'Escape') closeSessionModal(); }}>
					<h2>{editSession ? 'Редактировать смену' : 'Новая смена'}</h2>
					<form on:submit|preventDefault={saveSession}>
						<label>Название<input bind:value={sessionForm.name} required /></label>
						<label>Дата начала<input type="date" bind:value={sessionForm.startDate} required /></label>
						<label>Дата окончания<input type="date" bind:value={sessionForm.endDate} required /></label>
						<label>Описание<textarea bind:value={sessionForm.description} rows="2"></textarea></label>
						<div class="modal-actions">
							<button type="submit" class="save-btn">Сохранить</button>
							<button type="button" class="cancel-btn" on:click={closeSessionModal}>Отмена</button>
						</div>
					</form>
				</section>
			{/if}
		</div>
	{/if}
	{#if tab === 'menu'}
		<MenuAdmin {user} {theme} />
	{/if}
	{#if tab === 'medical-cards'}
		<MedicalCardAdmin {user} {theme} />
	{/if}
	{#if tab === 'medical-visits'}
		<MedicalVisitAdmin {user} {theme} />
	{/if}
	{#if tab === 'notifications'}
		<NotificationAdmin {user} {theme} />
	{/if}
	{#if tab === 'payments'}
		<PaymentAdmin {user} {theme} />
	{/if}
	{#if tab === 'employees'}
		<EmployeeAdmin {user} {theme} />
	{/if}
	{#if tab === 'activity-logs'}
		<ActivityLogAdmin {user} {theme} />
	{/if}
	{#if tab === 'export'}
		<AdminExport {user} {theme} />
	{/if}
</div>

<style>
.admin-page {
	padding: 2rem 1rem;
	max-width: 1100px;
	margin: 0 auto;
	color: var(--color-text, #222);
}
.admin-page[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-card: #23272f;
}
.admin-page[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-card: #fff;
}
h1 {
	display: flex;
	align-items: center;
	gap: 0.7rem;
	font-size: 1.7rem;
	color: var(--color-primary, #2d8cff);
	margin-bottom: 2rem;
}
.tabs {
	display: flex;
	gap: 1.2rem;
	margin-bottom: 2rem;
}
.tabs button {
	background: none;
	border: none;
	font-size: 1.1rem;
	font-weight: 500;
	color: var(--color-primary, #2d8cff);
	padding: 0.7rem 1.5rem;
	border-radius: 10px 10px 0 0;
	cursor: pointer;
	transition: background 0.18s, color 0.18s;
	display: flex;
	align-items: center;
	gap: 0.5rem;
}
.tabs button.active, .tabs button:hover {
	background: var(--color-card);
	color: var(--color-accent, #ffb347);
}
.tab-content {
	background: var(--color-card);
	border-radius: 0 0 18px 18px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	padding: 2rem 1.5rem;
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
.users-table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 1rem;
}
.users-table th, .users-table td {
	padding: 0.7rem 0.5rem;
	text-align: left;
	border-bottom: 1px solid #e0e0e0;
}
.users-table th {
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
.icon-btn:hover {
	background: #ffeaea;
}
.spin { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }
.sessions-table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 1rem;
}
.sessions-table th, .sessions-table td {
	padding: 0.7rem 0.5rem;
	text-align: left;
	border-bottom: 1px solid #e0e0e0;
}
.sessions-table th {
	color: var(--color-primary, #2d8cff);
	font-size: 1.05rem;
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
	transition: background 0.18s;
}
.add-btn:hover {
	background: var(--color-accent, #ffb347);
	color: #222;
}
.icon-btn.blue {
	color: #2d8cff;
}
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
.modal h2 {
	margin-bottom: 1.2rem;
	font-size: 1.3rem;
	color: var(--color-primary, #2d8cff);
}
.modal label {
	display: block;
	margin-bottom: 0.7rem;
	font-size: 1rem;
}
.modal input, .modal textarea {
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
</style>