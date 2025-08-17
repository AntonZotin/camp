<script lang="ts">
	import { onMount } from 'svelte';
	import type {DutyLog} from "$lib/models";
	import { fly } from 'svelte/transition';
	import {Activity, Clock, MapPin, Users, Loader, AlertCircle, CheckCircle, XCircle, Calendar} from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';

	export let user: UserSession;

	let duties: DutyLog[] = [];
	let loading = true;
	let error = '';

	async function loadDuties() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/duty-logs/employee/${user.userId}`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok)
				error = 'Ошибка загрузки дежурств';
			else
				duties = await res.json();
		} finally {
			loading = false;
		}
	}

	function getStatusIcon(status: string) {
		if (status === 'COMPLETED') return CheckCircle;
		if (status === 'CANCELLED') return XCircle;
		return Clock;
	}

	function getStatusColor(status: string) {
		if (status === 'COMPLETED') return 'var(--secondary)';
		if (status === 'CANCELLED') return 'var(--error)';
		return 'var(--primary)';
	}

	function getStatusText(status: string) {
		if (status === 'COMPLETED') return 'Завершено';
		if (status === 'CANCELLED') return 'Отменено';
		return 'В процессе';
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
							<span>{getStatusText(duty.status)}</span>
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
							<span class="value">{duty.location}</span>
						</div>
						<div class="info-item">
							<Users size={16} />
							<span class="label">Ответственный:</span>
							<span class="value">{duty.employee?.fullName}</span>
						</div>
						{#if duty.description}
							<div class="description">
								<span class="label">Описание:</span>
								<p>{duty.description}</p>
							</div>
						{/if}
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
</style> 