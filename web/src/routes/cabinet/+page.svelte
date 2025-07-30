<script lang="ts">
	import { fade, fly } from 'svelte/transition';
	import { userStore, logoutUser, type UserRole } from '$lib/stores/userStore';
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { User, LogOut, Shield, Users, HeartHandshake, Calendar, Activity, Settings } from 'lucide-svelte';
	import { get } from 'svelte/store';

	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	onMount(() => {
		if (!user) goto('/login');
		return () => { unsubUser(); };
	});

	function handleLogout() {
		logoutUser();
		goto('/login');
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
						<a class="menu-item" href="/cabinet/vouchers">
							<HeartHandshake size={22} />
							<span>Мои путёвки</span>
						</a>
						<a class="menu-item" href="/cabinet/children">
							<Users size={22} />
							<span>Мои дети</span>
						</a>
					{/if}
					{#if user.role === 'EMPLOYEE'}
						<a class="menu-item" href="/cabinet/duties">
							<Activity size={22} />
							<span>Журнал дежурств</span>
						</a>
						<a class="menu-item" href="/cabinet/schedule">
							<Calendar size={22} />
							<span>Моё расписание</span>
						</a>
					{/if}
					<a class="menu-item" href="/cabinet/settings">
						<Settings size={22} />
						<span>Настройки</span>
					</a>
					<button class="menu-item logout-btn" on:click={handleLogout}>
						<LogOut size={22} />
						<span>Выйти</span>
					</button>
				</div>
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
		padding: 4rem;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}

	.profile-card {
		background: var(--bg-secondary);
		border-radius: var(--radius);
		padding: 2rem;
		margin-bottom: 2rem;
		display: flex;
		gap: 1.5rem;
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
		font-size: 1rem;
		color: var(--text-secondary);
	}

	.cabinet-menu {
		display: flex;
		flex-direction: column;
		gap: 1rem;
	}

	.menu-item {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		background: var(--bg-primary);
		border-radius: var(--radius);
		padding: 1rem 1.5rem;
		font-size: 1rem;
		font-weight: 500;
		color: var(--text-primary);
		text-decoration: none;
		transition: var(--transition);
		border: 1px solid var(--border);
		cursor: pointer;
	}

	.menu-item:hover {
		background: var(--bg-hover);
		transform: translateY(-2px);
		box-shadow: var(--shadow);
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

	@media (max-width: 1024px) {
		.cabinet-container {
			grid-template-columns: 1fr;
		}

		.cabinet-hero {
			display: none;
		}

		.cabinet-content {
			padding: 2rem;
		}
	}

	@media (max-width: 768px) {
		.cabinet-section {
			padding: 1rem 0;
		}

		.cabinet-hero h1 {
			font-size: 2rem;
		}

		.profile-card {
			flex-direction: column;
			text-align: center;
		}
	}
</style>