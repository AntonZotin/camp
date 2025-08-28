<script lang="ts">
    import {onMount} from 'svelte';
    import {fade, fly} from 'svelte/transition';
    import {Loader, Plus, Trash2, Edit, AlertCircle, Users, Search, ArrowUpDown} from 'lucide-svelte';
    import {PUBLIC_API_URL} from '$env/static/public';
    import type {UserSession} from '$lib/stores/userStore';
    import {toast} from 'svelte-sonner';
    import type {Child, User} from "$lib/models";
    import SearchBox from "$lib/components/SearchBox.svelte";

    export let user: UserSession;

    let children: Child[] = [];
    let filteredChildren: Child[] = [];
    let loading = true;
    let error = '';
    let showModal = false;
    let editChild: Child | null = null;
    let searchQuery = '';
    let sortField: keyof Child = 'fullName';
    let sortDirection: 'asc' | 'desc' = 'asc';

    interface ChildForm {
        fullName: string;
        birthDate: string;
        parentId: number;
    }

    let childForm: ChildForm = {
        fullName: '',
        birthDate: '',
        parentId: 0
    };

    let users: User[] = [];
    let loadingUsers = false;

    async function loadChildren() {
        loading = true;
        error = '';
        try {
            const res = await fetch(`${PUBLIC_API_URL}/api/children`, {
                headers: {Authorization: `Bearer ${user.accessToken}`}
            });
            if (!res.ok) {
                error = 'Ошибка загрузки данных о детях';
            } else {
                children = await res.json();
                filterAndSortChildren();
            }
        } catch (e) {
            error = 'Ошибка подключения к серверу';
        } finally {
            loading = false;
        }
    }

    async function loadUsers() {
        loadingUsers = true;
        try {
            const res = await fetch(`${PUBLIC_API_URL}/api/admin/users`, {
                headers: {Authorization: `Bearer ${user.accessToken}`}
            });
            if (res.ok) {
                users = await res.json();
            }
        } catch (e) {
            console.error('Error loading users:', e);
        } finally {
            loadingUsers = false;
        }
    }

    function filterAndSortChildren() {
        filteredChildren = children.filter(child =>
            child.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
            (child.parentUsername.toLowerCase().includes(searchQuery.toLowerCase()) || false) ||
            child.birthDate.includes(searchQuery)
        );

        filteredChildren.sort((a, b) => {
            let valueA = a[sortField];
            let valueB = b[sortField];

            if (typeof valueA === 'string') valueA = valueA.toLowerCase();
            if (typeof valueB === 'string') valueB = valueB.toLowerCase();

            if (valueA < valueB) return sortDirection === 'asc' ? -1 : 1;
            if (valueA > valueB) return sortDirection === 'asc' ? 1 : -1;
            return 0;
        });
    }

    function sortBy(field: keyof Child) {
        if (sortField === field) {
            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDirection = 'asc';
        }
        filterAndSortChildren();
    }

    function openModal(child: Child | null = null) {
        showModal = true;
        editChild = child;
        if (child) {
            childForm = {
                fullName: child.fullName,
                birthDate: child.birthDate.split('T')[0],
                parentId: child.parentId || 0
            };
        } else {
            childForm = {fullName: '', birthDate: '', parentId: 0};
        }
    }

    function closeModal() {
        showModal = false;
        editChild = null;
    }

    async function saveChild() {
        const method = editChild ? 'PUT' : 'POST';
        const url = editChild ? `${PUBLIC_API_URL}/api/children/${editChild.id}` : `${PUBLIC_API_URL}/api/children`;

        const body = JSON.stringify({
            fullName: childForm.fullName,
            birthDate: childForm.birthDate,
            parentId: childForm.parentId
        });

        try {
            const response = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${user.accessToken}`
                },
                body
            });

            if (response.ok) {
                await loadChildren();
                closeModal();
                toast.success('Данные ребенка успешно сохранены');
            } else {
                const errorText = await response.text();
                toast.error(`Ошибка: ${errorText}`);
            }
        } catch (e) {
            toast.error('Ошибка подключения к серверу');
        }
    }

    async function deleteChild(id: number) {
        if (!confirm('Удалить данные ребенка?')) return;

        try {
            const response = await fetch(`${PUBLIC_API_URL}/api/children/${id}`, {
                method: 'DELETE',
                headers: {Authorization: `Bearer ${user.accessToken}`}
            });

            if (response.ok) {
                await loadChildren();
                toast.success('Данные ребенка успешно удалены');
            } else {
                const errorText = await response.text();
                toast.error(`Ошибка: ${errorText}`);
            }
        } catch (e) {
            toast.error('Ошибка подключения к серверу');
        }
    }

    $: if (searchQuery || sortField) {
        filterAndSortChildren();
    }

    onMount(() => {
        loadChildren();
        loadUsers();
    });
</script>

<div class="child-admin">
    <div class="header">
        <h2>
            <Users size={24}/>
            <span>Управление детьми</span>
        </h2>
        <button class="add-btn" on:click={() => openModal()}>
            <Plus size={18}/>
            <span>Добавить ребенка</span>
        </button>
    </div>

    <SearchBox
        bind:value={searchQuery}
        placeholder="Поиск по ФИО, родителю или дате рождения..."
    />

    {#if loading || loadingUsers}
        <div class="loader">
            <Loader size={24}/>
            <span>Загрузка...</span>
        </div>
    {:else if error}
        <div class="error">
            <AlertCircle size={20}/>
            <span>{error}</span>
        </div>
    {:else}
        <div class="child-table">
            <table>
                <thead>
                <tr>
                    <th on:click={() => sortBy('id')} class:active={sortField==='id'}>
                        <span>ID <ArrowUpDown size={14}/></span>
                    </th>
                    <th on:click={() => sortBy('fullName')} class:active={sortField==='fullName'}>
                        <span>ФИО <ArrowUpDown size={14}/></span>
                    </th>
                    <th on:click={() => sortBy('birthDate')} class:active={sortField==='birthDate'}>
                        <span>Дата рождения <ArrowUpDown size={14}/></span>
                    </th>
                    <th on:click={() => sortBy('parentUsername')} class:active={sortField==='parentUsername'}>
                        <span>Родитель <ArrowUpDown size={14}/></span>
                    </th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                {#each filteredChildren as child}
                    <tr>
                        <td>{child.id}</td>
                        <td>{child.fullName}</td>
                        <td>{new Date(child.birthDate).toLocaleDateString('ru-RU')}</td>
                        <td>{child.parentUsername || 'Не указан'}</td>
                        <td>
                            <button class="icon-btn edit" title="Редактировать" on:click={() => openModal(child)}>
                                <Edit size={16}/>
                            </button>
                            <button class="icon-btn delete" title="Удалить" on:click={() => deleteChild(child.id)}>
                                <Trash2 size={16}/>
                            </button>
                        </td>
                    </tr>
                {/each}
                </tbody>
            </table>

            {#if filteredChildren.length === 0 && children.length > 0}
                <div class="no-results">
                    <AlertCircle size={20}/>
                    <span>Ничего не найдено</span>
                </div>
            {/if}
        </div>
    {/if}
</div>

{#if showModal}
    <!-- svelte-ignore a11y_click_events_have_key_events, a11y_no_static_element_interactions -->
    <div class="modal-backdrop" out:fade={{ duration: 250 }} on:click={closeModal}></div>
    <div class="modal" in:fly={{ y: 30 }}>
        <h3>{editChild ? 'Редактировать данные ребенка' : 'Добавить ребенка'}</h3>
        <form on:submit|preventDefault={saveChild}>
            <div class="form-group">
                <label for="fullName">ФИО ребенка</label>
                <input id="fullName" bind:value={childForm.fullName} required/>
            </div>

            <div class="form-group">
                <label for="birthDate">Дата рождения</label>
                <input
                        id="birthDate"
                        type="date"
                        bind:value={childForm.birthDate}
                        required
                        max={new Date().toISOString().split('T')[0]}
                />
            </div>

            <div class="form-group">
                <label for="parentId">Родитель</label>
                <select id="parentId" bind:value={childForm.parentId} required>
                    <option value="" disabled>Выберите родителя</option>
                    {#each users as u}
                        <option value={u.id}>{u.username}</option>
                    {/each}
                </select>
            </div>

            <div class="modal-actions">
                <button type="submit" class="save-btn">Сохранить</button>
                <button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
            </div>
        </form>
    </div>
{/if}

<style>
    .child-admin {
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

    .add-btn {
        background: var(--primary);
        color: white;
        border: none;
        border-radius: var(--radius);
        padding: 0.75rem 1.5rem;
        font-size: 0.9rem;
        font-weight: 500;
        cursor: pointer;
        transition: var(--transition);
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .add-btn:hover {
        background: var(--primary-dark);
        transform: translateY(-2px);
    }

    .loader, .error, .no-results {
        text-align: center;
        margin: 2rem 0;
        font-size: 1rem;
        color: var(--text-secondary);
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
    }

    .error, .no-results {
        color: var(--error);
    }

    .child-table {
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

    .icon-btn {
        background: none;
        border: none;
        cursor: pointer;
        padding: 0.25rem;
        border-radius: var(--radius);
        transition: var(--transition);
        display: inline-flex;
        align-items: center;
        margin-right: 0.5rem;
    }

    .icon-btn.edit {
        color: var(--primary);
    }

    .icon-btn.delete {
        color: var(--error);
    }

    .icon-btn:hover {
        background: var(--bg-hover);
    }

    .modal-backdrop {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.5);
        z-index: 1000;
    }

    .modal {
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: var(--bg-primary);
        padding: 2rem;
        border-radius: var(--radius);
        box-shadow: var(--shadow);
        z-index: 1001;
        min-width: 500px;
        max-width: 90vw;
        max-height: 90vh;
        overflow-y: auto;
    }

    .modal h3 {
        margin-bottom: 1.5rem;
        font-size: 1.5rem;
        color: var(--primary);
    }

    .form-group {
        margin-bottom: 1.5rem;
    }

    .form-group label {
        display: block;
        margin-bottom: 0.5rem;
        font-weight: 500;
        color: var(--text-primary);
    }

    .form-group input, .form-group select {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid var(--border);
        border-radius: var(--radius);
        background: var(--bg-primary);
        color: var(--text-primary);
        font-size: 0.9rem;
        transition: var(--transition);
        box-sizing: border-box;
    }

    .form-group input:focus, .form-group select:focus {
        border-color: var(--primary);
        box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
    }

    .modal-actions {
        display: flex;
        gap: 1rem;
        justify-content: flex-end;
        margin-top: 2rem;
    }

    .save-btn, .cancel-btn {
        padding: 0.75rem 1.5rem;
        border-radius: var(--radius);
        font-weight: 500;
        text-decoration: none;
        transition: var(--transition);
        border: none;
        cursor: pointer;
        font-size: 0.9rem;
    }

    .save-btn {
        background: var(--primary);
        color: white;
    }

    .save-btn:hover {
        background: var(--primary-dark);
    }

    .cancel-btn {
        background: transparent;
        color: var(--text-primary);
        border: 1px solid var(--border);
    }

    .cancel-btn:hover {
        background: var(--bg-hover);
    }

    @media (max-width: 768px) {
        .header {
            flex-direction: column;
            gap: 1rem;
            align-items: stretch;
        }

        .modal {
            min-width: 300px;
            margin: 1rem;
            padding: 1.5rem;
        }

        .modal-actions {
            flex-direction: column;
        }

        th span {
            flex-direction: column;
            gap: 0.25rem;
        }
    }
</style>