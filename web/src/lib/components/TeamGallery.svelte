<script>
  import { fade } from 'svelte/transition';

  const team = [
    {
      name: 'Анна Петрова',
      role: 'Директор лагеря',
      photo: '/images/team/anna.jpg',
      experience: '15 лет опыта'
    },
    {
      name: 'Иван Сидоров',
      role: 'Старший вожатый',
      photo: '/images/team/ivan.jpg',
      experience: '10 лет опыта'
    },
    {
      name: 'Мария Козлова',
      role: 'Медсестра',
      photo: '/images/team/maria.jpg',
      experience: '12 лет опыта'
    },
    {
      name: 'Дмитрий Волков',
      role: 'Спортивный инструктор',
      photo: '/images/team/dmitry.jpg',
      experience: '8 лет опыта'
    }
  ];
</script>

<div class="team-gallery">
  {#each team as member (member.name)}
    <div class="team-card" transition:fade>
      <img src={member.photo} alt={member.name} class="team-photo" />
      <div class="team-info">
        <h3>{member.name}</h3>
        <p class="role">{member.role}</p>
        <p class="experience">{member.experience}</p>
      </div>
    </div>
  {/each}
</div>

<style>
  .team-gallery {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 2rem;
    margin-top: 3rem;
  }

  .team-card {
    background: var(--bg-primary);
    border-radius: var(--radius);
    overflow: hidden;
    box-shadow: var(--shadow);
    transition: var(--transition);
  }

  .team-card:hover {
    transform: translateY(-5px);
  }

  .team-photo {
    width: 100%;
    height: 300px;
    object-fit: cover;
  }

  .team-info {
    padding: 1.5rem;
    text-align: center;
  }

  .team-info h3 {
    margin-bottom: 0.5rem;
  }

  .role {
    color: var(--primary);
    font-weight: 500;
    margin-bottom: 0.5rem;
  }

  .experience {
    font-size: 0.9rem;
    color: var(--text-secondary);
  }
</style>