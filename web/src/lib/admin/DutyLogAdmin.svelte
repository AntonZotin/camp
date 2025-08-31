<script lang="ts">
    import { onMount } from 'svelte';
    import { fade, fly } from 'svelte/transition';
    import { Loader, Plus, Trash2, Edit, AlertCircle, Search, ArrowUpDown, Calendar, Clock, MapPin, FileText, User, Users } from 'lucide-svelte';
    import { PUBLIC_API_URL } from '$env/static/public';
    import type { UserSession } from '$lib/stores/userStore';
    import { toast } from 'svelte-sonner';
    import type { DutyLog, Employee, CampSession } from "$lib/models";
    import SearchBox from "$lib/components/SearchBox.svelte";

    export let user: UserSession;

    let dutyLogs: DutyLog[] = [];
    let filteredDutyLogs: DutyLog[] = [];
    let loading = true;
    let error = '';
    let showModal = false;
    let editDutyLog: DutyLog | null = null;
    let searchQuery = '';
    let sortField: keyof DutyLog = 'date';
    let sortDirection: 'asc' | 'desc' = 'desc';

    interface DutyLogForm {
        date: string;
        employeeId: number;
        sessionId: number;
        startTime: string;
        endTime: string;
        location: string;
        status: string;
        description: string;
        notes: string;
        report: string;
    }

    let dutyLogForm: DutyLogForm = {
        date: '',
        employeeId: 0,
        sessionId: 0,
        startTime: '08:00',
        endTime: '17:00',
        location: '',
        status: 'PLANNED',
        description: '',
        notes: '',
        report: ''
    };

    let employees: Employee[] = [];
    let sessions: CampSession[] = [];
    let loadingData = false;

    const statusOptions = [
        { value: 'PLANNED', label: 'Запланировано' },
        { value: 'COMPLETED', label: 'Выполнено' },
        { value: 'CANCELLED', label: 'Отменено' },
        { value: 'IN_PROGRESS', label: 'В процессе' }
    ];

    async function loadDutyLogs() {
        loading = true;
        error = '';
        try {
            const res = await fetch(`${PUBLIC_API_URL}/api/duty-logs`, {
                headers: { Authorization: `Bearer ${user.accessToken}` }
            });
            if (!res.ok) {
                error = 'Ошибка загрузки журнала дежурств';
            } else {
                dutyLogs = await res.json();
                filterAndSortDutyLogs();
            }
        } catch (e) {
            error = 'Ошибка подключения к серверу';
        } finally {
            loading = false;
        }
    }

    async function loadEmployees() {
        try {
            const res = await fetch(`${PUBLIC_API_URL}/api/employees`, {
                headers: { Authorization: `Bearer ${user.accessToken}` }
            });
            if (res.ok) {
                employees = await res.json();
            }
        } catch (e) {
            console.error('Error loading employees:', e);
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

    function filterAndSortDutyLogs() {
        filteredDutyLogs = dutyLogs.filter(log =>
            log.employee.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
            log.location.toLowerCase().includes(searchQuery.toLowerCase()) ||
            log.session.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            getStatusLabel(log.status).toLowerCase().includes(searchQuery.toLowerCase()) ||
            log.date.includes(searchQuery)
        );

        filteredDutyLogs.sort((a, b) => {
            let valueA = a[sortField];
            let valueB = b[sortField];

            if (typeof valueA === 'string') valueA = valueA.toLowerCase();
            if (typeof valueB === 'string') valueB = valueB.toLowerCase();

            if (valueA < valueB) return sortDirection === 'asc' ? -1 : 1;
            if (valueA > valueB) return sortDirection === 'asc' ? 1 : -1;
            return 0;
        });
    }

    function sortBy(field: keyof DutyLog) {
        if (sortField === field) {
            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDirection = 'asc';
        }
        filterAndSortDutyLogs();
    }

    function openModal(log: DutyLog | null = null) {
        showModal = true;
        editDutyLog = log;
        if (log) {
            dutyLogForm = {
                date: log.date.split('T')[0],
                employeeId: log.employee.id,
                sessionId: log.session.id,
                startTime: log.startTime,
                endTime: log.endTime,
                location: log.location,
                status: log.status,
                description: log.description || '',
                notes: log.notes || '',
                report: log.report || ''
            };
        } else {
            dutyLogForm = {
                date: new Date().toISOString().split('T')[0],
                employeeId: 0,
                sessionId: 0,
                startTime: '08:00',
                endTime: '17:00',
                location: '',
                status: 'PLANNED',
                description: '',
                notes: '',
                report: ''
            };
        }
    }

    function closeModal() {
        showModal = false;
        editDutyLog = null;
    }

    function getStatusLabel(status: string) {
        const statusObj = statusOptions.find(s => s.value === status);
        return statusObj ? statusObj.label : status;
    }

    function getStatusClass(status: string) {
        switch (status) {
            case 'PLANNED': return 'status-planned';
            case 'COMPLETED': return 'status-completed';
            case 'CANCELLED': return 'status-cancelled';
            case 'IN_PROGRESS': return 'status-in-progress';
            default: return '';
        }
    }

    async function saveDutyLog() {
        const method = editDutyLog ? 'PUT' : 'POST';
        const url = editDutyLog ? `${PUBLIC_API_URL}/api/duty-logs/${editDutyLog.id}` : `${PUBLIC_API_URL}/api/duty-logs`;

        const body = JSON.stringify(dutyLogForm);

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
                await loadDutyLogs();
                closeModal();
                toast.success('Запись журнала дежурств успешно сохранена');
            } else {
                const errorText = await response.text();
                toast.error(`Ошибка: ${errorText}`);
            }
        } catch (e) {
            toast.error('Ошибка подключения к серверу');
        }
    }

    async function deleteDutyLog(id: number) {
        if (!confirm('Удалить запись из журнала дежурств?')) return;

        try {
            const response = await fetch(`${PUBLIC_API_URL}/api/duty-logs/${id}`, {
                method: 'DELETE',
                headers: { Authorization: `Bearer ${user.accessToken}` }
            });

            if (response.ok) {
                await loadDutyLogs();
                toast.success('Запись успешно удалена');
            } else {
                const errorText = await response.text();
                toast.error(`Ошибка: ${errorText}`);
            }
        } catch (e) {
            toast.error('Ошибка подключения к серверу');
        }
    }

    $: if (searchQuery || sortField) {
        filterAndSortDutyLogs();
    }

    onMount(async () => {
        loadingData = true;
        await Promise.all([loadDutyLogs(), loadEmployees(), loadSessions()]);
        loadingData = false;
    });
</script>

<div class="duty-log-admin">
    <div class="header">
        <h2>
            <FileText size={24} />
            <span>Журнал дежурств</span>
        </h2>
        <button class="add-btn" on:click={() => openModal()}>
            <Plus size={18} />
            <span>Добавить запись</span>
        </button>
    </div>

    <SearchBox
        bind:value={searchQuery}
        placeholder="Поиск по сотруднику, локации, смене или статусу..."
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
        <div class="duty-log-table">
            <table>
                <thead>
                    <tr>
                        <th on:click={() => sortBy('date')} class:active={sortField==='date'}>
                            <span>Дата <ArrowUpDown size={14}/></span>
                        </th>
                        <th>Сотрудник</th>
                        <th>Смена</th>
                        <th on:click={() => sortBy('startTime')} class:active={sortField==='startTime'}>
                            <span>Время начала <ArrowUpDown size={14}/></span>
                        </th>
                        <th on:click={() => sortBy('endTime')} class:active={sortField==='endTime'}>
                            <span>Время окончания <ArrowUpDown size={14}/></span>
                        </th>
                        <th on:click={() => sortBy('location')} class:active={sortField==='location'}>
                            <span>Локация <ArrowUpDown size={14}/></span>
                        </th>
                        <th on:click={() => sortBy('status')} class:active={sortField==='status'}>
                            <span>Статус <ArrowUpDown size={14}/></span>
                        </th>
                        <th>Действия</th>
                    </tr>
                </thead>
                <tbody>
                    {#each filteredDutyLogs as log}
                        <tr>
                            <td>{new Date(log.date).toLocaleDateString('ru-RU')}</td>
                            <td>
                                <div>{log.employee.fullName}</div>
                                {#if log.employee.position}
                                    <small>{log.employee.position}</small>
                                {/if}
                            </td>
                            <td>{log.session.name || '-'}</td>
                            <td>{log.startTime}</td>
                            <td>{log.endTime}</td>
                            <td>{log.location}</td>
                            <td>
                                <span class={getStatusClass(log.status)}>
                                    {getStatusLabel(log.status)}
                                </span>
                            </td>
                            <td>
                                <button class="icon-btn edit" title="Редактировать" on:click={() => openModal(log)}>
                                    <Edit size={16} />
                                </button>
                                <button class="icon-btn delete" title="Удалить" on:click={() => deleteDutyLog(log.id)}>
                                    <Trash2 size={16} />
                                </button>
                            </td>
                        </tr>
                    {/each}
                </tbody>
            </table>
        </div>
    {/if}
</div>

{#if showModal}
    <!-- svelte-ignore a11y_click_events_have_key_events, a11y_no_static_element_interactions -->
    <div class="modal-backdrop" out:fade={{ duration: 250 }} on:click={closeModal}></div>
    <div class="modal large-modal" in:fly={{ y: 30 }}>
        <h3>{editDutyLog ? 'Редактировать запись дежурства' : 'Добавить запись дежурства'}</h3>
        <form on:submit|preventDefault={saveDutyLog}>
            <div class="form-row">
                <div class="form-group">
                    <label for="date">Дата</label>
                    <input id="date" type="date" bind:value={dutyLogForm.date} required />
                </div>

                <div class="form-group">
                    <label for="employeeId">Сотрудник</label>
                    <select id="employeeId" bind:value={dutyLogForm.employeeId} required>
                        <option value="" disabled>Выберите сотрудника</option>
                        {#each employees as emp}
                            <option value={emp.id}>{emp.fullName} - {emp.position}</option>
                        {/each}
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="sessionId">Смена</label>
                    <select id="sessionId" bind:value={dutyLogForm.sessionId} required>
                        <option value="" disabled>Выберите смену</option>
                        {#each sessions as session}
                            <option value={session.id}>{session.name}</option>
                        {/each}
                    </select>
                </div>

                <div class="form-group">
                    <label for="status">Статус</label>
                    <select id="status" bind:value={dutyLogForm.status} required>
                        {#each statusOptions as option}
                            <option value={option.value}>{option.label}</option>
                        {/each}
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="startTime">Время начала</label>
                    <input id="startTime" type="time" bind:value={dutyLogForm.startTime} required />
                </div>

                <div class="form-group">
                    <label for="endTime">Время окончания</label>
                    <input id="endTime" type="time" bind:value={dutyLogForm.endTime} required />
                </div>
            </div>

            <div class="form-group">
                <label for="location">Локация</label>
                <input id="location" bind:value={dutyLogForm.location} required />
            </div>

            <div class="form-group">
                <label for="description">Описание дежурства</label>
                <textarea id="description" bind:value={dutyLogForm.description} rows={3} placeholder="Обязанности и задачи дежурства..."></textarea>
            </div>

            <div class="form-group">
                <label for="notes">Заметки</label>
                <textarea id="notes" bind:value={dutyLogForm.notes} rows={2} placeholder="Дополнительные заметки..."></textarea>
            </div>

            <div class="form-group">
                <label for="report">Отчет</label>
                <textarea id="report" bind:value={dutyLogForm.report} rows={3} placeholder="Отчет о выполнении дежурства..."></textarea>
            </div>

            <div class="modal-actions">
                <button type="submit" class="save-btn">Сохранить</button>
                <button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
            </div>
        </form>
    </div>
{/if}

<style>
    .duty-log-admin {
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

    .duty-log-table {
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
        text-wrap: auto;
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

    .status-planned {
        background: rgb(255, 193, 7);
        color: rgb(204, 85, 0);
        padding: 0.25rem 0.5rem;
        border-radius: var(--radius);
        display: inline-block;
    }

    .status-completed {
        background: rgb(144, 238, 144);
        color: rgb(0, 100, 0);
        padding: 0.25rem 0.5rem;
        border-radius: var(--radius);
        display: inline-block;
    }

    .status-cancelled {
        background: rgb(255, 193, 193);
        color: rgb(178, 34, 34);
        padding: 0.25rem 0.5rem;
        border-radius: var(--radius);
        display: inline-block;
    }

    .status-in-progress {
        background: rgb(173, 216, 230);
        color: rgb(30, 144, 255);
        padding: 0.25rem 0.5rem;
        border-radius: var(--radius);
        display: inline-block;
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

    .large-modal {
        min-width: 800px;
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

    .form-group input, .form-group select, .form-group textarea {
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

    .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
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

    @media (max-width: 1024px) {
        .large-modal {
            min-width: 600px;
        }
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

        .modal, .large-modal {
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