<script lang="ts">
	import { fade, fly } from 'svelte/transition';
	import { userStore } from '$lib/stores/userStore';
	import { onMount } from 'svelte';
	import { Users, Trash2, Shield, Loader, AlertCircle, UserCog, Download, Plus, Calendar, Activity, Settings } from 'lucide-svelte';
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

	onMount(() => { loadUsers(); loadSessions(); return () => { unsubUser(); }; });
</script>

<div class="stars-bg"></div>

<section class="admin-section" transition:fade>
	<div class="container">
		<div class="admin-container" in:fly={{ y: 50 }}>
			<div class="admin-hero">
				<div class="logo">
					<span class="gradient-text">Sunny Camp</span>
				</div>
				<h1>Админ-панель</h1>
				<p>Управление системой лагеря, пользователями и контентом</p>
				<div class="admin-image">
					<img src="/src/images/camp-hero.png" alt="Администрирование" />
				</div>
			</div>

			<div class="admin-content">
				<div class="tabs" in:fly={{ y: 30, delay: 200 }}>
					<button class:active={tab==='users'} on:click={() => tab='users'}>
						<Users size={18} />
						<span>Пользователи</span>
					</button>
					<button class:active={tab==='sessions'} on:click={() => tab='sessions'}>
						<Calendar size={18} />
						<span>Смены</span>
					</button>
					<button class:active={tab==='menu'} on:click={() => tab='menu'}>
						<Settings size={18} />
						<span>Меню</span>
					</button>
					<button class:active={tab==='medical-cards'} on:click={() => tab='medical-cards'}>
						<Shield size={18} />
						<span>Медкарты</span>
					</button>
					<button class:active={tab==='medical-visits'} on:click={() => tab='medical-visits'}>
						<Activity size={18} />
						<span>Медосмотры</span>
					</button>
					<button class:active={tab==='notifications'} on:click={() => tab='notifications'}>
						<Settings size={18} />
						<span>Уведомления</span>
					</button>
					<button class:active={tab==='payments'} on:click={() => tab='payments'}>
						<Settings size={18} />
						<span>Оплаты</span>
					</button>
					<button class:active={tab==='employees'} on:click={() => tab='employees'}>
						<Users size={18} />
						<span>Сотрудники</span>
					</button>
					<button class:active={tab==='activity-logs'} on:click={() => tab='activity-logs'}>
						<Activity size={18} />
						<span>Журнал активности</span>
					</button>
					<button class:active={tab==='export'} on:click={() => tab='export'}>
						<Download size={18} />
						<span>Экспорт</span>
					</button>
				</div>

				<div class="tab-content" in:fly={{ y: 30, delay: 400 }}>
					{#if tab === 'users'}
						{#if loadingUsers}
							<div class="loader">
								<Loader size={24} />
								<span>Загрузка...</span>
							</div>
						{:else if errorUsers}
							<div class="error">
								<AlertCircle size={20} />
								<span>{errorUsers}</span>
							</div>
						{:else}
							<table class="data-table">
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
												<button class="icon-btn delete" title="Удалить" on:click={() => deleteUser(u.id)}>
													<Trash2 size={18} />
												</button>
											</td>
										</tr>
									{/each}
								</tbody>
							</table>
						{/if}
					{/if}

					{#if tab === 'sessions'}
						{#if loadingSessions}
							<div class="loader">
								<Loader size={24} />
								<span>Загрузка...</span>
							</div>
						{:else if errorSessions}
							<div class="error">
								<AlertCircle size={20} />
								<span>{errorSessions}</span>
							</div>
						{:else}
							<button class="add-btn" on:click={() => openSessionModal()}>
								<Plus size={18} />
								<span>Новая смена</span>
							</button>
							<table class="data-table">
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
												<button class="icon-btn edit" title="Редактировать" on:click={() => openSessionModal(s)}>
													<UserCog size={18} />
												</button>
												<button class="icon-btn delete" title="Удалить" on:click={() => deleteSession(s.id)}>
													<Trash2 size={18} />
												</button>
											</td>
										</tr>
									{/each}
								</tbody>
							</table>
						{/if}
					{/if}

					{#if tab === 'menu'}
						<MenuAdmin {user} />
					{/if}
					{#if tab === 'medical-cards'}
						<MedicalCardAdmin {user} />
					{/if}
					{#if tab === 'medical-visits'}
						<MedicalVisitAdmin {user} />
					{/if}
					{#if tab === 'notifications'}
						<NotificationAdmin {user} />
					{/if}
					{#if tab === 'payments'}
						<PaymentAdmin {user} />
					{/if}
					{#if tab === 'employees'}
						<EmployeeAdmin {user} />
					{/if}
					{#if tab === 'activity-logs'}
						<ActivityLogAdmin {user} />
					{/if}
					{#if tab === 'export'}
						<AdminExport {user} />
					{/if}
				</div>
			</div>
		</div>
	</div>
</section>

{#if showSessionModal}
	<div class="modal-backdrop" on:click={closeSessionModal}></div>
	<div class="modal" in:fly={{ y: 30 }}>
		<h2>{editSession ? 'Редактировать смену' : 'Новая смена'}</h2>
		<form on:submit|preventDefault={saveSession}>
			<div class="form-group">
				<label for="name">Название</label>
				<input id="name" bind:value={sessionForm.name} required />
			</div>
			<div class="form-group">
				<label for="startDate">Дата начала</label>
				<input id="startDate" type="date" bind:value={sessionForm.startDate} required />
			</div>
			<div class="form-group">
				<label for="endDate">Дата окончания</label>
				<input id="endDate" type="date" bind:value={sessionForm.endDate} required />
			</div>
			<div class="form-group">
				<label for="description">Описание</label>
				<textarea id="description" bind:value={sessionForm.description} rows="3"></textarea>
			</div>
			<div class="modal-actions">
				<button type="submit" class="button primary">Сохранить</button>
				<button type="button" class="button secondary" on:click={closeSessionModal}>Отмена</button>
			</div>
		</form>
	</div>
{/if}

<style>
	.stars-bg {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background: url("/src/images/star.png");
		z-index: -1;
		opacity: 0.3;
	}

	.admin-section {
		min-height: 100vh;
		display: flex;
		padding: 2rem 0;
	}

	.container {
		width: 100%;
		max-width: 1400px;
		margin: 0 auto;
		padding: 0 1rem;
	}

	.admin-container {
		display: grid;
		grid-template-columns: 300px 1fr;
		background: var(--bg-primary);
		border-radius: var(--radius);
		overflow: hidden;
		box-shadow: var(--shadow);
	}

	.admin-hero {
		background: linear-gradient(135deg, var(--primary), var(--primary-dark));
		color: white;
		padding: 2rem;
		display: flex;
		flex-direction: column;
	}

	.logo {
		font-size: 1.5rem;
		font-weight: 700;
		margin-bottom: 1.5rem;
	}

	.admin-hero h1 {
		font-size: 2rem;
		margin-bottom: 0.5rem;
		line-height: 1.2;
	}

	.admin-hero p {
		opacity: 0.9;
		margin-bottom: 1.5rem;
		font-size: 0.9rem;
	}

	.admin-image {
		margin-top: auto;
		text-align: center;
	}

	.admin-image img {
		max-width: 100%;
		height: auto;
		border-radius: var(--radius);
		opacity: 0.8;
	}

	.admin-content {
		padding: 2rem;
		display: flex;
		flex-direction: column;
	}

	.tabs {
		display: flex;
		flex-wrap: wrap;
		gap: 0.5rem;
		margin-bottom: 2rem;
		padding-bottom: 1rem;
		border-bottom: 1px solid var(--border);
	}

	.tabs button {
		background: var(--bg-secondary);
		border: 1px solid var(--border);
		border-radius: var(--radius);
		padding: 0.75rem 1rem;
		font-size: 0.9rem;
		font-weight: 500;
		color: var(--text-secondary);
		cursor: pointer;
		transition: var(--transition);
		display: flex;
		align-items: center;
		gap: 0.5rem;
	}

	.tabs button:hover {
		background: var(--bg-hover);
		color: var(--text-primary);
	}

	.tabs button.active {
		background: var(--primary);
		color: white;
		border-color: var(--primary);
	}

	.tab-content {
		flex: 1;
	}

	.loader, .error {
		text-align: center;
		margin: 2rem 0;
		font-size: 1rem;
		color: var(--text-secondary);
		display: flex;
		align-items: center;
		gap: 0.5rem;
		justify-content: center;
	}

	.error {
		color: var(--error);
	}

	.data-table {
		width: 100%;
		border-collapse: collapse;
		margin-top: 1rem;
		background: var(--bg-primary);
		border-radius: var(--radius);
		overflow: hidden;
		box-shadow: var(--shadow);
	}

	.data-table th, .data-table td {
		padding: 0.75rem 1rem;
		text-align: left;
		border-bottom: 1px solid var(--border);
	}

	.data-table th {
		background: var(--bg-secondary);
		color: var(--text-primary);
		font-weight: 600;
		font-size: 0.9rem;
	}

	.data-table td {
		color: var(--text-primary);
		font-size: 0.9rem;
	}

	.data-table select {
		padding: 0.25rem 0.5rem;
		border: 1px solid var(--border);
		border-radius: var(--radius);
		background: var(--bg-primary);
		color: var(--text-primary);
		font-size: 0.8rem;
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

	.add-btn {
		background: var(--primary);
		color: white;
		border: none;
		border-radius: var(--radius);
		padding: 0.75rem 1.5rem;
		font-size: 0.9rem;
		font-weight: 500;
		margin-bottom: 1rem;
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
		min-width: 400px;
		max-width: 90vw;
		max-height: 90vh;
		overflow-y: auto;
	}

	.modal h2 {
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

	.modal-actions {
		display: flex;
		gap: 1rem;
		justify-content: flex-end;
		margin-top: 1.5rem;
	}

	@media (max-width: 1024px) {
		.admin-container {
			grid-template-columns: 1fr;
		}

		.admin-hero {
			display: none;
		}

		.admin-content {
			padding: 1.5rem;
		}

		.tabs {
			justify-content: center;
		}
	}

	@media (max-width: 768px) {
		.admin-section {
			padding: 1rem 0;
		}

		.admin-hero h1 {
			font-size: 1.5rem;
		}

		.tabs {
			flex-direction: column;
		}

		.tabs button {
			justify-content: center;
		}

		.modal {
			min-width: 300px;
			margin: 1rem;
		}
	}
</style>