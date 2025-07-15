<script lang="ts">
	import { themeStore, toggleTheme } from '$lib/themeStore';
	import { userStore, logoutUser, UserRole } from '$lib/userStore';
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { User, LogOut, Sun, Moon, Shield, Users, HeartHandshake } from 'lucide-svelte';
	import { get } from 'svelte/store';

	let theme: 'light' | 'dark' = 'light';
	const unsubTheme = themeStore.subscribe((t) => (theme = t));
	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	onMount(() => {
		if (!user) goto('/login');
		return () => { unsubTheme(); unsubUser(); };
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

{#if user}
<div class="cabinet" data-theme={theme}>
	<header>
		<div class="logo"><User size={26}/> Личный кабинет</div>
		<nav>
			<button class="theme-toggle" on:click={toggleTheme} aria-label="Переключить тему">
				{#if theme === 'dark'}<Moon size={20}/>{:else}<Sun size={20}/>{/if}
			</button>
			<button class="logout-btn" on:click={handleLogout} aria-label="Выйти"><LogOut size={20}/> Выйти</button>
		</nav>
	</header>
	<main>
		<section class="profile-card fade-in">
			<div class="avatar"><User size={40}/></div>
			<div class="info">
				<h2>Здравствуйте, {getRoleLabel(user.role)}!</h2>
				<p>ID: {user.userId}</p>
				<p>Ваша роль: <b>{getRoleLabel(user.role)}</b></p>
			</div>
		</section>
		<section class="cabinet-menu fade-in">
			{#if user.role === 'ADMIN'}
				<a class="menu-item" href="/admin"><Shield size={22}/> Админ-панель</a>
			{/if}
			{#if user.role === 'PARENT'}
				<a class="menu-item" href="/cabinet/vouchers"><HeartHandshake size={22}/> Мои путёвки</a>
				<a class="menu-item" href="/cabinet/children"><Users size={22}/> Мои дети</a>
			{/if}
			{#if user.role === 'EMPLOYEE'}
				<a class="menu-item" href="/cabinet/duties"><Shield size={22}/> Журнал дежурств</a>
				<a class="menu-item" href="/cabinet/schedule"><Users size={22}/> Моё расписание</a>
			{/if}
			<!-- Можно добавить больше пунктов для всех ролей -->
		</section>
	</main>
</div>
{/if}

<style>
.cabinet {
	display: flex;
	flex-direction: column;
	min-height: 100vh;
	background: var(--color-bg, #f8fafc);
	color: var(--color-text, #222);
	transition: background 0.3s, color 0.3s;
}
.cabinet[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-primary: #2d8cff;
	--color-accent: #ffb347;
	--color-card: #23272f;
}
.cabinet[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-primary: #2d8cff;
	--color-accent: #ffb347;
	--color-card: #fff;
}
header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 1.2rem 2.5rem 0.5rem 2.5rem;
}
.logo {
	display: flex;
	align-items: center;
	gap: 0.5rem;
	font-size: 1.5rem;
	font-weight: bold;
	color: var(--color-primary);
}
nav {
	display: flex;
	align-items: center;
	gap: 1.2rem;
}
.theme-toggle {
	background: none;
	border: none;
	font-size: 1.3rem;
	cursor: pointer;
	color: var(--color-primary);
	transition: color 0.2s;
	display: flex;
	align-items: center;
}
.logout-btn {
	background: none;
	border: none;
	color: var(--color-primary);
	font-size: 1.05rem;
	font-weight: 500;
	display: flex;
	align-items: center;
	gap: 0.3rem;
	cursor: pointer;
	transition: color 0.2s;
	padding: 0.3rem 0.7rem;
	border-radius: 8px;
}
.logout-btn:hover {
	background: var(--color-primary);
	color: #fff;
}
main {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 2rem 1rem 3rem 1rem;
}
.profile-card {
	background: var(--color-card);
	border-radius: 18px;
	box-shadow: 0 6px 32px rgba(45,140,255,0.10);
	padding: 2.5rem 2rem 2rem 2rem;
	max-width: 400px;
	width: 100%;
	display: flex;
	gap: 1.5rem;
	align-items: center;
	margin-bottom: 2.5rem;
	animation: fadeInUp 1.1s;
}
.avatar {
	background: linear-gradient(135deg, var(--color-primary) 60%, var(--color-accent) 100%);
	border-radius: 50%;
	width: 64px;
	height: 64px;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #fff;
	font-size: 2.2rem;
}
.info h2 {
	margin: 0 0 0.5rem 0;
	font-size: 1.3rem;
	color: var(--color-primary);
}
.info p {
	margin: 0.1rem 0;
	font-size: 1.05rem;
}
.cabinet-menu {
	margin-top: 1.5rem;
	display: flex;
	flex-direction: column;
	gap: 1.2rem;
	max-width: 400px;
	width: 100%;
}
.menu-item {
	display: flex;
	align-items: center;
	gap: 0.7rem;
	background: var(--color-card);
	border-radius: 14px;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	padding: 1.1rem 1.2rem;
	font-size: 1.08rem;
	font-weight: 500;
	color: var(--color-primary);
	text-decoration: none;
	transition: background 0.18s, color 0.18s, transform 0.18s;
}
.menu-item:hover {
	background: linear-gradient(90deg, var(--color-primary), var(--color-accent));
	color: #fff;
	transform: translateY(-2px) scale(1.03);
}
@keyframes fadeInUp {
	from { opacity: 0; transform: translateY(60px); }
	to { opacity: 1; transform: none; }
}
.fade-in {
	animation: fadeInUp 1.1s;
}
</style> 