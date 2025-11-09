<script lang="ts">
	import { onMount } from 'svelte';
	import { fly } from 'svelte/transition';
	import type {UserSession} from '$lib/stores/userStore';
	import { Search, User, FileText } from 'lucide-svelte';
    import type {Child} from "$lib/models";
	import {PUBLIC_API_URL} from "$env/static/public";
	import {toast} from "svelte-sonner";

    export let user: UserSession;

	let currentStep: 'shift-list' | 'child-selection' | 'medical-card' | 'examination' = 'shift-list';
	let selectedChild: any = null;
	let medicalCard: any = null;
	let newVisit = {
		description: '',
		diagnosis: '',
		recommendations: '',
		medications: ''
	};

	let children: Child[] = [];
	let searchTerm = '';

	onMount(() => {
		loadChildren();
	});

	async function loadChildren() {
		try {
            const res = await fetch(`${PUBLIC_API_URL}/api/children`, {
                headers: {Authorization: `Bearer ${user.accessToken}`}
            });
            if (!res.ok) {
                toast.error('Ошибка загрузки данных о детях');
            } else {
                children = await res.json();
            }
		} catch (error) {
			console.error('Ошибка загрузки детей:', error);
		}
	}

	async function selectChild(child: any) {
		selectedChild = child;
		await loadMedicalCard(child.id);
		currentStep = 'medical-card';
	}

	async function loadMedicalCard(childId: number) {
		try {
            const res = await fetch(`${PUBLIC_API_URL}/api/medical-cards/child/${childId}`, {
                headers: {Authorization: `Bearer ${user.accessToken}`}
            });
            if (!res.ok) {
                toast.error('Ошибка загрузки карты ребёнка');
            } else {
                medicalCard = await res.json();
            }
		} catch (error) {
			console.error('Ошибка загрузки медкарты:', error);
			medicalCard = null;
		}
	}

	function startExamination() {
		currentStep = 'examination';
	}

	async function saveExamination() {
		try {
			const visitData = {
				date: new Date().toISOString().split('T')[0],
				child: { id: selectedChild.id },
				doctor: { id: user.userId },
				description: newVisit.description,
				recommendations: newVisit.recommendations,
				medications: newVisit.medications
			};

            const res = await fetch(`${PUBLIC_API_URL}/api/medical-visits`, {
				method: 'POST',
                headers: {
                    Authorization: `Bearer ${user.accessToken}`,
                    'Content-Type': 'application/json'
                },
				body: JSON.stringify(visitData)
            });

            if (!res.ok) {
                toast.error('Ошибка сохранения визита');
            } else {
				resetProcess();
				currentStep = 'shift-list';
                toast.success('Осмотр успешно сохранён');
			}
		} catch (error) {
			console.error('Ошибка сохранения осмотра:', error);
		}
	}

	function resetProcess() {
		selectedChild = null;
		medicalCard = null;
		newVisit = {
			description: '',
			diagnosis: '',
			recommendations: '',
			medications: ''
		};
	}

	function backToShiftList() {
		resetProcess();
		currentStep = 'shift-list';
	}

	function backToMedicalCard() {
		currentStep = 'medical-card';
	}

	$: filteredChildren = children.filter(child =>
		`${child.fullName}`.toLowerCase().includes(searchTerm.toLowerCase())
	);
</script>

<div class="medical-examinations">
	{#if currentStep === 'shift-list'}
		<div class="step-header" in:fly={{ y: 20 }}>
			<h2>Дети в вашей смене</h2>
			<p>Выберите ребёнка для проведения медицинского осмотра</p>
		</div>

		<div class="search-box" in:fly={{ y: 20, delay: 100 }}>
			<span class="icon">
				<Search size={20} />
			</span>
			<input
				type="text"
				placeholder="Поиск по имени..."
				bind:value={searchTerm}
			/>
		</div>

		<div class="children-grid" in:fly={{ y: 20, delay: 200 }}>
			{#each filteredChildren as child}
	            <!-- svelte-ignore a11y_click_events_have_key_events, a11y_no_static_element_interactions -->
				<div class="child-card" on:click={() => selectChild(child)}>
					<div class="child-avatar">
						<User size={24} />
					</div>
					<div class="child-info">
						<h3>{child.fullName}</h3>
						<p>Возраст: {child.birthDate}</p>
						<p>Родитель: {child.parentUsername}</p>
					</div>
					<div class="child-action">
						<FileText size={18} />
					</div>
				</div>
			{:else}
				<div class="empty-state">
					<p>Нет детей в вашей смене</p>
				</div>
			{/each}
		</div>

	{:else if currentStep === 'medical-card'}
		<div class="step-header" in:fly={{ y: 20 }}>
			<h2>Медицинская карта</h2>
			<p>{selectedChild.fullName}</p>
			<button class="btn-secondary" on:click={backToShiftList}>← Назад к списку</button>
		</div>

		<div class="medical-card-content" in:fly={{ y: 20, delay: 100 }}>
			{#if medicalCard}
				<div class="card-section">
					<h3>Информация о здоровье</h3>
					<p>{medicalCard.healthInfo || 'Не указано'}</p>
				</div>

				<div class="card-section">
					<h3>Хронические заболевания</h3>
					<p>{medicalCard.chronicDiseases || 'Не указано'}</p>
				</div>

				<div class="card-section">
					<h3>Аллергии</h3>
					<p>{medicalCard.allergies || 'Не указано'}</p>
				</div>

				<div class="card-section">
					<h3>Прививки</h3>
					<p>{medicalCard.vaccinations || 'Не указано'}</p>
				</div>

				<div class="card-section">
					<h3>Примечания</h3>
					<p>{medicalCard.notes || 'Не указано'}</p>
				</div>
			{:else}
				<div class="empty-state">
					<p>Медицинская карта не найдена</p>
				</div>
			{/if}

			<div class="actions">
				<button class="btn-primary" on:click={startExamination}>
					Начать осмотр
				</button>
			</div>
		</div>

	{:else if currentStep === 'examination'}
		<div class="step-header" in:fly={{ y: 20 }}>
			<h2>Медицинский осмотр</h2>
			<p>{selectedChild.fullName}</p>
			<button class="btn-secondary" on:click={backToMedicalCard}>← Назад к карте</button>
		</div>

		<div class="examination-form" in:fly={{ y: 20, delay: 100 }}>
			<div class="form-section">
				<label for="description">Описание осмотра</label>
				<textarea
					id="description"
					bind:value={newVisit.description}
					placeholder="Опишите состояние ребёнка, жалобы, результаты осмотра..."
					rows="4"
				></textarea>
			</div>

			<div class="form-section">
				<label for="diagnosis">Диагноз</label>
				<input
					id="diagnosis"
					type="text"
					bind:value={newVisit.diagnosis}
					placeholder="Укажите диагноз..."
				/>
			</div>

			<div class="form-section">
				<label for="recommendations">Рекомендации</label>
				<textarea
					id="recommendations"
					bind:value={newVisit.recommendations}
					placeholder="Дайте рекомендации по лечению и уходу..."
					rows="3"
				></textarea>
			</div>

			<div class="form-section">
				<label for="medications">Назначенные лекарства</label>
				<input
					id="medications"
					type="text"
					bind:value={newVisit.medications}
					placeholder="Укажите назначенные лекарства..."
				/>
			</div>

			<div class="actions">
				<button class="btn-secondary" on:click={backToMedicalCard}>
					Отмена
				</button>
				<button class="btn-primary" on:click={saveExamination}>
					Сохранить осмотр
				</button>
			</div>
		</div>
	{/if}
</div>

<style>
	.medical-examinations {
		padding: 1rem 0;
	}

	.step-header {
		margin-bottom: 2rem;
	}

	.step-header h2 {
		font-size: 1.5rem;
		margin-bottom: 0.5rem;
		color: var(--text-primary);
	}

	.step-header p {
		color: var(--text-secondary);
		margin-bottom: 1rem;
	}

	.search-box {
		position: relative;
		margin-bottom: 1.5rem;
	}

	.search-box .icon {
		position: absolute;
		left: 1rem;
		top: 50%;
		transform: translateY(-50%);
		color: var(--text-secondary);
	}

	.search-box input {
		width: 100%;
		padding: 0.75rem 1rem 0.75rem 3rem;
		border: 1px solid var(--border);
		border-radius: var(--radius);
		background: var(--bg-secondary);
		color: var(--text-primary);
		font-size: 0.9rem;
	}

	.children-grid {
		display: grid;
		gap: 1rem;
	}

	.child-card {
		display: flex;
		align-items: center;
		padding: 1rem;
		background: var(--bg-secondary);
		border: 1px solid var(--border);
		border-radius: var(--radius);
		cursor: pointer;
		transition: var(--transition);
		gap: 1rem;
	}

	.child-card:hover {
		background: var(--bg-hover);
		transform: translateY(-2px);
	}

	.child-avatar {
		width: 50px;
		height: 50px;
		border-radius: 50%;
		background: var(--primary);
		display: flex;
		align-items: center;
		justify-content: center;
		color: white;
	}

	.child-info {
		flex: 1;
	}

	.child-info h3 {
		margin: 0 0 0.25rem 0;
		font-size: 1rem;
		color: var(--text-primary);
	}

	.child-info p {
		margin: 0.125rem 0;
		font-size: 0.8rem;
		color: var(--text-secondary);
	}

	.child-action {
		color: var(--text-secondary);
	}

	.medical-card-content,
	.examination-form {
		background: var(--bg-secondary);
		border-radius: var(--radius);
		padding: 1.5rem;
	}

	.card-section {
		margin-bottom: 1.5rem;
	}

	.card-section h3 {
		font-size: 1rem;
		margin-bottom: 0.5rem;
		color: var(--text-primary);
	}

	.card-section p {
		color: var(--text-secondary);
		line-height: 1.5;
	}

	.form-section {
		margin-bottom: 1.5rem;
	}

	.form-section label {
		display: block;
		margin-bottom: 0.5rem;
		font-weight: 500;
		color: var(--text-primary);
	}

	.form-section input,
	.form-section textarea {
		width: -webkit-fill-available;
		padding: 0.75rem;
		border: 1px solid var(--border);
		border-radius: var(--radius);
		background: var(--bg-primary);
		color: var(--text-primary);
		font-size: 0.9rem;
		font-family: inherit;
	}

	.form-section textarea {
		resize: vertical;
		min-height: 80px;
	}

	.actions {
		display: flex;
		gap: 1rem;
		justify-content: flex-end;
		margin-top: 2rem;
	}

	.btn-primary,
	.btn-secondary {
		padding: 0.75rem 1.5rem;
		border: none;
		border-radius: var(--radius);
		font-weight: 500;
		cursor: pointer;
		transition: var(--transition);
		font-size: 0.9rem;
	}

	.btn-primary {
		background: var(--primary);
		color: white;
	}

	.btn-primary:hover {
		background: var(--primary-dark);
	}

	.btn-secondary {
		background: var(--bg-primary);
		color: var(--text-primary);
		border: 1px solid var(--border);
	}

	.btn-secondary:hover {
		background: var(--bg-hover);
	}

	.empty-state {
		text-align: center;
		padding: 3rem;
		color: var(--text-secondary);
	}

	@media (max-width: 768px) {
		.medical-card-content,
		.examination-form {
			padding: 1rem;
		}

		.actions {
			flex-direction: column;
		}

		.btn-primary,
		.btn-secondary {
			width: 100%;
		}
	}
</style>