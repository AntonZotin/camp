<script lang="ts">
	import { onMount } from 'svelte';
	import { fade, fly } from 'svelte/transition';
	import { Settings, User, Lock, Mail, Eye, EyeOff, Save, AlertCircle } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';

	export let user: UserSession;

	let loading = false;
	let error = '';
	let success = '';
	let showPassword = false;
	let showNewPassword = false;

	let profileForm = {
		username: '',
		email: '',
		currentPassword: '',
		newPassword: '',
		confirmPassword: ''
	};

	let settingsForm = {
		emailNotifications: true,
		smsNotifications: false,
		theme: 'auto'
	};

	async function loadProfile() {
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/users/profile`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (res.ok) {
				const profile = await res.json();
				profileForm.username = profile.username || '';
				profileForm.email = profile.email || '';
			}
		} catch (e) {
			console.error('Ошибка загрузки профиля:', e);
		}
	}

	async function loadSettings() {
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/users/settings`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (res.ok) {
				const settings = await res.json();
				settingsForm.emailNotifications = settings.emailNotifications || true;
				settingsForm.smsNotifications = settings.smsNotifications || false;
				settingsForm.theme = settings.theme || 'auto';
			}
		} catch (e) {
			console.error('Ошибка загрузки настроек:', e);
		}
	}

	async function updateProfile() {
		loading = true;
		error = '';
		success = '';

		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/users/profile`, {
				method: 'PUT',
				headers: {
					'Content-Type': 'application/json',
					Authorization: `Bearer ${user.accessToken}`
				},
				body: JSON.stringify({
					username: profileForm.username,
					email: profileForm.email
				})
			});

			if (!res.ok)
				error = 'Ошибка обновления профиля';
			else
				success = 'Профиль успешно обновлен';
		} finally {
			loading = false;
		}
	}

	async function changePassword() {
		if (profileForm.newPassword !== profileForm.confirmPassword) {
			error = 'Новые пароли не совпадают';
			return;
		}

		if (profileForm.newPassword.length < 6) {
			error = 'Новый пароль должен быть не менее 6 символов';
			return;
		}

		loading = true;
		error = '';
		success = '';

		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/users/change-password`, {
				method: 'PUT',
				headers: {
					'Content-Type': 'application/json',
					Authorization: `Bearer ${user.accessToken}`
				},
				body: JSON.stringify({
					currentPassword: profileForm.currentPassword,
					newPassword: profileForm.newPassword
				})
			});

			if (!res.ok)
				error = 'Ошибка смены пароля';
			else {
				success = 'Пароль успешно изменен';
				profileForm.currentPassword = '';
				profileForm.newPassword = '';
				profileForm.confirmPassword = '';
			}
		} finally {
			loading = false;
		}
	}

	async function updateSettings() {
		loading = true;
		error = '';
		success = '';

		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/users/settings`, {
				method: 'PUT',
				headers: {
					'Content-Type': 'application/json',
					Authorization: `Bearer ${user.accessToken}`
				},
				body: JSON.stringify(settingsForm)
			});

			if (!res.ok)
				error = 'Ошибка обновления настроек';
			else
				success = 'Настройки успешно обновлены';
		} finally {
			loading = false;
		}
	}

	onMount(() => { 
		loadProfile(); 
		loadSettings();
	});
</script>

<div class="settings-cabinet">
	<div class="header">
		<h2>
			<Settings size={24} />
			<span>Настройки</span>
		</h2>
	</div>

	{#if error}
		<div class="error-message" transition:fade>
			<AlertCircle size={20} />
			<span>{error}</span>
		</div>
	{/if}

	{#if success}
		<div class="success-message" transition:fade>
			<Save size={20} />
			<span>{success}</span>
		</div>
	{/if}

	<div class="settings-grid">
		<div class="settings-card" in:fly={{ y: 30, delay: 100 }}>
			<div class="card-header">
				<User size={20} />
				<h3>Профиль</h3>
			</div>
			<form on:submit|preventDefault={updateProfile}>
				<div class="form-group">
					<label for="username">Логин</label>
					<input id="username" bind:value={profileForm.username} required />
				</div>
				<div class="form-group">
					<label for="email">Email</label>
					<input id="email" type="email" bind:value={profileForm.email} required />
				</div>
				<button type="submit" class="save-btn" disabled={loading}>
					{#if loading}
						<span>Сохранение...</span>
					{:else}
						<Save size={16} />
						<span>Сохранить профиль</span>
					{/if}
				</button>
			</form>
		</div>

		<div class="settings-card" in:fly={{ y: 30, delay: 200 }}>
			<div class="card-header">
				<Lock size={20} />
				<h3>Смена пароля</h3>
			</div>
			<form on:submit|preventDefault={changePassword}>
				<div class="form-group">
					<label for="currentPassword">Текущий пароль</label>
					<div class="password-input">
						<input 
							id="currentPassword" 
							type={showPassword ? 'text' : 'password'} 
							bind:value={profileForm.currentPassword} 
							required
						/>
						<button type="button" class="toggle-password" on:click={() => showPassword = !showPassword}>
							<svelte:component this={showPassword ? EyeOff : Eye} size={16} />
						</button>
					</div>
				</div>
				<div class="form-group">
					<label for="newPassword">Новый пароль</label>
					<div class="password-input">
						<input 
							id="newPassword" 
							type={showNewPassword ? 'text' : 'password'} 
							bind:value={profileForm.newPassword} 
							required
						/>
						<button type="button" class="toggle-password" on:click={() => showNewPassword = !showNewPassword}>
							<svelte:component this={showNewPassword ? EyeOff : Eye} size={16} />
						</button>
					</div>
				</div>
				<div class="form-group">
					<label for="confirmPassword">Подтвердите новый пароль</label>
					<input 
						id="confirmPassword" 
						type="password" 
						bind:value={profileForm.confirmPassword} 
						required
					/>
				</div>
				<button type="submit" class="save-btn" disabled={loading}>
					{#if loading}
						<span>Смена пароля...</span>
					{:else}
						<Lock size={16} />
						<span>Сменить пароль</span>
					{/if}
				</button>
			</form>
		</div>

		<div class="settings-card" in:fly={{ y: 30, delay: 300 }}>
			<div class="card-header">
				<Mail size={20} />
				<h3>Уведомления</h3>
			</div>
			<form on:submit|preventDefault={updateSettings}>
				<div class="form-group">
					<label class="checkbox-label">
						<input type="checkbox" bind:checked={settingsForm.emailNotifications} />
						<span>Email уведомления</span>
					</label>
				</div>
				<div class="form-group">
					<label class="checkbox-label">
						<input type="checkbox" bind:checked={settingsForm.smsNotifications} />
						<span>SMS уведомления</span>
					</label>
				</div>
				<div class="form-group">
					<label for="theme">Тема оформления</label>
					<select id="theme" bind:value={settingsForm.theme}>
						<option value="auto">Автоматически</option>
						<option value="light">Светлая</option>
						<option value="dark">Темная</option>
					</select>
				</div>
				<button type="submit" class="save-btn" disabled={loading}>
					{#if loading}
						<span>Сохранение...</span>
					{:else}
						<Save size={16} />
						<span>Сохранить настройки</span>
					{/if}
				</button>
			</form>
		</div>
	</div>
</div>

<style>
	.settings-cabinet {
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

	.settings-grid {
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
		gap: 2rem;
	}

	.settings-card {
		background: var(--bg-primary);
		border: 1px solid var(--border);
		border-radius: var(--radius);
		padding: 1.5rem;
		transition: var(--transition);
	}

	.settings-card:hover {
		transform: translateY(-2px);
		box-shadow: var(--shadow);
	}

	.card-header {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		margin-bottom: 1.5rem;
	}

	.card-header h3 {
		margin: 0;
		font-size: 1.2rem;
		color: var(--primary);
	}

	.form-group {
		margin-bottom: 1rem;
	}

	.form-group label {
		display: block;
		margin-bottom: 0.5rem;
		font-weight: 500;
		color: var(--text-primary);
	}

	.form-group input, .form-group select {
		width: 100%;
		padding: 0.75rem;
		border: 1px solid var(--border);
		border-radius: var(--radius);
		background: var(--bg-primary);
		color: var(--text-primary);
		font-size: 0.9rem;
		transition: var(--transition);
	}

	.form-group input:focus, .form-group select:focus {
		border-color: var(--primary);
		box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
	}

	.password-input {
		position: relative;
		display: flex;
		align-items: center;
	}

	.password-input input {
		padding-right: 3rem;
	}

	.toggle-password {
		position: absolute;
		right: 0.75rem;
		background: none;
		border: none;
		cursor: pointer;
		color: var(--text-secondary);
		padding: 0.25rem;
		border-radius: var(--radius);
	}

	.toggle-password:hover {
		background: var(--bg-hover);
	}

	.checkbox-label {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		cursor: pointer;
	}

	.checkbox-label input[type="checkbox"] {
		width: auto;
		margin: 0;
	}

	.save-btn {
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
		width: 100%;
		justify-content: center;
	}

	.save-btn:hover:not(:disabled) {
		background: var(--primary-dark);
		transform: translateY(-2px);
	}

	.save-btn:disabled {
		opacity: 0.7;
		cursor: not-allowed;
		transform: none;
	}

	@media (max-width: 768px) {
		.header {
			flex-direction: column;
			gap: 1rem;
			align-items: stretch;
		}

		.settings-grid {
			grid-template-columns: 1fr;
		}
	}
</style> 