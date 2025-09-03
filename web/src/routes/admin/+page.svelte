<script lang="ts">
    import {fade, fly} from 'svelte/transition';
    import {userStore} from '$lib/stores/userStore';
    import {onMount} from 'svelte';
    import {goto} from '$app/navigation';
    import {get} from 'svelte/store';
    import {
        Shield,
        Users,
        Baby,
        Calendar,
        Activity,
        Settings,
        Menu,
        Bell,
        CreditCard,
        FileText,
        UserCheck,
        Download, ClipboardCheck, TicketCheck
    } from 'lucide-svelte';
    import MenuAdmin from '$lib/admin/MenuAdmin.svelte';
    import EmployeeAdmin from '$lib/admin/EmployeeAdmin.svelte';
    import MedicalCardAdmin from '$lib/admin/MedicalCardAdmin.svelte';
    import MedicalVisitAdmin from '$lib/admin/MedicalVisitAdmin.svelte';
    import NotificationAdmin from '$lib/admin/NotificationAdmin.svelte';
    import PaymentAdmin from '$lib/admin/PaymentAdmin.svelte';
    import ActivityLogAdmin from '$lib/admin/ActivityLogAdmin.svelte';
    import AdminExport from '$lib/admin/AdminExport.svelte';
    import UsersAdmin from '$lib/admin/UsersAdmin.svelte';
    import SessionsAdmin from '$lib/admin/CampSessionsAdmin.svelte';
    import ChildAdmin from "$lib/admin/ChildAdmin.svelte";
    import DutyLogAdmin from "$lib/admin/DutyLogAdmin.svelte";
    import ScheduleAdmin from "$lib/admin/ScheduleAdmin.svelte";
    import VoucherAdmin from "$lib/admin/VoucherAdmin.svelte";

    let user = get(userStore);
    const unsubUser = userStore.subscribe((u) => (user = u));

    let tab:
        'users' | 'children' | 'employees' | 'sessions' | 'menu' | 'medical-cards' | 'medical-visits'
        | 'notifications' | 'payments' | 'vouchers'
        | 'schedules' | 'duty-logs' | 'activity-logs' | 'export' = 'users';

    onMount(() => {
        if (!user || user.role !== 'ADMIN') goto('/login');
        return () => {
            unsubUser();
        };
    });
</script>

<div class="stars-bg"></div>

<section class="admin-section" transition:fade>
    <div class="container">
        <div class="admin-container" in:fly={{ y: 50 }}>
            {#if user}
                <div class="admin-header" in:fly={{ y: 30, delay: 200 }}>
                    <div class="header-content">
                        <h1>
                            <Shield size={32}/>
                            <span>Панель администратора</span>
                        </h1>
                        <p>Управление системой лагеря, пользователями и контентом</p>
                    </div>
                </div>

                <div class="tabs" in:fly={{ y: 30, delay: 400 }}>
                    <button class:active={tab==='users'} on:click={() => tab='users'}>
                        <Users size={18}/>
                        <span>Пользователи</span>
                    </button>
					<button class:active={tab==='children'} on:click={() => tab='children'}>
						<Baby size={18} />
						<span>Дети</span>
					</button>
                    <button class:active={tab==='employees'} on:click={() => tab='employees'}>
                        <Users size={18}/>
                        <span>Сотрудники</span>
                    </button>
                    <button class:active={tab==='sessions'} on:click={() => tab='sessions'}>
                        <Calendar size={18}/>
                        <span>Смены</span>
                    </button>
                    <button class:active={tab==='menu'} on:click={() => tab='menu'}>
                        <Menu size={18}/>
                        <span>Меню</span>
                    </button>
                    <button class:active={tab==='medical-cards'} on:click={() => tab='medical-cards'}>
                        <Shield size={18}/>
                        <span>Медкарты</span>
                    </button>
                    <button class:active={tab==='medical-visits'} on:click={() => tab='medical-visits'}>
                        <Activity size={18}/>
                        <span>Медосмотры</span>
                    </button>
                    <button class:active={tab==='notifications'} on:click={() => tab='notifications'}>
                        <Bell size={18}/>
                        <span>Уведомления</span>
                    </button>
                    <button class:active={tab==='payments'} on:click={() => tab='payments'}>
                        <CreditCard size={18}/>
                        <span>Оплаты</span>
                    </button>
					<button class:active={tab==='vouchers'} on:click={() => tab='vouchers'}>
						<TicketCheck size={18} />
						<span>Путевки</span>
					</button>
					<button class:active={tab==='schedules'} on:click={() => tab='schedules'}>
						<ClipboardCheck size={18} />
						<span>Расписание</span>
					</button>
					<button class:active={tab==='duty-logs'} on:click={() => tab='duty-logs'}>
						<Calendar size={18} />
						<span>Журнал дежурств</span>
					</button>
                    <button class:active={tab==='activity-logs'} on:click={() => tab='activity-logs'}>
                        <Activity size={18}/>
                        <span>Журнал активности</span>
                    </button>
                    <button class:active={tab==='export'} on:click={() => tab='export'}>
                        <Download size={18}/>
                        <span>Экспорт</span>
                    </button>
                </div>

                <div class="tab-content" in:fly={{ y: 30, delay: 600 }}>
                    {#if tab === 'users'}
                        <UsersAdmin {user}/>
					{:else if tab === 'children'}
						<ChildAdmin {user} />
                    {:else if tab === 'employees'}
                        <EmployeeAdmin {user}/>
                    {:else if tab === 'sessions'}
                        <SessionsAdmin {user}/>
                    {:else if tab === 'menu'}
                        <MenuAdmin {user}/>
                    {:else if tab === 'medical-cards'}
                        <MedicalCardAdmin {user}/>
                    {:else if tab === 'medical-visits'}
                        <MedicalVisitAdmin {user}/>
                    {:else if tab === 'notifications'}
                        <NotificationAdmin {user}/>
                    {:else if tab === 'payments'}
                        <PaymentAdmin {user}/>
					{:else if tab === 'vouchers'}
						<VoucherAdmin {user} />
					{:else if tab === 'schedules'}
						<ScheduleAdmin {user} />
					{:else if tab === 'duty-logs'}
						<DutyLogAdmin {user} />
                    {:else if tab === 'activity-logs'}
                        <ActivityLogAdmin {user}/>
                    {:else if tab === 'export'}
                        <AdminExport {user}/>
                    {/if}
                </div>
            {/if}
        </div>
    </div>
</section>

<style>
    .stars-bg {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: url("/images/star.png");
        z-index: -1;
        opacity: 0.3;
    }

    .admin-section {
        min-height: 100vh;
        display: flex;
        padding: 2rem 0;
    }

    .container {
        width: 100%;
        max-width: 1400px;
        margin: 0 auto;
        padding: 0 1rem;
    }

    .admin-container {
        background: var(--bg-primary);
        border-radius: var(--radius);
        overflow: hidden;
        box-shadow: var(--shadow);
        padding: 2rem;
    }

    .admin-header {
        margin-bottom: 2rem;
    }

    .header-content h1 {
        display: flex;
        align-items: center;
        gap: 1rem;
        font-size: 2.5rem;
        color: var(--primary);
        margin: 0 0 0.5rem 0;
    }

    .header-content p {
        margin: 0;
        color: var(--text-secondary);
        font-size: 1.1rem;
    }

    .tabs {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
        margin-bottom: 2rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid var(--border);
    }

    .tabs button {
        background: var(--bg-secondary);
        border: 1px solid var(--border);
        border-radius: var(--radius);
        padding: 0.75rem 1rem;
        font-size: 0.9rem;
        font-weight: 500;
        color: var(--text-secondary);
        cursor: pointer;
        transition: var(--transition);
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .tabs button:hover {
        background: var(--bg-hover);
        color: var(--text-primary);
    }

    .tabs button.active {
        background: var(--primary);
        color: white;
        border-color: var(--primary);
    }

    .tab-content {
        flex: 1;
    }

    @media (max-width: 1024px) {
        .admin-container {
            padding: 1.5rem;
        }

        .tabs {
            justify-content: center;
        }
    }

    @media (max-width: 768px) {
        .admin-section {
            padding: 1rem 0;
        }

        .header-content h1 {
            font-size: 2rem;
        }

        .tabs {
            flex-direction: column;
        }

        .tabs button {
            justify-content: center;
        }
    }
</style>