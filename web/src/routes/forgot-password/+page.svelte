<script lang="ts">
  import { fade, fly } from 'svelte/transition';
  import { Mail, ArrowRight, Key } from 'lucide-svelte';
  import { goto } from '$app/navigation';
  import { PUBLIC_API_URL } from '$env/static/public';

  let email = '';
  let loading = false;
  let error = '';
  let success = false;

  async function handleSubmit(e: Event) {
    e.preventDefault();
    error = '';
    loading = true;

    try {
      const res = await fetch(`${PUBLIC_API_URL}/api/auth/forgot-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
      });

      if (!res.ok) {
        const data = await res.json();
        throw new Error(data.message || 'Ошибка при отправке запроса');
      }

      success = true;
    } catch (e) {
      error = e.message || 'Произошла ошибка. Попробуйте позже.';
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
        <h1>Восстановление пароля</h1>
        <p>Укажите email, который вы использовали при регистрации, и мы вышлем инструкции по восстановлению</p>
        <div class="auth-image">
          <img src="/images/forgot-password-hero.jpg" alt="Дети в лагере" />
        </div>
      </div>

      {#if success}
        <div class="success-message">
          <h2>Письмо отправлено!</h2>
          <p>Мы отправили инструкции по восстановлению пароля на адрес <strong>{email}</strong>.</p>
          <p>Проверьте вашу почту и следуйте указаниям в письме.</p>
          <button on:click={() => goto('/login')} class="back-to-login">
            <ArrowRight size={16} />
            <span>Вернуться к входу</span>
          </button>
        </div>
      {:else}
        <form class="auth-form" on:submit={handleSubmit}>
          <h2>Восстановить пароль</h2>

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

          {#if error}
            <div class="error-message" transition:fade>
              <span>{error}</span>
            </div>
          {/if}

          <button type="submit" class="submit-button" disabled={loading}>
            {#if loading}
              <span>Отправка...</span>
            {:else}
              <Key size={18} />
              <span>Отправить инструкции</span>
            {/if}
          </button>

          <div class="auth-footer">
            <a href="/login" class="back-link">
              <ArrowRight size={16} />
              <span>Вернуться к входу</span>
            </a>
          </div>
        </form>
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

  .auth-form, .success-message {
    padding: 4rem;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  .auth-form h2, .success-message h2 {
    font-size: 2rem;
    margin-bottom: 1.5rem;
    color: var(--primary);
  }

  .success-message p {
    margin-bottom: 1rem;
    color: var(--text-secondary);
  }

  .success-message p strong {
    color: var(--text-primary);
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

  .submit-button, .back-to-login {
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
    text-decoration: none;
  }

  .submit-button:hover, .back-to-login:hover {
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
  }

  .back-link {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: var(--primary);
    text-decoration: none;
    font-weight: 500;
    transition: var(--transition);
  }

  .back-link:hover {
    gap: 0.75rem;
  }

  @media (max-width: 1024px) {
    .auth-container {
      grid-template-columns: 1fr;
    }

    .auth-hero {
      display: none;
    }

    .auth-form, .success-message {
      padding: 2rem;
    }
  }

  @media (max-width: 768px) {
    .auth-section {
      padding: 1rem 0;
    }

    .auth-form h2, .success-message h2 {
      font-size: 1.75rem;
    }
  }
</style>