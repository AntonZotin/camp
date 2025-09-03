<script>
  import {Search, X} from "lucide-svelte";
    export let value = "";
    export let placeholder = "Поиск...";
</script>

<div class="controls">
    <div class="search-box">
        <span class="icon">
            <Search size={18}/>
        </span>
        <input
            type="text"
            {placeholder}
            bind:value
        />
        {#if value}
            <button
                class="clear-btn"
                type="button"
                on:click={() => value = ""}
                title="Сбросить поиск"
            >
                <X size={16}/>
            </button>
        {/if}
    </div>
</div>

<style>
    .controls {
        margin-bottom: 1.5rem;
    }

    .search-box {
        position: relative;
    }

    .search-box input {
        width: 100%;
        padding: 0.75rem 1rem 0.75rem 2.5rem;
        border: 1px solid var(--border);
        border-radius: var(--radius);
        background: var(--bg-primary);
        color: var(--text-primary);
        font-size: 0.9rem;
        box-sizing: border-box;
    }

    .search-box .icon {
        position: absolute;
        left: 0.75rem;
        top: 50%;
        transform: translateY(-50%);
        color: var(--text-secondary);
    }

    .search-box .clear-btn {
        position: absolute;
        right: 0.5rem;
        top: 50%;
        transform: translateY(-50%);
        background: transparent;
        border: none;
        cursor: pointer;
        color: var(--text-secondary);
        padding: 0.25rem;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .search-box .clear-btn:hover {
        color: var(--text-primary);
    }
</style>