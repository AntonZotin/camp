<script lang="ts">
	import { onMount } from 'svelte';
	import type {Schedule} from "$lib/models";
	import { fly } from 'svelte/transition';
	import {Calendar, Clock, MapPin, Users, Loader, AlertCircle, Activity} from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';
    import { Play } from 'lucide-svelte';
	import {toast} from "svelte-sonner";

	export let user: UserSession;

	let schedules: Schedule[] = [];
	let loading = true;
	let error = '';
    let startingSchedule: number | null = null;

	async function loadSchedules() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/schedules/employee/${user.userId}`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok)
				error = 'Ошибка загрузки расписания';
			else
				schedules = await res.json();
		} finally {
			loading = false;
		}
	}

    async function startSchedule(scheduleId: number) {
        startingSchedule = scheduleId;
        try {
            const res = await fetch(`${PUBLIC_API_URL}/api/duty-logs/start-from-schedule/${scheduleId}`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${user.accessToken}`,
                    'Content-Type': 'application/json'
                }
            });

            if (res.ok) {
                await loadSchedules();
    			toast.success('Дежурство успешно создано');
            } else {
    			toast.error('Ошибка при начале события');
            }
        } catch (err) {
            error = 'Ошибка при начале события';
        } finally {
            startingSchedule = null;
        }
    }

	onMount(() => { loadSchedules(); });
</script>

<div class="schedule-cabinet">
	<div class="header">
		<h2>
			<Calendar size={24} />
			<span>Моё расписание</span>
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
	{:else if schedules.length === 0}
		<div class="empty">
			<Calendar size={48} />
			<h3>У вас пока нет расписания</h3>
			<p>Ваше расписание будет отображаться здесь</p>
		</div>
	{:else}
		<div class="schedule-grid">
			{#each schedules as schedule, i (schedule.id)}
				<div class="schedule-card" in:fly={{ y: 30, delay: i * 100 }}>
					<div class="schedule-header">
						<Activity size={24} />
						<h3>{schedule.title}</h3>
						<button
							class="btn-start"
							on:click={() => startSchedule(schedule.id)}
							disabled={startingSchedule === schedule.id}
						>
							{#if startingSchedule === schedule.id}
								<Loader size={16} />
							{:else}
								<Play size={16} />
							{/if}
							<span>Начать</span>
						</button>
					</div>
					<div class="schedule-info">
						<div class="info-item">
							<Calendar size={16} />
							<span class="label">Дата:</span>
							<span class="value">{schedule.date}</span>
						</div>
						<div class="info-item">
							<Clock size={16} />
							<span class="label">Время:</span>
							<span class="value">{schedule.time}</span>
						</div>
						<div class="info-item">
							<MapPin size={16} />
							<span class="label">Место:</span>
							<span class="value">{schedule.location}</span>
						</div>
						<div class="info-item">
							<Users size={16} />
							<span class="label">Группа:</span>
							<span class="value">{schedule.team}</span>
						</div>
						{#if schedule.description}
							<div class="description">
								<span class="label">Описание:</span>
								<p>{schedule.description}</p>
							</div>
						{/if}
					</div>
				</div>
			{/each}
		</div>
	{/if}
</div>

<style>
	.schedule-cabinet {
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

	.schedule-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
		gap: 1.5rem;
	}

	.schedule-card {
		background: var(--bg-primary);
		border: 1px solid var(--border);
		border-radius: var(--radius);
		padding: 1.5rem;
		transition: var(--transition);
	}

	.schedule-card:hover {
		transform: translateY(-5px);
		box-shadow: var(--shadow);
	}

	.schedule-header {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		margin-bottom: 1rem;
	}

	.schedule-header h3 {
		margin: 0;
		flex: 1;
		color: var(--primary);
	}

	.schedule-info {
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
		min-width: 60px;
	}

	.value {
		font-weight: 500;
		color: var(--text-primary);
		flex: 1;
	}

	.description {
		margin-top: 0.5rem;
		padding-top: 0.5rem;
		border-top: 1px solid var(--border);
	}

	.description .label {
		display: block;
		margin-bottom: 0.25rem;
	}

	.description p {
		margin: 0;
		font-size: 0.9rem;
		color: var(--text-secondary);
		line-height: 1.4;
	}

	@media (max-width: 768px) {
		.header {
			flex-direction: column;
			gap: 1rem;
			align-items: stretch;
		}

		.schedule-grid {
			grid-template-columns: 1fr;
		}
	}

    .btn-start {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 0.75rem;
        background: var(--primary);
        color: white;
        border: none;
        border-radius: var(--radius);
        font-size: 0.8rem;
        font-weight: 500;
        cursor: pointer;
        transition: var(--transition);
    }

    .btn-start:hover:not(:disabled) {
        background: var(--primary-dark);
    }

    .btn-start:disabled {
        opacity: 0.7;
        cursor: not-allowed;
    }
</style> 