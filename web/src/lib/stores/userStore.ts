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
            } catch {
                localStorage.removeItem('user');
            }
        }
    }
    return null;
}

export const userStore = writable<UserSession | null>(getInitialUser());

function initializeAuthInterceptor() {
    if (typeof window === 'undefined') return;

    const { fetch: originalFetch } = window;

    window.fetch = async function(input: RequestInfo | URL, init?: RequestInit): Promise<Response> {
        const user = getInitialUser();
        const authInit = init || {};

        if (user?.accessToken) {
            authInit.headers = {
                ...authInit.headers,
                'Authorization': `Bearer ${user.accessToken}`
            };
        }

        try {
            const response = await originalFetch(input, authInit);
            if (response.status === 401) {
                throw new Error('Логин или пароль введены не верно');
            }
            return response;
        } catch (error) {
            if (error instanceof TypeError && error.message === 'Failed to fetch') {
                await logoutUser('Ваша сессия истекла. Пожалуйста, войдите снова.');
            }
            throw error;
        }
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

export async function logoutUser(message?: string) {
    userStore.set(null);
    if (message) {
        toast.error(message);
    }
    await goto('/login');
}