import { writable } from 'svelte/store';
import { goto } from '$app/navigation';
import { toast } from 'svelte-sonner';

export type UserRole = 'ADMIN' | 'PARENT' | 'EMPLOYEE' | 'USER';

export interface UserSession {
    accessToken: string;
    userId: number;
    role: UserRole;
}

function getInitialUser(): UserSession | null {
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
        const raw = localStorage.getItem('user');
        if (raw) {
            try {
                return JSON.parse(raw);
            } catch {}
        }
    }
    return null;
}

export const userStore = writable<UserSession | null>(getInitialUser());

function initializeAuthInterceptor() {
    if (typeof window === 'undefined') return;

    const originalFetch = window.fetch;

    window.fetch = async function(input: RequestInfo | URL, init?: RequestInit): Promise<Response> {
        const response = await originalFetch(input, init);

        if (response.status === 401) {
            logoutUser('Ваша сессия истекла. Пожалуйста, войдите снова.');
            throw new Error('Session expired');
        }

        return response;
    };
}

if (typeof window !== 'undefined') {
    initializeAuthInterceptor();

    userStore.subscribe((user) => {
        if (user) {
            localStorage.setItem('user', JSON.stringify(user));
        } else {
            localStorage.removeItem('user');
        }
    });
}

export function loginUser(user: UserSession) {
    userStore.set(user);
    toast.success('Вы успешно вошли в систему');
}

export function logoutUser(message?: string) {
    userStore.set(null);
    if (message) {
        toast.error(message);
    }
    goto('/login');
}