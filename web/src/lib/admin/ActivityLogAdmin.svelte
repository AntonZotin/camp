<script lang="ts">
	import { onMount } from 'svelte';
	import type {ActivityLog} from "$lib/models";
	import {Loader, AlertCircle, Activity, ArrowUpDown} from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';
	import SearchBox from "$lib/components/SearchBox.svelte";

	export let user: UserSession;

	let logs: ActivityLog[] = [];
	let filteredLogs: ActivityLog[] = [];
	let loading = true;
	let error = '';
    let searchQuery = '';
    let sortField: keyof ActivityLog = 'action';
    let sortDirection: 'asc' | 'desc' = 'asc';

	async function loadLogs() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/activity-logs`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok)
				error = 'Ошибка загрузки журнала';
			else {
				logs = await res.json();
				filterAndSortLogs();
			}
		} finally {
			loading = false;
		}
	}

    function filterAndSortLogs() {
        filteredLogs = logs.filter(log =>
			(log.user?.username?.toLowerCase() || '').includes(searchQuery.toLowerCase()) ||
            log.timestamp.toLowerCase().includes(searchQuery.toLowerCase()) ||
            log.description.toLowerCase().includes(searchQuery) ||
            log.action.toLowerCase().includes(searchQuery)
        );

        filteredLogs.sort((a, b) => {
            let valueA = a[sortField];
            let valueB = b[sortField];

            if (typeof valueA === 'string') valueA = valueA.toLowerCase();
            if (typeof valueB === 'string') valueB = valueB.toLowerCase();

            if (valueA < valueB) return sortDirection === 'asc' ? -1 : 1;
            if (valueA > valueB) return sortDirection === 'asc' ? 1 : -1;
            return 0;
        });
    }

    function sortBy(field: keyof ActivityLog) {
        if (sortField === field) {
            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDirection = 'asc';
        }
        filterAndSortLogs();
    }

    $: if (searchQuery || sortField) {
        filterAndSortLogs();
    }

	onMount(() => { loadLogs(); });
</script>

<div class="activity-log-admin">
	<div class="header">
		<h2>
			<Activity size={24} />
			<span>Журнал активности</span>
		</h2>
	</div>

    <SearchBox
        bind:value={searchQuery}
        placeholder="Поиск по всем полям..."
    />

	{#if loading}
		<div class="loader">
			<Loader size={24} />
			<span>Загрузка...</span>
		</div>
	{:else if error}
		<div class="error">
			<AlertCircle size={20} />
			<span>{error}</span>
		</div>
	{:else}
		<div class="log-table">
			<table>
				<thead>
					<tr>
						<th on:click={() => sortBy('id')} class:active={sortField==='id'}>
							<span>ID <ArrowUpDown size={14}/></span>
						</th>
						<th>Пользователь</th>
						<th on:click={() => sortBy('action')} class:active={sortField==='action'}>
							<span>Действие <ArrowUpDown size={14}/></span>
						</th>
						<th on:click={() => sortBy('timestamp')} class:active={sortField==='timestamp'}>
							<span>Время <ArrowUpDown size={14}/></span>
						</th>
						<th on:click={() => sortBy('description')} class:active={sortField==='description'}>
							<span>Описание <ArrowUpDown size={14}/></span>
						</th>
					</tr>
				</thead>
				<tbody>
					{#each filteredLogs as l}
						<tr>
							<td>{l.id}</td>
							{#if l.user}
								<td>{l.user?.username}</td>
							{:else}
								<td>unknown</td>
							{/if}
							<td>{l.action}</td>
							<td>{l.timestamp}</td>
							<td>{l.description}</td>
						</tr>
					{/each}
				</tbody>
			</table>
		</div>
	{/if}
</div>

<style>
	.activity-log-admin {
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

	.loader, .error {
		text-align: center;
		margin: 2rem 0;
		font-size: 1rem;
		color: var(--text-secondary);
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 0.5rem;
	}

	.error {
		color: var(--error);
	}

	.log-table {
		overflow-x: auto;
	}

    table {
        width: 100%;
        border-collapse: collapse;
        background: var(--bg-primary);
        border-radius: var(--radius);
        overflow: hidden;
        border: 1px solid var(--border);
    }

    th {
        background: var(--bg-secondary);
        color: var(--text-primary);
        font-weight: 600;
        padding: 1rem;
        text-align: left;
        border-bottom: 1px solid var(--border);
        cursor: pointer;
        user-select: none;
        transition: var(--transition);
    }

    th:hover {
        background: var(--bg-hover);
    }

    th.active {
        background: var(--primary-light);
        color: var(--primary);
    }

    th span {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    td {
        padding: 1rem;
        border-bottom: 1px solid var(--border);
        color: var(--text-primary);
    }

    tr:hover {
        background: var(--bg-hover);
    }

	@media (max-width: 768px) {
		.header {
			flex-direction: column;
			gap: 1rem;
			align-items: stretch;
		}
	}
</style> 