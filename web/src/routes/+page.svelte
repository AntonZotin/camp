<script>
  import { fade, fly } from 'svelte/transition';
  import { Tent, Utensils, Palette, Medal, HeartPulse, Trees, Activity, Users, Star, Calendar } from 'lucide-svelte';

  const features = [
    {
      icon: Tent,
      title: 'Комфортное проживание',
      description: 'Современные корпуса с удобствами, раздельные комнаты по возрастам'
    },
    {
      icon: Utensils,
      title: '5-разовое питание',
      description: 'Сбалансированное меню от профессиональных поваров'
    },
    {
      icon: Palette,
      title: 'Творческое развитие',
      description: 'Мастер-классы, кружки и студии на любой вкус'
    },
    {
      icon: Activity,
      title: 'Спортивные программы',
      description: 'Бассейн, скалодром, веревочный парк и многое другое'
    },
    {
      icon: Users,
      title: 'Опытные вожатые',
      description: 'Профессиональные педагоги с большим опытом работы'
    },
    {
      icon: HeartPulse,
      title: 'Безопасность',
      description: 'Круглосуточная охрана и медицинское сопровождение'
    }
  ];

  const stats = [
    { value: '15+', label: 'Лет успешной работы', icon: Medal },
    { value: '1000+', label: 'Довольных детей', icon: HeartPulse },
    { value: '50+', label: 'Профессиональных вожатых', icon: Users },
    { value: '24/7', label: 'Медсопровождение', icon: HeartPulse }
  ];

  const programs = [
    {
      title: 'Творческая смена',
      description: 'Театр, живопись, музыка и танцы',
      icon: Palette
    },
    {
      title: 'Спортивная смена',
      description: 'Соревнования, эстафеты и тренировки',
      icon: Activity
    },
    {
      title: 'Научная смена',
      description: 'Эксперименты, квесты и исследования',
      icon: Star
    }
  ];

  const schedule = [
    { time: '08:00', activity: 'Подъем, зарядка' },
    { time: '09:00', activity: 'Завтрак' },
    { time: '10:00', activity: 'Основные занятия' },
    { time: '13:00', activity: 'Обед' },
    { time: '14:00', activity: 'Тихий час' },
    { time: '16:00', activity: 'Кружки и секции' },
    { time: '19:00', activity: 'Ужин' },
    { time: '20:00', activity: 'Вечерние мероприятия' }
  ];
</script>

<div class="stars-bg"></div>
<section class="hero">
  <div class="container">
    <div class="hero-content" in:fly={{ y: 50, duration: 800 }}>
      <h1 transition:fade>
        <span class="gradient-text">Sunny Camp</span> -<br>
        место, где рождаются<br>
        <span class="highlight">счастливые воспоминания</span>
      </h1>
      <p transition:fade={{ delay: 200 }}>
        Премиальный детский лагерь с 15-летним опытом организации незабываемого отдыха
      </p>
      <div class="hero-actions" transition:fade={{ delay: 400 }}>
        <a href="#booking" class="button primary">
          <Calendar size={18} class="icon" />
          <span>Забронировать путёвку</span>
        </a>
        <a href="#programs" class="button secondary">
          <span>Узнать о программах</span>
        </a>
      </div>
    </div>
    <div class="hero-image" in:fly={{ x: 50, duration: 800, delay: 200 }}>
      <img src="/src/images/camp-hero.png" alt="Дети в лагере" class="hero-img" />
      <div class="floating-elements">
        <div class="floating-icon" style="--delay: 0s">
          <Tent size={32} />
        </div>
        <div class="floating-icon" style="--delay: 1s">
          <Trees size={32} />
        </div>
        <div class="floating-icon" style="--delay: 2s">
          <Activity size={32} />
        </div>
      </div>
    </div>
  </div>
</section>

<section class="features" id="about">
  <div class="container">
    <h2 class="title" in:fly={{ y: 30 }}>Почему родители <span class="gradient-text">выбирают нас</span></h2>
    <p class="section-description">
      15 лет мы создаем идеальные условия для детского отдыха, сочетая безопасность, развитие и развлечения
    </p>

    <div class="features-grid">
      {#each features as feature, i (feature.title)}
        <div class="feature-card"
             in:fly={{ y: 50, duration: 500, delay: i * 100 }}>
          <div class="feature-icon">
            <svelte:component this={feature.icon} size={40} />
          </div>
          <h3>{feature.title}</h3>
          <p>{feature.description}</p>
          <div class="feature-hover-bg"></div>
        </div>
      {/each}
    </div>
  </div>
</section>

<section class="programs" id="programs">
  <div class="container">
    <h2 class="title" in:fly={{ y: 30 }}>Наши <span class="gradient-text">программы</span></h2>
    <p class="section-description">
      Каждая смена - это уникальное приключение с тематическими занятиями и мероприятиями
    </p>

    <div class="programs-grid">
      {#each programs as program, i (program.title)}
        <div class="program-card" in:fly={{ y: 50, duration: 500, delay: i * 150 }}>
          <div class="program-icon">
            <svelte:component this={program.icon} size={48} />
          </div>
          <h3>{program.title}</h3>
          <p>{program.description}</p>
          <div class="program-details">
            <div class="detail-item">
              <Calendar size={16} />
              <span>Июнь-Август</span>
            </div>
            <div class="detail-item">
              <Users size={16} />
              <span>7-15 лет</span>
            </div>
          </div>
        </div>
      {/each}
    </div>
  </div>
</section>

<section class="stats">
  <div class="container">
    <div class="stats-grid">
      {#each stats as stat, i (stat.label)}
        <div class="stat-item" in:fly={{ y: 50, duration: 500, delay: i * 150 }}>
          {#if stat.icon}
            <div class="stat-icon">
              <svelte:component this={stat.icon} size={48} />
            </div>
          {/if}
          <span class="stat-value">{stat.value}</span>
          <span class="stat-label">{stat.label}</span>
        </div>
      {/each}
    </div>
  </div>
</section>

<section class="schedule">
  <div class="container">
    <h2 class="title" in:fly={{ y: 30 }}>Типичный <span class="gradient-text">день в лагере</span></h2>
    <p class="section-description">
      Продуманный режим дня сочетает активность, отдых и развитие
    </p>

    <div class="timeline">
      {#each schedule as item, i (item.time)}
        <div class="timeline-item" in:fly={{ y: 30, duration: 500, delay: i * 100 }}>
          <div class="timeline-time">{item.time}</div>
          <div class="timeline-dot" />
          <div class="timeline-content">{item.activity}</div>
        </div>
      {/each}
    </div>
  </div>
</section>

<section class="cta">
  <div class="container">
    <div class="cta-card">
      <h2>Готовы к незабываемым каникулам?</h2>
      <p>Оставьте заявку и мы подберем идеальную программу для вашего ребенка</p>
      <a href="#booking" class="button primary">Забронировать сейчас</a>
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
    background: url("/src/images/star.png");
    z-index: -1;
    opacity: 0.3;
  }

  .hero {
    padding: 6rem 0;
    position: relative;
    overflow: hidden;
  }

  .hero .container {
    display: flex;
    align-items: center;
    gap: 4rem;
  }

  .hero-content {
    flex: 1;
  }

  .hero h1 {
    font-size: 3.5rem;
    margin-bottom: 1.5rem;
    line-height: 1.2;
  }

  .highlight {
    background: linear-gradient(90deg, var(--primary), var(--secondary));
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }

  .hero p {
    font-size: 1.25rem;
    margin-bottom: 2.5rem;
    max-width: 500px;
    opacity: 0.9;
  }

  .hero-image {
    flex: 1;
    position: relative;
  }

  .hero-img {
    width: 100%;
    max-width: 600px;
    border-radius: var(--radius);
    box-shadow: var(--shadow);
    animation: float 6s ease-in-out infinite;
  }

  .hero-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-start;
    align-items: center;
    flex-wrap: wrap;
  }

  .hero-actions .button {
    height: 48px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }

  .floating-elements {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }

  .floating-icon {
    position: absolute;
    background: var(--bg-primary);
    padding: 0.75rem;
    border-radius: 50%;
    box-shadow: var(--shadow);
    animation: float 4s ease-in-out infinite;
    animation-delay: var(--delay);
  }

  .floating-icon:nth-child(1) {
    top: 10%;
    left: 10%;
  }

  .floating-icon:nth-child(2) {
    top: 70%;
    left: 15%;
  }

  .floating-icon:nth-child(3) {
    top: 40%;
    right: 10%;
  }

  .title {
    text-align: center;
  }

  .section-description {
    text-align: center;
    max-width: 800px;
    margin: 0 auto 3rem;
    font-size: 1.1rem;
    color: var(--text-secondary);
  }

  .features {
    padding: 6rem 0;
    background: var(--bg-secondary);
  }

  .features-grid {
    padding: 0 20px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 2rem;
  }

  .feature-card {
    background: var(--bg-primary);
    border-radius: var(--radius);
    padding: 2.5rem 2rem;
    position: relative;
    overflow: hidden;
    transition: var(--transition);
    border: 1px solid var(--border);
    z-index: 1;
  }

  .feature-card:hover {
    transform: translateY(-10px);
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  }

  .feature-icon {
    margin-bottom: 1.5rem;
    color: var(--primary);
  }

  .feature-card h3 {
    font-size: 1.5rem;
    margin-bottom: 1rem;
  }

  .feature-hover-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle at center, rgba(79, 70, 229, 0.05) 0%, transparent 70%);
    z-index: -1;
    opacity: 0;
    transition: var(--transition);
  }

  .feature-card:hover .feature-hover-bg {
    opacity: 1;
  }

  .programs {
    padding: 6rem 0;
  }

  .programs-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 2rem;
  }

  .program-card {
    background: var(--bg-primary);
    border-radius: var(--radius);
    padding: 2rem;
    transition: var(--transition);
    border: 1px solid var(--border);
    text-align: center;
  }

  .program-card:hover {
    transform: translateY(-5px);
    box-shadow: var(--shadow);
  }

  .program-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 1.5rem;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(79, 70, 229, 0.1);
    border-radius: 50%;
    color: var(--primary);
  }

  .program-details {
    display: flex;
    justify-content: center;
    gap: 1.5rem;
    margin-top: 1.5rem;
  }

  .detail-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.9rem;
    color: var(--text-secondary);
  }

  .stats {
    padding: 6rem 0;
    background: linear-gradient(135deg, var(--primary), var(--primary-dark));
    position: relative;
    overflow: hidden;
  }

  .stats::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%23ffffff' fill-opacity='0.05' fill-rule='evenodd'/%3E%3C/svg%3E");
    animation: gradientBG 15s ease infinite;
    background-size: 200% 200%;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 3rem;
  }

  .stat-item {
    text-align: center;
    position: relative;
    z-index: 2;
  }

  .stat-icon {
    color: white;
    margin-bottom: 1rem;
    animation: float 4s ease-in-out infinite;
  }

  .stat-value {
    display: block;
    font-size: 3.5rem;
    font-weight: 700;
    margin-bottom: 0.5rem;
    color: white;
  }

  .stat-label {
    font-size: 1.25rem;
    opacity: 0.9;
    color: rgba(255, 255, 255, 0.9);
  }

  .schedule {
    padding: 6rem 0;
    background: var(--bg-secondary);
  }

  .timeline {
    position: relative;
    max-width: 800px;
    margin: 0 auto;
  }

  .timeline::before {
    content: '';
    position: absolute;
    top: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 2px;
    height: 100%;
    background: var(--primary);
  }

  .timeline-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 2rem;
    position: relative;
  }

  .timeline-time {
    width: 45%;
    text-align: right;
    padding-right: 2rem;
    font-weight: 600;
    color: var(--primary);
  }

  .timeline-dot {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: var(--primary);
    border: 3px solid var(--bg-secondary);
  }

  .timeline-content {
    width: 45%;
    padding-left: 2rem;
    background: var(--bg-primary);
    padding: 1rem;
    border-radius: var(--radius);
    box-shadow: var(--shadow);
  }

  .cta {
    padding: 6rem 0;
  }

  .cta-card {
    background: linear-gradient(135deg, var(--primary), var(--primary-dark));
    color: white;
    padding: 4rem;
    border-radius: var(--radius);
    text-align: center;
    max-width: 800px;
    margin: 0 auto;
    box-shadow: var(--shadow);
  }

  .cta-card h2 {
    margin-bottom: 1rem;
  }

  .cta-card p {
    margin-bottom: 2rem;
    opacity: 0.9;
  }

  @keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-15px); }
  }

  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.7; }
  }

  @keyframes gradientBG {
    0% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
    100% { background-position: 0% 50%; }
  }

  @media (max-width: 1024px) {
    .hero .container {
      flex-direction: column;
      text-align: center;
    }

    .hero-content {
      margin-bottom: 3rem;
    }

    .hero-actions {
      justify-content: center;
    }

    .hero h1 {
      font-size: 2.5rem;
    }

    .timeline::before {
      left: 30px;
    }

    .timeline-item {
      flex-direction: column;
      align-items: flex-start;
      padding-left: 60px;
    }

    .timeline-time,
    .timeline-content {
      width: 100%;
      text-align: left;
      padding: 0;
    }

    .timeline-time {
      margin-bottom: 0.5rem;
    }

    .timeline-dot {
      left: 22px;
    }
  }

  @media (max-width: 768px) {
    .hero {
      padding: 4rem 0;
    }

    .hero h1 {
      font-size: 2rem;
    }

    .hero p {
      font-size: 1rem;
    }

    .section-description {
      margin-bottom: 2rem;
    }

    .cta-card {
      padding: 2rem;
    }

    .stats-grid {
      grid-template-columns: 1fr;
    }

    .features-grid {
      grid-template-columns: 1fr;
    }

    .hero-actions {
      flex-direction: column;
      align-items: flex-start;
    }

    .hero-actions .button {
      width: 100%;
      max-width: 300px;
    }
  }
</style>