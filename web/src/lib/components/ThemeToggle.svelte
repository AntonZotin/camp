<script>
  import { Sun, Moon } from 'lucide-svelte';
  export let currentTheme;
  export let toggleTheme;
</script>

<button on:click={toggleTheme} class="theme-toggle" aria-label="Toggle theme">
  {#if currentTheme === 'light'}
    <Moon size={20} />
  {:else}
    <Sun size={20} />
  {/if}
</button>

<style>
  .theme-toggle {
    background: none;
    border: none;
    cursor: pointer;
    padding: 0.5rem;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-primary);
    transition: var(--transition);
  }

  .theme-toggle:hover {
    background: var(--bg-hover);
    transform: translateY(-2px);
  }
</style> 