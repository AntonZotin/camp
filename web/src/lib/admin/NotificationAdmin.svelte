<script lang="ts">
	import { onMount } from 'svelte';
	import type {MedicalVisit, Notification, User} from "$lib/models";
	import { fade, fly } from 'svelte/transition';
	import {Loader, Plus, Trash2, Edit, AlertCircle, Bell, ArrowUpDown} from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';
	import {toast} from "svelte-sonner";
	import SearchBox from "$lib/components/SearchBox.svelte";

	export let user: UserSession;

	let notifications: Notification[] = [];
	let filteredNotifications: Notification[] = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editNotification: Notification | null = null;
	type NotificationForm = {
	  recipientId: number | null;
	  type: string;
	  subject: string;
	  message: string;
	  status: string;
	};
	let notificationForm: NotificationForm = { recipientId: null, type: 'internal', subject: '', message: '', status: 'pending' };
	let users: User[] = [];
	let loadingUsers = false;
    let searchQuery = '';
    let sortField: keyof Notification = 'type';
    let sortDirection: 'asc' | 'desc' = 'asc';
	const types = ['internal', 'email', 'telegram'];
	const statuses = ['pending', 'sent', 'failed'];

	async function loadNotifications() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/notifications`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok)
				error = 'Ошибка загрузки уведомлений';
			else {
				notifications = await res.json();
				filterAndSortNotifications();
			}
		} finally {
			loading = false;
		}
	}

	async function loadUsers() {
		loadingUsers = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/admin/users`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (res.ok) users = await res.json();
		} finally {
			loadingUsers = false;
		}
	}

    function filterAndSortNotifications() {
        filteredNotifications = notifications.filter(notification =>
            notification.recipient.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
            notification.type.toLowerCase().includes(searchQuery.toLowerCase()) ||
            notification.subject.toLowerCase().includes(searchQuery) ||
            notification.message.toLowerCase().includes(searchQuery) ||
            notification.status.toLowerCase().includes(searchQuery)
        );

        filteredNotifications.sort((a, b) => {
            let valueA = a[sortField];
            let valueB = b[sortField];

            if (typeof valueA === 'string') valueA = valueA.toLowerCase();
            if (typeof valueB === 'string') valueB = valueB.toLowerCase();

            if (valueA < valueB) return sortDirection === 'asc' ? -1 : 1;
            if (valueA > valueB) return sortDirection === 'asc' ? 1 : -1;
            return 0;
        });
    }

    function sortBy(field: keyof Notification) {
        if (sortField === field) {
            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDirection = 'asc';
        }
        filterAndSortNotifications();
    }

	function openModal(notification: Notification | null = null) {
		showModal = true;
		editNotification = notification;
		if (notification) {
			notificationForm = {
				recipientId: notification.recipient.id || null,
				type: notification.type || 'internal',
				subject: notification.subject || '',
				message: notification.message || '',
				status: notification.status || 'pending'
			};
		} else {
			notificationForm = { recipientId: null, type: 'internal', subject: '', message: '', status: 'pending' };
		}
	}

	function closeModal() {
		showModal = false;
		editNotification = null;
	}

	async function saveNotification() {
		const method = editNotification ? 'PUT' : 'POST';
		const url = editNotification ? `${PUBLIC_API_URL}/api/notifications/${editNotification.id}` : `${PUBLIC_API_URL}/api/notifications`;
		const body = JSON.stringify({
			recipient: users.find(u => u.id == notificationForm.recipientId),
			type: notificationForm.type,
			subject: notificationForm.subject,
			message: notificationForm.message,
			status: notificationForm.status
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
			await loadNotifications();
			closeModal();
			toast.success('Уведомление успешно сохранено');
		} else {
			const error = await response.text();
			toast.error(`Ошибка: ${error}`);
		}
	}

	async function deleteNotification(id: number) {
		if (!confirm('Удалить уведомление?')) return;
		const response = await fetch(`${PUBLIC_API_URL}/api/notifications/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user.accessToken}` }
		});
		if (response.ok) {
			await loadNotifications();
			toast.success('Уведомление успешно удалено');
		} else {
			const error = await response.text();
			toast.error(`Ошибка: ${error}`);
		}
	}

    $: if (searchQuery || sortField) {
        filterAndSortNotifications();
    }

	onMount(() => { loadNotifications(); loadUsers(); });
</script>

<div class="notification-admin">
	<div class="header">
		<h2>
			<Bell size={24} />
			<span>Управление уведомлениями</span>
		</h2>
		<button class="add-btn" on:click={() => openModal()}>
			<Plus size={18} />
			<span>Добавить уведомление</span>
		</button>
	</div>

    <SearchBox
        bind:value={searchQuery}
        placeholder="Поиск по всем полям..."
    />

	{#if loading || loadingUsers}
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
		<div class="notification-table">
			<table>
				<thead>
					<tr>
						<th on:click={() => sortBy('id')} class:active={sortField==='id'}>
							<span>ID <ArrowUpDown size={14}/></span>
						</th>
						<th>Получатель</th>
						<th on:click={() => sortBy('type')} class:active={sortField==='type'}>
							<span>Тип <ArrowUpDown size={14}/></span>
						</th>
						<th on:click={() => sortBy('subject')} class:active={sortField==='subject'}>
							<span>Тема <ArrowUpDown size={14}/></span>
						</th>
						<th on:click={() => sortBy('message')} class:active={sortField==='message'}>
							<span>Сообщение <ArrowUpDown size={14}/></span>
						</th>
						<th on:click={() => sortBy('status')} class:active={sortField==='status'}>
							<span>Статус <ArrowUpDown size={14}/></span>
						</th>
						<th on:click={() => sortBy('createdAt')} class:active={sortField==='createdAt'}>
							<span>Создано <ArrowUpDown size={14}/></span>
						</th>
						<th on:click={() => sortBy('sentAt')} class:active={sortField==='sentAt'}>
							<span>Отправлено <ArrowUpDown size={14}/></span>
						</th>
						<th>Действия</th>
					</tr>
				</thead>
				<tbody>
					{#each filteredNotifications as n}
						<tr>
							<td>{n.id}</td>
							<td>{n.recipient?.username}</td>
							<td>{n.type}</td>
							<td>{n.subject}</td>
							<td>{n.message}</td>
							<td>{n.status}</td>
							<td>{n.createdAt}</td>
							<td>{n.sentAt}</td>
							<td>
								<button class="icon-btn edit" title="Редактировать" on:click={() => openModal(n)}>
									<Edit size={16} />
								</button>
								<button class="icon-btn delete" title="Удалить" on:click={() => deleteNotification(n.id)}>
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
		<h3>{editNotification ? 'Редактировать уведомление' : 'Добавить уведомление'}</h3>
		<form on:submit|preventDefault={saveNotification}>
			<div class="form-row">
				<div class="form-group">
					<label for="recipientId">Получатель</label>
					<select id="recipientId" bind:value={notificationForm.recipientId} required>
						<option value="" disabled>Выберите пользователя</option>
						{#each users as u}
							<option value={u.id}>{u.username}</option>
						{/each}
					</select>
				</div>
				<div class="form-group">
					<label for="type">Тип</label>
					<select id="type" bind:value={notificationForm.type} required>
						{#each types as t}
							<option value={t}>{t}</option>
						{/each}
					</select>
				</div>
			</div>

			<div class="form-group">
				<label for="subject">Тема</label>
				<input id="subject" bind:value={notificationForm.subject} required />
			</div>

			<div class="form-group">
				<label for="message">Сообщение</label>
				<textarea id="message" bind:value={notificationForm.message} rows="3" required placeholder="Текст уведомления..."></textarea>
			</div>

			<div class="form-group">
				<label for="status">Статус</label>
				<select id="status" bind:value={notificationForm.status} required>
					{#each statuses as s}
						<option value={s}>{s}</option>
					{/each}
				</select>
			</div>

			<div class="modal-actions">
				<button type="submit" class="save-btn">Сохранить</button>
				<button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
			</div>
		</form>
	</div>
{/if}

<style>
	.notification-admin {
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

	.notification-table {
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