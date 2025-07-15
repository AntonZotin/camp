<script lang="ts">
	import { themeStore } from '$lib/themeStore';
	import { userStore } from '$lib/userStore';
	import { onMount } from 'svelte';
	import { Users, Trash2, Shield, Loader, AlertCircle, UserCog, Download } from 'lucide-svelte';
	import { get } from 'svelte/store';

	let theme: 'light' | 'dark' = 'light';
	const unsubTheme = themeStore.subscribe((t) => (theme = t));
	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	let tab: 'users' | 'sessions' | 'export' = 'users';

	// Users
	let users = [];
	let loadingUsers = true;
	let errorUsers = '';
	let roles = ['ADMIN', 'PARENT', 'EMPLOYEE', 'USER'];

	async function loadUsers() {
		loadingUsers = true;
		errorUsers = '';
		try {
			const res = await fetch('/api/admin/users', {
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
		await fetch(`/api/admin/users/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user?.accessToken}` }
		});
		await loadUsers();
	}

	async function changeRole(id: number, role: string) {
		await fetch(`/api/admin/users/${id}/role?role=${role}`, {
			method: 'PUT',
			headers: { Authorization: `Bearer ${user?.accessToken}` }
		});
		await loadUsers();
	}

	onMount(() => { loadUsers(); return () => { unsubTheme(); unsubUser(); }; });
</script>

<div class="admin-page" data-theme={theme}>
	<h1><Shield size={28}/> Админ-панель</h1>
	<div class="tabs">
		<button class:active={tab==='users'} on:click={() => tab='users'}><Users size={18}/> Пользователи</button>
		<button class:active={tab==='sessions'} on:click={() => tab='sessions'}>Смены</button>
		<button class:active={tab==='export'} on:click={() => tab='export'}><Download size={18}/> Экспорт</button>
	</div>

	{#if tab === 'users'}
		<div class="tab-content">
			{#if loadingUsers}
				<div class="loader"><Loader size={24} class="spin"/> Загрузка...</div>
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
	<!-- Вкладки sessions и export будут реализованы далее -->
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
</style> 