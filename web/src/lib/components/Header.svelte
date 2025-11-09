<script lang="ts">
  import ThemeToggle from './ThemeToggle.svelte';
  import { Home, Info, Calendar, Phone, LogIn, User, LogOut, Bell, X } from 'lucide-svelte';
  import {userStore, logoutUser, type UserSession} from '$lib/stores/userStore';
  import { goto } from '$app/navigation';
  import { get } from 'svelte/store';
  import { onMount } from 'svelte';
  import type { Notification as NotificationType } from '$lib/models';
  import {PUBLIC_API_URL} from "$env/static/public";
  import {toast} from "svelte-sonner";

  export let currentTheme: string;
  export let toggleTheme: () => void;

  let user: UserSession | null = get(userStore);
  const unsubUser = userStore.subscribe((u) => (user = u));

  let notifications: NotificationType[] = [];
  let unreadCount: number = 0;
  let showNotifications: boolean = false;
  let loading: boolean = false;

  async function handleLogout(): Promise<void> {
    await logoutUser();
    await goto('/');
  }

  interface NavLink {
    name: string;
    href: string;
    icon: any;
  }

  const navLinks: NavLink[] = [
    { name: 'Главная', href: '/', icon: Home },
    { name: 'О лагере', href: '/about', icon: Info },
    { name: 'Программы', href: '/programs', icon: Calendar },
    { name: 'Контакты', href: '/contacts', icon: Phone }
  ];

  async function loadNotifications(): Promise<void> {
    if (!user) return;

    try {
      loading = true;
      const res = await fetch(`${PUBLIC_API_URL}/api/notifications/user/${user.userId}`, {
          headers: {Authorization: `Bearer ${user.accessToken}`}
      });
      if (!res.ok) {
          toast.error('Ошибка загрузки уведомлений');
      } else {
        const allNotifications: NotificationType[] = await res.json();
        notifications = allNotifications.filter(notification =>
          notification.recipient && notification.recipient.id === user!.userId && notification.type === 'internal'
        );
        calculateUnreadCount();
      }
    } catch (error) {
      console.error('Ошибка загрузки уведомлений:', error);
    } finally {
      loading = false;
    }
  }

  function calculateUnreadCount(): void {
    unreadCount = notifications.filter(n => n.status === 'pending').length;
  }

  async function toggleNotifications(): Promise<void> {
    if (!showNotifications) {
      await loadNotifications();
    }
    showNotifications = !showNotifications;
  }

  async function markAsRead(notification: NotificationType): Promise<void> {
    if (notification.status === 'pending') {
      try {
        const updatedNotification: NotificationType = {
          ...notification,
          status: 'sent',
          sentAt: new Date().toISOString()
        };

        const res = await fetch(`${PUBLIC_API_URL}/api/notifications/${notification.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${user?.accessToken}`
          },
          body: JSON.stringify(updatedNotification)
        });
        if (!res.ok) {
          toast.error('Ошибка обновления уведомления');
        } else {
          notifications = notifications.map(n =>
            n.id === notification.id
              ? { ...n, status: 'sent', sentAt: updatedNotification.sentAt }
              : n
          );
          calculateUnreadCount();
        }
      } catch (error) {
        console.error('Ошибка обновления уведомления:', error);
      }
    }
  }

  function closeNotifications(): void {
    showNotifications = false;
  }

  function formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU', {
      day: 'numeric',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  onMount(() => {
    if (user) {
      loadNotifications();
      return () => { unsubUser(); };
    }
  });
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
      {#if user}
        <button class="button icon-button notification-btn" on:click={toggleNotifications} title="Уведомления">
          <span class="icon">
            <Bell size={18} />
          </span>
          {#if unreadCount > 0}
            <div class="notification-badge">
              {unreadCount}
            </div>
          {/if}
        </button>

        <a href="/cabinet" class="button primary">
          <div class="icon">
            <User size={18} />
          </div>
          <span>Кабинет</span>
        </a>
        <button class="button secondary logout-btn" on:click={handleLogout}>
          <span class="icon">
            <LogOut size={18} />
          </span>
          <span>Выйти</span>
        </button>
      {:else}
        <a href="/login" class="button primary">
          <div class="icon">
            <LogIn size={18} />
          </div>
          <span>Войти</span>
        </a>
      {/if}
    </div>
  </div>

  {#if showNotifications && user}
	<!-- svelte-ignore a11y_click_events_have_key_events, a11y_no_static_element_interactions -->
    <div class="modal-overlay" on:click={closeNotifications}>
      <div class="modal-content" on:click|stopPropagation>
        <div class="modal-header">
          <h3>Уведомления</h3>
          <button class="close-btn" on:click={closeNotifications}>
            <X size={20} />
          </button>
        </div>

        <div class="notifications-list">
          {#if loading}
            <div class="loading">Загрузка уведомлений...</div>
          {:else if notifications.length === 0}
            <div class="empty-state">
              <span>
                <Bell size={32} />
              </span>
              <p>У вас пока нет уведомлений</p>
            </div>
          {:else}
            {#each notifications as notification (notification.id)}
              <div
                class="notification-item {notification.status === 'pending' ? 'unread' : ''}"
                on:click={() => markAsRead(notification)}
              >
                <div class="notification-header">
                  <h4 class="notification-subject">{notification.subject}</h4>
                  {#if notification.status === 'pending'}
                    <div class="unread-indicator"></div>
                  {/if}
                </div>
                <p class="notification-message">{notification.message}</p>
                <div class="notification-footer">
                  <span class="notification-type {notification.type}">
                    {notification.type === 'email' ? 'Email' : 'Внутреннее'}
                  </span>
                  <span class="notification-date">
                    {formatDate(notification.createdAt)}
                  </span>
                  <span class="notification-status {notification.status}">
                    {notification.status === 'pending' ? 'Новое' :
                     notification.status === 'sent' ? 'Прочитано' :
                     notification.status === 'failed' ? 'Ошибка' : notification.status}
                  </span>
                </div>
              </div>
            {/each}
          {/if}
        </div>
      </div>
    </div>
  {/if}
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

  .button {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.5rem;
    border-radius: var(--radius);
    font-weight: 500;
    text-decoration: none;
    transition: var(--transition);
    border: none;
    cursor: pointer;
    font-size: 0.9rem;
  }

  .button.primary {
    background: var(--primary);
    color: white;
  }

  .button.primary:hover {
    background: var(--primary-dark);
    transform: translateY(-2px);
  }

  .button.secondary {
    background: transparent;
    color: var(--text-primary);
    border: 1px solid var(--border);
  }

  .button.secondary:hover {
    background: var(--bg-hover);
    transform: translateY(-2px);
  }

  .logout-btn {
    color: var(--error);
    border-color: var(--error);
  }

  .logout-btn:hover {
    background: var(--error);
    color: white;
  }

  .nav-text {
    position: relative;
    transition: var(--transition);
  }

  .icon-button {
    position: relative;
    padding: 0.5rem;
    background: transparent;
    border: none;
    border-radius: 50%;
    color: var(--text-primary);
  }

  .icon-button:hover {
    background: var(--bg-hover);
    transform: translateY(-2px);
  }

  .notification-badge {
    position: absolute;
    top: -5px;
    right: -5px;
    background: var(--error);
    color: white;
    border-radius: 50%;
    width: 18px;
    height: 18px;
    font-size: 0.7rem;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
    animation: pulse 2s infinite;
  }

  @keyframes pulse {
    0% { transform: scale(1); }
    50% { transform: scale(1.1); }
    100% { transform: scale(1); }
  }

  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }

  .modal-content {
    background: var(--bg-primary);
    border-radius: var(--radius);
    box-shadow: var(--shadow);
    width: 90%;
    max-width: 500px;
    max-height: 80vh;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }

  .modal-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 1.5rem;
    border-bottom: 1px solid var(--border);
  }

  .modal-header h3 {
    margin: 0;
    font-size: 1.25rem;
    color: var(--text-primary);
  }

  .close-btn {
    background: none;
    border: none;
    color: var(--text-secondary);
    cursor: pointer;
    padding: 0.25rem;
    border-radius: var(--radius);
    transition: var(--transition);
  }

  .close-btn:hover {
    background: var(--bg-hover);
    color: var(--text-primary);
  }

  .notifications-list {
    flex: 1;
    overflow-y: auto;
    padding: 0.5rem;
  }

  .loading, .empty-state {
    text-align: center;
    padding: 2rem;
    color: var(--text-secondary);
  }

  .empty-state span {
    margin-bottom: 1rem;
    opacity: 0.5;
  }

  .notification-item {
    padding: 1rem;
    border-radius: var(--radius);
    margin-bottom: 0.5rem;
    cursor: pointer;
    transition: var(--transition);
    border: 1px solid var(--border);
  }

  .notification-item:hover {
    background: var(--bg-hover);
    transform: translateY(-1px);
  }

  .notification-item.unread {
    background: var(--primary-light);
    border-color: var(--primary);
  }

  .notification-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 0.5rem;
  }

  .notification-subject {
    margin: 0;
    font-size: 1rem;
    font-weight: 600;
    color: var(--text-primary);
  }

  .unread-indicator {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: var(--primary);
  }

  .notification-message {
    margin: 0 0 0.75rem 0;
    color: var(--text-secondary);
    font-size: 0.9rem;
    line-height: 1.4;
  }

  .notification-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 0.8rem;
    color: var(--text-secondary);
  }

  .notification-type {
    padding: 0.2rem 0.5rem;
    border-radius: 12px;
    font-size: 0.7rem;
    font-weight: 500;
  }

  .notification-type.email {
    background: var(--secondary);
    color: white;
  }

  .notification-type.internal {
    background: var(--primary);
    color: white;
  }

  .notification-status {
    font-weight: 500;
  }

  .notification-status.pending {
    color: var(--primary);
  }

  .notification-status.sent {
    color: var(--success);
  }

  .notification-status.failed {
    color: var(--error);
  }

  @media (max-width: 768px) {
    nav {
      display: none;
    }

    .actions {
      gap: 0.5rem;
    }

    .button {
      padding: 0.5rem 1rem;
      font-size: 0.8rem;
    }

    .button span {
      display: none;
    }

    .modal-content {
      width: 95%;
      margin: 1rem;
    }

    .notification-footer {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.25rem;
    }
  }
</style>