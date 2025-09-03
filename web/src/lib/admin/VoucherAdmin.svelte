<script lang="ts">
    import { onMount } from 'svelte';
    import { fade, fly } from 'svelte/transition';
    import { Loader, Plus, Trash2, Edit, AlertCircle, ArrowUpDown, Ticket } from 'lucide-svelte';
    import { PUBLIC_API_URL } from '$env/static/public';
    import type { UserSession } from '$lib/stores/userStore';
    import { toast } from 'svelte-sonner';
    import type { Voucher, Child, CampSession } from "$lib/models";
    import SearchBox from "$lib/components/SearchBox.svelte";

    export let user: UserSession;

    let vouchers: Voucher[] = [];
    let filteredVouchers: Voucher[] = [];
    let loading = true;
    let error = '';
    let showModal = false;
    let editVoucher: Voucher | null = null;
    let searchQuery = '';
    let sortField: keyof Voucher = 'bookingDate';
    let sortDirection: 'asc' | 'desc' = 'desc';

    interface VoucherForm {
        childId: number;
        sessionId: number;
        status: string;
        bookingDate: string;
    }

    let voucherForm: VoucherForm = {
        childId: 0,
        sessionId: 0,
        status: 'BOOKED',
        bookingDate: ''
    };

    let children: Child[] = [];
    let sessions: CampSession[] = [];
    let loadingData = false;

    const statusOptions = [
        { value: 'BOOKED', label: 'Ожидание' },
        { value: 'CONFIRMED', label: 'Подтверждена' },
        { value: 'PAID', label: 'Оплачена' },
        { value: 'CANCELLED', label: 'Отменена' },
        { value: 'COMPLETED', label: 'Завершена' }
    ];

    async function loadVouchers() {
        loading = true;
        error = '';
        try {
            const res = await fetch(`${PUBLIC_API_URL}/api/vouchers`, {
                headers: { Authorization: `Bearer ${user.accessToken}` }
            });
            if (!res.ok) {
                error = 'Ошибка загрузки путевок';
            } else {
                vouchers = await res.json();
                filterAndSortVouchers();
            }
        } catch (e) {
            error = 'Ошибка подключения к серверу';
        } finally {
            loading = false;
        }
    }

    async function loadChildren() {
        try {
            const res = await fetch(`${PUBLIC_API_URL}/api/children`, {
                headers: { Authorization: `Bearer ${user.accessToken}` }
            });
            if (res.ok) {
                children = await res.json();
            }
        } catch (e) {
            console.error('Error loading children:', e);
        }
    }

    async function loadSessions() {
        try {
            const res = await fetch(`${PUBLIC_API_URL}/api/sessions`, {
                headers: { Authorization: `Bearer ${user.accessToken}` }
            });
            if (res.ok) {
                sessions = await res.json();
            }
        } catch (e) {
            console.error('Error loading sessions:', e);
        }
    }

    function filterAndSortVouchers() {
        filteredVouchers = vouchers.filter(voucher =>
            voucher.child.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
            voucher.session.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            voucher.status.toLowerCase().includes(searchQuery.toLowerCase()) ||
            voucher.bookingDate.includes(searchQuery)
        );

        filteredVouchers.sort((a, b) => {
            let valueA = a[sortField];
            let valueB = b[sortField];

            if (typeof valueA === 'string') valueA = valueA.toLowerCase();
            if (typeof valueB === 'string') valueB = valueB.toLowerCase();

            if (valueA < valueB) return sortDirection === 'asc' ? -1 : 1;
            if (valueA > valueB) return sortDirection === 'asc' ? 1 : -1;
            return 0;
        });
    }

    function sortBy(field: keyof Voucher) {
        if (sortField === field) {
            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDirection = 'asc';
        }
        filterAndSortVouchers();
    }

    function openModal(voucher: Voucher | null = null) {
        showModal = true;
        editVoucher = voucher;
        if (voucher) {
            voucherForm = {
                childId: voucher.child.id,
                sessionId: voucher.session.id,
                status: voucher.status,
                bookingDate: voucher.bookingDate ? voucher.bookingDate.split('T')[0] : ''
            };
        } else {
            voucherForm = {
                childId: 0,
                sessionId: 0,
                status: 'BOOKED',
                bookingDate: new Date().toISOString().split('T')[0]
            };
        }
    }

    function closeModal() {
        showModal = false;
        editVoucher = null;
    }

    function getStatusLabel(status: string) {
        const statusObj = statusOptions.find(s => s.value === status);
        return statusObj ? statusObj.label : status;
    }

    function getStatusClass(status: string) {
        switch (status) {
            case 'BOOKED': return 'status-booked';
            case 'CONFIRMED': return 'status-confirmed';
            case 'PAID': return 'status-paid';
            case 'CANCELLED': return 'status-cancelled';
            case 'COMPLETED': return 'status-completed';
            default: return '';
        }
    }

    async function saveVoucher() {
        const method = editVoucher ? 'PUT' : 'POST';
        const url = editVoucher ? `${PUBLIC_API_URL}/api/vouchers/${editVoucher.id}` : `${PUBLIC_API_URL}/api/vouchers`;

        const body = JSON.stringify(voucherForm);

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
                await loadVouchers();
                closeModal();
                toast.success('Путевка успешно сохранена');
            } else {
                const errorText = await response.text();
                toast.error(`Ошибка: ${errorText}`);
            }
        } catch (e) {
            toast.error('Ошибка подключения к серверу');
        }
    }

    async function deleteVoucher(id: number) {
        if (!confirm('Удалить путевку?')) return;

        try {
            const response = await fetch(`${PUBLIC_API_URL}/api/vouchers/${id}`, {
                method: 'DELETE',
                headers: { Authorization: `Bearer ${user.accessToken}` }
            });

            if (response.ok) {
                await loadVouchers();
                toast.success('Путевка успешно удалена');
            } else {
                const errorText = await response.text();
                toast.error(`Ошибка: ${errorText}`);
            }
        } catch (e) {
            toast.error('Ошибка подключения к серверу');
        }
    }

    $: if (searchQuery || sortField) {
        filterAndSortVouchers();
    }

    onMount(async () => {
        loadingData = true;
        await Promise.all([loadVouchers(), loadChildren(), loadSessions()]);
        loadingData = false;
    });
</script>

<div class="voucher-admin">
    <div class="header">
        <h2>
            <Ticket size={24} />
            <span>Управление путевками</span>
        </h2>
        <button class="add-btn" on:click={() => openModal()}>
            <Plus size={18} />
            <span>Добавить путевку</span>
        </button>
    </div>

    <SearchBox
        bind:value={searchQuery}
        placeholder="Поиск по ребенку, смене или статусу..."
    />

    {#if loading || loadingData}
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
        <div class="voucher-table">
            <table>
                <thead>
                    <tr>
                        <th on:click={() => sortBy('id')} class:active={sortField==='id'}>
                            <span>ID <ArrowUpDown size={14}/></span>
                        </th>
                        <th>Ребенок</th>
                        <th>Смена</th>
                        <th on:click={() => sortBy('status')} class:active={sortField==='status'}>
                            <span>Статус <ArrowUpDown size={14}/></span>
                        </th>
                        <th on:click={() => sortBy('bookingDate')} class:active={sortField==='bookingDate'}>
                            <span>Дата бронирования <ArrowUpDown size={14}/></span>
                        </th>
                        <th>Действия</th>
                    </tr>
                </thead>
                <tbody>
                    {#each filteredVouchers as voucher}
                        <tr>
                            <td>{voucher.id}</td>
                            <td>
                                <div>{voucher.child.fullName}</div>
                                {#if voucher.child.birthDate}
                                    <small>Родитель: {voucher.child.parentUsername}</small>
                                {/if}
                            </td>
                            <td>{voucher.session.name || '-'}</td>
                            <td>
                                <span class={getStatusClass(voucher.status)}>
                                    {getStatusLabel(voucher.status)}
                                </span>
                            </td>
                            <td>
                                {#if voucher.bookingDate}
                                    {new Date(voucher.bookingDate).toLocaleDateString('ru-RU')}
                                {:else}
                                    <span class="text-muted">Не указана</span>
                                {/if}
                            </td>
                            <td>
                                <button class="icon-btn edit" title="Редактировать" on:click={() => openModal(voucher)}>
                                    <Edit size={16} />
                                </button>
                                <button class="icon-btn delete" title="Удалить" on:click={() => deleteVoucher(voucher.id)}>
                                    <Trash2 size={16} />
                                </button>
                            </td>
                        </tr>
                    {/each}
                </tbody>
            </table>

            {#if filteredVouchers.length === 0 && vouchers.length > 0}
                <div class="no-results">
                    <AlertCircle size={20} />
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
        <h3>{editVoucher ? 'Редактировать путевку' : 'Добавить путевку'}</h3>
        <form on:submit|preventDefault={saveVoucher}>
            <div class="form-row">
                <div class="form-group">
                    <label for="childId">Ребенок</label>
                    <select id="childId" bind:value={voucherForm.childId} required>
                        <option value="" disabled>Выберите ребенка</option>
                        {#each children as child}
                            <option value={child.id}>
                                {child.fullName}
                                {#if child.parentUsername}
                                    (Родитель: {child.parentUsername})
                                {/if}
                            </option>
                        {/each}
                    </select>
                </div>

                <div class="form-group">
                    <label for="sessionId">Смена</label>
                    <select id="sessionId" bind:value={voucherForm.sessionId} required>
                        <option value="" disabled>Выберите смену</option>
                        {#each sessions as session}
                            <option value={session.id}>{session.name}</option>
                        {/each}
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="status">Статус</label>
                    <select id="status" bind:value={voucherForm.status} required>
                        {#each statusOptions as option}
                            <option value={option.value}>{option.label}</option>
                        {/each}
                    </select>
                </div>

                <div class="form-group">
                    <label for="bookingDate">Дата бронирования</label>
                    <input id="bookingDate" type="date" bind:value={voucherForm.bookingDate} />
                </div>
            </div>

            <div class="modal-actions">
                <button type="submit" class="save-btn">Сохранить</button>
                <button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
            </div>
        </form>
    </div>
{/if}

<style>
    .voucher-admin {
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

    .voucher-table {
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
        white-space: nowrap;
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
        padding: 0.75rem;
        border-bottom: 1px solid var(--border);
        color: var(--text-primary);
        vertical-align: top;
    }

    tr:hover {
        background: var(--bg-hover);
    }

    .status-booked {
        background: var(--warning-light);
        color: var(--warning-dark);
        padding: 0.25rem 0.5rem;
        border-radius: var(--radius);
        font-size: 0.8rem;
        display: inline-block;
    }

    .status-confirmed {
        background: var(--info-light);
        color: var(--info-dark);
        padding: 0.25rem 0.5rem;
        border-radius: var(--radius);
        font-size: 0.8rem;
        display: inline-block;
    }

    .status-paid {
        background: var(--success-light);
        color: var(--success-dark);
        padding: 0.25rem 0.5rem;
        border-radius: var(--radius);
        font-size: 0.8rem;
        display: inline-block;
    }

    .status-cancelled {
        background: var(--error-light);
        color: var(--error-dark);
        padding: 0.25rem 0.5rem;
        border-radius: var(--radius);
        font-size: 0.8rem;
        display: inline-block;
    }

    .status-completed {
        background: var(--primary-light);
        color: var(--primary-dark);
        padding: 0.25rem 0.5rem;
        border-radius: var(--radius);
        font-size: 0.8rem;
        display: inline-block;
    }

    .text-muted {
        color: var(--text-secondary);
        font-style: italic;
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
        min-width: 600px;
        max-width: 90vw;
        max-height: 90vh;
        overflow-y: auto;
    }

    .modal h3 {
        margin-bottom: 1.5rem;
        font-size: 1.5rem;
        color: var(--primary);
    }

    .form-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1rem;
        margin-bottom: 1rem;
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
        font-family: inherit;
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

        .form-row {
            grid-template-columns: 1fr;
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

        table {
            font-size: 0.8rem;
        }

        td, th {
            padding: 0.5rem;
        }
    }
</style>