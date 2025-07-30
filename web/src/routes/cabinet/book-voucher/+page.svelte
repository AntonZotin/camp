<script lang="ts">
	import { fade, fly } from 'svelte/transition';
	import { Ticket, Calendar, User, Loader, AlertCircle, ArrowLeft, CheckCircle } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';
	import { goto } from '$app/navigation';
	import { userStore } from '$lib/stores/userStore';
	import { get } from 'svelte/store';
	import { onMount } from 'svelte';

	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	let loading = true;
	let submitting = false;
	let error = '';
	let success = '';

	let children: any[] = [];
	let sessions: any[] = [];
	let selectedChild: string = '';
	let selectedSession: string = '';

	onMount(() => {
		if (!user) goto('/login');
		loadData();
		return () => { unsubUser(); };
	});

	async function loadData() {
		loading = true;
		error = '';
		try {
			// Загружаем детей пользователя
			const childrenRes = await fetch(`${PUBLIC_API_URL}/api/children/parent/${user.userId}`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (childrenRes.ok) {
				children = await childrenRes.json();
			}

			// Загружаем доступные смены
			const sessionsRes = await fetch(`${PUBLIC_API_URL}/api/sessions`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (sessionsRes.ok) {
				sessions = await sessionsRes.json();
			}
		} catch (e) {
			error = (e as Error).message || 'Ошибка загрузки данных';
		} finally {
			loading = false;
		}
	}

	async function bookVoucher() {
		if (!selectedChild || !selectedSession) {
			error = 'Пожалуйста, выберите ребёнка и смену';
			return;
		}

		submitting = true;
		error = '';
		success = '';

		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/vouchers`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
					Authorization: `Bearer ${user.accessToken}`
				},
				body: JSON.stringify({
					childId: parseInt(selectedChild),
					sessionId: parseInt(selectedSession)
				})
			});

			if (!res.ok) {
				const data = await res.json();
				throw new Error(data.message || 'Ошибка бронирования путёвки');
			}

			success = 'Путёвка успешно забронирована!';
			setTimeout(() => {
				goto('/cabinet');
			}, 2000);
		} catch (e) {
			error = (e as Error).message || 'Ошибка бронирования путёвки';
		} finally {
			submitting = false;
		}
	}
</script>

<div class="book-voucher-page">
	<div class="header">
		<button class="back-btn" on:click={() => goto('/cabinet')}>
			<ArrowLeft size={20} />
			<span>Назад</span>
		</button>
		<h1>
			<Ticket size={28} />
			<span>Забронировать путёвку</span>
		</h1>
	</div>

	{#if loading}
		<div class="loader">
			<Loader size={32} />
			<span>Загрузка данных...</span>
		</div>
	{:else if error}
		<div class="error-message" transition:fade>
			<AlertCircle size={20} />
			<span>{error}</span>
		</div>
	{:else if success}
		<div class="success-message" transition:fade>
			<CheckCircle size={20} />
			<span>{success}</span>
		</div>
	{:else}
		<div class="booking-form" in:fly={{ y: 30, delay: 100 }}>
			<div class="form-section">
				<h3>
					<User size={20} />
					<span>Выберите ребёнка</span>
				</h3>
				{#if children.length === 0}
					<div class="empty-state">
						<User size={48} />
						<h4>У вас нет детей в системе</h4>
						<p>Сначала добавьте информацию о вашем ребёнке в настройках профиля</p>
					</div>
				{:else}
					<div class="children-grid">
						{#each children as child}
							<label class="child-option">
								<input type="radio" name="child" value={child.id} bind:group={selectedChild} />
								<div class="child-card">
									<User size={24} />
									<div class="child-info">
										<h4>{child.name}</h4>
										<p>Дата рождения: {child.birthDate}</p>
									</div>
								</div>
							</label>
						{/each}
					</div>
				{/if}
			</div>

			<div class="form-section">
				<h3>
					<Calendar size={20} />
					<span>Выберите смену</span>
				</h3>
				{#if sessions.length === 0}
					<div class="empty-state">
						<Calendar size={48} />
						<h4>Нет доступных смен</h4>
						<p>В данный момент нет активных смен для бронирования</p>
					</div>
				{:else}
					<div class="sessions-grid">
						{#each sessions as session}
							<label class="session-option">
								<input type="radio" name="session" value={session.id} bind:group={selectedSession} />
								<div class="session-card">
									<Calendar size={24} />
									<div class="session-info">
										<h4>{session.name}</h4>
										<p>{session.description}</p>
										<div class="session-dates">
											<span>С {session.startDate} по {session.endDate}</span>
										</div>
									</div>
								</div>
							</label>
						{/each}
					</div>
				{/if}
			</div>

			{#if selectedChild && selectedSession}
				<div class="booking-summary" in:fly={{ y: 30, delay: 200 }}>
					<h3>Подтверждение бронирования</h3>
					<div class="summary-item">
						<span class="label">Ребёнок:</span>
						<span class="value">{children.find(c => c.id == selectedChild)?.name}</span>
					</div>
					<div class="summary-item">
						<span class="label">Смена:</span>
						<span class="value">{sessions.find(s => s.id == selectedSession)?.name}</span>
					</div>
					<div class="summary-item">
						<span class="label">Период:</span>
						<span class="value">
							{sessions.find(s => s.id == selectedSession)?.startDate} - {sessions.find(s => s.id == selectedSession)?.endDate}
						</span>
					</div>
				</div>
			{/if}

			<button 
				class="book-btn" 
				disabled={!selectedChild || !selectedSession || submitting}
				on:click={bookVoucher}
			>
				{#if submitting}
					<Loader size={18} />
					<span>Бронирование...</span>
				{:else}
					<Ticket size={18} />
					<span>Забронировать путёвку</span>
				{/if}
			</button>
		</div>
	{/if}
</div>

<style>
	.book-voucher-page {
		padding: 1rem;
		max-width: 800px;
		margin: 0 auto;
	}

	.header {
		display: flex;
		align-items: center;
		gap: 1rem;
		margin-bottom: 2rem;
	}

	.back-btn {
		background: none;
		border: none;
		cursor: pointer;
		color: var(--text-secondary);
		display: flex;
		align-items: center;
		gap: 0.5rem;
		padding: 0.5rem;
		border-radius: var(--radius);
		transition: var(--transition);
	}

	.back-btn:hover {
		background: var(--bg-hover);
		color: var(--text-primary);
	}

	.header h1 {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		font-size: 1.8rem;
		color: var(--primary);
		margin: 0;
	}

	.loader {
		text-align: center;
		margin: 3rem 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 1rem;
		color: var(--text-secondary);
	}

	.error-message, .success-message {
		padding: 1rem;
		border-radius: var(--radius);
		margin-bottom: 1.5rem;
		display: flex;
		align-items: center;
		gap: 0.5rem;
		font-weight: 500;
	}

	.error-message {
		background: rgba(239, 68, 68, 0.1);
		color: var(--error);
		border: 1px solid var(--error);
	}

	.success-message {
		background: rgba(34, 197, 94, 0.1);
		color: var(--secondary);
		border: 1px solid var(--secondary);
	}

	.booking-form {
		display: flex;
		flex-direction: column;
		gap: 2rem;
	}

	.form-section {
		background: var(--bg-primary);
		border: 1px solid var(--border);
		border-radius: var(--radius);
		padding: 1.5rem;
	}

	.form-section h3 {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		margin: 0 0 1.5rem 0;
		color: var(--primary);
		font-size: 1.2rem;
	}

	.empty-state {
		text-align: center;
		padding: 2rem;
		color: var(--text-secondary);
	}

	.empty-state h4 {
		margin: 1rem 0 0.5rem 0;
		color: var(--text-primary);
	}

	.empty-state p {
		margin: 0;
		font-size: 0.9rem;
	}

	.children-grid, .sessions-grid {
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
		gap: 1rem;
	}

	.child-option, .session-option {
		cursor: pointer;
	}

	.child-option input, .session-option input {
		display: none;
	}

	.child-card, .session-card {
		background: var(--bg-hover);
		border: 2px solid transparent;
		border-radius: var(--radius);
		padding: 1rem;
		transition: var(--transition);
		display: flex;
		align-items: center;
		gap: 1rem;
	}

	.child-option input:checked + .child-card,
	.session-option input:checked + .session-card {
		border-color: var(--primary);
		background: rgba(79, 70, 229, 0.1);
	}

	.child-info, .session-info {
		flex: 1;
	}

	.child-info h4, .session-info h4 {
		margin: 0 0 0.25rem 0;
		color: var(--text-primary);
		font-size: 1rem;
	}

	.child-info p, .session-info p {
		margin: 0;
		font-size: 0.9rem;
		color: var(--text-secondary);
	}

	.session-dates {
		margin-top: 0.5rem;
		font-size: 0.8rem;
		color: var(--primary);
		font-weight: 500;
	}

	.booking-summary {
		background: var(--bg-hover);
		border: 1px solid var(--border);
		border-radius: var(--radius);
		padding: 1.5rem;
		margin-top: 1rem;
	}

	.booking-summary h3 {
		margin: 0 0 1rem 0;
		color: var(--primary);
	}

	.summary-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 0.5rem;
	}

	.summary-item:last-child {
		margin-bottom: 0;
	}

	.summary-item .label {
		font-weight: 500;
		color: var(--text-secondary);
	}

	.summary-item .value {
		color: var(--text-primary);
		font-weight: 500;
	}

	.book-btn {
		background: var(--primary);
		color: white;
		border: none;
		border-radius: var(--radius);
		padding: 1rem 2rem;
		font-size: 1rem;
		font-weight: 500;
		cursor: pointer;
		transition: var(--transition);
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 0.5rem;
		margin-top: 1rem;
	}

	.book-btn:hover:not(:disabled) {
		background: var(--primary-dark);
		transform: translateY(-2px);
	}

	.book-btn:disabled {
		opacity: 0.7;
		cursor: not-allowed;
		transform: none;
	}

	@media (max-width: 768px) {
		.header {
			flex-direction: column;
			align-items: flex-start;
			gap: 1rem;
		}

		.children-grid, .sessions-grid {
			grid-template-columns: 1fr;
		}

		.child-card, .session-card {
			flex-direction: column;
			text-align: center;
		}
	}
</style> 