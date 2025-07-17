<script lang="ts">
	import { themeStore, toggleTheme } from '$lib/themeStore';
	import { UserPlus, User, Lock, Mail, Eye, EyeOff, Sun, Moon } from 'lucide-svelte';
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	let theme: 'light' | 'dark' = 'light';
	const unsubscribe = themeStore.subscribe((t) => (theme = t));
	onMount(() => () => unsubscribe());

	let username = '';
	let email = '';
	let password = '';
	let confirmPassword = '';
	let showPassword = false;
	let loading = false;
	let error = '';
	let userType = 'PARENT';
	let role = 'PARENT';

	async function handleRegister(e: Event) {
		e.preventDefault();
		error = '';
		if (password !== confirmPassword) {
			error = 'Пароли не совпадают';
			return;
		}
		loading = true;
		try {
			const res = await fetch('/api/auth/register', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ username, email, password, userType, role })
			});
			if (!res.ok) {
				const data = await res.json();
				throw new Error(data.message || 'Ошибка регистрации');
			}
			goto('/login');
		} catch (e) {
			error = e.message || 'Ошибка регистрации';
		} finally {
			loading = false;
		}
	}
</script>

<div class="auth-page" data-theme={theme}>
	<header>
		<div class="logo"><UserPlus size={28}/> CampIT</div>
		<button class="theme-toggle" on:click={toggleTheme} aria-label="Переключить тему">
			{#if theme === 'dark'}<Moon size={22}/>{:else}<Sun size={22}/>{/if}
		</button>
	</header>
	<main>
		<form class="auth-form" on:submit={handleRegister}>
			<h1>Регистрация</h1>
			<div class="input-group">
				<User size={20}/>
				<input type="text" placeholder="Логин" bind:value={username} required autocomplete="username" />
			</div>
			<div class="input-group">
				<Mail size={20}/>
				<input type="email" placeholder="Email" bind:value={email} required autocomplete="email" />
			</div>
			<div class="input-group">
				<Lock size={20}/>
				<input type={showPassword ? 'text' : 'password'} placeholder="Пароль" bind:value={password} required autocomplete="new-password" />
				<button type="button" class="eye-btn" on:click={() => showPassword = !showPassword} tabindex="-1">
					{#if showPassword}<EyeOff size={18}/>{:else}<Eye size={18}/>{/if}
				</button>
			</div>
			<div class="input-group">
				<Lock size={20}/>
				<input type={showPassword ? 'text' : 'password'} placeholder="Повторите пароль" bind:value={confirmPassword} required autocomplete="new-password" />
			</div>
			{#if error}
				<div class="error">{error}</div>
			{/if}
			<button class="submit-btn" type="submit" disabled={loading}>
				{#if loading}⏳ Регистрируем...{:else}Зарегистрироваться{/if}
			</button>
			<div class="switch-link">
				Уже есть аккаунт? <a href="/login">Войти</a>
			</div>
		</form>
	</main>
</div>

<style>
.auth-page {
	display: flex;
	flex-direction: column;
	align-items: center;
	min-height: 100vh;
	background: var(--color-bg, #f8fafc);
	color: var(--color-text, #222);
	transition: background 0.3s, color 0.3s;
}
.auth-page[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-primary: #2d8cff;
	--color-accent: #ffb347;
	--color-card: #23272f;
}
.auth-page[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-primary: #2d8cff;
	--color-accent: #ffb347;
	--color-card: #fff;
}
header {
	width: 100%;
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
main {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	width: 100%;
}
.auth-form {
	background: var(--color-card);
	border-radius: 18px;
	box-shadow: 0 6px 32px rgba(45,140,255,0.10);
	padding: 2.5rem 2rem 2rem 2rem;
	max-width: 370px;
	width: 100%;
	display: flex;
	flex-direction: column;
	gap: 1.2rem;
	animation: fadeInUp 1.1s;
}
.auth-form h1 {
	text-align: center;
	font-size: 2rem;
	color: var(--color-primary);
	margin-bottom: 0.5rem;
}
.input-group {
	display: flex;
	align-items: center;
	gap: 0.7rem;
	background: rgba(45,140,255,0.04);
	border-radius: 10px;
	padding: 0.7rem 1rem;
	margin-bottom: 0.2rem;
	position: relative;
}
.input-group input {
	flex: 1;
	border: none;
	background: transparent;
	outline: none;
	font-size: 1.08rem;
	color: var(--color-text);
}
.eye-btn {
	background: none;
	border: none;
	cursor: pointer;
	color: var(--color-primary);
	padding: 0 0.2rem;
	display: flex;
	align-items: center;
}
.error {
	color: #e74c3c;
	background: rgba(231,76,60,0.08);
	padding: 0.5rem 1rem;
	border-radius: 8px;
	text-align: center;
	font-size: 1rem;
}
.submit-btn {
	background: linear-gradient(90deg, var(--color-primary), var(--color-accent));
	color: #fff;
	border: none;
	border-radius: 12px;
	padding: 0.9rem 2.2rem;
	font-size: 1.1rem;
	font-weight: 600;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
	transition: background 0.2s, color 0.2s, box-shadow 0.2s;
	cursor: pointer;
}
.submit-btn:disabled {
	opacity: 0.7;
	cursor: not-allowed;
}
.switch-link {
	text-align: center;
	font-size: 1rem;
	margin-top: 0.5rem;
}
.switch-link a {
	color: var(--color-primary);
	text-decoration: underline;
	transition: color 0.2s;
}
.switch-link a:hover {
	color: var(--color-accent);
}
@keyframes fadeInUp {
	from { opacity: 0; transform: translateY(60px); }
	to { opacity: 1; transform: none; }
}
</style>