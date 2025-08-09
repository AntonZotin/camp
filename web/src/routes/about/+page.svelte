<script>
  import { fade, fly } from 'svelte/transition';
  import {Tent, MapPin, Medal, Users, HeartPulse, Star, ChevronRight, Trees} from 'lucide-svelte';
  import TeamGallery from "$lib/components/TeamGallery.svelte";
  import PhotoGallery from "$lib/components/PhotoGallery.svelte";

  const history = [
    { year: '2008', event: 'Основание лагеря Sunny Camp' },
    { year: '2012', event: 'Открытие нового корпуса с бассейном' },
    { year: '2015', event: 'Получение премии "Лучший детский лагерь года"' },
    { year: '2018', event: 'Расширение территории и строительство веревочного парка' },
    { year: '2022', event: 'Модернизация инфраструктуры и внедрение новых программ' }
  ];

  const principles = [
    {
      title: 'Безопасность',
      description: 'Круглосуточная охрана, медицинский пункт, соблюдение всех норм',
      icon: HeartPulse
    },
    {
      title: 'Развитие',
      description: 'Программы, направленные на раскрытие потенциала каждого ребенка',
      icon: Star
    },
    {
      title: 'Комфорт',
      description: 'Современные корпуса, 5-разовое питание, уютные комнаты',
      icon: Tent
    },
    {
      title: 'Профессионализм',
      description: 'Опытные вожатые и педагоги с профильным образованием',
      icon: Users
    }
  ];
</script>

<div class="stars-bg"></div>

<section class="about-hero">
  <div class="container">
    <div class="hero-content" in:fly={{ y: 50, duration: 800 }}>
      <h1 transition:fade>
        <span class="gradient-text">Sunny Camp</span> -<br>
        больше, чем просто<br>
        <span class="highlight">детский лагерь</span>
      </h1>
      <p transition:fade={{ delay: 200 }}>
        15 лет создаем пространство, где дети раскрывают таланты, находят друзей и заряжаются впечатлениями
      </p>
    </div>
    <div class="hero-image" in:fly={{ x: 50, duration: 800, delay: 200 }}>
      <img src="/images/about-hero.jpg" alt="Дети в лагере" class="hero-img" />
    </div>
  </div>
</section>

<section class="about-section">
  <div class="container">
    <div class="about-grid">
      <div class="about-content" in:fly={{ x: -50 }}>
        <h2>Наша <span class="gradient-text">история</span></h2>
        <p>
          Sunny Camp был основан в 2008 году группой энтузиастов - педагогов и психологов, которые мечтали создать идеальное место для детского отдыха.
          С тех пор мы выросли из небольшого палаточного лагеря в современный комплекс с развитой инфраструктурой.
        </p>
        <p>
          Каждый год мы совершенствуем наши программы, внедряем новые технологии и сохраняем главное - атмосферу тепла, заботы и настоящих приключений.
        </p>

        <div class="history-timeline">
          {#each history as item, index (item.year)}
            <div class="history-item" in:fly={{ y: 20, delay: 100 * index }}>
              <div class="history-year">{item.year}</div>
              <div class="history-arrow">
                <ChevronRight size={20} />
              </div>
              <div class="history-event">{item.event}</div>
            </div>
          {/each}
        </div>
      </div>

      <div class="about-image" in:fly={{ x: 50 }}>
        <img src="/images/camp-history.jpg" alt="История лагеря" />
        <div class="stats-card">
          <div class="stat-item">
            <Medal size={32} />
            <div>
              <span class="stat-value">15+</span>
              <span class="stat-label">Лет опыта</span>
            </div>
          </div>
          <div class="stat-item">
            <Users size={32} />
            <div>
              <span class="stat-value">50+</span>
              <span class="stat-label">Профессионалов</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>

<section class="principles-section">
  <div class="container">
    <h2 class="section-title" in:fly={{ y: 30 }}>Наши <span class="gradient-text">принципы</span></h2>
    <p class="section-description">
      В основе нашей работы лежат ценности, которые делают Sunny Camp особенным местом
    </p>

    <div class="principles-grid">
      {#each principles as principle, i (principle.title)}
        <div class="principle-card" in:fly={{ y: 50, delay: i * 100 }}>
          <div class="principle-icon">
            <svelte:component this={principle.icon} size={40} />
          </div>
          <h3>{principle.title}</h3>
          <p>{principle.description}</p>
        </div>
      {/each}
    </div>
  </div>
</section>

<section class="location-section">
  <div class="container">
    <div class="location-grid">
      <div class="location-content" in:fly={{ x: -50 }}>
        <h2>Расположение</h2>
        <p>
          Наш лагерь расположен в экологически чистом районе Подмосковья, в 30 км от МКАД, на территории соснового бора.
          Чистый воздух, живописные пейзажи и собственная огороженная территория площадью 5 гектаров создают идеальные условия для отдыха.
        </p>
        <div class="location-features">
          <div class="feature-item">
            <MapPin size={24} />
            <span>30 км от МКАД</span>
          </div>
          <div class="feature-item">
            <Trees size={24} />
            <span>Сосновый бор</span>
          </div>
          <div class="feature-item">
            <Tent size={24} />
            <span>5 гектаров территории</span>
          </div>
        </div>
      </div>
      <div class="location-map" in:fly={{ x: 50 }}>
        <iframe
          title="Месторасположение"
          src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2242.772742760182!2d37.62246731593189!3d55.78802398056068!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x46b535a6e4b5f6a5%3A0x1b8e90b6a4d5e5b7!2z0JzQvtGB0LrQvtCy0YHQutC40Lkg0LrQvtC80L_Qu9C10LogItCh0YPQvNGB0YLRgNCwIg!5e0!3m2!1sru!2sru!4v1620000000000!5m2!1sru!2sru"
          width="100%"
          height="400"
          style="border:0;"
          loading="lazy">
        </iframe>
      </div>
    </div>
  </div>
</section>

<section class="team-section">
  <div class="container">
    <h2 class="section-title" in:fly={{ y: 30 }}>Наша <span class="gradient-text">команда</span></h2>
    <p class="section-description">
      Профессионалы с большим опытом работы, для которых детский отдых - это призвание
    </p>

    <TeamGallery />
  </div>
</section>

<section class="gallery-section">
  <div class="container">
    <h2 class="section-title" in:fly={{ y: 30 }}>Фото <span class="gradient-text">лагеря</span></h2>
    <p class="section-description">
      Увидите, где и как живут наши ребята
    </p>

    <PhotoGallery />
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

  .about-hero {
    padding: 6rem 0;
    position: relative;
    overflow: hidden;
  }

  .about-hero .container {
    display: flex;
    align-items: center;
    gap: 4rem;
  }

  .hero-content {
    flex: 1;
  }

  .about-hero h1 {
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

  .about-hero p {
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

  .about-section {
    padding: 6rem 0;
    background: var(--bg-secondary);
  }

  .about-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 4rem;
    align-items: center;
  }

  .about-content h2 {
    font-size: 2.5rem;
    margin-bottom: 1.5rem;
  }

  .about-content p {
    margin-bottom: 1.5rem;
    line-height: 1.6;
  }

  .about-image {
    position: relative;
  }

  .about-image img {
    width: 100%;
    border-radius: var(--radius);
    box-shadow: var(--shadow);
  }

  .stats-card {
    position: absolute;
    bottom: -2rem;
    right: -2rem;
    background: var(--bg-primary);
    padding: 2rem;
    border-radius: var(--radius);
    box-shadow: var(--shadow);
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
    width: 80%;
  }

  .stat-item {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .stat-value {
    font-size: 1.5rem;
    font-weight: 700;
    color: var(--primary);
  }

  .stat-label {
    font-size: 0.9rem;
    color: var(--text-secondary);
  }

  .history-timeline {
    margin-top: 3rem;
  }

  .history-item {
    display: flex;
    align-items: center;
    margin-bottom: 1.5rem;
    padding: 1rem;
    background: var(--bg-primary);
    border-radius: var(--radius);
    transition: var(--transition);
  }

  .history-item:hover {
    transform: translateX(5px);
    box-shadow: var(--shadow);
  }

  .history-year {
    font-weight: 700;
    color: var(--primary);
    min-width: 60px;
  }

  .history-arrow {
    margin: 0 1rem;
    color: var(--primary);
  }

  .history-event {
    flex: 1;
  }

  .principles-section {
    padding: 6rem 0;
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

  .principles-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 2rem;
  }

  .principle-card {
    background: var(--bg-primary);
    padding: 2rem;
    border-radius: var(--radius);
    transition: var(--transition);
    text-align: center;
  }

  .principle-card:hover {
    transform: translateY(-5px);
    box-shadow: var(--shadow);
  }

  .principle-icon {
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

  .principle-card h3 {
    margin-bottom: 1rem;
    font-size: 1.25rem;
  }

  .location-section {
    padding: 6rem 0;
    background: var(--bg-secondary);
  }

  .location-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 4rem;
    align-items: center;
  }

  .location-content h2 {
    font-size: 2.5rem;
    margin-bottom: 1.5rem;
  }

  .location-content p {
    margin-bottom: 2rem;
    line-height: 1.6;
  }

  .location-features {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
  }

  .feature-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: var(--primary);
    font-weight: 500;
  }

  .location-map {
    height: 100%;
    border-radius: var(--radius);
    overflow: hidden;
    box-shadow: var(--shadow);
  }

  .team-section, .gallery-section {
    padding: 6rem 0;
  }

  @keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-15px); }
  }

  @media (max-width: 1024px) {
    .about-grid, .location-grid {
      grid-template-columns: 1fr;
      gap: 3rem;
    }

    .about-image {
      order: -1;
    }

    .stats-card {
      position: relative;
      bottom: auto;
      right: auto;
      width: 100%;
      margin-top: 2rem;
    }
  }

  @media (max-width: 768px) {
    .about-hero {
      padding: 4rem 0;
    }

    .about-hero .container {
      flex-direction: column;
    }

    .about-hero h1 {
      font-size: 2.5rem;
    }

    .section-title {
      font-size: 2rem;
    }

    .location-features {
      grid-template-columns: 1fr;
    }

    .stats-card {
      grid-template-columns: 1fr;
    }
  }
</style>