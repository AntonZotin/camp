<script lang="ts">
	import { themeStore } from '$lib/themeStore';
	import { userStore } from '$lib/userStore';
	import { onMount } from 'svelte';
	import { CalendarDays, Clock, Loader, AlertCircle } from 'lucide-svelte';
	import { get } from 'svelte/store';

	let theme: 'light' | 'dark' = 'light';
	const unsubTheme = themeStore.subscribe((t) => (theme = t));
	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	let schedule = [];
	let loading = true;
	let error = '';

	onMount(async () => {
		if (!user) return;
		try {
			const res = await fetch(`/api/schedules/employee/${user.userId}`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки расписания');
			schedule = await res.json();
		} catch (e) {
			error = e.message || 'Ошибка';
		} finally {
			loading = false;
		}
		return () => { unsubTheme(); unsubUser(); };
	});
</script>

<div class="schedule-page" data-theme={theme}>
	<h1><CalendarDays size={28}/> Моё расписание</h1>
	{#if loading}
		<div class="loader"><Loader size={24} class="spin"/> Загрузка...</div>
	{:else if error}
		<div class="error"><AlertCircle size={20}/> {error}</div>
	{:else if schedule.length === 0}
		<div class="empty">У вас пока нет расписания</div>
	{:else}
		<div class="schedule-list">
			{#each schedule as s}
				<div class="schedule-card">
					<div class="top">
						<CalendarDays size={22}/>
						<span>{s.date} {s.time}</span>
					</div>
					<div class="info">
						<p>Смена: <b>{s.session?.name}</b></p>
						<p>Тип: <b>{s.eventType}</b></p>
						<p>Описание: <b>{s.description}</b></p>
					</div>
				</div>
			{/each}
		</div>
	{/if}
</div>

<style>
.schedule-page {
	padding: 2rem 1rem;
	max-width: 700px;
	margin: 0 auto;
	color: var(--color-text, #222);
}
.schedule-page[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-card: #23272f;
}
.schedule-page[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-card: #fff;
}
h1 {
	display: flex;
	align-items: center;
	gap: 0.7rem;
	font-size: 1.5rem;
	color: var(--color-primary, #2d8cff);
	margin-bottom: 2rem;
}
.loader, .error, .empty {
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
.schedule-list {
	display: flex;
	flex-direction: column;
	gap: 1.2rem;
}
.schedule-card {
	background: var(--color-card);
	border-radius: 16px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	padding: 1.2rem 1.5rem;
	display: flex;
	flex-direction: column;
	gap: 0.7rem;
	animation: fadeInUp 0.9s;
}
.schedule-card .top {
	display: flex;
	align-items: center;
	gap: 0.7rem;
	font-weight: 600;
	color: var(--color-primary, #2d8cff);
	margin-bottom: 0.3rem;
}
.schedule-card .info p {
	margin: 0.1rem 0;
	font-size: 1.05rem;
}
.spin { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(40px); } to { opacity: 1; transform: none; } }
</style>