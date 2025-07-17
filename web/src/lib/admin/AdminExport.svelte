<script lang="ts">
	import { Loader, Download, AlertCircle } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	export let user;
	export let theme: 'light' | 'dark' = 'light';

	let loading = false;
	let error = '';

	async function exportCsv(type: 'users' | 'vouchers') {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/admin/export/${type}/csv`, {
				headers: { Authorization: `Bearer ${user?.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка экспорта');
			const csv = await res.text();
			const blob = new Blob([csv], { type: 'text/csv' });
			const url = URL.createObjectURL(blob);
			const a = document.createElement('a');
			a.href = url;
			a.download = `${type}.csv`;
			document.body.appendChild(a);
			a.click();
			document.body.removeChild(a);
			URL.revokeObjectURL(url);
		} catch (e) {
			error = e.message || 'Ошибка';
		} finally {
			loading = false;
		}
	}
</script>

<div class="export-tab" data-theme={theme}>
	<h2><Download size={22}/> Экспорт данных</h2>
	<div class="export-actions">
		<button class="export-btn" on:click={() => exportCsv('users')} disabled={loading}>
			{#if loading}
				<div class="spin">
					<Loader size={18}/>
				</div>
			{:else}
				<Download size={18}/>} Экспорт пользователей
			{/if}
		</button>
		<button class="export-btn" on:click={() => exportCsv('vouchers')} disabled={loading}>
			{#if loading}
				<div class="spin">
					<Loader size={18}/>
				</div>
			{:else}
				<Download size={18}/>
			{/if} Экспорт путёвок
		</button>
	</div>
	{#if error}
		<div class="error"><AlertCircle size={18}/> {error}</div>
	{/if}
</div>

<style>
.export-tab {
	padding: 2rem 1.5rem;
	background: var(--color-card);
	border-radius: 18px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	max-width: 600px;
	margin: 2rem auto 0 auto;
}
.export-tab[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-card: #23272f;
}
.export-tab[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-card: #fff;
}
.export-tab h2 {
	display: flex;
	align-items: center;
	gap: 0.7rem;
	font-size: 1.3rem;
	color: var(--color-primary, #2d8cff);
	margin-bottom: 2rem;
}
.export-actions {
	display: flex;
	gap: 1.5rem;
	margin-bottom: 1.5rem;
}
.export-btn {
	background: var(--color-primary, #2d8cff);
	color: #fff;
	border: none;
	border-radius: 8px;
	padding: 0.7rem 1.5rem;
	font-size: 1rem;
	cursor: pointer;
	display: flex;
	align-items: center;
	gap: 0.6rem;
	transition: background 0.18s;
}
.export-btn:disabled {
	background: #b0c4de;
	cursor: not-allowed;
}
.export-btn:hover:not(:disabled) {
	background: var(--color-accent, #ffb347);
	color: #222;
}
.error {
	color: #e74c3c;
	display: flex;
	align-items: center;
	gap: 0.5rem;
	margin-top: 1rem;
	font-size: 1.05rem;
	justify-content: center;
}
.spin { animation: spin 1s linear infinite; }
@keyframes spin { 100% { transform: rotate(360deg); } }
</style> 