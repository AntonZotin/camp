<script lang="ts">
	import { Loader, Download, AlertCircle, FileDown } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';

	export let user: UserSession;

	let loading = false;
	let error = '';

	async function exportCsv(type: 'users' | 'vouchers') {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/admin/export/${type}/csv`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok) {
				error = 'Ошибка экспорта';
			} else {
				const csv = await res.text();
				const blob = new Blob([csv], {type: 'text/csv'});
				const url = URL.createObjectURL(blob);
				const a = document.createElement('a');
				a.href = url;
				a.download = `${type}.csv`;
				document.body.appendChild(a);
				a.click();
				document.body.removeChild(a);
				URL.revokeObjectURL(url);
			}
		} finally {
			loading = false;
		}
	}
</script>

<div class="export-admin">
	<div class="header">
		<h2>
			<FileDown size={24} />
			<span>Экспорт данных</span>
		</h2>
	</div>

	<div class="export-content">
		<div class="export-actions">
			<button class="export-btn" on:click={() => exportCsv('users')} disabled={loading}>
				{#if loading}
					<Loader size={18} />
				{:else}
					<Download size={18} />
				{/if}
				<span>Экспорт пользователей</span>
			</button>
			<button class="export-btn" on:click={() => exportCsv('vouchers')} disabled={loading}>
				{#if loading}
					<Loader size={18} />
				{:else}
					<Download size={18} />
				{/if}
				<span>Экспорт путёвок</span>
			</button>
		</div>
		
		{#if error}
			<div class="error">
				<AlertCircle size={18} />
				<span>{error}</span>
			</div>
		{/if}
	</div>
</div>

<style>
	.export-admin {
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

	.export-content {
		max-width: 600px;
		margin: 0 auto;
	}

	.export-actions {
		display: flex;
		gap: 1.5rem;
		margin-bottom: 1.5rem;
		flex-wrap: wrap;
	}

	.export-btn {
		background: var(--primary);
		color: white;
		border: none;
		border-radius: var(--radius);
		padding: 0.75rem 1.5rem;
		font-size: 0.9rem;
		font-weight: 500;
		cursor: pointer;
		transition: var(--transition);
		display: flex;
		align-items: center;
		gap: 0.5rem;
		flex: 1;
		min-width: 200px;
		justify-content: center;
	}

	.export-btn:disabled {
		background: var(--text-secondary);
		cursor: not-allowed;
		opacity: 0.6;
	}

	.export-btn:hover:not(:disabled) {
		background: var(--primary-dark);
		transform: translateY(-2px);
	}

	.error {
		color: var(--error);
		display: flex;
		align-items: center;
		gap: 0.5rem;
		margin-top: 1rem;
		font-size: 1rem;
		justify-content: center;
		padding: 1rem;
		background: var(--bg-hover);
		border-radius: var(--radius);
		border: 1px solid var(--error);
	}

	@media (max-width: 768px) {
		.header {
			flex-direction: column;
			gap: 1rem;
			align-items: stretch;
		}

		.export-actions {
			flex-direction: column;
		}

		.export-btn {
			min-width: auto;
		}
	}
</style> 