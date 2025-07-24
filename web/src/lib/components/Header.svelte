<script>
  import ThemeToggle from './ThemeToggle.svelte';
  import { Home, Info, Calendar, Phone, LogIn } from 'lucide-svelte';

  export let currentTheme;
  export let toggleTheme;

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

  .nav-text {
    position: relative;
    transition: var(--transition);
  }

  @media (max-width: 768px) {
    nav {
      display: none;
    }
  }
</style>