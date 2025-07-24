<script lang="ts">
  import { fade, fly } from 'svelte/transition';
  import { themeStore } from '$lib/stores/themeStore';
  import { User, Lock, Mail, Eye, EyeOff, UserPlus, ArrowRight } from 'lucide-svelte';
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { PUBLIC_API_URL } from '$env/static/public';

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
      const res = await fetch(`${PUBLIC_API_URL}/api/auth/register`, {
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

<div class="stars-bg"></div>

<section class="auth-section" transition:fade>
  <div class="container">
    <div class="auth-container" in:fly={{ y: 50 }}>
      <div class="auth-hero">
        <div class="logo">
          <span class="gradient-text">Sunny Camp</span>
        </div>
        <h1>Присоединяйтесь!</h1>
        <p>Создайте аккаунт, чтобы бронировать путевки, получать скидки и следить за успехами ребенка</p>
        <div class="auth-image">
          <img src="/src/images/register-hero.jpg" alt="Дети в лагере" />
        </div>
      </div>

      <form class="auth-form" on:submit={handleRegister}>
        <h2>Создать аккаунт</h2>

        <div class="form-group">
          <label for="username">
            <User size={18} />
            <span>Логин</span>
          </label>
          <input
            id="username"
            type="text"
            bind:value={username}
            placeholder="Придумайте логин"
            required
            autocomplete="username"
          />
        </div>

        <div class="form-group">
          <label for="email">
            <Mail size={18} />
            <span>Email</span>
          </label>
          <input
            id="email"
            type="email"
            bind:value={email}
            placeholder="Введите ваш email"
            required
            autocomplete="email"
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
              placeholder="Придумайте пароль"
              required
              autocomplete="new-password"
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

        <div class="form-group">
          <label for="confirmPassword">
            <Lock size={18} />
            <span>Подтвердите пароль</span>
          </label>
          <input
            id="confirmPassword"
            type={showPassword ? 'text' : 'password'}
            bind:value={confirmPassword}
            placeholder="Повторите пароль"
            required
            autocomplete="new-password"
          />
        </div>

        <div class="form-group">
          <label for="userType">
            <User size={18} />
            <span>Тип аккаунта</span>
          </label>
          <select
            id="userType"
            bind:value={userType}
            class="select-input"
          >
            <option value="PARENT">Родитель</option>
            <option value="CHILD">Ребенок</option>
          </select>
        </div>

        {#if error}
          <div class="error-message" transition:fade>
            <span>{error}</span>
          </div>
        {/if}

        <button type="submit" class="submit-button" disabled={loading}>
          {#if loading}
            <span>Регистрация...</span>
          {:else}
            <UserPlus size={18} />
            <span>Зарегистрироваться</span>
          {/if}
        </button>

        <div class="auth-footer">
          <a href="/login" class="register-link">
            <span>Уже есть аккаунт?</span>
            <span>Войти</span>
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

  .form-group input,
  .form-group select {
    width: 100%;
    padding: 0.75rem 1rem;
    border: 1px solid var(--border);
    border-radius: var(--radius);
    background: var(--bg-primary);
    color: var(--text-primary);
    transition: var(--transition);
  }

  .form-group input:focus,
  .form-group select:focus {
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
  }

  .select-input {
    appearance: none;
    background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
    background-repeat: no-repeat;
    background-position: right 0.75rem center;
    background-size: 1rem;
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
    justify-content: flex-end;
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