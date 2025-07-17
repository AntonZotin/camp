<script lang="ts">
	import { Loader, AlertCircle } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	export let user;
	export let theme: 'light' | 'dark' = 'light';

	let logs = [];
	let loading = true;
	let error = '';

	async function loadLogs() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/activity-logs`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки журнала');
			logs = await res.json();
		} catch (e) {
			error = e.message || 'Ошибка';
		} finally {
			loading = false;
		}
	}

	import { onMount } from 'svelte';
	onMount(() => { loadLogs(); });
</script>

<div class="activity-log-admin" data-theme={theme}>
	<h2>Журнал активности</h2>
	{#if loading}
		<div class="loader spin"><Loader size={24}/> Загрузка...</div>
	{:else if error}
		<div class="error"><AlertCircle size={20}/> {error}</div>
	{:else}
		<table class="log-table">
			<thead>
				<tr>
					<th>Пользователь</th>
					<th>Действие</th>
					<th>Время</th>
					<th>Описание</th>
				</tr>
			</thead>
			<tbody>
				{#each logs as l}
					<tr>
						<td>{l.user?.username}</td>
						<td>{l.action}</td>
						<td>{l.timestamp}</td>
						<td>{l.description}</td>
					</tr>
				{/each}
			</tbody>
		</table>
	{/if}
</div>

<style>
.activity-log-admin {
	padding: 2rem 1.5rem;
	background: var(--color-card);
	border-radius: 18px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	max-width: 1100px;
	margin: 2rem auto 0 auto;
}
.activity-log-admin[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-card: #23272f;
}
.activity-log-admin[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-card: #fff;
}
.activity-log-admin h2 {
	font-size: 1.4rem;
	color: var(--color-primary, #2d8cff);
	margin-bottom: 2rem;
}
.log-table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 1rem;
}
.log-table th, .log-table td {
	padding: 0.7rem 0.5rem;
	text-align: left;
	border-bottom: 1px solid #e0e0e0;
}
.log-table th {
	color: var(--color-primary, #2d8cff);
	font-size: 1.05rem;
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
.spin { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }
</style> 