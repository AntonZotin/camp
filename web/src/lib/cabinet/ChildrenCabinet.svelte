<script lang="ts">
	import { onMount } from 'svelte';
	import type {Child} from "$lib/models";
	import { fade, fly } from 'svelte/transition';
	import { Users, Baby, Loader, AlertCircle, Plus, Edit, Trash2 } from 'lucide-svelte';
	import { PUBLIC_API_URL } from '$env/static/public';
	import type { UserSession } from '$lib/stores/userStore';
	export let user: UserSession;

	let children: Child[] = [];
	let loading = true;
	let error = '';
	let showModal = false;
	let editChild: Child | null = null;
	let childForm = { fullName: '', birthDate: '' };

	async function loadChildren() {
		loading = true;
		error = '';
		try {
			const res = await fetch(`${PUBLIC_API_URL}/api/children/parent/${user.userId}`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});
			if (!res.ok)
				error = 'Ошибка загрузки детей';
			else
				children = await res.json();
		} finally {
			loading = false;
		}
	}

	function openModal(child: Child | null = null) {
		showModal = true;
		editChild = child;
		if (child) {
			childForm = { fullName: child.fullName, birthDate: child.birthDate };
		} else {
			childForm = { fullName: '', birthDate: '' };
		}
	}

	function closeModal() {
		showModal = false;
		editChild = null;
	}

	async function saveChild() {
		const method = editChild ? 'PUT' : 'POST';
		const url = editChild ? `${PUBLIC_API_URL}/api/children/${editChild.id}` : `${PUBLIC_API_URL}/api/children`;
		const body = JSON.stringify({
			...childForm,
			parentId: user.userId
		});
		await fetch(url, {
			method,
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${user.accessToken}`
			},
			body
		});
		await loadChildren();
		closeModal();
	}

	async function deleteChild(id: number) {
		if (!confirm('Удалить ребенка?')) return;
		await fetch(`${PUBLIC_API_URL}/api/children/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${user.accessToken}` }
		});
		await loadChildren();
	}

	onMount(() => { loadChildren(); });
</script>

<div class="children-cabinet">
	<div class="header">
		<h2>
			<Users size={24} />
			<span>Мои дети</span>
		</h2>
		<button class="add-btn" on:click={() => openModal()}>
			<Plus size={18} />
			<span>Добавить ребенка</span>
		</button>
	</div>

	{#if loading}
		<div class="loader">
			<Loader size={24} />
			<span>Загрузка...</span>
		</div>
	{:else if error}
		<div class="error">
			<AlertCircle size={20} />
			<span>{error}</span>
		</div>
	{:else if children.length === 0}
		<div class="empty">
			<Baby size={48} />
			<h3>У вас пока нет добавленных детей</h3>
			<p>Добавьте ребенка, чтобы начать бронирование путевок</p>
		</div>
	{:else}
		<div class="children-grid">
			{#each children as child, i (child.id)}
				<div class="child-card" in:fly={{ y: 30, delay: i * 100 }}>
					<div class="child-header">
						<Baby size={24} />
						<h3>{child.fullName}</h3>
						<div class="actions">
							<button class="icon-btn edit" title="Редактировать" on:click={() => openModal(child)}>
								<Edit size={16} />
							</button>
							<button class="icon-btn delete" title="Удалить" on:click={() => deleteChild(child.id)}>
								<Trash2 size={16} />
							</button>
						</div>
					</div>
					<div class="child-info">
						<div class="info-item">
							<span class="label">Дата рождения:</span>
							<span class="value">{child.birthDate}</span>
						</div>
					</div>
				</div>
			{/each}
		</div>
	{/if}
</div>

{#if showModal}
	<!-- svelte-ignore a11y_click_events_have_key_events, a11y_no_static_element_interactions -->
	<div class="modal-backdrop" out:fade={{ duration: 250 }} on:click={closeModal}></div>
	<div class="modal" in:fly={{ y: 30 }}>
		<h3>{editChild ? 'Редактировать ребенка' : 'Добавить ребенка'}</h3>
		<form on:submit|preventDefault={saveChild}>
			<div class="form-group">
				<label for="name">Имя ребенка</label>
				<input id="name" bind:value={childForm.fullName} required />
			</div>
			<div class="form-group">
				<label for="birthDate">Дата рождения</label>
				<input id="birthDate" type="date" bind:value={childForm.birthDate} required />
			</div>
			<div class="modal-actions">
				<button type="submit" class="button primary">Сохранить</button>
				<button type="button" class="button secondary" on:click={closeModal}>Отмена</button>
			</div>
		</form>
	</div>
{/if}

<style>
	.children-cabinet {
		padding: 1rem;
	}

	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 2rem;
	}

	.header h2 {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		font-size: 1.5rem;
		color: var(--primary);
		margin: 0;
	}

	.add-btn {
		background: var(--primary);
		color: white;
		border: none;
		border-radius: var(--radius);
		padding: 0.75rem 1.5rem;
		font-size: 0.9rem;
		font-weight: 500;
		cursor: pointer;
		transition: var(--transition);
		display: flex;
		align-items: center;
		gap: 0.5rem;
	}

	.add-btn:hover {
		background: var(--primary-dark);
		transform: translateY(-2px);
	}

	.loader, .error, .empty {
		text-align: center;
		margin: 2rem 0;
		font-size: 1rem;
		color: var(--text-secondary);
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 0.5rem;
	}

	.error {
		color: var(--error);
	}

	.empty {
		color: var(--text-secondary);
	}

	.empty h3 {
		margin: 1rem 0 0.5rem 0;
		color: var(--text-primary);
	}

	.empty p {
		margin: 0;
		font-size: 0.9rem;
	}

	.children-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
		gap: 1.5rem;
	}

	.child-card {
		background: var(--bg-primary);
		border: 1px solid var(--border);
		border-radius: var(--radius);
		padding: 1.5rem;
		transition: var(--transition);
	}

	.child-card:hover {
		transform: translateY(-5px);
		box-shadow: var(--shadow);
	}

	.child-header {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		margin-bottom: 1rem;
	}

	.child-header h3 {
		margin: 0;
		flex: 1;
		color: var(--primary);
	}

	.actions {
		display: flex;
		gap: 0.5rem;
	}

	.icon-btn {
		background: none;
		border: none;
		cursor: pointer;
		padding: 0.25rem;
		border-radius: var(--radius);
		transition: var(--transition);
		display: flex;
		align-items: center;
	}

	.icon-btn.edit {
		color: var(--primary);
	}

	.icon-btn.delete {
		color: var(--error);
	}

	.icon-btn:hover {
		background: var(--bg-hover);
	}

	.child-info {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}

	.info-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.label {
		font-size: 0.9rem;
		color: var(--text-secondary);
	}

	.value {
		font-weight: 500;
		color: var(--text-primary);
	}

	.modal-backdrop {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		z-index: 1000;
	}

	.modal {
		position: fixed;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		background: var(--bg-primary);
		padding: 2rem;
		border-radius: var(--radius);
		box-shadow: var(--shadow);
		z-index: 1001;
		min-width: 400px;
		max-width: 90vw;
		max-height: 90vh;
		overflow-y: auto;
	}

	.modal h3 {
		margin-bottom: 1.5rem;
		font-size: 1.5rem;
		color: var(--primary);
	}

	.form-group {
		margin-bottom: 1rem;
	}

	.form-group label {
		display: block;
		margin-bottom: 0.5rem;
		font-weight: 500;
		color: var(--text-primary);
	}

	.form-group input {
		width: 100%;
		padding: 0.75rem;
		border: 1px solid var(--border);
		border-radius: var(--radius);
		background: var(--bg-primary);
		color: var(--text-primary);
		font-size: 0.9rem;
		transition: var(--transition);
	}

	.form-group input:focus {
		border-color: var(--primary);
		box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
	}

	.modal-actions {
		display: flex;
		gap: 1rem;
		justify-content: flex-end;
		margin-top: 1.5rem;
	}

	.button {
		padding: 0.75rem 1.5rem;
		border-radius: var(--radius);
		font-weight: 500;
		text-decoration: none;
		transition: var(--transition);
		border: none;
		cursor: pointer;
		font-size: 0.9rem;
	}

	.button.primary {
		background: var(--primary);
		color: white;
	}

	.button.primary:hover {
		background: var(--primary-dark);
	}

	.button.secondary {
		background: transparent;
		color: var(--text-primary);
		border: 1px solid var(--border);
	}

	.button.secondary:hover {
		background: var(--bg-hover);
	}

	@media (max-width: 768px) {
		.header {
			flex-direction: column;
			gap: 1rem;
			align-items: stretch;
		}

		.children-grid {
			grid-template-columns: 1fr;
		}

		.modal {
			min-width: 300px;
			margin: 1rem;
		}
	}
</style> 