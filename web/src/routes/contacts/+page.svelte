<script lang="ts">
  import { fade, fly } from 'svelte/transition';
  import { MapPin, Phone, Mail, Clock, Send, MessageSquare } from 'lucide-svelte';

  const contacts = [
    {
      icon: MapPin,
      title: 'Адрес',
      text: 'Московская обл., д. Солнечная, ул. Лесная, 15',
      link: 'https://yandex.ru/maps/-/CDqQ7C~',
      linkText: 'Посмотреть на карте'
    },
    {
      icon: Phone,
      title: 'Телефоны',
      text: '+7 (123) 456-78-90 (администрация)\n+7 (987) 654-32-10 (вахта)',
      link: 'tel:+71234567890',
      linkText: 'Позвонить'
    },
    {
      icon: Mail,
      title: 'Email',
      text: 'info@sunnycamp.ru\nbooking@sunnycamp.ru',
      link: 'mailto:info@sunnycamp.ru',
      linkText: 'Написать'
    },
    {
      icon: Clock,
      title: 'Часы работы',
      text: 'Пн-Пт: 9:00-18:00\nСб-Вс: 10:00-16:00',
      link: null,
      linkText: null
    }
  ];

  let formData = {
    name: '',
    email: '',
    phone: '',
    message: ''
  };

  interface FormErrors {
      name: string | null;
      email: string | null;
      phone: string | null;
      message: string | null;
  }

  let formErrors: FormErrors = {
    name: null,
    email: null,
    phone: null,
    message: null
  };
  let isSubmitting = false;
  let isSuccess = false;

  const validateForm = () => {
    const errors: FormErrors = {
      name: null,
      email: null,
      phone: null,
      message: null
    };
    if (!formData.name) errors.name = 'Введите имя';
    if (!formData.email || !/^\S+@\S+\.\S+$/.test(formData.email)) errors.email = 'Введите корректный email';
    if (!formData.phone || !/^[\d\s+\-()]{10,}$/.test(formData.phone)) errors.phone = 'Введите корректный телефон';
    if (!formData.message) errors.message = 'Введите сообщение';
    return errors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    formErrors = validateForm();

    if (Object.keys(formErrors).length === 0) {
      isSubmitting = true;
      await new Promise(resolve => setTimeout(resolve, 1000));
      isSubmitting = false;
      isSuccess = true;
      formData = { name: '', email: '', phone: '', message: '' };
      setTimeout(() => isSuccess = false, 5000);
    }
  };
</script>

<div class="stars-bg"></div>

<section class="contacts-hero">
  <div class="container">
    <div class="hero-content" in:fly={{ y: 50, duration: 800 }}>
      <h1 transition:fade>
        <span class="gradient-text">Контакты</span> лагеря<br>
        <span class="highlight">Sunny Camp</span>
      </h1>
      <p transition:fade={{ delay: 200 }}>
        Мы всегда рады ответить на ваши вопросы и помочь с организацией отдыха вашего ребенка
      </p>
    </div>
  </div>
</section>

<section class="contacts-section">
  <div class="container">
    <div class="contacts-grid">
      <div class="contacts-info" in:fly={{ x: -50 }}>
        <h2>Как с нами связаться</h2>
        <p>
          Вы можете использовать любой удобный способ связи - телефон, email или форму обратной связи.
          Наши специалисты ответят вам в кратчайшие сроки.
        </p>

        <div class="contacts-list">
          {#each contacts as contact, i (contact.title)}
            <div class="contact-card" transition:fade={{ delay: i * 100 }}>
              <div class="contact-icon">
                <svelte:component this={contact.icon} size={32} />
              </div>
              <div class="contact-content">
                <h3>{contact.title}</h3>
                <p>{@html contact.text.replace(/\n/g, '<br>')}</p>
                {#if contact.link}
                  <a href={contact.link} target="_blank" rel="noopener" class="contact-link">
                    {contact.linkText}
                  </a>
                {/if}
              </div>
            </div>
          {/each}
        </div>
      </div>

      <div class="contacts-form" in:fly={{ x: 50 }}>
        <h2>Форма обратной связи</h2>
        <p>
          Оставьте свои контакты и вопрос, и мы свяжемся с вами в течение рабочего дня
        </p>

        {#if isSuccess}
          <div class="form-success" transition:fade>
            <MessageSquare size={24} />
            <div>
              <h3>Спасибо за ваше сообщение!</h3>
              <p>Мы свяжемся с вами в ближайшее время</p>
            </div>
          </div>
        {:else}
          <form on:submit={handleSubmit}>
            <div class="form-group">
              <label for="name">Ваше имя</label>
              <input
                id="name"
                type="text"
                bind:value={formData.name}
                class:error={formErrors.name}
                placeholder="Иван Иванов"
              />
              {#if formErrors.name}
                <span class="error-message">{formErrors.name}</span>
              {/if}
            </div>

            <div class="form-group">
              <label for="email">Email</label>
              <input
                id="email"
                type="email"
                bind:value={formData.email}
                class:error={formErrors.email}
                placeholder="example@mail.ru"
              />
              {#if formErrors.email}
                <span class="error-message">{formErrors.email}</span>
              {/if}
            </div>

            <div class="form-group">
              <label for="phone">Телефон</label>
              <input
                id="phone"
                type="tel"
                bind:value={formData.phone}
                class:error={formErrors.phone}
                placeholder="+7 (123) 456-78-90"
              />
              {#if formErrors.phone}
                <span class="error-message">{formErrors.phone}</span>
              {/if}
            </div>

            <div class="form-group">
              <label for="message">Ваше сообщение</label>
              <textarea
                id="message"
                bind:value={formData.message}
                class:error={formErrors.message}
                placeholder="Ваш вопрос или комментарий..."
                rows="5"
              ></textarea>
              {#if formErrors.message}
                <span class="error-message">{formErrors.message}</span>
              {/if}
            </div>

            <button type="submit" class="button primary" disabled={isSubmitting}>
              {#if isSubmitting}
                <span>Отправка...</span>
              {:else}
                <Send size={18} class="icon" />
                <span>Отправить сообщение</span>
              {/if}
            </button>
          </form>
        {/if}
      </div>
    </div>
  </div>
</section>

<section class="map-section">
  <div class="container">
    <h2 class="section-title" in:fly={{ y: 30 }}>Мы на <span class="gradient-text">карте</span></h2>
    <p class="section-description">
      Наш лагерь расположен в живописном месте Подмосковья
    </p>

    <div class="map-container">
      <iframe
        title="Месторасположение"
        src="https://yandex.ru/map-widget/v1/?um=constructor%3A1a2b3c4d5e6f7g8h9i0j&amp;source=constructor"
        width="100%"
        height="500"
        frameborder="0"
      ></iframe>
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

  .contacts-hero {
    padding: 6rem 0;
    background: linear-gradient(135deg, var(--primary), var(--primary-dark));
    color: white;
    text-align: center;
  }

  .contacts-hero .container {
    max-width: 800px;
    margin: 0 auto;
  }

  .contacts-hero h1 {
    font-size: 3.5rem;
    margin-bottom: 1.5rem;
    line-height: 1.2;
  }

  .highlight {
    background: linear-gradient(90deg, var(--secondary), var(--accent));
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }

  .contacts-hero p {
    font-size: 1.25rem;
    opacity: 0.9;
  }

  .contacts-section {
    padding: 6rem 0;
  }

  .contacts-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 4rem;
  }

  .contacts-info h2,
  .contacts-form h2 {
    font-size: 2rem;
    margin-bottom: 1.5rem;
  }

  .contacts-info p,
  .contacts-form p {
    margin-bottom: 2rem;
    color: var(--text-secondary);
  }

  .contacts-list {
    display: grid;
    gap: 1.5rem;
  }

  .contact-card {
    display: flex;
    gap: 1.5rem;
    background: var(--bg-primary);
    border-radius: var(--radius);
    padding: 1.5rem;
    border: 1px solid var(--border);
    transition: var(--transition);
  }

  .contact-card:hover {
    transform: translateY(-5px);
    box-shadow: var(--shadow);
  }

  .contact-icon {
    width: 60px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(79, 70, 229, 0.05);
    border-radius: 50%;
    color: var(--primary);
    flex-shrink: 0;
  }

  .contact-content h3 {
    margin-bottom: 0.5rem;
  }

  .contact-link {
    display: inline-block;
    margin-top: 0.5rem;
    color: var(--primary);
    font-weight: 500;
    text-decoration: none;
    transition: var(--transition);
  }

  .contact-link:hover {
    text-decoration: underline;
  }

  form {
    display: grid;
    gap: 1.5rem;
  }

  .form-group {
    display: grid;
    gap: 0.5rem;
  }

  label {
    font-weight: 500;
  }

  input,
  textarea {
    width: 100%;
    padding: 0.75rem 1rem;
    border: 1px solid var(--border);
    border-radius: var(--radius);
    background: var(--bg-primary);
    color: var(--text-primary);
    transition: var(--transition);
  }

  input.error,
  textarea.error {
    border-color: var(--error);
  }

  .error-message {
    font-size: 0.875rem;
    color: var(--error);
  }

  textarea {
    resize: vertical;
  }

  .form-success {
    display: flex;
    gap: 1rem;
    background: var(--bg-secondary);
    padding: 1.5rem;
    border-radius: var(--radius);
    border: 1px solid var(--border);
  }

  .form-success h3 {
    margin-bottom: 0.25rem;
  }

  .form-success p {
    margin-bottom: 0;
    font-size: 0.9rem;
    color: var(--text-secondary);
  }

  .map-section {
    padding: 0 0 6rem;
  }

  .section-title {
    text-align: center;
    font-size: 2.5rem;
    margin-bottom: 1rem;
  }

  .section-description {
    text-align: center;
    max-width: 700px;
    margin: 0 auto 3rem;
    color: var(--text-secondary);
  }

  .map-container {
    border-radius: var(--radius);
    overflow: hidden;
    box-shadow: var(--shadow);
  }

  .map-container iframe {
    display: block;
    border: none;
  }

  @media (max-width: 1024px) {
    .contacts-grid {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 768px) {
    .contacts-hero {
      padding: 4rem 0;
    }

    .contacts-hero h1 {
      font-size: 2.5rem;
    }

    .contacts-info h2,
    .contacts-form h2 {
      font-size: 1.75rem;
    }

    .section-title {
      font-size: 2rem;
    }
  }
</style>