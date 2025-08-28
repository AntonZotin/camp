<script lang="ts">
	import { onMount } from 'svelte';
	import type {User} from "$lib/models";
	import { fade, fly } from 'svelte/transition';
	import { Users, Loader, AlertCircle, Plus, Edit, Trash2, Eye, EyeOff } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';
	import {toast} from "svelte-sonner";

	export let user: UserSession;

    interface ChildForm {
        id?: number;
        fullName: string;
        birthDate: string;
    }

    interface EmployeeForm {
        id?: number;
        fullName: string;
        position: string;
    }

	let userForm = {
		username: '',
		email: '',
		password: '',
		role: 'PARENT',
        employee: null as EmployeeForm | null,
        children: [] as ChildForm[]
	};

    let childForm: ChildForm = {
        fullName: '',
        birthDate: ''
    };

	let users: User[] = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editUser: User | null = null;
	let showPassword = false;
    let childrenForms: ChildForm[] = [];
    let showChildModal = false;
    let editChildIndex: number | null = null;

	const roles = ['ADMIN', 'PARENT', 'EMPLOYEE'];

	async function loadUsers() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/admin/users`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok)
				error = 'Ошибка загрузки пользователей';
			else
				users = await res.json();
		} finally {
			loading = false;
		}
	}

	function openModal(userData: User | null = null) {
		showModal = true;
		editUser = userData;
		if (userData) {
            userForm = {
                username: userData.username || '',
                email: userData.email || '',
                password: '',
                role: userData.role || 'PARENT',
                employee: userData.employee ? {
                    id: userData.employee.id,
                    fullName: userData.employee.fullName,
                    position: userData.employee.position
                } : { fullName: '', position: '' },
                children: userData.children ? userData.children.map(c => ({
                    id: c.id,
                    fullName: c.fullName,
                    birthDate: c.birthDate
                })) : []
            };
		} else {
            userForm = {
                username: '',
                email: '',
                password: '',
                role: 'PARENT',
                employee: { fullName: '', position: '' },
                children: []
            };
		}
	}

	function closeModal() {
		showModal = false;
		editUser = null;
		showPassword = false;
	}

	async function saveUser() {
		const method = editUser ? 'PUT' : 'POST';
		const url = editUser ? `${PUBLIC_API_URL}/api/admin/users/${editUser.id}` : `${PUBLIC_API_URL}/api/auth/register`;
        const dataToSend = {
            ...userForm,
            employee: userForm.role === 'EMPLOYEE' ? userForm.employee : null,
            children: userForm.role === 'PARENT' ? userForm.children : []
        };
		const body = JSON.stringify(dataToSend);
		const response = await fetch(url, {
			method,
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${user.accessToken}`
			},
			body
		});
		if (response.ok) {
			await loadUsers();
			closeModal();
			toast.success('Пользователь успешно сохранён');
		} else {
			const error = await response.text();
			toast.error(`Ошибка: ${error}`);
		}
	}

	async function deleteUser(id: number) {
		if (!confirm('Удалить пользователя?')) return;
		const response = await fetch(`${PUBLIC_API_URL}/api/admin/users/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user.accessToken}` }
		});
		if (response.ok) {
			toast.success('Пользователь успешно удалён');
		} else {
			const error = await response.text();
			toast.error(`Ошибка: ${error}`);
		}
		await loadUsers();
	}

	async function changeRole(id: number, e: Event) {
		const target = e.target as HTMLSelectElement;
		const role = target.value;
		const response = await fetch(`${PUBLIC_API_URL}/api/admin/users/${id}/role?role=${role}`, {
			method: 'PUT',
			headers: { Authorization: `Bearer ${user.accessToken}` }
		});
		if (response.ok) {
			toast.success('Роль успешно изменена');
		} else {
			const error = await response.text();
			toast.error(`Ошибка: ${error}`);
		}
		await loadUsers();
	}

    function openChildModal(childData: ChildForm | null = null, index: number | null = null) {
        showChildModal = true;
        editChildIndex = index;
        if (childData) {
            childForm = {
                id: childData.id,
                fullName: childData.fullName,
                birthDate: childData.birthDate
            };
        } else {
            childForm = {
                fullName: '',
                birthDate: ''
            };
        }
    }

    function closeChildModal() {
        showChildModal = false;
        editChildIndex = null;
    }

    function saveChild() {
        if (editChildIndex !== null) {
            userForm.children[editChildIndex] = { ...childForm };
        } else {
            userForm.children.push({ ...childForm });
        }
		userForm = {...userForm};
        closeChildModal();
    }

    function deleteChild(index: number) {
        if (confirm('Удалить ребенка?')) {
            childrenForms.splice(index, 1);
        }
    }

	onMount(() => { loadUsers(); });
</script>

<div class="users-admin">
	<div class="header">
		<h2>
			<Users size={24} />
			<span>Управление пользователями</span>
		</h2>
		<button class="add-btn" on:click={() => openModal()}>
			<Plus size={18} />
			<span>Добавить пользователя</span>
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
		<div class="users-table">
			<table>
				<thead>
					<tr>
						<th>ID</th>
						<th>Логин</th>
						<th>Email</th>
						<th>Роль</th>
						<th>Доп. информация</th>
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
								<select bind:value={u.role} on:change={(e: Event) => changeRole(u.id, e)}>
									{#each roles as r}
										<option value={r}>{r}</option>
									{/each}
								</select>
							</td>
							<td>
								{#if u.employee}
									{u.employee.fullName} - {u.employee.position}
								{:else if u.children && u.children.length > 0}
									Детей: {u.children.length}
								{:else}
									-
								{/if}
							</td>
							<td>
								<button class="icon-btn edit" title="Редактировать" on:click={() => openModal(u)}>
									<Edit size={16} />
								</button>
								<button class="icon-btn delete" title="Удалить" on:click={() => deleteUser(u.id)}>
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
        <h3>{editUser ? 'Редактировать пользователя' : 'Добавить пользователя'}</h3>
        <form on:submit|preventDefault={saveUser}>
			<div class="form-row">
				<div class="form-group">
					<label for="username">Логин</label>
					<input id="username" bind:value={userForm.username} required />
				</div>
				<div class="form-group">
					<label for="email">Email</label>
					<input id="email" type="email" bind:value={userForm.email} required />
				</div>
			</div>

			<div class="form-row">
				<div class="form-group">
					<label for="password">
						Пароль {#if editUser}(оставьте пустым, чтобы не менять){/if}
					</label>
					<div class="password-input">
						<input
							id="password"
							type={showPassword ? 'text' : 'password'}
							bind:value={userForm.password}
							required={!editUser}
						/>
						<button type="button" class="toggle-password" on:click={() => showPassword = !showPassword}>
							<svelte:component this={showPassword ? EyeOff : Eye} size={16} />
						</button>
					</div>
				</div>
				<div class="form-group">
					<label for="role">Роль</label>
					<select id="role" bind:value={userForm.role} required>
						{#each roles as r}
							<option value={r}>{r}</option>
						{/each}
					</select>
				</div>
			</div>

			{#if userForm.role === 'EMPLOYEE' && userForm.employee !== null}
				<div class="form-row">
					<div class="form-group">
						<label for="fullName">Полное имя</label>
						<input id="fullName" bind:value={userForm.employee.fullName} />
					</div>
					<div class="form-group">
						<label for="position">Должность</label>
						<input id="position" bind:value={userForm.employee.position} />
					</div>
				</div>
			{/if}

			{#if userForm.role === 'PARENT'}
				<div class="children-section">
					<h4>Дети</h4>
					{#if userForm.children.length > 0}
						<div class="children-list">
							{#each userForm.children as child, index}
								<div class="child-item">
									<div class="child-info">
										<span>{child.fullName}</span>
										<span class="birth-date">{child.birthDate}</span>
									</div>
									<div class="child-actions">
										<button type="button" class="icon-btn edit" on:click={() => openChildModal(child, index)}>
											<Edit size={14} />
										</button>
										<button type="button" class="icon-btn delete" on:click={() => deleteChild(index)}>
											<Trash2 size={14} />
										</button>
									</div>
								</div>
							{/each}
						</div>
					{:else}
						<p class="no-children">Нет добавленных детей</p>
					{/if}
					<button type="button" class="add-child-btn" on:click={() => openChildModal()}>
						<Plus size={14} />
						<span>Добавить ребенка</span>
					</button>
				</div>
			{/if}

            <div class="modal-actions">
                <button type="submit" class="save-btn">Сохранить</button>
                <button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
            </div>
        </form>
    </div>
{/if}

{#if showChildModal}
    <!-- svelte-ignore a11y_click_events_have_key_events, a11y_no_static_element_interactions -->
    <div class="modal-backdrop" out:fade={{ duration: 250 }} on:click={closeChildModal}></div>
    <div class="modal child-modal" in:fly={{ y: 30 }}>
        <h3>{editChildIndex !== null ? 'Редактировать ребенка' : 'Добавить ребенка'}</h3>
        <form on:submit|preventDefault={saveChild}>
            <div class="form-group">
                <label for="childFullName">Полное имя</label>
                <input id="childFullName" bind:value={childForm.fullName} required />
            </div>
            <div class="form-group">
                <label for="childBirthDate">Дата рождения</label>
                <input id="childBirthDate" type="date" bind:value={childForm.birthDate} required />
            </div>
            <div class="modal-actions">
                <button type="submit" class="save-btn">Сохранить</button>
                <button type="button" class="cancel-btn" on:click={closeChildModal}>Отмена</button>
            </div>
        </form>
    </div>
{/if}

<style>
	.users-admin {
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

	.users-table {
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

	select {
		padding: 0.5rem;
		border: 1px solid var(--border);
		border-radius: var(--radius);
		background: var(--bg-primary);
		color: var(--text-primary);
		font-size: 0.9rem;
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

	.form-group input, .form-group select {
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

	.form-group input:focus, .form-group select:focus {
		border-color: var(--primary);
		box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
	}

	.password-input {
		position: relative;
		display: flex;
		align-items: center;
	}

	.password-input input {
		padding-right: 3rem;
	}

	.toggle-password {
		position: absolute;
		right: 0.75rem;
		background: none;
		border: none;
		cursor: pointer;
		color: var(--text-secondary);
		padding: 0.25rem;
		border-radius: var(--radius);
	}

	.toggle-password:hover {
		background: var(--bg-hover);
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

    .children-section {
        margin: 1.5rem 0;
        padding: 1rem;
        border: 1px solid var(--border);
        border-radius: var(--radius);
    }

    .children-section h4 {
        margin-top: 0;
        margin-bottom: 1rem;
        color: var(--text-primary);
    }

    .children-list {
        margin-bottom: 1rem;
    }

    .child-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.75rem;
        border-bottom: 1px solid var(--border-light);
    }

    .child-item:last-child {
        border-bottom: none;
    }

    .child-info {
        display: flex;
        flex-direction: column;
    }

    .child-info .birth-date {
        font-size: 0.8rem;
        color: var(--text-secondary);
    }

    .child-actions {
        display: flex;
        gap: 0.5rem;
    }

    .no-children {
        color: var(--text-secondary);
        font-style: italic;
        margin: 1rem 0;
    }

    .add-child-btn {
        background: var(--primary-light);
        color: var(--primary);
        border: none;
        border-radius: var(--radius);
        padding: 0.5rem 1rem;
        font-size: 0.9rem;
        cursor: pointer;
        transition: var(--transition);
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .add-child-btn:hover {
        background: var(--primary-light-hover);
    }

    .child-modal {
        min-width: 400px;
    }

    @media (max-width: 768px) {
        .child-modal {
            min-width: 300px;
        }
    }
</style> 