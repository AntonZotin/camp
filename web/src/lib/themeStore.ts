import { writable } from 'svelte/store';

const isBrowser = typeof window !== 'undefined' && typeof localStorage !== 'undefined';

const getInitialTheme = () => {
	if (isBrowser) {
		const saved = localStorage.getItem('theme');
		if (saved === 'dark' || saved === 'light') return saved;
		return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
	}
	return 'light'; // SSR fallback
};

export const themeStore = writable<'light' | 'dark'>(getInitialTheme());

if (isBrowser) {
	themeStore.subscribe((theme) => {
		document.documentElement.setAttribute('data-theme', theme);
		localStorage.setItem('theme', theme);
	});
}

export function toggleTheme() {
	themeStore.update((t) => (t === 'light' ? 'dark' : 'light'));
} 