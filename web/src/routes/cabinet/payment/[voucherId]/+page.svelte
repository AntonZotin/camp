<script lang="ts">
	import {onMount} from 'svelte';
	import {fade, fly} from 'svelte/transition';
	import {ArrowLeft, Calendar, CheckCircle, CreditCard, Loader, Ticket, User} from 'lucide-svelte';
	import {PUBLIC_API_URL} from '$env/static/public';
	import {userStore} from '$lib/stores/userStore';
	import {goto} from '$app/navigation';
	import {get} from 'svelte/store';
	import {toast} from 'svelte-sonner';
	import type {Voucher} from '$lib/models';
	import {page} from "$app/stores";

	let user = get(userStore);
	const unsubUser = userStore.subscribe((u) => (user = u));

	let voucher: Voucher | null = null;
	let loading = true;
	let processing = false;
	let paymentSuccess = false;

	let cardNumber = '';
	let cardHolder = '';
	let expiryDate = '';
	let cvv = '';
	let saveCard = false;

	onMount(async () => {
		await loadVoucher();
	});

	async function loadVoucher() {
		try {
			if (!user) {
				await goto('/login');
				return () => { unsubUser(); };
			}

			const response = await fetch(`${PUBLIC_API_URL}/api/vouchers/${$page.params.voucherId}`, {
				headers: { Authorization: `Bearer ${user.accessToken}` }
			});

			if (!response.ok) {
				throw new Error('Путёвка не найдена');
			}

			voucher = await response.json();
			if (voucher?.status !== 'CONFIRMED') {
				toast.error('Эту путёвку нельзя оплатить');
				goto('/cabinet');
				return;
			}
		} catch (error) {
			toast.error('Ошибка загрузки данных путёвки');
			await goto('/cabinet');
		} finally {
			loading = false;
		}
	}

	async function processPayment() {
		if (processing || !user || !voucher) return;

		if (!cardNumber || !cardHolder || !expiryDate || !cvv) {
			toast.error('Заполните все поля формы');
			return;
		}

		if (cardNumber.replace(/\s/g, '').length !== 16) {
			toast.error('Номер карты должен содержать 16 цифр');
			return;
		}

		processing = true;

		try {
			const body = JSON.stringify({
				voucherId: voucher.id,
				parentId: user.userId,
				amount: calculateAmount(),
				method: 'credit_card',
				status: 'paid'
			});
			const paymentResponse = await fetch(`${PUBLIC_API_URL}/api/payments`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
					Authorization: `Bearer ${user.accessToken}`
				},
				body
			});

			if (!paymentResponse.ok) {
				toast.error('Ошибка при обработке платежа');
			} else {
				paymentSuccess = true;
				toast.success('Путёвка успешно оплачена!');

				setTimeout(() => {
					goto('/cabinet');
				}, 3000);
			}
		} catch (error) {
			toast.error('Ошибка при обработке платежа');
			console.error('Payment error:', error);
		} finally {
			processing = false;
		}
	}

	function calculateAmount(): number {
		return 15000;
	}

	function formatCardNumber(value: string): string {
		const cleaned = value.replace(/\s/g, '').replace(/\D/g, '');
		const matches = cleaned.match(/\d{1,4}/g);
		return matches ? matches.join(' ') : '';
	}

	function formatExpiryDate(value: string): string {
		const cleaned = value.replace(/\D/g, '');
		if (cleaned.length >= 3) {
			return cleaned.slice(0, 2) + '/' + cleaned.slice(2, 4);
		}
		return cleaned;
	}
</script>

<div class="payment-page">
	<div class="stars-bg"></div>

	<div class="container">
		<div class="payment-container" transition:fade>
			{#if loading}
				<div class="loader-full">
					<Loader size={32} />
					<span>Загрузка данных...</span>
				</div>
			{:else if voucher && !paymentSuccess}
				<div class="payment-header" in:fly={{ y: 30 }}>
					<a href="/cabinet" class="back-btn">
						<ArrowLeft size={20} />
						<span>Назад к путёвкам</span>
					</a>
					<h1>
						<CreditCard size={28} />
						<span>Оплата путёвки</span>
					</h1>
				</div>

				<div class="payment-content">
					<div class="voucher-summary" in:fly={{ y: 30, delay: 200 }}>
						<h2>Информация о путёвке</h2>
						<div class="summary-grid">
							<div class="summary-item">
								<Ticket size={18} />
								<span class="label">Номер путёвки:</span>
								<span class="value">#{voucher.id}</span>
							</div>
							<div class="summary-item">
								<User size={18} />
								<span class="label">Ребёнок:</span>
								<span class="value">{voucher.child?.fullName || 'Не указан'}</span>
							</div>
							<div class="summary-item">
								<Calendar size={18} />
								<span class="label">Смена:</span>
								<span class="value">{voucher.session?.name || 'Не указана'}</span>
							</div>
							<div class="summary-item">
								<CreditCard size={18} />
								<span class="label">Сумма к оплате:</span>
								<span class="value amount">{calculateAmount().toLocaleString()} ₽</span>
							</div>
						</div>
					</div>

					<div class="payment-form" in:fly={{ y: 30, delay: 400 }}>
						<h2>Данные банковской карты</h2>
						<form on:submit|preventDefault={processPayment}>
							<div class="form-group">
								<label for="cardNumber">Номер карты</label>
								<input
									id="cardNumber"
									type="text"
									bind:value={cardNumber}
									on:input={(e) => cardNumber = formatCardNumber(e.currentTarget.value)}
									placeholder="1234 5678 9012 3456"
									maxlength="19"
									required
									disabled={processing}
								/>
							</div>

							<div class="form-group">
								<label for="cardHolder">Имя владельца карты</label>
								<input
									id="cardHolder"
									type="text"
									bind:value={cardHolder}
									placeholder="IVAN IVANOV"
									required
									disabled={processing}
								/>
							</div>

							<div class="form-row">
								<div class="form-group">
									<label for="expiryDate">Срок действия</label>
									<input
										id="expiryDate"
										type="text"
										bind:value={expiryDate}
										on:input={(e) => expiryDate = formatExpiryDate(e.currentTarget.value)}
										placeholder="MM/YY"
										maxlength="5"
										required
										disabled={processing}
									/>
								</div>

								<div class="form-group">
									<label for="cvv">CVV/CVC</label>
									<input
										id="cvv"
										type="password"
										bind:value={cvv}
										placeholder="123"
										maxlength="3"
										required
										disabled={processing}
									/>
								</div>
							</div>

							<div class="form-checkbox">
								<input
									id="saveCard"
									type="checkbox"
									bind:checked={saveCard}
									disabled={processing}
								/>
								<label for="saveCard">Сохранить карту для будущих платежей</label>
							</div>

							<button class="pay-btn" type="submit" disabled={processing}>
								{#if processing}
									<Loader size={18} />
									<span>Обработка...</span>
								{:else}
									<CreditCard size={18} />
									<span>Оплатить {calculateAmount().toLocaleString()} ₽</span>
								{/if}
							</button>
						</form>
					</div>
				</div>
			{:else if paymentSuccess}
				<div class="success-screen" in:fly={{ y: 30 }}>
					<div class="success-icon">
						<CheckCircle size={64} />
					</div>
					<h2>Оплата успешно завершена!</h2>
					<p>Путёвка #{voucher?.id} для {voucher?.child?.fullName} оплачена.</p>
					<p>Статус путёвки изменён на "Оплачено".</p>
					<div class="success-actions">
						<a href="/cabinet" class="btn-primary">Вернуться в кабинет</a>
						<a href="/cabinet" class="btn-secondary">К моим путёвкам</a>
					</div>
				</div>
			{/if}
		</div>
	</div>
</div>

<style>
	.payment-page {
		min-height: 100vh;
		padding: 2rem 0;
		display: flex;
		align-items: center;
	}

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

	.container {
		width: 100%;
		max-width: 800px;
		margin: 0 auto;
		padding: 0 1rem;
	}

	.payment-container {
		background: var(--bg-primary);
		border-radius: var(--radius);
		padding: 2rem;
		box-shadow: var(--shadow);
	}

	.loader-full {
		text-align: center;
		padding: 4rem 2rem;
		color: var(--text-secondary);
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 1rem;
	}

	.payment-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-bottom: 2rem;
		padding-bottom: 1rem;
		border-bottom: 1px solid var(--border);
	}

	.back-btn {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		color: var(--primary);
		text-decoration: none;
		font-weight: 500;
		transition: var(--transition);
	}

	.back-btn:hover {
		color: var(--primary-dark);
	}

	.payment-header h1 {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		margin: 0;
		color: var(--primary);
		font-size: 1.75rem;
	}

	.payment-content {
		display: grid;
		gap: 2rem;
	}

	.voucher-summary, .payment-form {
		background: var(--bg-secondary);
		border-radius: var(--radius);
		padding: 1.5rem;
	}

	.voucher-summary h2, .payment-form h2 {
		margin: 0 0 1.5rem 0;
		color: var(--text-primary);
		font-size: 1.25rem;
	}

	.summary-grid {
		display: grid;
		gap: 1rem;
	}

	.summary-item {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		padding: 0.75rem;
		background: var(--bg-primary);
		border-radius: var(--radius);
	}

	.summary-item .label {
		color: var(--text-secondary);
		font-size: 0.9rem;
	}

	.summary-item .value {
		font-weight: 500;
		color: var(--text-primary);
		margin-left: auto;
	}

	.summary-item .amount {
		color: var(--success);
		font-weight: 600;
		font-size: 1.1rem;
	}

	.form-group {
		margin-bottom: 1.5rem;
	}

	.form-row {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 1rem;
	}

	label {
		display: block;
		margin-bottom: 0.5rem;
		font-weight: 500;
		color: var(--text-primary);
	}

	input[type="text"],
	input[type="password"] {
		width: 100%;
		padding: 0.75rem 1rem;
		border: 1px solid var(--border);
		border-radius: var(--radius);
		background: var(--bg-primary);
		color: var(--text-primary);
		font-size: 0.9rem;
		transition: var(--transition);
	}

	input[type="text"]:focus,
	input[type="password"]:focus {
		outline: none;
		border-color: var(--primary);
		box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.1);
	}

	input:disabled {
		opacity: 0.6;
		cursor: not-allowed;
	}

	.form-checkbox {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		margin-bottom: 2rem;
	}

	.form-checkbox input[type="checkbox"] {
		width: auto;
	}

	.pay-btn {
		width: 100%;
		background: var(--success-light);
		color: white;
		border: none;
		border-radius: var(--radius);
		padding: 1rem 2rem;
		font-size: 1rem;
		font-weight: 600;
		cursor: pointer;
		transition: var(--transition);
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 0.75rem;
	}

	.pay-btn:hover:not(:disabled) {
		background: var(--success-dark);
		transform: translateY(-2px);
	}

	.pay-btn:disabled {
		opacity: 0.7;
		cursor: not-allowed;
		transform: none;
	}

	.success-screen {
		text-align: center;
		padding: 3rem 2rem;
	}

	.success-icon {
		color: var(--success);
		margin-bottom: 1.5rem;
	}

	.success-screen h2 {
		color: var(--success);
		margin-bottom: 1rem;
		font-size: 1.75rem;
	}

	.success-screen p {
		color: var(--text-secondary);
		margin-bottom: 0.5rem;
	}

	.success-actions {
		display: flex;
		gap: 1rem;
		justify-content: center;
		margin-top: 2rem;
	}

	.btn-primary, .btn-secondary {
		padding: 0.75rem 1.5rem;
		border-radius: var(--radius);
		text-decoration: none;
		font-weight: 500;
		transition: var(--transition);
	}

	.btn-primary {
		background: var(--primary);
		color: white;
	}

	.btn-primary:hover {
		background: var(--primary-dark);
	}

	.btn-secondary {
		background: var(--bg-secondary);
		color: var(--text-primary);
		border: 1px solid var(--border);
	}

	.btn-secondary:hover {
		background: var(--bg-hover);
	}

	@media (max-width: 768px) {
		.payment-container {
			padding: 1.5rem;
			margin: 1rem;
		}

		.payment-header {
			flex-direction: column;
			gap: 1rem;
			text-align: center;
		}

		.form-row {
			grid-template-columns: 1fr;
		}

		.success-actions {
			flex-direction: column;
		}
	}
</style>