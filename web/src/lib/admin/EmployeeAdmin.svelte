<script lang="ts">
	import { fade, fly } from 'svelte/transition';
	import { Loader, Plus, Trash2, Edit, AlertCircle, Users } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';
	export let user: UserSession;

	let employees = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editEmployee = null;
	let employeeForm = { fullName: '', position: '', userId: '' };
	let users = [];
	let loadingUsers = false;

	async function loadEmployees() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/employees`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки сотрудников');
			employees = await res.json();
		} catch (e) {
			error = (e as Error).message || 'Ошибка';
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

	function openModal(employee = null) {
		showModal = true;
		editEmployee = employee;
		if (employee) {
			employeeForm = {
				fullName: employee.fullName || '',
				position: employee.position || '',
				userId: employee.user?.id || ''
			};
		} else {
			employeeForm = { fullName: '', position: '', userId: '' };
		}
	}

	function closeModal() {
		showModal = false;
		editEmployee = null;
	}

	async function saveEmployee() {
		const method = editEmployee ? 'PUT' : 'POST';
		const url = editEmployee ? `${PUBLIC_API_URL}/api/employees/${editEmployee.id}` : `${PUBLIC_API_URL}/api/employees`;
		const body = JSON.stringify({
			fullName: employeeForm.fullName,
			position: employeeForm.position,
			user: users.find(u => u.id == employeeForm.userId)
		});
		await fetch(url, {
			method,
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${user.accessToken}`
			},
			body
		});
		await loadEmployees();
		closeModal();
	}

	async function deleteEmployee(id: number) {
		if (!confirm('Удалить сотрудника?')) return;
		await fetch(`${PUBLIC_API_URL}/api/employees/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user.accessToken}` }
		});
		await loadEmployees();
	}

	import { onMount } from 'svelte';
	onMount(() => { loadEmployees(); loadUsers(); });
</script>

<div class="employee-admin">
	<div class="header">
		<h2>
			<Users size={24} />
			<span>Управление сотрудниками</span>
		</h2>
		<button class="add-btn" on:click={() => openModal()}>
			<Plus size={18} />
			<span>Добавить сотрудника</span>
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
		<div class="employee-table">
			<table>
				<thead>
					<tr>
						<th>ID</th>
						<th>ФИО</th>
						<th>Должность</th>
						<th>Пользователь</th>
						<th>Действия</th>
					</tr>
				</thead>
				<tbody>
					{#each employees as e}
						<tr>
							<td>{e.id}</td>
							<td>{e.fullName}</td>
							<td>{e.position}</td>
							<td>{e.user?.username}</td>
							<td>
								<button class="icon-btn edit" title="Редактировать" on:click={() => openModal(e)}>
									<Edit size={16} />
								</button>
								<button class="icon-btn delete" title="Удалить" on:click={() => deleteEmployee(e.id)}>
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
	<div class="modal-backdrop" on:click={closeModal}></div>
	<div class="modal" in:fly={{ y: 30 }}>
		<h3>{editEmployee ? 'Редактировать сотрудника' : 'Добавить сотрудника'}</h3>
		<form on:submit|preventDefault={saveEmployee}>
			<div class="form-row">
				<div class="form-group">
					<label for="fullName">ФИО</label>
					<input id="fullName" bind:value={employeeForm.fullName} required />
				</div>
				<div class="form-group">
					<label for="position">Должность</label>
					<input id="position" bind:value={employeeForm.position} required />
				</div>
			</div>

			<div class="form-group">
				<label for="userId">Пользователь</label>
				<select id="userId" bind:value={employeeForm.userId} required>
					<option value="" disabled>Выберите пользователя</option>
					{#each users as u}
						<option value={u.id}>{u.username}</option>
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
	.employee-admin {
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

	.employee-table {
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

	.form-group input, .form-group select {
		width: 100%;
		padding: 0.75rem;
		border: 1px solid var(--border);
		border-radius: var(--radius);
		background: var(--bg-primary);
		color: var(--text-primary);
		font-size: 0.9rem;
		transition: var(--transition);
	}

	.form-group input:focus, .form-group select:focus {
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