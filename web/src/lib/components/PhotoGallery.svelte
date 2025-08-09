<script>
  import { fade } from 'svelte/transition';

  const photos = [
    '/images/gallery/1.jpg',
    '/images/gallery/2.jpg',
    '/images/gallery/3.jpg',
    '/images/gallery/4.jpg',
    '/images/gallery/5.jpg',
    '/images/gallery/6.jpg'
  ];
</script>

<div class="photo-gallery">
  {#each photos as photo (photo)}
    <div class="photo-item" transition:fade>
      <img src={photo} alt="Фото лагеря" class="gallery-photo" />
    </div>
  {/each}
</div>

<style>
  .photo-gallery {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1rem;
    margin-top: 3rem;
  }

  .photo-item {
    border-radius: var(--radius);
    overflow: hidden;
    box-shadow: var(--shadow);
    transition: var(--transition);
  }

  .photo-item:hover {
    transform: scale(1.03);
  }

  .gallery-photo {
    width: 100%;
    height: 200px;
    object-fit: cover;
    display: block;
  }
</style>