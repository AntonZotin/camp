<script lang="ts">
	import { Loader, Plus, Trash2, Edit, AlertCircle } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	export let user;
	export let theme: 'light' | 'dark' = 'light';

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
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки сотрудников');
			employees = await res.json();
		} catch (e) {
			error = e.message || 'Ошибка';
		} finally {
			loading = false;
		}
	}

	async function loadUsers() {
		loadingUsers = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/admin/users`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
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
				Authorization: `Bearer ${user?.accessToken}`
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
			headers: { Authorization: `Bearer ${user?.accessToken}` }
		});
		await loadEmployees();
	}

	import { onMount } from 'svelte';
	onMount(() => { loadEmployees(); loadUsers(); });
</script>

<div class="employee-admin" data-theme={theme}>
	<h2>Сотрудники</h2>
	{#if loading}
		<div class="loader"><Loader size={24} class="spin"/> Загрузка...</div>
	{:else if error}
		<div class="error"><AlertCircle size={20}/> {error}</div>
	{:else}
		<button class="add-btn" on:click={() => openModal()}><Plus size={18}/> Новый сотрудник</button>
		<table class="employee-table">
			<thead>
				<tr>
					<th>ФИО</th>
					<th>Должность</th>
					<th>Пользователь</th>
					<th>Действия</th>
				</tr>
			</thead>
			<tbody>
				{#each employees as e}
					<tr>
						<td>{e.fullName}</td>
						<td>{e.position}</td>
						<td>{e.user?.username}</td>
						<td>
							<button class="icon-btn blue" title="Редактировать" on:click={() => openModal(e)}><Edit size={16}/></button>
							<button class="icon-btn" title="Удалить" on:click={() => deleteEmployee(e.id)}><Trash2 size={16}/></button>
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	{/if}
	{#if showModal}
		<div class="modal-backdrop" on:click={closeModal}></div>
		<div class="modal" on:click|stopPropagation>
			<h3>{editEmployee ? 'Редактировать сотрудника' : 'Новый сотрудник'}</h3>
			<form on:submit|preventDefault={saveEmployee}>
				<label>ФИО<input bind:value={employeeForm.fullName} required /></label>
				<label>Должность<input bind:value={employeeForm.position} required /></label>
				<label>Пользователь
					<select bind:value={employeeForm.userId} required>
						<option value="" disabled>Выберите пользователя</option>
						{#each users as u}
							<option value={u.id}>{u.username}</option>
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
.employee-admin {
	padding: 2rem 1.5rem;
	background: var(--color-card);
	border-radius: 18px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	max-width: 1100px;
	margin: 2rem auto 0 auto;
}
.employee-admin[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-card: #23272f;
}
.employee-admin[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-card: #fff;
}
.employee-admin h2 {
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
.employee-table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 1rem;
}
.employee-table th, .employee-table td {
	padding: 0.7rem 0.5rem;
	text-align: left;
	border-bottom: 1px solid #e0e0e0;
}
.employee-table th {
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
.modal input, .modal select {
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