<script lang="ts">
	import { Loader, Plus, Trash2, Edit, AlertCircle } from 'lucide-svelte';
	export let user;
	export let theme: 'light' | 'dark' = 'light';

	let notifications = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editNotification = null;
	let notificationForm = { recipientId: '', type: 'internal', subject: '', message: '', status: 'pending' };
	let users = [];
	let loadingUsers = false;
	const types = ['internal', 'email', 'telegram'];
	const statuses = ['pending', 'sent', 'failed'];

	async function loadNotifications() {
		loading = true;
		error = '';
		try {
			const res = await fetch('/api/notifications', {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки уведомлений');
			notifications = await res.json();
		} catch (e) {
			error = e.message || 'Ошибка';
		} finally {
			loading = false;
		}
	}

	async function loadUsers() {
		loadingUsers = true;
		try {
			const res = await fetch('/api/admin/users', {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (res.ok) users = await res.json();
		} finally {
			loadingUsers = false;
		}
	}

	function openModal(notification = null) {
		showModal = true;
		editNotification = notification;
		if (notification) {
			notificationForm = {
				recipientId: notification.recipient?.id || '',
				type: notification.type || 'internal',
				subject: notification.subject || '',
				message: notification.message || '',
				status: notification.status || 'pending'
			};
		} else {
			notificationForm = { recipientId: '', type: 'internal', subject: '', message: '', status: 'pending' };
		}
	}
	function closeModal() {
		showModal = false;
		editNotification = null;
	}
	async function saveNotification() {
		const method = editNotification ? 'PUT' : 'POST';
		const url = editNotification ? `/api/notifications/${editNotification.id}` : '/api/notifications';
		const body = JSON.stringify({
			recipient: users.find(u => u.id == notificationForm.recipientId),
			type: notificationForm.type,
			subject: notificationForm.subject,
			message: notificationForm.message,
			status: notificationForm.status
		});
		await fetch(url, {
			method,
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${user?.accessToken}`
			},
			body
		});
		await loadNotifications();
		closeModal();
	}
	async function deleteNotification(id: number) {
		if (!confirm('Удалить уведомление?')) return;
		await fetch(`/api/notifications/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user?.accessToken}` }
		});
		await loadNotifications();
	}

	import { onMount } from 'svelte';
	onMount(() => { loadNotifications(); loadUsers(); });
</script>

<div class="notification-admin" data-theme={theme}>
	<h2>Уведомления</h2>
	{#if loading}
		<div class="loader"><Loader size={24} class="spin"/> Загрузка...</div>
	{:else if error}
		<div class="error"><AlertCircle size={20}/> {error}</div>
	{:else}
		<button class="add-btn" on:click={() => openModal()}><Plus size={18}/> Новое уведомление</button>
		<table class="notification-table">
			<thead>
				<tr>
					<th>Получатель</th>
					<th>Тип</th>
					<th>Тема</th>
					<th>Сообщение</th>
					<th>Статус</th>
					<th>Создано</th>
					<th>Отправлено</th>
					<th>Действия</th>
				</tr>
			</thead>
			<tbody>
				{#each notifications as n}
					<tr>
						<td>{n.recipient?.username}</td>
						<td>{n.type}</td>
						<td>{n.subject}</td>
						<td>{n.message}</td>
						<td>{n.status}</td>
						<td>{n.createdAt}</td>
						<td>{n.sentAt}</td>
						<td>
							<button class="icon-btn blue" title="Редактировать" on:click={() => openModal(n)}><Edit size={16}/></button>
							<button class="icon-btn" title="Удалить" on:click={() => deleteNotification(n.id)}><Trash2 size={16}/></button>
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	{/if}
	{#if showModal}
		<div class="modal-backdrop" on:click={closeModal}></div>
		<div class="modal" on:click|stopPropagation>
			<h3>{editNotification ? 'Редактировать уведомление' : 'Новое уведомление'}</h3>
			<form on:submit|preventDefault={saveNotification}>
				<label>Получатель
					<select bind:value={notificationForm.recipientId} required>
						<option value="" disabled>Выберите пользователя</option>
						{#each users as u}
							<option value={u.id}>{u.username}</option>
						{/each}
					</select>
				</label>
				<label>Тип
					<select bind:value={notificationForm.type} required>
						{#each types as t}
							<option value={t}>{t}</option>
						{/each}
					</select>
				</label>
				<label>Тема<input bind:value={notificationForm.subject} required /></label>
				<label>Сообщение<textarea bind:value={notificationForm.message} rows="2" required /></label>
				<label>Статус
					<select bind:value={notificationForm.status} required>
						{#each statuses as s}
							<option value={s}>{s}</option>
						{/each}
					</select>
				</label>
				<div class="modal-actions">
					<button type="submit" class="save-btn">Сохранить</button>
					<button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
				</div>
			</form>
		</div>
	{/if}
</div>

<style>
.notification-admin {
	padding: 2rem 1.5rem;
	background: var(--color-card);
	border-radius: 18px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	max-width: 1100px;
	margin: 2rem auto 0 auto;
}
.notification-admin[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-card: #23272f;
}
.notification-admin[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-card: #fff;
}
.notification-admin h2 {
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
.notification-table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 1rem;
}
.notification-table th, .notification-table td {
	padding: 0.7rem 0.5rem;
	text-align: left;
	border-bottom: 1px solid #e0e0e0;
}
.notification-table th {
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