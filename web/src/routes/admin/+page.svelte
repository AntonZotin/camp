<script lang="ts">
	import { fade, fly } from 'svelte/transition';
	import { userStore } from '$lib/stores/userStore';
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { get } from 'svelte/store';
	import { Shield, Users, HeartHandshake, Calendar, Activity, Settings, Menu, Bell, CreditCard, FileText, UserCheck, Download } from 'lucide-svelte';
	import MenuAdmin from '$lib/admin/MenuAdmin.svelte';
	import EmployeeAdmin from '$lib/admin/EmployeeAdmin.svelte';
	import MedicalCardAdmin from '$lib/admin/MedicalCardAdmin.svelte';
	import MedicalVisitAdmin from '$lib/admin/MedicalVisitAdmin.svelte';
	import NotificationAdmin from '$lib/admin/NotificationAdmin.svelte';
	import PaymentAdmin from '$lib/admin/PaymentAdmin.svelte';
	import ActivityLogAdmin from '$lib/admin/ActivityLogAdmin.svelte';
	import AdminExport from '$lib/admin/AdminExport.svelte';

	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	let tab: 'users' | 'sessions' | 'menu' | 'medical-cards' | 'medical-visits' | 'notifications' | 'payments' | 'employees' | 'activity-logs' | 'export' = 'users';

	onMount(() => {
		if (!user || user.role !== 'ADMIN') goto('/login');
		return () => { unsubUser(); };
	});
</script>

<div class="stars-bg"></div>

<section class="admin-section" transition:fade>
	<div class="container">
		<div class="admin-container" in:fly={{ y: 50 }}>
			<div class="admin-hero">
				<div class="logo">
					<span class="gradient-text">Sunny Camp</span>
				</div>
				<h1>Админ-панель</h1>
				<p>Управление системой лагеря, пользователями и контентом</p>
				<div class="admin-image">
					<img src="/src/images/camp-hero.png" alt="Администрирование лагеря" />
				</div>
			</div>

			<div class="admin-content">
				{#if user}
					<div class="admin-header" in:fly={{ y: 30, delay: 200 }}>
						<h2>
							<Shield size={24} />
							<span>Панель администратора</span>
						</h2>
						<p>Добро пожаловать в систему управления лагерем</p>
					</div>

					<div class="tabs" in:fly={{ y: 30, delay: 400 }}>
						<button class:active={tab==='users'} on:click={() => tab='users'}>
							<Users size={18} />
							<span>Пользователи</span>
						</button>
						<button class:active={tab==='sessions'} on:click={() => tab='sessions'}>
							<Calendar size={18} />
							<span>Смены</span>
						</button>
						<button class:active={tab==='menu'} on:click={() => tab='menu'}>
							<Menu size={18} />
							<span>Меню</span>
						</button>
						<button class:active={tab==='medical-cards'} on:click={() => tab='medical-cards'}>
							<Shield size={18} />
							<span>Медкарты</span>
						</button>
						<button class:active={tab==='medical-visits'} on:click={() => tab='medical-visits'}>
							<Activity size={18} />
							<span>Медосмотры</span>
						</button>
						<button class:active={tab==='notifications'} on:click={() => tab='notifications'}>
							<Bell size={18} />
							<span>Уведомления</span>
						</button>
						<button class:active={tab==='payments'} on:click={() => tab='payments'}>
							<CreditCard size={18} />
							<span>Оплаты</span>
						</button>
						<button class:active={tab==='employees'} on:click={() => tab='employees'}>
							<Users size={18} />
							<span>Сотрудники</span>
						</button>
						<button class:active={tab==='activity-logs'} on:click={() => tab='activity-logs'}>
							<Activity size={18} />
							<span>Журнал активности</span>
						</button>
						<button class:active={tab==='export'} on:click={() => tab='export'}>
							<Download size={18} />
							<span>Экспорт</span>
						</button>
					</div>

					<div class="tab-content" in:fly={{ y: 30, delay: 600 }}>
						{#if tab === 'users'}
							<div class="coming-soon">
								<Users size={48} />
								<h3>Управление пользователями</h3>
								<p>Функция находится в разработке</p>
							</div>
						{:else if tab === 'sessions'}
							<div class="coming-soon">
								<Calendar size={48} />
								<h3>Управление сменами</h3>
								<p>Функция находится в разработке</p>
							</div>
						{:else if tab === 'menu'}
							<MenuAdmin {user} />
						{:else if tab === 'employees'}
							<EmployeeAdmin {user} />
						{:else if tab === 'medical-cards'}
							<MedicalCardAdmin {user} />
						{:else if tab === 'medical-visits'}
							<MedicalVisitAdmin {user} />
						{:else if tab === 'notifications'}
							<NotificationAdmin {user} />
						{:else if tab === 'payments'}
							<PaymentAdmin {user} />
						{:else if tab === 'activity-logs'}
							<ActivityLogAdmin {user} />
						{:else if tab === 'export'}
							<AdminExport {user} />
						{/if}
					</div>
				{/if}
			</div>
		</div>
	</div>
</section>

<style>
	.stars-bg {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background: url("/src/images/star.png");
		z-index: -1;
		opacity: 0.3;
	}

	.admin-section {
		min-height: 100vh;
		display: flex;
		padding: 2rem 0;
	}

	.container {
		width: 100%;
		max-width: 1200px;
		margin: 0 auto;
		padding: 0 1rem;
	}

	.admin-container {
		display: grid;
		grid-template-columns: 300px 1fr;
		background: var(--bg-primary);
		border-radius: var(--radius);
		overflow: hidden;
		box-shadow: var(--shadow);
	}

	.admin-hero {
		background: linear-gradient(135deg, var(--primary), var(--primary-dark));
		color: white;
		padding: 2rem;
		display: flex;
		flex-direction: column;
	}

	.logo {
		font-size: 1.5rem;
		font-weight: 700;
		margin-bottom: 1.5rem;
	}

	.admin-hero h1 {
		font-size: 2rem;
		margin-bottom: 0.5rem;
		line-height: 1.2;
	}

	.admin-hero p {
		opacity: 0.9;
		margin-bottom: 1.5rem;
		font-size: 0.9rem;
	}

	.admin-image {
		margin-top: auto;
		text-align: center;
	}

	.admin-image img {
		max-width: 100%;
		height: auto;
		border-radius: var(--radius);
		opacity: 0.8;
	}

	.admin-content {
		padding: 2rem;
		display: flex;
		flex-direction: column;
	}

	.admin-header {
		margin-bottom: 2rem;
	}

	.admin-header h2 {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		font-size: 1.5rem;
		color: var(--primary);
		margin: 0 0 0.5rem 0;
	}

	.admin-header p {
		margin: 0;
		color: var(--text-secondary);
		font-size: 0.9rem;
	}

	.tabs {
		display: flex;
		flex-wrap: wrap;
		gap: 0.5rem;
		margin-bottom: 2rem;
		padding-bottom: 1rem;
		border-bottom: 1px solid var(--border);
	}

	.tabs button {
		background: var(--bg-secondary);
		border: 1px solid var(--border);
		border-radius: var(--radius);
		padding: 0.75rem 1rem;
		font-size: 0.9rem;
		font-weight: 500;
		color: var(--text-secondary);
		cursor: pointer;
		transition: var(--transition);
		display: flex;
		align-items: center;
		gap: 0.5rem;
	}

	.tabs button:hover {
		background: var(--bg-hover);
		color: var(--text-primary);
	}

	.tabs button.active {
		background: var(--primary);
		color: white;
		border-color: var(--primary);
	}

	.tab-content {
		flex: 1;
	}

	.coming-soon {
		text-align: center;
		margin: 2rem 0;
		color: var(--text-secondary);
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 1rem;
	}

	.coming-soon h3 {
		margin: 0;
		color: var(--text-primary);
	}

	.coming-soon p {
		margin: 0;
		font-size: 0.9rem;
	}

	@media (max-width: 1024px) {
		.admin-container {
			grid-template-columns: 1fr;
		}

		.admin-hero {
			display: none;
		}

		.admin-content {
			padding: 1.5rem;
		}

		.tabs {
			justify-content: center;
		}
	}

	@media (max-width: 768px) {
		.admin-section {
			padding: 1rem 0;
		}

		.admin-hero h1 {
			font-size: 1.5rem;
		}

		.tabs {
			flex-direction: column;
		}

		.tabs button {
			justify-content: center;
		}
	}
</style>