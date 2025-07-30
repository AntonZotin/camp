<script lang="ts">
	import { Loader, AlertCircle, Activity } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';
	export let user: UserSession;

	let logs = [];
	let loading = true;
	let error = '';

	async function loadLogs() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/activity-logs`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки журнала');
			logs = await res.json();
		} catch (e) {
			error = (e as Error).message || 'Ошибка';
		} finally {
			loading = false;
		}
	}

	import { onMount } from 'svelte';
	onMount(() => { loadLogs(); });
</script>

<div class="activity-log-admin">
	<div class="header">
		<h2>
			<Activity size={24} />
			<span>Журнал активности</span>
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
	{:else}
		<div class="log-table">
			<table>
				<thead>
					<tr>
						<th>ID</th>
						<th>Пользователь</th>
						<th>Действие</th>
						<th>Время</th>
						<th>Описание</th>
					</tr>
				</thead>
				<tbody>
					{#each logs as l}
						<tr>
							<td>{l.id}</td>
							<td>{l.user?.username}</td>
							<td>{l.action}</td>
							<td>{l.timestamp}</td>
							<td>{l.description}</td>
						</tr>
					{/each}
				</tbody>
			</table>
		</div>
	{/if}
</div>

<style>
	.activity-log-admin {
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

	.log-table {
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

	@media (max-width: 768px) {
		.header {
			flex-direction: column;
			gap: 1rem;
			align-items: stretch;
		}
	}
</style> 