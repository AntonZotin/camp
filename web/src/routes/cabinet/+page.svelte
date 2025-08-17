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
	import SettingsCabinet from '$lib/cabinet/SettingsCabinet.svelte';

	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	let tab: 'children' | 'vouchers' | 'schedule' | 'duties' | 'settings';

	onMount(() => {
		if (!user) goto('/login');
		return () => { unsubUser(); };
	});

	function handleLogout() {
		logoutUser();
		goto('/');
	}

	function getRoleLabel(role: UserRole): string {
		switch (role) {
			case 'ADMIN':
				return 'Администратор';
			case 'PARENT':
				tab = 'children';
				return 'Родитель';
			case 'EMPLOYEE':
				tab = 'schedule';
				return 'Сотрудник';
			default:
				return 'Пользователь';
		}
	}
</script>

<div class="stars-bg"></div>

<section class="cabinet-section" transition:fade>
	<div class="container">
		<div class="cabinet-container" in:fly={{ y: 50 }}>
			{#if user}
				<div class="cabinet-hero">
					<div class="logo">
						<span class="gradient-text">Sunny Camp</span>
					</div>
					<h1>Личный кабинет</h1>
					<p>Управляйте бронированием, следите за расписанием и получайте персональные предложения</p>
					<div class="cabinet-image">
						<img src="/images/camp-hero.png" alt="Дети в лагере" />
					</div>
				</div>

				<div class="cabinet-content">
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

					<div class="cabinet-menu" in:fly={{ y: 30, delay: 400 }}>
						{#if user.role === 'ADMIN'}
							<a class="menu-item" href="/admin">
								<Shield size={22} />
								<span>Админ-панель</span>
							</a>
						{/if}
						{#if user.role === 'PARENT'}
							<button class="menu-item" class:active={tab==='children'} on:click={() => tab='children'}>
								<Users size={22} />
								<span>Мои дети</span>
							</button>
							<button class="menu-item" class:active={tab==='vouchers'} on:click={() => tab='vouchers'}>
								<HeartHandshake size={22} />
								<span>Мои путёвки</span>
							</button>
						{/if}
						{#if user.role === 'EMPLOYEE'}
							<button class="menu-item" class:active={tab==='schedule'} on:click={() => tab='schedule'}>
								<Calendar size={22} />
								<span>Расписание</span>
							</button>
							<button class="menu-item" class:active={tab==='duties'} on:click={() => tab='duties'}>
								<Activity size={22} />
								<span>Дежурства</span>
							</button>
						{/if}
						<button class="menu-item" class:active={tab==='settings'} on:click={() => tab='settings'}>
							<Settings size={22} />
							<span>Настройки</span>
						</button>
						<button class="menu-item logout" on:click={handleLogout}>
							<LogOut size={22} />
							<span>Выйти</span>
						</button>
					</div>

					<div class="tab-content" in:fly={{ y: 30, delay: 600 }}>
						{#if tab === 'children' && user.role === 'PARENT'}
							<ChildrenCabinet {user} />
						{:else if tab === 'vouchers' && user.role === 'PARENT'}
							<VouchersCabinet {user} />
						{:else if tab === 'schedule' && user.role === 'EMPLOYEE'}
							<ScheduleCabinet {user} />
						{:else if tab === 'duties' && user.role === 'EMPLOYEE'}
							<DutiesCabinet {user} />
						{:else if tab === 'settings'}
							<SettingsCabinet {user} />
						{/if}
					</div>
				</div>
			{/if}
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
		background: url("/images/star.png");
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
		grid-template-columns: 1fr 1fr;
		background: var(--bg-primary);
		border-radius: var(--radius);
		overflow: hidden;
		box-shadow: var(--shadow);
	}

	.cabinet-hero {
		background: linear-gradient(135deg, var(--primary), var(--primary-dark));
		color: white;
		padding: 4rem;
		display: flex;
		flex-direction: column;
	}

	.logo {
		font-size: 1.75rem;
		font-weight: 700;
		margin-bottom: 2rem;
	}

	.cabinet-hero h1 {
		font-size: 2.5rem;
		margin-bottom: 1rem;
		line-height: 1.2;
	}

	.cabinet-hero p {
		opacity: 0.9;
		margin-bottom: 2rem;
	}

	.cabinet-image {
		margin-top: auto;
		text-align: center;
	}

	.cabinet-image img {
		max-width: 100%;
		height: auto;
		border-radius: var(--radius);
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
		align-items: center;
		gap: 1rem;
	}

	.avatar {
		background: var(--primary);
		color: white;
		width: 60px;
		height: 60px;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.info h2 {
		margin: 0 0 0.5rem 0;
		font-size: 1.25rem;
		color: var(--text-primary);
	}

	.info p {
		margin: 0.25rem 0;
		color: var(--text-secondary);
		font-size: 0.9rem;
	}

	.cabinet-menu {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
		margin-bottom: 2rem;
	}

	.menu-item {
		background: var(--bg-secondary);
		border: 1px solid var(--border);
		border-radius: var(--radius);
		padding: 1rem 1.5rem;
		text-decoration: none;
		color: var(--text-primary);
		font-weight: 500;
		transition: var(--transition);
		display: flex;
		align-items: center;
		gap: 0.75rem;
		cursor: pointer;
		border: none;
		font-size: 0.9rem;
	}

	.menu-item:hover {
		background: var(--bg-hover);
		transform: translateY(-2px);
	}

	.menu-item.active {
		background: var(--primary);
		color: white;
		border-color: var(--primary);
	}

	.menu-item.logout {
		color: var(--error);
		border-color: var(--error);
		margin-top: auto;
	}

	.menu-item.logout:hover {
		background: var(--error);
		color: white;
	}

	.tab-content {
		flex: 1;
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
	}

	@media (max-width: 768px) {
		.cabinet-section {
			padding: 1rem 0;
		}

		.cabinet-hero h1 {
			font-size: 2rem;
		}

		.cabinet-menu {
			flex-direction: row;
			flex-wrap: wrap;
		}

		.menu-item {
			flex: 1;
			min-width: 120px;
			justify-content: center;
		}
	}
</style>