<script>
  import { fade, fly } from 'svelte/transition';
  import { Palette, Activity, Star, Calendar, Users, Award, Smile, BookOpen, Music, Microscope, Globe } from 'lucide-svelte';

  const programs = [
    {
      title: 'Творческая смена',
      description: 'Театр, живопись, музыка и танцы',
      icon: Palette,
      details: [
        {
          title: 'Театральная студия',
          description: 'Постановка спектаклей, актерское мастерство, сценическая речь',
          icon: Award
        },
        {
          title: 'Художественная мастерская',
          description: 'Живопись, графика, лепка, декоративно-прикладное искусство',
          icon: Palette
        },
        {
          title: 'Музыка и вокал',
          description: 'Обучение игре на инструментах, вокальные занятия, создание музыки',
          icon: Music
        },
        {
          title: 'Танцевальные направления',
          description: 'Современные танцы, хип-хоп, бальные танцы, хореография',
          icon: Smile
        }
      ],
      schedule: [
        { time: '10:00-12:00', activity: 'Мастер-классы по направлениям' },
        { time: '14:00-16:00', activity: 'Индивидуальные занятия' },
        { time: '17:00-19:00', activity: 'Подготовка к итоговому выступлению' }
      ],
      age: '7-15 лет',
      duration: '14 дней',
      results: 'Итоговый спектакль, выставка работ, музыкальный концерт'
    },
    {
      title: 'Спортивная смена',
      description: 'Соревнования, эстафеты и тренировки',
      icon: Activity,
      details: [
        {
          title: 'Командные виды спорта',
          description: 'Футбол, волейбол, баскетбол, пионербол',
          icon: Globe
        },
        {
          title: 'Водные виды',
          description: 'Плавание, водное поло, аквааэробика',
          icon: Award
        },
        {
          title: 'Экстремальные виды',
          description: 'Скалодром, веревочный парк, стрельба из лука',
          icon: Activity
        },
        {
          title: 'Фитнес и ЗОЖ',
          description: 'Йога, стретчинг, функциональные тренировки',
          icon: Smile
        }
      ],
      schedule: [
        { time: '09:00-11:00', activity: 'Утренние тренировки' },
        { time: '15:00-17:00', activity: 'Спортивные игры' },
        { time: '18:00-19:00', activity: 'Вечерние активности' }
      ],
      age: '8-16 лет',
      duration: '14 дней',
      results: 'Спортивные соревнования, вручение наград, показательные выступления'
    },
    {
      title: 'Научная смена',
      description: 'Эксперименты, квесты и исследования',
      icon: Star,
      details: [
        {
          title: 'Естественные науки',
          description: 'Физика, химия, биология через эксперименты',
          icon: Microscope
        },
        {
          title: 'Робототехника',
          description: 'Создание и программирование роботов',
          icon: Activity
        },
        {
          title: 'Астрономия',
          description: 'Наблюдения за звездами, изучение космоса',
          icon: Globe
        },
        {
          title: 'Научные квесты',
          description: 'Интерактивные игры и головоломки',
          icon: BookOpen
        }
      ],
      schedule: [
        { time: '10:00-12:00', activity: 'Лабораторные работы' },
        { time: '14:00-16:00', activity: 'Практические занятия' },
        { time: '17:00-18:00', activity: 'Научные дискуссии' }
      ],
      age: '9-16 лет',
      duration: '21 день',
      results: 'Научная конференция, презентация проектов, выставка изобретений'
    }
  ];

  const allFeatures = [
    {
      icon: Calendar,
      title: 'Гибкие даты',
      description: 'Выбирайте удобные сроки заезда'
    },
    {
      icon: Users,
      title: 'Маленькие группы',
      description: 'Не более 15 человек в отряде'
    },
    {
      icon: Award,
      title: 'Сертификаты',
      description: 'Документы об участии в программе'
    },
    {
      icon: Smile,
      title: 'Профессиональные педагоги',
      description: 'Опытные преподаватели и тренеры'
    }
  ];
</script>

<div class="stars-bg"></div>

<section class="programs-hero">
  <div class="container">
    <div class="hero-content" in:fly={{ y: 50, duration: 800 }}>
      <h1 transition:fade>
        <span class="gradient-text">Программы</span> лагеря<br>
        <span class="highlight">Sunny Camp</span>
      </h1>
      <p transition:fade={{ delay: 200 }}>
        Каждая смена - это уникальное приключение с тематическими занятиями,
        которые помогают детям раскрыть таланты и приобрести новые навыки
      </p>
    </div>
  </div>
</section>

<section class="programs-section">
  <div class="container">
    {#each programs as program, i (program.title)}
      <div class="program-block" in:fly={{ y: 50, delay: i * 100 }}>
        <div class="program-header">
          <div class="program-icon">
            <svelte:component this={program.icon} size={48} />
          </div>
          <h2>{program.title}</h2>
          <p class="program-description">{program.description}</p>
        </div>

        <div class="program-details">
          <div class="details-content">
            <h3>Что входит в программу:</h3>
            <div class="features-grid">
              {#each program.details as detail, j (detail.title)}
                <div class="feature-card" transition:fade={{ delay: j * 50 }}>
                  <div class="feature-icon">
                    <svelte:component this={detail.icon} size={60} />
                  </div>
                  <h4>{detail.title}</h4>
                  <p>{detail.description}</p>
                </div>
              {/each}
            </div>

            <div class="program-info">
              <div class="info-item">
                <Calendar size={20} />
                <div>
                  <span class="info-label">Длительность:</span>
                  <span class="info-value">{program.duration}</span>
                </div>
              </div>
              <div class="info-item">
                <Users size={20} />
                <div>
                  <span class="info-label">Возраст:</span>
                  <span class="info-value">{program.age}</span>
                </div>
              </div>
              <div class="info-item">
                <Award size={20} />
                <div>
                  <span class="info-label">Итоги смены:</span>
                  <span class="info-value">{program.results}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="details-schedule">
            <h3>Расписание занятий:</h3>
            <div class="schedule-timeline">
              {#each program.schedule as item, k (item.time)}
                <div class="schedule-item" transition:fade={{ delay: k * 50 }}>
                  <div class="schedule-time">{item.time}</div>
                  <div class="schedule-dot"></div>
                  <div class="schedule-activity">{item.activity}</div>
                </div>
              {/each}
            </div>
          </div>
        </div>
      </div>
    {/each}
  </div>
</section>

<section class="common-features">
  <div class="container">
    <h2 class="section-title" in:fly={{ y: 30 }}>Что <span class="gradient-text">общего</span> во всех программах</h2>
    <p class="section-description">
      Независимо от выбранной смены, каждый ребенок получит максимум пользы и удовольствия
    </p>

    <div class="features-grid">
      {#each allFeatures as feature, i (feature.title)}
        <div class="feature-card" in:fly={{ y: 50, delay: i * 100 }}>
          <div class="feature-icon">
            <svelte:component this={feature.icon} size={60} />
          </div>
          <h3>{feature.title}</h3>
          <p>{feature.description}</p>
        </div>
      {/each}
    </div>
  </div>
</section>

<section class="cta">
  <div class="container">
    <div class="cta-card">
      <h2>Не можете выбрать программу?</h2>
      <p>Оставьте заявку и мы поможем подобрать идеальный вариант для вашего ребенка</p>
      <a href="#booking" class="button primary">Получить консультацию</a>
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

  .programs-hero {
    padding: 6rem 0;
    background: linear-gradient(135deg, var(--primary), var(--primary-dark));
    color: white;
    text-align: center;
  }

  .programs-hero .container {
    max-width: 800px;
    margin: 0 auto;
  }

  .programs-hero h1 {
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

  .programs-hero p {
    font-size: 1.25rem;
    opacity: 0.9;
  }

  .programs-section {
    padding: 6rem 0;
  }

  .program-block {
    margin-bottom: 6rem;
  }

  .program-header {
    text-align: center;
    margin-bottom: 3rem;
  }

  .program-icon {
    width: 100px;
    height: 100px;
    margin: 0 auto 1.5rem;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(79, 70, 229, 0.1);
    border-radius: 50%;
    color: var(--primary);
  }

  .program-header h2 {
    font-size: 2.5rem;
    margin-bottom: 1rem;
  }

  .program-description {
    font-size: 1.25rem;
    color: var(--text-secondary);
    max-width: 600px;
    margin: 0 auto;
  }

  .program-details {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 3rem;
  }

  .details-content h3,
  .details-schedule h3 {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
    color: var(--primary);
  }

  .features-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
    margin-bottom: 2rem;
  }

  .feature-card {
    background: var(--bg-primary);
    border-radius: var(--radius);
    padding: 1.5rem;
    border: 1px solid var(--border);
    transition: var(--transition);
  }

  .feature-card:hover {
    transform: translateY(-5px);
    box-shadow: var(--shadow);
  }

  .feature-card .feature-icon {
    width: 60px;
    height: 60px;
    margin: 0 0 1rem;
    background: rgba(79, 70, 229, 0.10);
  }

  .feature-card h4 {
    font-size: 1.1rem;
    margin-bottom: 0.5rem;
  }

  .program-info {
    background: var(--bg-secondary);
    padding: 1.5rem;
    border-radius: var(--radius);
  }

  .info-item {
    display: flex;
    gap: 1rem;
    margin-bottom: 1rem;
    align-items: flex-start;
  }

  .info-item:last-child {
    margin-bottom: 0;
  }

  .info-label {
    display: block;
    font-size: 0.9rem;
    color: var(--text-secondary);
  }

  .info-value {
    font-weight: 500;
  }

  .details-schedule {
    background: var(--bg-secondary);
    padding: 2rem;
    border-radius: var(--radius);
    position: relative;
  }

  .schedule-timeline {
    position: relative;
    padding-left: 20px;
  }

  .schedule-timeline::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 2px;
    height: 100%;
    background: var(--primary);
  }

  .schedule-item {
    position: relative;
    margin-bottom: 1.5rem;
    padding-left: 2rem;
  }

  .schedule-item:last-child {
    margin-bottom: 0;
  }

  .schedule-dot {
    position: absolute;
    left: -9px;
    top: 5px;
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: var(--primary);
    border: 3px solid var(--bg-secondary);
  }

  .schedule-time {
    font-weight: 600;
    color: var(--primary);
    margin-bottom: 0.25rem;
  }

  .common-features {
    padding: 6rem 0;
    background: var(--bg-secondary);
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

  .common-features .features-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }

  .common-features .feature-card {
    text-align: center;
  }

  .common-features .feature-icon {
    margin: 0 auto 1rem;
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

  @media (max-width: 1024px) {
    .program-details {
      grid-template-columns: 1fr;
    }

    .features-grid {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 768px) {
    .programs-hero {
      padding: 4rem 0;
    }

    .programs-hero h1 {
      font-size: 2.5rem;
    }

    .program-header h2 {
      font-size: 2rem;
    }

    .section-title {
      font-size: 2rem;
    }

    .cta-card {
      padding: 2rem;
    }
  }
</style>