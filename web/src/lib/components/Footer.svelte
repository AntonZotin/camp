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
      <div class="footer-main">
        <div class="footer-info">
          <h3>Sunny Camp</h3>
          <p>Детский оздоровительный лагерь нового поколения</p>
        </div>

        <div class="footer-columns">
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

      <div class="footer-bottom">
        <p class="copyright">© {new Date().getFullYear()} Sunny Camp. Все права защищены.</p>
      </div>
    </div>
  </div>
</footer>

<style>
  footer {
    background: var(--bg-footer);
    color: var(--text-secondary);
    padding: 3rem 0 1rem;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 1.5rem;
  }

  .footer-content {
    display: flex;
    flex-direction: column;
    gap: 2rem;
  }

  .footer-main {
    display: flex;
    justify-content: center;
    gap: 10rem;
  }

  .footer-info {
    max-width: 300px;
  }

  .footer-info h3 {
    color: var(--primary);
    margin-bottom: 1rem;
    font-size: 1.5rem;
  }

  .footer-columns {
    display: flex;
    gap: 3rem;
  }

  .footer-section {
    min-width: 150px;
  }

  .footer-section h4 {
    margin-bottom: 1.25rem;
    color: var(--text-primary);
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 1.1rem;
  }

  .footer-section ul {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .footer-section li {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .footer-bottom {
    border-top: 1px solid var(--border);
    padding-top: 1.5rem;
    text-align: center;
  }

  .copyright {
    font-size: 0.9rem;
    opacity: 0.8;
  }

  @media (max-width: 768px) {
    .footer-main {
      flex-direction: column;
    }

    .footer-columns {
      flex-wrap: wrap;
      gap: 1.5rem;
    }

    .footer-section {
      min-width: calc(50% - 1.5rem);
    }
  }
</style>