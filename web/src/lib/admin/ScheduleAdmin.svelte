<script lang="ts">
    import { onMount } from 'svelte';
    import { fade, fly } from 'svelte/transition';
    import { Loader, Plus, Trash2, Edit, AlertCircle, Search, ArrowUpDown, Calendar, Clock, MapPin, Users, BookOpen } from 'lucide-svelte';
    import { PUBLIC_API_URL } from '$env/static/public';
    import type { UserSession } from '$lib/stores/userStore';
    import { toast } from 'svelte-sonner';
    import type { Schedule, Employee, CampSession } from "$lib/models";
    import SearchBox from "$lib/components/SearchBox.svelte";

    export let user: UserSession;

    let schedules: Schedule[] = [];
    let filteredSchedules: Schedule[] = [];
    let loading = true;
    let error = '';
    let showModal = false;
    let editSchedule: Schedule | null = null;
    let searchQuery = '';
    let sortField: keyof Schedule = 'date';
    let sortDirection: 'asc' | 'desc' = 'desc';

    interface ScheduleForm {
        sessionId: number;
        employeeId: number;
        date: string;
        time: string;
        title: string;
        description: string;
        location: string;
        team: string;
    }

    let scheduleForm: ScheduleForm = {
        sessionId: 0,
        employeeId: 0,
        date: '',
        time: '09:00',
        title: '',
        description: '',
        location: '',
        team: ''
    };

    let employees: Employee[] = [];
    let sessions: CampSession[] = [];
    let loadingData = false;

    const teamOptions = ['Все группы', 'Младшая', 'Средняя', 'Старшая'];

    async function loadSchedules() {
        loading = true;
        error = '';
        try {
            const res = await fetch(`${PUBLIC_API_URL}/api/schedules`, {
                headers: { Authorization: `Bearer ${user.accessToken}` }
            });
            if (!res.ok) {
                error = 'Ошибка загрузки расписания';
            } else {
                schedules = await res.json();
                filterAndSortSchedules();
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

    function filterAndSortSchedules() {
        filteredSchedules = schedules.filter(schedule =>
            schedule.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
            schedule.employee.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
            schedule.session.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            schedule.location?.toLowerCase().includes(searchQuery.toLowerCase()) ||
            schedule.team?.toLowerCase().includes(searchQuery.toLowerCase()) ||
            schedule.date?.includes(searchQuery)
        );

        filteredSchedules.sort((a, b) => {
            let valueA = a[sortField];
            let valueB = b[sortField];

            if (typeof valueA === 'string') valueA = valueA.toLowerCase();
            if (typeof valueB === 'string') valueB = valueB.toLowerCase();

            if (valueA < valueB) return sortDirection === 'asc' ? -1 : 1;
            if (valueA > valueB) return sortDirection === 'asc' ? 1 : -1;
            return 0;
        });
    }

    function sortBy(field: keyof Schedule) {
        if (sortField === field) {
            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDirection = 'asc';
        }
        filterAndSortSchedules();
    }

    function openModal(schedule: Schedule | null = null) {
        showModal = true;
        editSchedule = schedule;
        if (schedule) {
            scheduleForm = {
                sessionId: schedule.session.id,
                employeeId: schedule.employee.id,
                date: schedule.date.split('T')[0],
                time: schedule.time,
                title: schedule.title,
                description: schedule.description || '',
                location: schedule.location,
                team: schedule.team
            };
        } else {
            scheduleForm = {
                sessionId: 0,
                employeeId: 0,
                date: new Date().toISOString().split('T')[0],
                time: '09:00',
                title: '',
                description: '',
                location: '',
                team: 'ALL'
            };
        }
    }

    function closeModal() {
        showModal = false;
        editSchedule = null;
    }

    async function saveSchedule() {
        const method = editSchedule ? 'PUT' : 'POST';
        const url = editSchedule ? `${PUBLIC_API_URL}/api/schedules/${editSchedule.id}` : `${PUBLIC_API_URL}/api/schedules`;

        const body = JSON.stringify(scheduleForm);

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
                await loadSchedules();
                closeModal();
                toast.success('Запись расписания успешно сохранена');
            } else {
                const errorText = await response.text();
                toast.error(`Ошибка: ${errorText}`);
            }
        } catch (e) {
            toast.error('Ошибка подключения к серверу');
        }
    }

    async function deleteSchedule(id: number) {
        if (!confirm('Удалить запись из расписания?')) return;

        try {
            const response = await fetch(`${PUBLIC_API_URL}/api/schedules/${id}`, {
                method: 'DELETE',
                headers: { Authorization: `Bearer ${user.accessToken}` }
            });

            if (response.ok) {
                await loadSchedules();
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
        filterAndSortSchedules();
    }

    onMount(async () => {
        loadingData = true;
        await Promise.all([loadSchedules(), loadEmployees(), loadSessions()]);
        loadingData = false;
    });
</script>

<div class="schedule-admin">
    <div class="header">
        <h2>
            <Calendar size={24} />
            <span>Управление расписанием</span>
        </h2>
        <button class="add-btn" on:click={() => openModal()}>
            <Plus size={18} />
            <span>Добавить мероприятие</span>
        </button>
    </div>

    <SearchBox
        bind:value={searchQuery}
        placeholder="Поиск по названию, сотруднику, смене, локации или отряду..."
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
        <div class="schedule-table">
            <table>
                <thead>
                    <tr>
                        <th on:click={() => sortBy('date')} class:active={sortField==='date'}>
                            <span>Дата <ArrowUpDown size={14}/></span>
                        </th>
                        <th on:click={() => sortBy('time')} class:active={sortField==='time'}>
                            <span>Время <ArrowUpDown size={14}/></span>
                        </th>
                        <th on:click={() => sortBy('title')} class:active={sortField==='title'}>
                            <span>Мероприятие <ArrowUpDown size={14}/></span>
                        </th>
                        <th>Ответственный</th>
                        <th>Смена</th>
                        <th on:click={() => sortBy('location')} class:active={sortField==='location'}>
                            <span>Локация <ArrowUpDown size={14}/></span>
                        </th>
                        <th on:click={() => sortBy('team')} class:active={sortField==='team'}>
                            <span>Группа <ArrowUpDown size={14}/></span>
                        </th>
                        <th>Действия</th>
                    </tr>
                </thead>
                <tbody>
                    {#each filteredSchedules as schedule}
                        <tr>
                            <td>{new Date(schedule.date).toLocaleDateString('ru-RU')}</td>
                            <td>{schedule.time}</td>
                            <td>
                                <div class="schedule-title">{schedule.title}</div>
                                {#if schedule.description}
                                    <div class="schedule-description" title={schedule.description}>
                                        {schedule.description.length > 50 ? schedule.description.substring(0, 50) + '...' : schedule.description}
                                    </div>
                                {/if}
                            </td>
                            <td>
                                {#if schedule.employee.fullName}
                                    <div>{schedule.employee.fullName}</div>
                                    {#if schedule.employee.position}
                                        <small>{schedule.employee.position}</small>
                                    {/if}
                                {:else}
                                    <span class="text-muted">Не назначен</span>
                                {/if}
                            </td>
                            <td>{schedule.session.name || '-'}</td>
                            <td>{schedule.location}</td>
                            <td>{schedule.team}</td>
                            <td>
                                <button class="icon-btn edit" title="Редактировать" on:click={() => openModal(schedule)}>
                                    <Edit size={16} />
                                </button>
                                <button class="icon-btn delete" title="Удалить" on:click={() => deleteSchedule(schedule.id)}>
                                    <Trash2 size={16} />
                                </button>
                            </td>
                        </tr>
                    {/each}
                </tbody>
            </table>

            {#if filteredSchedules.length === 0 && schedules.length > 0}
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
    <div class="modal large-modal" in:fly={{ y: 30 }}>
        <h3>{editSchedule ? 'Редактировать мероприятие' : 'Добавить мероприятие'}</h3>
        <form on:submit|preventDefault={saveSchedule}>
            <div class="form-row">
                <div class="form-group">
                    <label for="date">Дата</label>
                    <input id="date" type="date" bind:value={scheduleForm.date} required />
                </div>

                <div class="form-group">
                    <label for="time">Время</label>
                    <input id="time" type="time" bind:value={scheduleForm.time} required />
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="sessionId">Смена</label>
                    <select id="sessionId" bind:value={scheduleForm.sessionId} required>
                        <option value="" disabled>Выберите смену</option>
                        {#each sessions as session}
                            <option value={session.id}>{session.name}</option>
                        {/each}
                    </select>
                </div>

                <div class="form-group">
                    <label for="employeeId">Ответственный</label>
                    <select id="employeeId" bind:value={scheduleForm.employeeId}>
                        <option value="" disabled>Выберите ответственного</option>
                        {#each employees as emp}
                            <option value={emp.id}>{emp.fullName} - {emp.position}</option>
                        {/each}
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label for="title">Название мероприятия</label>
                <input id="title" bind:value={scheduleForm.title} required placeholder="Название мероприятия..." />
            </div>

            <div class="form-group">
                <label for="location">Локация</label>
                <input id="location" bind:value={scheduleForm.location} required placeholder="Место проведения..." />
            </div>

            <div class="form-group">
                <label for="team">Группа</label>
                <select id="team" bind:value={scheduleForm.team} required>
                    {#each teamOptions as option}
                        <option value={option}>{option}</option>
                    {/each}
                </select>
            </div>

            <div class="form-group">
                <label for="description">Описание мероприятия</label>
                <textarea
                    id="description"
                    bind:value={scheduleForm.description}
                    rows={4}
                    placeholder="Подробное описание мероприятия, программа, цели..."
                ></textarea>
            </div>

            <div class="modal-actions">
                <button type="submit" class="save-btn">Сохранить</button>
                <button type="button" class="cancel-btn" on:click={closeModal}>Отмена</button>
            </div>
        </form>
    </div>
{/if}

<style>
    .schedule-admin {
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

    .schedule-table {
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

    .schedule-title {
        font-weight: 500;
        margin-bottom: 0.25rem;
    }

    .schedule-description {
        font-size: 0.8rem;
        color: var(--text-secondary);
        line-height: 1.3;
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