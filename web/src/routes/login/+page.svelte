<script lang="ts">
  import { fade, fly } from 'svelte/transition';
  import { Eye, EyeOff, User, Lock, LogIn, ArrowRight } from 'lucide-svelte';
  import { goto } from '$app/navigation';
  import { loginUser } from '$lib/stores/userStore';
  import { PUBLIC_API_URL } from '$env/static/public';

  let username = '';
  let password = '';
  let showPassword = false;
  let loading = false;
  let error = '';

  async function handleLogin(e: Event) {
    e.preventDefault();
    error = '';
    loading = true;
    try {
      const res = await fetch(`${PUBLIC_API_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ usernameOrEmail: username, password })
      });
      if (!res.ok) {
        const data = await res.json();
        throw new Error(data.message || 'Ошибка входа');
      }
      const data = await res.json();
      loginUser({
        accessToken: data.accessToken,
        userId: data.userId,
        role: data.role
      });
      goto('/cabinet');
    } catch (e) {
      error = (e as Error).message || 'Ошибка входа';
    } finally {
      loading = false;
    }
  }
</script>

<div class="stars-bg"></div>

<section class="auth-section" transition:fade>
  <div class="container">
    <div class="auth-container" in:fly={{ y: 50 }}>
      <div class="auth-hero">
        <div class="logo">
          <span class="gradient-text">Sunny Camp</span>
        </div>
        <h1>Добро пожаловать!</h1>
        <p>Войдите в личный кабинет, чтобы управлять бронированием и получать персональные предложения</p>
        <div class="auth-image">
          <img src="/src/images/login-hero.jpg" alt="Дети в лагере" />
        </div>
      </div>

      <form class="auth-form" on:submit={handleLogin}>
        <h2>Вход в аккаунт</h2>

        <div class="form-group">
          <label for="username">
            <User size={18} />
            <span>Логин или Email</span>
          </label>
          <input
            id="username"
            type="text"
            bind:value={username}
            placeholder="Введите ваш логин или email"
            required
            autocomplete="username"
          />
        </div>

        <div class="form-group">
          <label for="password">
            <Lock size={18} />
            <span>Пароль</span>
          </label>
          <div class="password-input">
            <input
              id="password"
              type={showPassword ? 'text' : 'password'}
              bind:value={password}
              placeholder="Введите ваш пароль"
              required
              autocomplete="current-password"
            />
            <button
              type="button"
              class="toggle-password"
              on:click={() => showPassword = !showPassword}
              tabindex="-1"
            >
              <svelte:component this={showPassword ? EyeOff : Eye} size={18} />
            </button>
          </div>
        </div>

        {#if error}
          <div class="error-message" transition:fade>
            <span>{error}</span>
          </div>
        {/if}

        <button type="submit" class="submit-button" disabled={loading}>
          {#if loading}
            <span>Вход...</span>
          {:else}
            <LogIn size={18} />
            <span>Войти</span>
          {/if}
        </button>

        <div class="auth-footer">
          <a href="/forgot-password" class="forgot-password">Забыли пароль?</a>
          <a href="/register" class="register-link">
            <span>Нет аккаунта?</span>
            <span>Зарегистрироваться</span>
            <ArrowRight size={16} />
          </a>
        </div>
      </form>
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

  .auth-section {
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

  .auth-container {
    display: grid;
    grid-template-columns: 1fr 1fr;
    background: var(--bg-primary);
    border-radius: var(--radius);
    overflow: hidden;
    box-shadow: var(--shadow);
  }

  .auth-hero {
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

  .auth-hero h1 {
    font-size: 2.5rem;
    margin-bottom: 1rem;
    line-height: 1.2;
  }

  .auth-hero p {
    opacity: 0.9;
    margin-bottom: 2rem;
  }

  .auth-image {
    margin-top: auto;
    text-align: center;
  }

  .auth-image img {
    max-width: 100%;
    height: auto;
    border-radius: var(--radius);
  }

  .auth-form {
    padding: 4rem;
    display: flex;
    flex-direction: column;
  }

  .auth-form h2 {
    font-size: 2rem;
    margin-bottom: 2rem;
    color: var(--primary);
  }

  .form-group {
    margin-bottom: 1.5rem;
  }

  .form-group label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 0.5rem;
    font-weight: 500;
    color: var(--text-secondary);
  }

  .form-group input {
    width: 100%;
    padding: 0.75rem 1rem;
    border: 1px solid var(--border);
    border-radius: var(--radius);
    background: var(--bg-primary);
    color: var(--text-primary);
    transition: var(--transition);
  }

  .form-group input:focus {
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
  }

  .password-input {
    position: relative;
  }

  .toggle-password {
    position: absolute;
    right: 0.75rem;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    color: var(--text-secondary);
    cursor: pointer;
    padding: 0.25rem;
  }

  .error-message {
    background: rgba(239, 68, 68, 0.1);
    color: var(--error);
    padding: 0.75rem 1rem;
    border-radius: var(--radius);
    margin-bottom: 1.5rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .submit-button {
    background: linear-gradient(90deg, var(--primary), var(--primary-dark));
    color: white;
    border: none;
    border-radius: var(--radius);
    padding: 0.75rem 1.5rem;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    cursor: pointer;
    transition: var(--transition);
    margin-top: 1rem;
  }

  .submit-button:hover {
    opacity: 0.9;
    transform: translateY(-2px);
  }

  .submit-button:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    transform: none;
  }

  .auth-footer {
    margin-top: 2rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .forgot-password {
    color: var(--text-secondary);
    text-decoration: none;
    font-size: 0.875rem;
    transition: var(--transition);
  }

  .forgot-password:hover {
    color: var(--primary);
  }

  .register-link {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: var(--primary);
    text-decoration: none;
    font-weight: 500;
    transition: var(--transition);
  }

  .register-link:hover {
    gap: 0.75rem;
  }

  .register-link span:first-child {
    color: var(--text-secondary);
    font-weight: normal;
  }

  @media (max-width: 1024px) {
    .auth-container {
      grid-template-columns: 1fr;
    }

    .auth-hero {
      display: none;
    }

    .auth-form {
      padding: 2rem;
    }
  }

  @media (max-width: 768px) {
    .auth-section {
      padding: 1rem 0;
    }

    .auth-form h2 {
      font-size: 1.75rem;
    }
  }
</style>