<script>
  import ThemeToggle from './ThemeToggle.svelte';
  import { Home, Info, Calendar, Phone, LogIn } from 'lucide-svelte';

  export let currentTheme;
  export let toggleTheme;

  const navLinks = [
    { name: 'Главная', href: '/', icon: Home },
    { name: 'О лагере', href: '#about', icon: Info },
    { name: 'Программы', href: '#programs', icon: Calendar },
    { name: 'Контакты', href: '#contacts', icon: Phone }
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
          <svelte:component this={link.icon} size={18} class="icon" />
          <span>{link.name}</span>
        </a>
      {/each}
    </nav>

    <div class="actions">
      <ThemeToggle {currentTheme} {toggleTheme} />
      <a href="/login" class="button primary">
        <div class="icon">
          <LogIn size={18} />
        </div>
        <span>Войти</span>
      </a>
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
    gap: 1.5rem;
  }

  .nav-link {
    color: var(--text-primary);
    text-decoration: none;
    font-weight: 500;
    transition: var(--transition);
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 0;
  }

  .nav-link:hover {
    color: var(--primary);
  }

  .icon {
    flex-shrink: 0;
  }

  .actions {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .button {
    padding: 0.5rem 1rem;
    border-radius: var(--radius);
    text-decoration: none;
    font-weight: 500;
    transition: var(--transition);
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .primary {
    background: var(--primary);
    color: white;
  }

  .primary:hover {
    background: var(--primary-dark);
    transform: translateY(-2px);
  }

  @media (max-width: 768px) {
    nav {
      display: none;
    }

    .container {
      justify-content: space-between;
    }
  }
</style> 