<script lang="ts">
	import { themeStore } from '$lib/themeStore';
	import { userStore } from '$lib/userStore';
	import { onMount } from 'svelte';
	import { Ticket, CalendarCheck, Loader, AlertCircle } from 'lucide-svelte';
	import { get } from 'svelte/store';

	let theme: 'light' | 'dark' = 'light';
	const unsubTheme = themeStore.subscribe((t) => (theme = t));
	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	let vouchers = [];
	let loading = true;
	let error = '';

	onMount(async () => {
		if (!user) return;
		try {
			const res = await fetch(`/api/vouchers/parent/${user.userId}`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки путёвок');
			vouchers = await res.json();
		} catch (e) {
			error = e.message || 'Ошибка';
		} finally {
			loading = false;
		}
		return () => { unsubTheme(); unsubUser(); };
	});
</script>

<div class="vouchers-page" data-theme={theme}>
	<h1><Ticket size={28}/> Мои путёвки</h1>
	{#if loading}
		<div class="loader"><Loader size={24} class="spin"/> Загрузка...</div>
	{:else if error}
		<div class="error"><AlertCircle size={20}/> {error}</div>
	{:else if vouchers.length === 0}
		<div class="empty">У вас пока нет путёвок</div>
	{:else}
		<div class="vouchers-list">
			{#each vouchers as v}
				<div class="voucher-card">
					<div class="top">
						<Ticket size={22}/>
						<span>Путёвка #{v.id}</span>
					</div>
					<div class="info">
						<p>Ребёнок: <b>{v.child?.name}</b></p>
						<p>Смена: <b>{v.session?.name}</b></p>
						<p>Статус: <b>{v.status}</b></p>
						<p>Дата бронирования: <b>{v.bookingDate}</b></p>
					</div>
				</div>
			{/each}
		</div>
	{/if}
</div>

<style>
.vouchers-page {
	padding: 2rem 1rem;
	max-width: 700px;
	margin: 0 auto;
	color: var(--color-text, #222);
}
.vouchers-page[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-card: #23272f;
}
.vouchers-page[data-theme="light"] {
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
.vouchers-list {
	display: flex;
	flex-direction: column;
	gap: 1.2rem;
}
.voucher-card {
	background: var(--color-card);
	border-radius: 16px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	padding: 1.2rem 1.5rem;
	display: flex;
	flex-direction: column;
	gap: 0.7rem;
	animation: fadeInUp 0.9s;
}
.voucher-card .top {
	display: flex;
	align-items: center;
	gap: 0.7rem;
	font-weight: 600;
	color: var(--color-primary, #2d8cff);
	margin-bottom: 0.3rem;
}
.voucher-card .info p {
	margin: 0.1rem 0;
	font-size: 1.05rem;
}
.spin { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(40px); } to { opacity: 1; transform: none; } }
</style> 