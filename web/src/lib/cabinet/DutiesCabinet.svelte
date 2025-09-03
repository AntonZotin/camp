<script lang="ts">
	import { onMount } from 'svelte';
	import type {DutyLog} from "$lib/models";
	import { fly } from 'svelte/transition';
	import {Activity, Clock, MapPin, Users, Loader, AlertCircle, CheckCircle, XCircle, Calendar} from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';
    import { Edit, Save, X } from 'lucide-svelte';
	import {toast} from "svelte-sonner";

	export let user: UserSession;

	let duties: DutyLog[] = [];
	let loading = true;
	let error = '';
    let editingDuty: number | null = null;
    let editedNotes = '';
    let editedReport = '';
    let editedStatus = '';

	async function loadDuties() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/duty-logs/employee/${user.userId}`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok)
				error = 'Ошибка загрузки дежурств';
			else {
				duties = await res.json();
            	duties.sort((a, b) => b.id - a.id);
			}
		} finally {
			loading = false;
		}
	}

	function getStatusIcon(status: string) {
		if (status === 'COMPLETED') return CheckCircle;
		if (status === 'CANCELLED') return XCircle;
		if (status === 'PLANNED') return Calendar;
		return Clock;
	}

	function getStatusColor(status: string) {
		if (status === 'COMPLETED') return 'var(--secondary)';
		if (status === 'CANCELLED') return 'var(--error)';
		if (status === 'PLANNED') return 'var(--primary-dark)';
		return 'var(--primary)';
	}

	function getStatusText(status: string) {
		if (status === 'COMPLETED') return 'Завершено';
		if (status === 'CANCELLED') return 'Отменено';
		if (status === 'PLANNED') return 'Планируется';
		return 'В процессе';
	}

    function startEdit(duty: DutyLog) {
        editingDuty = duty.id;
        editedNotes = duty.notes || '';
        editedReport = duty.report || '';
        editedStatus = duty.status;
    }

    function cancelEdit() {
        editingDuty = null;
        editedNotes = '';
        editedReport = '';
        editedStatus = '';
    }

    async function saveDuty(dutyId: number) {
        try {
            const res = await fetch(`${PUBLIC_API_URL}/api/duty-logs/${dutyId}`, {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${user.accessToken}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    notes: editedNotes,
                    report: editedReport,
                    status: editedStatus
                })
            });

            if (res.ok) {
                await loadDuties();
                editingDuty = null;
    			toast.success('Дежурство успешно изменено');
            } else {
    			toast.error('Ошибка при сохранении');
            }
        } catch (err) {
            error = 'Ошибка при сохранении';
        }
    }

	onMount(() => { loadDuties(); });
</script>

<div class="duties-cabinet">
	<div class="header">
		<h2>
			<Activity size={24} />
			<span>Журнал дежурств</span>
		</h2>
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
	{:else if duties.length === 0}
		<div class="empty">
			<Activity size={48} />
			<h3>У вас пока нет дежурств</h3>
			<p>Ваши дежурства будут отображаться здесь</p>
		</div>
	{:else}
		<div class="duties-grid">
			{#each duties as duty, i (duty.id)}
				<div class="duty-card" in:fly={{ y: 30, delay: i * 100 }}>
					<div class="duty-header">
						<Activity size={24} />
						<h3>Дежурство #{duty.id}</h3>
						<div class="status" style="color: {getStatusColor(duty.status)}">
							<svelte:component this={getStatusIcon(duty.status)} size={20} />
							{#if editingDuty === duty.id}
								<select bind:value={editedStatus}>
									<option value="PLANNED">Планируется</option>
									<option value="IN_PROGRESS">В процессе</option>
									<option value="COMPLETED">Завершено</option>
									<option value="CANCELLED">Отменено</option>
								</select>
							{:else}
								<span>{getStatusText(duty.status)}</span>
							{/if}
						</div>
					</div>
					<div class="duty-info">
						<div class="info-item">
							<Calendar size={16} />
							<span class="label">Дата:</span>
							<span class="value">{duty.date}</span>
						</div>
						<div class="info-item">
							<Clock size={16} />
							<span class="label">Время:</span>
							<span class="value">{duty.startTime} - {duty.endTime}</span>
						</div>
						<div class="info-item">
							<MapPin size={16} />
							<span class="label">Место:</span>
							<span class="value">{duty.schedule.location}</span>
						</div>
						<div class="info-item">
							<Users size={16} />
							<span class="label">Ответственный:</span>
							<span class="value">{duty.schedule.employee.fullName}</span>
						</div>
						{#if duty.schedule.description}
							<div class="description">
								<span class="label">Описание:</span>
								<p>{duty.schedule.description}</p>
							</div>
						{/if}
						{#if editingDuty === duty.id}
							<div class="edit-section">
								<label for="editedNotes">Заметки:</label>
								<textarea id="editedNotes"
									bind:value={editedNotes}
									placeholder="Добавьте заметки о дежурстве"
									rows={3}
								></textarea>

								<label for="editedReport">Отчёт:</label>
								<textarea id="editedReport"
									bind:value={editedReport}
									placeholder="Добавьте отчёт о выполненной работе"
									rows={4}
								></textarea>

								<div class="edit-actions">
									<button class="btn-save" on:click={() => saveDuty(duty.id)}>
										<Save size={16} />
										<span>Сохранить</span>
									</button>
									<button class="btn-cancel" on:click={cancelEdit}>
										<X size={16} />
										<span>Отмена</span>
									</button>
								</div>
							</div>
						{:else}
							{#if duty.notes}
								<div class="notes">
									<span class="label">Заметки:</span>
									<p>{duty.notes}</p>
								</div>
							{/if}
							{#if duty.report}
								<div class="report">
									<span class="label">Отчёт:</span>
									<p>{duty.report}</p>
								</div>
							{/if}

							<button class="btn-edit" on:click={() => startEdit(duty)}>
								<Edit size={16} />
								<span>Редактировать</span>
							</button>
						{/if}
					</div>
				</div>
			{/each}
		</div>
	{/if}
</div>

<style>
	.duties-cabinet {
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

	.loader, .error, .empty {
		text-align: center;
		margin: 2rem 0;
		font-size: 1rem;
		color: var(--text-secondary);
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 0.5rem;
	}

	.error {
		color: var(--error);
	}

	.empty {
		color: var(--text-secondary);
	}

	.empty h3 {
		margin: 1rem 0 0.5rem 0;
		color: var(--text-primary);
	}

	.empty p {
		margin: 0;
		font-size: 0.9rem;
	}

	.duties-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
		gap: 1.5rem;
	}

	.duty-card {
		background: var(--bg-primary);
		border: 1px solid var(--border);
		border-radius: var(--radius);
		padding: 1.5rem;
		transition: var(--transition);
	}

	.duty-card:hover {
		transform: translateY(-5px);
		box-shadow: var(--shadow);
	}

	.duty-header {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		margin-bottom: 1rem;
	}

	.duty-header h3 {
		margin: 0;
		flex: 1;
		color: var(--primary);
	}

	.status {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		font-size: 0.9rem;
		font-weight: 500;
	}

	.duty-info {
		display: flex;
		flex-direction: column;
		gap: 0.75rem;
	}

	.info-item {
		display: flex;
		align-items: center;
		gap: 0.5rem;
	}

	.label {
		font-size: 0.9rem;
		color: var(--text-secondary);
		min-width: 80px;
	}

	.value {
		font-weight: 500;
		color: var(--text-primary);
		flex: 1;
	}

	.description, .notes {
		margin-top: 0.5rem;
		padding-top: 0.5rem;
		border-top: 1px solid var(--border);
	}

	.description .label, .notes .label {
		display: block;
		margin-bottom: 0.25rem;
	}

	.description p, .notes p {
		margin: 0;
		font-size: 0.9rem;
		color: var(--text-secondary);
		line-height: 1.4;
	}

	.notes {
		background: rgba(79, 70, 229, 0.05);
		padding: 0.75rem;
		border-radius: var(--radius);
		border-left: 3px solid var(--primary);
	}

	.report {
		background: rgba(79, 70, 229, 0.05);
		padding: 0.75rem;
		border-radius: var(--radius);
		border-left: 3px solid var(--secondary);
	}

	@media (max-width: 768px) {
		.header {
			flex-direction: column;
			gap: 1rem;
			align-items: stretch;
		}

		.duties-grid {
			grid-template-columns: 1fr;
		}
	}

    .edit-section {
        margin-top: 1rem;
        padding-top: 1rem;
        border-top: 2px solid var(--border);
    }

    .edit-section label {
        display: block;
        margin-bottom: 0.5rem;
        font-weight: 500;
        color: var(--text-primary);
    }

    .edit-section textarea {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid var(--border);
        border-radius: var(--radius);
        margin-bottom: 1rem;
        font-family: inherit;
        resize: vertical;
    }

    .status select {
        width: 100%;
        padding: 0.3rem;
        border: 1px solid var(--border);
        border-radius: var(--radius);
        background: var(--bg-primary);
        color: var(--text-primary);
        font-size: 0.9rem;
        transition: var(--transition);
        box-sizing: border-box;
        font-family: inherit;
    }

    .status select {
        border-color: var(--primary);
        box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
    }

    .edit-actions {
        display: flex;
        gap: 0.75rem;
        justify-content: flex-end;
    }

    .btn-edit, .btn-save, .btn-cancel {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 0.75rem;
        border: none;
        border-radius: var(--radius);
        font-size: 0.8rem;
        font-weight: 500;
        cursor: pointer;
        transition: var(--transition);
    }

    .btn-edit {
        background: rgba(79, 70, 229, 0.1);
        color: var(--primary);
        border: 1px solid rgba(79, 70, 229, 0.2);
    }

    .btn-edit:hover {
        background: rgba(79, 70, 229, 0.2);
    }

    .btn-save {
        background: var(--secondary);
        color: white;
    }

    .btn-save:hover {
        background: var(--secondary-dark);
    }

    .btn-cancel {
        background: var(--error-light);
        color: var(--error);
    }

    .btn-cancel:hover {
        background: var(--error);
        color: white;
    }
</style> 