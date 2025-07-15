import { writable } from 'svelte/store';

const getInitialTheme = () => {
	if (typeof localStorage !== 'undefined') {
		const saved = localStorage.getItem('theme');
		if (saved === 'dark' || saved === 'light') return saved;
	}
	return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
};

export const themeStore = writable<'light' | 'dark'>(getInitialTheme());

themeStore.subscribe((theme) => {
	if (typeof document !== 'undefined') {
		document.documentElement.setAttribute('data-theme', theme);
		localStorage.setItem('theme', theme);
	}
});

export function toggleTheme() {
	themeStore.update((t) => (t === 'light' ? 'dark' : 'light'));
} 