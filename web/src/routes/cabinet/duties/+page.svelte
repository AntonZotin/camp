<script lang="ts">
	import { themeStore } from '$lib/themeStore';
	import { userStore } from '$lib/userStore';
	import { onMount } from 'svelte';
	import { ClipboardList, CalendarCheck, Loader, AlertCircle } from 'lucide-svelte';
	import { get } from 'svelte/store';
	import { PUBLIC_API_URL } from '$env/static/public';

	let theme: 'light' | 'dark' = 'light';
	const unsubTheme = themeStore.subscribe((t) => (theme = t));
	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	let duties = [];
	let loading = true;
	let error = '';

	onMount(async () => {
		if (!user) return;
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/duty-logs/employee/${user.userId}`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки дежурств');
			duties = await res.json();
		} catch (e) {
			error = e.message || 'Ошибка';
		} finally {
			loading = false;
		}
		return () => { unsubTheme(); unsubUser(); };
	});
</script>

<div class="duties-page" data-theme={theme}>
	<h1><ClipboardList size={28}/> Журнал дежурств</h1>
	{#if loading}
		<div class="loader spin"><Loader size={24}/> Загрузка...</div>
	{:else if error}
		<div class="error"><AlertCircle size={20}/> {error}</div>
	{:else if duties.length === 0}
		<div class="empty">У вас пока нет дежурств</div>
	{:else}
		<div class="duties-list">
			{#each duties as d}
				<div class="duty-card">
					<div class="top">
						<CalendarCheck size={22}/>
						<span>{d.date}</span>
					</div>
					<div class="info">
						<p>Смена: <b>{d.session?.name}</b></p>
						<p>Отчёт: <b>{d.report}</b></p>
					</div>
				</div>
			{/each}
		</div>
	{/if}
</div>

<style>
.duties-page {
	padding: 2rem 1rem;
	max-width: 700px;
	margin: 0 auto;
	color: var(--color-text, #222);
}
.duties-page[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-card: #23272f;
}
.duties-page[data-theme="light"] {
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
.duties-list {
	display: flex;
	flex-direction: column;
	gap: 1.2rem;
}
.duty-card {
	background: var(--color-card);
	border-radius: 16px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	padding: 1.2rem 1.5rem;
	display: flex;
	flex-direction: column;
	gap: 0.7rem;
	animation: fadeInUp 0.9s;
}
.duty-card .top {
	display: flex;
	align-items: center;
	gap: 0.7rem;
	font-weight: 600;
	color: var(--color-primary, #2d8cff);
	margin-bottom: 0.3rem;
}
.duty-card .info p {
	margin: 0.1rem 0;
	font-size: 1.05rem;
}
.spin { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(40px); } to { opacity: 1; transform: none; } }
</style>