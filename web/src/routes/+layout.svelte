<script>
  import { page } from '$app/stores';
  import Header from '$lib/components/Header.svelte';
  import Footer from '$lib/components/Footer.svelte';
  import { ArrowUp } from 'lucide-svelte';
  import { onMount } from 'svelte';
  import { Toaster } from 'svelte-sonner';
  import '../lib/global.css';

  let currentTheme = 'light';
  let showScrollButton = false;

  onMount(() => {
    const savedTheme = localStorage.getItem('theme') || 'light';
    currentTheme = savedTheme;
    document.documentElement.setAttribute('data-theme', currentTheme);

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  });

  const toggleTheme = () => {
    currentTheme = currentTheme === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', currentTheme);
    localStorage.setItem('theme', currentTheme);
  };

  const handleScroll = () => {
    showScrollButton = window.scrollY > 300;
  };

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  };
</script>

<svelte:head>
  <title>Детский оздоровительный лагерь</title>
  <meta name="description" content="Информационная система детского оздоровительного лагеря" />
</svelte:head>

<div class="layout" class:dark={currentTheme === 'dark'}>
  <Toaster richColors position="top-right" />
  <Header {currentTheme} {toggleTheme} />

  <main class:auth={$page.url.pathname.startsWith('/auth')}>
    <slot />
  </main>

  {#if !$page.url.pathname.startsWith('/auth')}
    <Footer />
  {/if}

  <button
    class="scroll-top-button"
    class:visible={showScrollButton}
    on:click={scrollToTop}
    aria-label="Прокрутить наверх"
  >
    <ArrowUp size={20} />
  </button>
</div>

<style>
  :global(:root) {
    --primary: #4f46e5;
    --primary-dark: #4338ca;
    --secondary: #10b981;
    --error: #ef4444;
    --radius: 6px;
    --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
    --transition: all 0.3s ease;
    --bg-primary: #ffffff;
    --bg-secondary: #f9fafb;
    --bg-header: #ffffff;
    --bg-footer: #f3f4f6;
    --bg-hover: #f1f5f9;
    --text-primary: #1f2937;
    --text-secondary: #4b5563;
    --border: #e5e7eb;
  }
  :global([data-theme='dark']) {
    --bg-primary: #111827;
    --bg-secondary: #1f2937;
    --bg-header: #111827;
    --bg-footer: #1f2937;
    --bg-hover: #374151;
    --text-primary: #f9fafb;
    --text-secondary: #d1d5db;
    --border: #4b5563;
  }
  :global(body) {
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    line-height: 1.5;
    margin: 0;
    padding: 0;
    background: var(--bg-primary);
    color: var(--text-primary);
    transition: background 0.3s ease, color 0.3s ease;
  }
  .layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    position: relative;
  }
  main {
    flex: 1;
    padding: 2rem;
  }
  main.auth {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .scroll-top-button {
    position: fixed;
    bottom: 2rem;
    right: 2rem;
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background: var(--primary);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
    cursor: pointer;
    opacity: 0;
    visibility: hidden;
    transform: translateY(20px);
    transition: var(--transition);
    box-shadow: var(--shadow);
    z-index: 100;
  }
  .scroll-top-button:hover {
    background: var(--primary-dark);
    transform: translateY(-5px);
  }
  .scroll-top-button.visible {
    opacity: 1;
    visibility: visible;
    transform: translateY(0);
  }
</style>