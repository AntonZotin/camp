<script lang="ts">
	import { fade, fly } from 'svelte/transition';
	import { userStore, logoutUser, type UserRole } from '$lib/stores/userStore';
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { User, LogOut, Shield, Users, HeartHandshake, Calendar, Activity, Settings } from 'lucide-svelte';
	import { get } from 'svelte/store';
	import ChildrenCabinet from '$lib/cabinet/ChildrenCabinet.svelte';
	import VouchersCabinet from '$lib/cabinet/VouchersCabinet.svelte';
	import ScheduleCabinet from '$lib/cabinet/ScheduleCabinet.svelte';
	import DutiesCabinet from '$lib/cabinet/DutiesCabinet.svelte';

	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	let tab: 'profile' | 'children' | 'vouchers' | 'duties' | 'schedule' | 'settings' = 'profile';

	onMount(() => {
		if (!user) goto('/login');
		return () => { unsubUser(); };
	});

	function handleLogout() {
		logoutUser();
		goto('/');
	}

	function getRoleLabel(role: UserRole) {
		if (role === 'ADMIN') return 'Администратор';
		if (role === 'PARENT') return 'Родитель';
		if (role === 'EMPLOYEE') return 'Сотрудник';
		return 'Пользователь';
	}
</script>

<div class="stars-bg"></div>

<section class="cabinet-section" transition:fade>
	<div class="container">
		<div class="cabinet-container" in:fly={{ y: 50 }}>
			<div class="cabinet-hero">
				<div class="logo">
					<span class="gradient-text">Sunny Camp</span>
				</div>
				<h1>Личный кабинет</h1>
				<p>Управляйте бронированием, следите за расписанием и получайте персональные предложения</p>
				<div class="cabinet-image">
					<img src="/src/images/camp-hero.png" alt="Дети в лагере" />
				</div>
			</div>

			<div class="cabinet-content">
				{#if user}
					<div class="profile-card" in:fly={{ y: 30, delay: 200 }}>
						<div class="avatar">
							<User size={40} />
						</div>
						<div class="info">
							<h2>Здравствуйте, {getRoleLabel(user.role)}!</h2>
							<p>ID: {user.userId}</p>
							<p>Ваша роль: <strong>{getRoleLabel(user.role)}</strong></p>
						</div>
					</div>

					<div class="tabs" in:fly={{ y: 30, delay: 400 }}>
						<button class:active={tab==='profile'} on:click={() => tab='profile'}>
							<User size={18} />
							<span>Профиль</span>
						</button>
						{#if user.role === 'ADMIN'}
							<button class:active={tab==='admin'} on:click={() => goto('/admin')}>
								<Shield size={18} />
								<span>Админ-панель</span>
							</button>
						{/if}
						{#if user.role === 'PARENT'}
							<button class:active={tab==='children'} on:click={() => tab='children'}>
								<Users size={18} />
								<span>Мои дети</span>
							</button>
							<button class:active={tab==='vouchers'} on:click={() => tab='vouchers'}>
								<HeartHandshake size={18} />
								<span>Мои путёвки</span>
							</button>
						{/if}
						{#if user.role === 'EMPLOYEE'}
							<button class:active={tab==='duties'} on:click={() => tab='duties'}>
								<Activity size={18} />
								<span>Журнал дежурств</span>
							</button>
							<button class:active={tab==='schedule'} on:click={() => tab='schedule'}>
								<Calendar size={18} />
								<span>Моё расписание</span>
							</button>
						{/if}
						<button class:active={tab==='settings'} on:click={() => tab='settings'}>
							<Settings size={18} />
							<span>Настройки</span>
						</button>
						<button class="logout-btn" on:click={handleLogout}>
							<LogOut size={18} />
							<span>Выйти</span>
						</button>
					</div>

					<div class="tab-content" in:fly={{ y: 30, delay: 600 }}>
						{#if tab === 'profile'}
							<div class="profile-tab">
								<h3>Информация о профиле</h3>
								<div class="profile-info">
									<div class="info-item">
										<span class="label">ID пользователя:</span>
										<span class="value">{user.userId}</span>
									</div>
									<div class="info-item">
										<span class="label">Роль:</span>
										<span class="value">{getRoleLabel(user.role)}</span>
									</div>
									<div class="info-item">
										<span class="label">Статус:</span>
										<span class="value status-active">Активен</span>
									</div>
								</div>
							</div>
						{:else if tab === 'children' && user.role === 'PARENT'}
							<ChildrenCabinet {user} />
						{:else if tab === 'vouchers' && user.role === 'PARENT'}
							<VouchersCabinet {user} />
						{:else if tab === 'duties' && user.role === 'EMPLOYEE'}
							<DutiesCabinet {user} />
						{:else if tab === 'schedule' && user.role === 'EMPLOYEE'}
							<ScheduleCabinet {user} />
						{:else if tab === 'settings'}
							<div class="coming-soon">
								<Settings size={48} />
								<h3>Настройки</h3>
								<p>Функция находится в разработке</p>
							</div>
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

	.cabinet-section {
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

	.cabinet-container {
		display: grid;
		grid-template-columns: 300px 1fr;
		background: var(--bg-primary);
		border-radius: var(--radius);
		overflow: hidden;
		box-shadow: var(--shadow);
	}

	.cabinet-hero {
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

	.cabinet-hero h1 {
		font-size: 2rem;
		margin-bottom: 0.5rem;
		line-height: 1.2;
	}

	.cabinet-hero p {
		opacity: 0.9;
		margin-bottom: 1.5rem;
		font-size: 0.9rem;
	}

	.cabinet-image {
		margin-top: auto;
		text-align: center;
	}

	.cabinet-image img {
		max-width: 100%;
		height: auto;
		border-radius: var(--radius);
		opacity: 0.8;
	}

	.cabinet-content {
		padding: 2rem;
		display: flex;
		flex-direction: column;
	}

	.profile-card {
		background: var(--bg-secondary);
		border-radius: var(--radius);
		padding: 1.5rem;
		margin-bottom: 2rem;
		display: flex;
		gap: 1rem;
		align-items: center;
		border: 1px solid var(--border);
	}

	.avatar {
		background: linear-gradient(135deg, var(--primary), var(--primary-dark));
		border-radius: 50%;
		width: 64px;
		height: 64px;
		display: flex;
		align-items: center;
		justify-content: center;
		color: white;
		flex-shrink: 0;
	}

	.info h2 {
		margin: 0 0 0.5rem 0;
		font-size: 1.3rem;
		color: var(--primary);
	}

	.info p {
		margin: 0.1rem 0;
		font-size: 0.9rem;
		color: var(--text-secondary);
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

	.logout-btn {
		background: rgba(239, 68, 68, 0.1);
		color: var(--error);
		border-color: var(--error);
	}

	.logout-btn:hover {
		background: var(--error);
		color: white;
	}

	.tab-content {
		flex: 1;
	}

	.profile-tab h3 {
		margin-bottom: 1.5rem;
		font-size: 1.5rem;
		color: var(--primary);
	}

	.profile-info {
		display: flex;
		flex-direction: column;
		gap: 1rem;
	}

	.info-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 1rem;
		background: var(--bg-primary);
		border: 1px solid var(--border);
		border-radius: var(--radius);
	}

	.label {
		font-size: 0.9rem;
		color: var(--text-secondary);
	}

	.value {
		font-weight: 500;
		color: var(--text-primary);
	}

	.status-active {
		color: var(--secondary);
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
		.cabinet-container {
			grid-template-columns: 1fr;
		}

		.cabinet-hero {
			display: none;
		}

		.cabinet-content {
			padding: 1.5rem;
		}

		.tabs {
			justify-content: center;
		}
	}

	@media (max-width: 768px) {
		.cabinet-section {
			padding: 1rem 0;
		}

		.cabinet-hero h1 {
			font-size: 1.5rem;
		}

		.tabs {
			flex-direction: column;
		}

		.tabs button {
			justify-content: center;
		}

		.profile-card {
			flex-direction: column;
			text-align: center;
		}
	}
</style>