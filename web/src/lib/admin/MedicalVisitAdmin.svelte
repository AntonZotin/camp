<script lang="ts">
	import { onMount } from 'svelte';
	import type {Child, Employee, MedicalVisit} from "$lib/models";
	import { fade, fly } from 'svelte/transition';
	import { Loader, Plus, Trash2, Edit, AlertCircle, Stethoscope } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';

	export let user: UserSession;

	let visits: MedicalVisit[] = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editVisit: MedicalVisit | null = null;
	type VisitForm = {
	  date: string;
	  childId: number | null;
	  doctorId: number | null;
	  description: string;
	  recommendations: string;
	  medications: string;
	};
	let visitForm: VisitForm = { date: '', childId: null, doctorId: null, description: '', recommendations: '', medications: '' };
	let children: Child[] = [];
	let doctors: Employee[] = [];
	let loadingChildren = false;
	let loadingDoctors = false;

	async function loadVisits() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/medical-visits`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok)
				error = 'Ошибка загрузки медосмотров';
			else
				visits = await res.json();
		} finally {
			loading = false;
		}
	}

	async function loadChildren() {
		loadingChildren = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/children`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (res.ok) children = await res.json();
		} finally {
			loadingChildren = false;
		}
	}

	async function loadDoctors() {
		loadingDoctors = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/employees`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (res.ok) doctors = (await res.json() as Employee[]).filter(e =>
					e.position?.toLowerCase().includes('медсестра') ||
					e.position?.toLowerCase().includes('психолог')
			);
		} finally {
			loadingDoctors = false;
		}
	}

	function openModal(visit: MedicalVisit | null = null) {
		showModal = true;
		editVisit = visit;
		if (visit) {
			visitForm = {
				date: visit.date,
				childId: visit.child.id || null,
				doctorId: visit.doctor.id || null,
				description: visit.description || '',
				recommendations: visit.recommendations || '',
				medications: visit.medications || ''
			};
		} else {
			visitForm = { date: '', childId: null, doctorId: null, description: '', recommendations: '', medications: '' };
		}
	}

	function closeModal() {
		showModal = false;
		editVisit = null;
	}

	async function saveVisit() {
		const method = editVisit ? 'PUT' : 'POST';
		const url = editVisit ? `${PUBLIC_API_URL}/api/medical-visits/${editVisit.id}` : `${PUBLIC_API_URL}/api/medical-visits`;
		const body = JSON.stringify({
			date: visitForm.date,
			child: children.find(c => c.id == visitForm.childId),
			doctor: doctors.find(d => d.id == visitForm.doctorId),
			description: visitForm.description,
			recommendations: visitForm.recommendations,
			medications: visitForm.medications
		});
		await fetch(url, {
			method,
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${user.accessToken}`
			},
			body
		});
		await loadVisits();
		closeModal();
	}

	async function deleteVisit(id: number) {
		if (!confirm('Удалить медосмотр?')) return;
		await fetch(`${PUBLIC_API_URL}/api/medical-visits/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user.accessToken}` }
		});
		await loadVisits();
	}

	onMount(() => { loadVisits(); loadChildren(); loadDoctors(); });
</script>

<div class="medical-visit-admin">
	<div class="header">
		<h2>
			<Stethoscope size={24} />
			<span>Управление медицинскими осмотрами</span>
		</h2>
		<button class="add-btn" on:click={() => openModal()}>
			<Plus size={18} />
			<span>Добавить осмотр</span>
		</button>
	</div>

	{#if loading || loadingChildren || loadingDoctors}
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
		<div class="visit-table">
			<table>
				<thead>
					<tr>
						<th>ID</th>
						<th>Дата</th>
						<th>Ребёнок</th>
						<th>Врач</th>
						<th>Описание</th>
						<th>Рекомендации</th>
						<th>Лекарства</th>
						<th>Действия</th>
					</tr>
				</thead>
				<tbody>
					{#each visits as v}
						<tr>
							<td>{v.id}</td>
							<td>{v.date}</td>
							<td>{v.child?.fullName}</td>
							<td>{v.doctor?.fullName}</td>
							<td>{v.description}</td>
							<td>{v.recommendations}</td>
							<td>{v.medications}</td>
							<td>
								<button class="icon-btn edit" title="Редактировать" on:click={() => openModal(v)}>
									<Edit size={16} />
								</button>
								<button class="icon-btn delete" title="Удалить" on:click={() => deleteVisit(v.id)}>
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
		<h3>{editVisit ? 'Редактировать осмотр' : 'Добавить осмотр'}</h3>
		<form on:submit|preventDefault={saveVisit}>
			<div class="form-row">
				<div class="form-group">
					<label for="date">Дата</label>
					<input id="date" type="date" bind:value={visitForm.date} required />
				</div>
				<div class="form-group">
					<label for="childId">Ребёнок</label>
					<select id="childId" bind:value={visitForm.childId} required>
						<option value="" disabled>Выберите ребёнка</option>
						{#each children as ch}
							<option value={ch.id}>{ch.fullName}</option>
						{/each}
					</select>
				</div>
			</div>

			<div class="form-group">
				<label for="doctorId">Врач</label>
				<select id="doctorId" bind:value={visitForm.doctorId} required>
					<option value="" disabled>Выберите врача</option>
					{#each doctors as d}
						<option value={d.id}>{d.fullName}</option>
					{/each}
				</select>
			</div>

			<div class="form-group">
				<label for="description">Описание</label>
				<textarea id="description" bind:value={visitForm.description} rows="2" placeholder="Описание осмотра..."></textarea>
			</div>

			<div class="form-group">
				<label for="recommendations">Рекомендации</label>
				<textarea id="recommendations" bind:value={visitForm.recommendations} rows="2" placeholder="Рекомендации врача..."></textarea>
			</div>

			<div class="form-group">
				<label for="medications">Лекарства</label>
				<textarea id="medications" bind:value={visitForm.medications} rows="2" placeholder="Назначенные лекарства..."></textarea>
			</div>

			<div class="modal-actions">
				<button type="submit" class="save-btn">Сохранить</button>
				<button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
			</div>
		</form>
	</div>
{/if}

<style>
	.medical-visit-admin {
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

	.visit-table {
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