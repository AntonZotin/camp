import { writable } from 'svelte/store';

export type UserRole = 'ADMIN' | 'PARENT' | 'EMPLOYEE' | 'USER';
export interface UserSession {
  accessToken: string;
  userId: number;
  role: UserRole;
}

function getInitialUser(): UserSession | null {
  if (typeof localStorage !== 'undefined') {
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

if (typeof localStorage !== 'undefined') {
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
}

export function logoutUser() {
  userStore.set(null);
} 