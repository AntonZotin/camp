<script>
  import { fade } from 'svelte/transition';
  import { Tent, Utensils, Palette, Medal, HeartPulse } from 'lucide-svelte';

  const features = [
    {
      icon: Tent,
      title: 'Комфортное проживание',
      description: 'Уютные домики и современные корпуса'
    },
    {
      icon: Utensils,
      title: 'Питание',
      description: 'Сбалансированное 5-разовое питание'
    },
    {
      icon: Palette,
      title: 'Развитие',
      description: 'Творческие и спортивные занятия'
    }
  ];

  const stats = [
    { value: '15+', label: 'Лет опыта', icon: Medal },
    { value: '1000+', label: 'Довольных детей', icon: HeartPulse },
    { value: '50+', label: 'Профессиональных вожатых' },
    { value: '24/7', label: 'Медицинское сопровождение' }
  ];
</script>

<section class="hero">
  <div class="container">
    <div class="hero-content">
      <h1 transition:fade>Летний отдых для вашего ребенка</h1>
      <p transition:fade={{ delay: 100 }}>Создаём незабываемые каникулы с 2008 года</p>
      <div class="hero-actions" transition:fade={{ delay: 200 }}>
        <a href="#booking" class="button primary">Забронировать путёвку</a>
        <a href="#about" class="button secondary">Узнать больше</a>
      </div>
    </div>
  </div>
</section>

<section class="features" id="about">
  <div class="container">
    <h2>Почему выбирают нас</h2>
    <div class="features-grid">
      {#each features as feature}
        <div class="feature-card" transition:fade>
          <div class="feature-icon">
            <svelte:component this={feature.icon} size={32} />
          </div>
          <h3>{feature.title}</h3>
          <p>{feature.description}</p>
        </div>
      {/each}
    </div>
  </div>
</section>

<section class="stats">
  <div class="container">
    <div class="stats-grid">
      {#each stats as stat}
        <div class="stat-item" transition:fade>
          {#if stat.icon}
            <svelte:component this={stat.icon} size={32} class="stat-icon" />
          {/if}
          <span class="stat-value">{stat.value}</span>
          <span class="stat-label">{stat.label}</span>
        </div>
      {/each}
    </div>
  </div>
</section>

<style>
  .hero {
    background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('/images/camp-hero.jpg');
    background-size: cover;
    background-position: center;
    color: white;
    padding: 8rem 2rem;
    text-align: center;
  }
  .container {
    max-width: 1200px;
    margin: 0 auto;
  }
  .hero-content {
    max-width: 800px;
    margin: 0 auto;
  }
  .hero h1 {
    font-size: 3rem;
    margin-bottom: 1rem;
    line-height: 1.2;
  }
  .hero p {
    font-size: 1.25rem;
    margin-bottom: 2rem;
    opacity: 0.9;
  }
  .hero-actions {
    display: flex;
    gap: 1rem;
    justify-content: center;
    flex-wrap: wrap;
  }
  .button {
    padding: 0.75rem 1.5rem;
    border-radius: var(--radius);
    text-decoration: none;
    font-weight: 500;
    transition: var(--transition);
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
  }
  .primary {
    background: var(--primary);
    color: white;
  }
  .primary:hover {
    background: var(--primary-dark);
    transform: translateY(-2px);
  }
  .secondary {
    background: transparent;
    color: white;
    border: 1px solid white;
  }
  .secondary:hover {
    background: rgba(255, 255, 255, 0.1);
    transform: translateY(-2px);
  }
  .features {
    padding: 5rem 2rem;
    background: var(--bg-secondary);
  }
  h2 {
    text-align: center;
    margin-bottom: 3rem;
    font-size: 2rem;
    color: var(--text-primary);
  }
  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 2rem;
  }
  .feature-card {
    background: var(--bg-primary);
    padding: 2rem;
    border-radius: var(--radius);
    box-shadow: var(--shadow);
    transition: var(--transition);
    text-align: center;
  }
  .feature-card:hover {
    transform: translateY(-5px);
  }
  .feature-icon {
    margin-bottom: 1.5rem;
    color: var(--primary);
  }
  .feature-card h3 {
    margin-bottom: 1rem;
    color: var(--text-primary);
  }
  .feature-card p {
    color: var(--text-secondary);
  }
  .stats {
    padding: 4rem 2rem;
    background: var(--primary);
    color: white;
  }
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 2rem;
    text-align: center;
  }
  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.5rem;
  }
  .stat-icon {
    margin-bottom: 0.5rem;
  }
  .stat-value {
    display: block;
    font-size: 2.5rem;
    font-weight: 700;
    margin-bottom: 0.25rem;
  }
  .stat-label {
    font-size: 1.1rem;
    opacity: 0.9;
  }
  @media (max-width: 768px) {
    .hero {
      padding: 6rem 1.5rem;
    }
    .hero h1 {
      font-size: 2rem;
    }
    .hero p {
      font-size: 1rem;
    }
    .features {
      padding: 3rem 1.5rem;
    }
    .features-grid {
      grid-template-columns: 1fr;
    }
  }
</style>
