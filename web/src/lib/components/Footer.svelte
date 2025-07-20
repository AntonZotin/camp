<script>
  import { MapPin, Phone, Mail, Clock } from 'lucide-svelte';

  const footerLinks = [
    {
      title: 'Контакты',
      icon: MapPin,
      links: [
        { text: 'Московская обл., д. Солнечная', icon: MapPin },
        { text: '+7 (123) 456-78-90', icon: Phone },
        { text: 'info@sunnycamp.ru', icon: Mail },
        { text: 'Пн-Пт: 9:00-18:00', icon: Clock }
      ]
    },
    {
      title: 'Родителям',
      icon: null,
      links: [
        { text: 'Частые вопросы', icon: null },
        { text: 'Документы', icon: null },
        { text: 'Советы', icon: null }
      ]
    }
  ];
</script>

<footer>
  <div class="container">
    <div class="footer-content">
      <div class="footer-info">
        <h3>Sunny Camp</h3>
        <p>Детский оздоровительный лагерь нового поколения</p>
        <p class="copyright">© {new Date().getFullYear()} Sunny Camp. Все права защищены.</p>
      </div>

      {#each footerLinks as section}
        <div class="footer-section">
          <h4>
            {#if section.icon}
              <svelte:component this={section.icon} size={18} class="icon" />
            {/if}
            {section.title}
          </h4>
          <ul>
            {#each section.links as link}
              <li>
                {#if link.icon}
                  <svelte:component this={link.icon} size={16} class="icon" />
                {/if}
                {link.text}
              </li>
            {/each}
          </ul>
        </div>
      {/each}
    </div>
  </div>
</footer>

<style>
  footer {
    background: var(--bg-footer);
    color: var(--text-secondary);
    padding: 3rem 2rem;
    margin-top: auto;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .footer-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 2rem;
  }

  .footer-info {
    max-width: 300px;
  }

  .footer-info h3 {
    color: var(--primary);
    margin-bottom: 1rem;
    font-size: 1.5rem;
  }

  .footer-info p {
    margin-bottom: 0.5rem;
  }

  .copyright {
    opacity: 0.8;
    font-size: 0.9rem;
  }

  .footer-section h4 {
    margin-bottom: 1.5rem;
    color: var(--text-primary);
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .footer-section ul {
    list-style: none;
    padding: 0;
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .footer-section li {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    transition: var(--transition);
  }

  .footer-section li:hover {
    color: var(--primary);
    cursor: pointer;
  }
</style> 