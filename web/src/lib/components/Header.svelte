<script>
  import ThemeToggle from './ThemeToggle.svelte';
  import { Home, Info, Calendar, Phone, LogIn, User, LogOut } from 'lucide-svelte';
  import { userStore, logoutUser } from '$lib/stores/userStore';
  import { goto } from '$app/navigation';
  import { get } from 'svelte/store';

  export let currentTheme;
  export let toggleTheme;

  let user = get(userStore);
  userStore.subscribe((u) => (user = u));

  function handleLogout() {
    logoutUser();
    goto('/');
  }

  const navLinks = [
    { name: 'Главная', href: '/', icon: Home },
    { name: 'О лагере', href: '/about', icon: Info },
    { name: 'Программы', href: '/programs', icon: Calendar },
    { name: 'Контакты', href: '/contacts', icon: Phone }
  ];
</script>

<header>
  <div class="container">
    <a href="/" class="logo">
      <span>Sunny Camp</span>
    </a>

    <nav>
      {#each navLinks as link}
        <a href={link.href} class="nav-link">
          <div class="icon">
            <svelte:component this={link.icon} size={18}/>
          </div>
          <span class="nav-text">{link.name}</span>
          <div class="nav-highlight"></div>
        </a>
      {/each}
    </nav>

    <div class="actions">
      <ThemeToggle {currentTheme} {toggleTheme} />
      {#if user}
        <a href="/cabinet" class="button primary">
          <div class="icon">
            <User size={18} />
          </div>
          <span>Кабинет</span>
        </a>
        <button class="button secondary logout-btn" on:click={handleLogout}>
          <div class="icon">
            <LogOut size={18} />
          </div>
          <span>Выйти</span>
        </button>
      {:else}
        <a href="/login" class="button primary">
          <div class="icon">
            <LogIn size={18} />
          </div>
          <span>Войти</span>
        </a>
      {/if}
    </div>
  </div>
</header>

<style>
  header {
    background: var(--bg-header);
    padding: 1rem 2rem;
    box-shadow: var(--shadow);
    position: sticky;
    top: 0;
    z-index: 100;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
  }

  .logo {
    font-size: 1.5rem;
    font-weight: 700;
    color: var(--primary);
    text-decoration: none;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  nav {
    display: flex;
    gap: 0.5rem;
  }

  .nav-link {
    position: relative;
    color: var(--text-primary);
    text-decoration: none;
    font-weight: 500;
    padding: 0.75rem 1rem;
    border-radius: var(--radius);
    transition: var(--transition);
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .nav-link:hover {
    background: var(--bg-hover);
  }

  .nav-highlight {
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 0;
    height: 3px;
    background: var(--primary);
    border-radius: 3px 3px 0 0;
    transition: var(--transition);
  }

  .nav-link:hover .nav-highlight {
    width: 70%;
  }

  .nav-link .icon {
    transition: transform 0.2s ease;
  }

  .nav-link:hover .icon {
    transform: scale(1.1);
    color: var(--primary);
  }

  .actions {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .button {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.5rem;
    border-radius: var(--radius);
    font-weight: 500;
    text-decoration: none;
    transition: var(--transition);
    border: none;
    cursor: pointer;
    font-size: 0.9rem;
  }

  .button.primary {
    background: var(--primary);
    color: white;
  }

  .button.primary:hover {
    background: var(--primary-dark);
    transform: translateY(-2px);
  }

  .button.secondary {
    background: transparent;
    color: var(--text-primary);
    border: 1px solid var(--border);
  }

  .button.secondary:hover {
    background: var(--bg-hover);
    transform: translateY(-2px);
  }

  .logout-btn {
    color: var(--error);
    border-color: var(--error);
  }

  .logout-btn:hover {
    background: var(--error);
    color: white;
  }

  .nav-text {
    position: relative;
    transition: var(--transition);
  }

  @media (max-width: 768px) {
    nav {
      display: none;
    }
    
    .actions {
      gap: 0.5rem;
    }
    
    .button {
      padding: 0.5rem 1rem;
      font-size: 0.8rem;
    }
    
    .button span {
      display: none;
    }
  }
</style>