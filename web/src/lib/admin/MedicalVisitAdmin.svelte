<script lang="ts">
	import { Loader, Plus, Trash2, Edit, AlertCircle } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	export let user;
	export let theme: 'light' | 'dark' = 'light';

	let visits = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editVisit = null;
	let visitForm = { date: '', childId: '', doctorId: '', description: '', recommendations: '', medications: '' };
	let children = [];
	let doctors = [];
	let loadingChildren = false;
	let loadingDoctors = false;

	async function loadVisits() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/medical-visits`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки медосмотров');
			visits = await res.json();
		} catch (e) {
			error = e.message || 'Ошибка';
		} finally {
			loading = false;
		}
	}

	async function loadChildren() {
		loadingChildren = true;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/children`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
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
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (res.ok) doctors = (await res.json()).filter(e => e.position?.toLowerCase().includes('врач') || e.position?.toLowerCase().includes('doctor'));
		} finally {
			loadingDoctors = false;
		}
	}

	function openModal(visit = null) {
		showModal = true;
		editVisit = visit;
		if (visit) {
			visitForm = {
				date: visit.date,
				childId: visit.child?.id || '',
				doctorId: visit.doctor?.id || '',
				description: visit.description || '',
				recommendations: visit.recommendations || '',
				medications: visit.medications || ''
			};
		} else {
			visitForm = { date: '', childId: '', doctorId: '', description: '', recommendations: '', medications: '' };
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
				Authorization: `Bearer ${user?.accessToken}`
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
			headers: { Authorization: `Bearer ${user?.accessToken}` }
		});
		await loadVisits();
	}

	import { onMount } from 'svelte';
	onMount(() => { loadVisits(); loadChildren(); loadDoctors(); });
</script>

<div class="medical-visit-admin" data-theme={theme}>
	<h2>Медицинские осмотры</h2>
	{#if loading}
		<div class="loader spin"><Loader size={24}/> Загрузка...</div>
	{:else if error}
		<div class="error"><AlertCircle size={20}/> {error}</div>
	{:else}
		<button class="add-btn" on:click={() => openModal()}><Plus size={18}/> Новый осмотр</button>
		<table class="visit-table">
			<thead>
				<tr>
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
						<td>{v.date}</td>
						<td>{v.child?.name}</td>
						<td>{v.doctor?.fullName}</td>
						<td>{v.description}</td>
						<td>{v.recommendations}</td>
						<td>{v.medications}</td>
						<td>
							<button class="icon-btn blue" title="Редактировать" on:click={() => openModal(v)}><Edit size={16}/></button>
							<button class="icon-btn" title="Удалить" on:click={() => deleteVisit(v.id)}><Trash2 size={16}/></button>
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	{/if}
	{#if showModal}
		<button type="button" class="modal-backdrop" on:click={closeModal} aria-label="Закрыть модальное окно"></button>
		<section class="modal" on:click|stopPropagation tabindex="0" role="dialog" aria-modal="true" on:keydown={e => { if (e.key === 'Escape') closeModal(); }}>
			<h3>{editVisit ? 'Редактировать осмотр' : 'Новый осмотр'}</h3>
			<form on:submit|preventDefault={saveVisit}>
				<label>Дата<input type="date" bind:value={visitForm.date} required /></label>
				<label>Ребёнок
					<select bind:value={visitForm.childId} required>
						<option value="" disabled>Выберите ребёнка</option>
						{#each children as ch}
							<option value={ch.id}>{ch.name}</option>
						{/each}
					</select>
				</label>
				<label>Врач
					<select bind:value={visitForm.doctorId} required>
						<option value="" disabled>Выберите врача</option>
						{#each doctors as d}
							<option value={d.id}>{d.fullName}</option>
						{/each}
					</select>
				</label>
				<label>Описание<textarea bind:value={visitForm.description} rows="2"></textarea></label>
				<label>Рекомендации<textarea bind:value={visitForm.recommendations} rows="2"></textarea></label>
				<label>Лекарства<textarea bind:value={visitForm.medications} rows="2"></textarea></label>
				<div class="modal-actions">
					<button type="submit" class="save-btn">Сохранить</button>
					<button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
				</div>
			</form>
		</section>
	{/if}
</div>

<style>
.medical-visit-admin {
	padding: 2rem 1.5rem;
	background: var(--color-card);
	border-radius: 18px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	max-width: 1100px;
	margin: 2rem auto 0 auto;
}
.medical-visit-admin[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-card: #23272f;
}
.medical-visit-admin[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-card: #fff;
}
.medical-visit-admin h2 {
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
.visit-table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 1rem;
}
.visit-table th, .visit-table td {
	padding: 0.7rem 0.5rem;
	text-align: left;
	border-bottom: 1px solid #e0e0e0;
}
.visit-table th {
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