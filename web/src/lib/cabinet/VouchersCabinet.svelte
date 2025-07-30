<script lang="ts">
	import { fade, fly } from 'svelte/transition';
	import { Ticket, CalendarCheck, Loader, AlertCircle, Plus, Eye, Trash2 } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';
	export let user: UserSession;

	let vouchers: any[] = [];
	let loading = true;
	let error = '';

	async function loadVouchers() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/vouchers/parent/${user.userId}`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok) throw new Error('Ошибка загрузки путёвок');
			vouchers = await res.json();
		} catch (e) {
			error = (e as Error).message || 'Ошибка';
		} finally {
			loading = false;
		}
	}

	async function deleteVoucher(id: number) {
		if (!confirm('Удалить путёвку?')) return;
		await fetch(`${PUBLIC_API_URL}/api/vouchers/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user.accessToken}` }
		});
		await loadVouchers();
	}

	import { onMount } from 'svelte';
	onMount(() => { loadVouchers(); });
</script>

<div class="vouchers-cabinet">
	<div class="header">
		<h2>
			<Ticket size={24} />
			<span>Мои путёвки</span>
		</h2>
		<a href="/cabinet/book-voucher" class="add-btn">
			<Plus size={18} />
			<span>Забронировать путёвку</span>
		</a>
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
	{:else if vouchers.length === 0}
		<div class="empty">
			<Ticket size={48} />
			<h3>У вас пока нет путёвок</h3>
			<p>Забронируйте путёвку для вашего ребенка</p>
		</div>
	{:else}
		<div class="vouchers-grid">
			{#each vouchers as voucher, i (voucher.id)}
				<div class="voucher-card" in:fly={{ y: 30, delay: i * 100 }}>
					<div class="voucher-header">
						<Ticket size={24} />
						<h3>Путёвка #{voucher.id}</h3>
						<div class="actions">
							<button class="icon-btn view" title="Просмотреть детали">
								<Eye size={16} />
							</button>
							<button class="icon-btn delete" title="Удалить" on:click={() => deleteVoucher(voucher.id)}>
								<Trash2 size={16} />
							</button>
						</div>
					</div>
					<div class="voucher-info">
						<div class="info-item">
							<span class="label">Ребёнок:</span>
							<span class="value">{voucher.child?.name || 'Не указан'}</span>
						</div>
						<div class="info-item">
							<span class="label">Смена:</span>
							<span class="value">{voucher.session?.name || 'Не указана'}</span>
						</div>
						<div class="info-item">
							<span class="label">Статус:</span>
							<span class="value status-{voucher.status?.toLowerCase()}">{voucher.status}</span>
						</div>
						<div class="info-item">
							<span class="label">Дата бронирования:</span>
							<span class="value">{voucher.bookingDate}</span>
						</div>
					</div>
				</div>
			{/each}
		</div>
	{/if}
</div>

<style>
	.vouchers-cabinet {
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

	.add-btn {
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
		text-decoration: none;
	}

	.add-btn:hover {
		background: var(--primary-dark);
		transform: translateY(-2px);
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

	.vouchers-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
		gap: 1.5rem;
	}

	.voucher-card {
		background: var(--bg-primary);
		border: 1px solid var(--border);
		border-radius: var(--radius);
		padding: 1.5rem;
		transition: var(--transition);
	}

	.voucher-card:hover {
		transform: translateY(-5px);
		box-shadow: var(--shadow);
	}

	.voucher-header {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		margin-bottom: 1rem;
	}

	.voucher-header h3 {
		margin: 0;
		flex: 1;
		color: var(--primary);
	}

	.actions {
		display: flex;
		gap: 0.5rem;
	}

	.icon-btn {
		background: none;
		border: none;
		cursor: pointer;
		padding: 0.25rem;
		border-radius: var(--radius);
		transition: var(--transition);
		display: flex;
		align-items: center;
	}

	.icon-btn.view {
		color: var(--primary);
	}

	.icon-btn.delete {
		color: var(--error);
	}

	.icon-btn:hover {
		background: var(--bg-hover);
	}

	.voucher-info {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}

	.info-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.label {
		font-size: 0.9rem;
		color: var(--text-secondary);
	}

	.value {
		font-weight: 500;
		color: var(--text-primary);
	}

	.status-booked {
		color: var(--primary);
	}

	.status-confirmed {
		color: var(--secondary);
	}

	.status-cancelled {
		color: var(--error);
	}

	.status-completed {
		color: var(--text-secondary);
	}

	@media (max-width: 768px) {
		.header {
			flex-direction: column;
			gap: 1rem;
			align-items: stretch;
		}

		.vouchers-grid {
			grid-template-columns: 1fr;
		}
	}
</style> 