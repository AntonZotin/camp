<script lang="ts">
	import { themeStore, toggleTheme } from '$lib/themeStore';
	import { Sun, Moon, Users, HeartHandshake, CalendarCheck, ShieldCheck, MessageCircle, MapPin, Phone, Mail, Image, Star } from 'lucide-svelte';
	import { onMount } from 'svelte';
	import { get } from 'svelte/store';

	let theme: 'light' | 'dark' = 'light';
	const unsubscribe = themeStore.subscribe((t) => (theme = t));
	
	onMount(() => () => unsubscribe());
</script>

<div class="landing" data-theme={theme}>
	<header>
		<div class="logo"><Users size={32}/> CampIT</div>
		<nav>
			<button class="theme-toggle" on:click={toggleTheme} aria-label="Переключить тему">
				{#if theme === 'dark'}
					<Moon size={24}/>
				{:else}
					<Sun size={24}/>
				{/if}
			</button>
			<a href="/login" class="login-btn">Войти</a>
			<a href="/register" class="register-btn">Регистрация</a>
		</nav>
	</header>
	<main>
		<section class="hero">
			<h1>CampIT — <span>инновационный</span> лагерь</h1>
			<p>Ваша уверенность в безопасности, развитии и комфорте ребёнка летом</p>
			<div class="cta">
				<a href="#about" class="primary">Узнать больше</a>
				<a href="/register" class="secondary">Забронировать путёвку</a>
			</div>
		</section>

		<section id="about" class="about fade-in">
			<h2><HeartHandshake size={28}/> О лагере</h2>
			<p>CampIT — это современная платформа для управления детским лагерем: онлайн-бронирование, медицинский учёт, расписание, питание, уведомления и многое другое.</p>
			<ul class="features">
				<li><ShieldCheck size={22}/> Безопасность и медицинский контроль</li>
				<li><CalendarCheck size={22}/> Удобное расписание и мероприятия</li>
				<li><MessageCircle size={22}/> Электронные уведомления для родителей</li>
				<li><Users size={22}/> Профессиональный персонал и забота</li>
			</ul>
		</section>

		<section class="benefits fade-in">
			<h2>Преимущества</h2>
			<div class="cards">
				<div class="card"><Image size={32}/><span>Фотогалерея смен</span><p>Смотрите, как проходят смены — фотоотчёты и лучшие моменты!</p></div>
				<div class="card"><Star size={32}/><span>Отзывы родителей</span><p>Реальные истории и впечатления от наших гостей.</p></div>
				<div class="card"><HeartHandshake size={32}/><span>Забота о каждом</span><p>Индивидуальный подход и поддержка для каждого ребёнка.</p></div>
				<div class="card"><CalendarCheck size={32}/><span>Гибкое бронирование</span><p>Онлайн-оплата, выбор смены и быстрая регистрация.</p></div>
			</div>
		</section>

		<section class="gallery fade-in">
			<h2>Галерея</h2>
			<div class="gallery-grid">
				<div class="img-placeholder"></div>
				<div class="img-placeholder"></div>
				<div class="img-placeholder"></div>
				<div class="img-placeholder"></div>
			</div>
		</section>

		<section class="reviews fade-in">
			<h2>Отзывы</h2>
			<div class="review-cards">
				<div class="review">
					<Star size={20}/><Star size={20}/><Star size={20}/><Star size={20}/><Star size={20}/>
					<p>"Лучший лагерь! Дочка в восторге, всё удобно и безопасно."</p>
					<span>— Анна, мама</span>
				</div>
				<div class="review">
					<Star size={20}/><Star size={20}/><Star size={20}/><Star size={20}/><Star size={20}/>
					<p>"Очень понравился персонал и организация. Спасибо!"</p>
					<span>— Олег, папа</span>
				</div>
			</div>
		</section>

		<section class="faq fade-in">
			<h2>FAQ</h2>
			<ul class="faq-list">
				<li><b>Как забронировать путёвку?</b> — Просто зарегистрируйтесь и выберите смену онлайн.</li>
				<li><b>Есть ли медицинское сопровождение?</b> — Да, в лагере всегда дежурит врач.</li>
				<li><b>Как узнать расписание?</b> — В личном кабинете доступно актуальное расписание и меню.</li>
			</ul>
		</section>

		<section class="contacts fade-in">
			<h2>Контакты</h2>
			<div class="contact-info">
				<div><MapPin size={20}/> г. Казань, ул. Примерная, 1</div>
				<div><Phone size={20}/> +7 (900) 123-45-67</div>
				<div><Mail size={20}/> info@campit.ru</div>
			</div>
		</section>
	</main>
	<footer>
		&copy; {new Date().getFullYear()} CampIT. Все права защищены.
	</footer>
</div>

<style>
.landing {
	display: flex;
	flex-direction: column;
	min-height: 100vh;
	background: var(--color-bg);
	color: var(--color-text);
	transition: background 0.3s, color 0.3s;
}
.landing[data-theme="dark"] {
	--color-bg: #181c24;
	--color-text: #f1f5f9;
	--color-primary: #2d8cff;
	--color-secondary: #23272f;
	--color-accent: #ffb347;
	--color-header: #23272f;
	--color-footer: #181c24;
}
.landing[data-theme="light"] {
	--color-bg: #f8fafc;
	--color-text: #222;
	--color-primary: #2d8cff;
	--color-secondary: #fff;
	--color-accent: #ffb347;
	--color-header: #fff;
	--color-footer: #f1f5f9;
}
header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	background: var(--color-header);
	box-shadow: 0 4px 24px rgba(0,0,0,0.07);
	padding: 1.2rem 2.5rem;
	position: sticky;
	top: 0;
	z-index: 10;
}
.logo {
	display: flex;
	align-items: center;
	gap: 0.5rem;
	font-size: 2rem;
	font-weight: bold;
	color: var(--color-primary);
	letter-spacing: 2px;
}
nav {
	display: flex;
	align-items: center;
	gap: 1.2rem;
}
.theme-toggle {
	background: none;
	border: none;
	font-size: 1.5rem;
	cursor: pointer;
	transition: color 0.2s;
	color: var(--color-primary);
	display: flex;
	align-items: center;
}
.login-btn, .register-btn {
	padding: 0.5rem 1.2rem;
	border-radius: 8px;
	text-decoration: none;
	font-weight: 500;
	transition: background 0.2s, color 0.2s, box-shadow 0.2s;
	box-shadow: 0 2px 8px rgba(45,140,255,0.07);
}
.login-btn {
	background: var(--color-secondary);
	color: var(--color-primary);
	border: 1.5px solid var(--color-primary);
}
.login-btn:hover {
	background: var(--color-primary);
	color: #fff;
}
.register-btn {
	background: linear-gradient(90deg, var(--color-primary), var(--color-accent));
	color: #fff;
	margin-left: 0.5rem;
	border: none;
}
.register-btn:hover {
	background: linear-gradient(90deg, var(--color-accent), var(--color-primary));
	color: #222;
}
main {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 2rem 1rem 3rem 1rem;
}
.hero {
	text-align: center;
	margin-top: 3rem;
	animation: fadeIn 1.2s;
}
.hero h1 {
	font-size: 2.8rem;
	font-weight: 800;
	margin-bottom: 1rem;
	color: var(--color-primary);
	letter-spacing: 1px;
}
.hero h1 span {
	color: var(--color-accent);
}
.hero p {
	font-size: 1.3rem;
	margin-bottom: 2rem;
	color: var(--color-text);
}
.cta {
	display: flex;
	justify-content: center;
	gap: 1.2rem;
	margin-bottom: 1.5rem;
}
.cta .primary, .cta .secondary {
	padding: 0.9rem 2.2rem;
	border-radius: 12px;
	font-size: 1.1rem;
	font-weight: 600;
	text-decoration: none;
	transition: background 0.2s, color 0.2s, box-shadow 0.2s;
	box-shadow: 0 4px 16px rgba(45,140,255,0.09);
}
.cta .primary {
	background: linear-gradient(90deg, var(--color-primary), var(--color-accent));
	color: #fff;
	border: none;
}
.cta .primary:hover {
	background: linear-gradient(90deg, var(--color-accent), var(--color-primary));
	color: #222;
}
.cta .secondary {
	background: var(--color-secondary);
	color: var(--color-primary);
	border: 1.5px solid var(--color-primary);
}
.cta .secondary:hover {
	background: var(--color-primary);
	color: #fff;
}
.about {
	margin-top: 4rem;
	max-width: 650px;
	background: var(--color-secondary);
	border-radius: 20px;
	box-shadow: 0 6px 32px rgba(45,140,255,0.08);
	padding: 2.5rem 2rem;
	animation: fadeInUp 1.2s;
}
.about h2 {
	display: flex;
	align-items: center;
	gap: 0.5rem;
	font-size: 2rem;
	margin-bottom: 1rem;
	color: var(--color-primary);
}
.about p {
	font-size: 1.1rem;
	margin-bottom: 1.5rem;
}
.features {
	list-style: none;
	padding: 0;
	margin: 0;
	display: flex;
	flex-direction: column;
	gap: 0.7rem;
}
.features li {
	display: flex;
	align-items: center;
	gap: 0.5rem;
	font-size: 1.05rem;
	color: var(--color-text);
	background: none;
}
.benefits {
	margin-top: 3.5rem;
	max-width: 900px;
	width: 100%;
}
.benefits h2 {
	font-size: 1.7rem;
	margin-bottom: 1.2rem;
	color: var(--color-primary);
}
.cards {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
	gap: 1.5rem;
}
.card {
	background: var(--color-secondary);
	border-radius: 18px;
	box-shadow: 0 4px 24px rgba(45,140,255,0.10);
	padding: 1.5rem 1.2rem;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 0.7rem;
	transition: transform 0.18s, box-shadow 0.18s;
	font-size: 1.08rem;
}
.card:hover {
	transform: translateY(-6px) scale(1.03);
	box-shadow: 0 8px 32px rgba(45,140,255,0.16);
}
.card span {
	font-weight: 600;
	margin-bottom: 0.2rem;
	color: var(--color-primary);
}
.gallery {
	margin-top: 3.5rem;
	max-width: 900px;
	width: 100%;
}
.gallery h2 {
	font-size: 1.7rem;
	margin-bottom: 1.2rem;
	color: var(--color-primary);
}
.gallery-grid {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
	gap: 1.2rem;
}
.img-placeholder {
	background: linear-gradient(135deg, var(--color-primary) 60%, var(--color-accent) 100%);
	border-radius: 16px;
	height: 140px;
	box-shadow: 0 2px 12px rgba(45,140,255,0.10);
	opacity: 0.7;
}
.reviews {
	margin-top: 3.5rem;
	max-width: 900px;
	width: 100%;
}
.reviews h2 {
	font-size: 1.7rem;
	margin-bottom: 1.2rem;
	color: var(--color-primary);
}
.review-cards {
	display: flex;
	gap: 1.5rem;
	flex-wrap: wrap;
}
.review {
	background: var(--color-secondary);
	border-radius: 16px;
	box-shadow: 0 4px 24px rgba(45,140,255,0.10);
	padding: 1.2rem 1rem;
	flex: 1 1 220px;
	min-width: 220px;
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	gap: 0.5rem;
	font-size: 1.05rem;
}
.review span {
	font-size: 0.95rem;
	color: #888;
}
.faq {
	margin-top: 3.5rem;
	max-width: 900px;
	width: 100%;
}
.faq h2 {
	font-size: 1.7rem;
	margin-bottom: 1.2rem;
	color: var(--color-primary);
}
.faq-list {
	list-style: none;
	padding: 0;
	margin: 0;
	display: flex;
	flex-direction: column;
	gap: 0.7rem;
}
.contacts {
	margin-top: 3.5rem;
	max-width: 900px;
	width: 100%;
}
.contacts h2 {
	font-size: 1.7rem;
	margin-bottom: 1.2rem;
	color: var(--color-primary);
}
.contact-info {
	display: flex;
	gap: 2.5rem;
	flex-wrap: wrap;
	font-size: 1.1rem;
}
.contact-info > div {
	display: flex;
	align-items: center;
	gap: 0.5rem;
}
footer {
	background: var(--color-footer);
	text-align: center;
	padding: 1.2rem 0 1rem 0;
	font-size: 1rem;
	color: #888;
	margin-top: auto;
	box-shadow: 0 -2px 12px rgba(0,0,0,0.04);
}
@media (max-width: 900px) {
	.cards, .gallery-grid, .review-cards, .faq-list, .contact-info {
		flex-direction: column;
		gap: 1.2rem;
	}
}
@media (max-width: 600px) {
	header {
		padding: 0.7rem 1rem;
	}
	.hero h1 {
		font-size: 2rem;
	}
	.about, .benefits, .gallery, .reviews, .faq, .contacts {
		padding: 0.7rem;
	}
	.cards, .gallery-grid, .review-cards, .faq-list, .contact-info {
		gap: 0.7rem;
	}
}
@keyframes fadeIn {
	from { opacity: 0; transform: translateY(30px); }
	to { opacity: 1; transform: none; }
}
@keyframes fadeInUp {
	from { opacity: 0; transform: translateY(60px); }
	to { opacity: 1; transform: none; }
}
.fade-in {
	animation: fadeInUp 1.2s;
}
</style>
