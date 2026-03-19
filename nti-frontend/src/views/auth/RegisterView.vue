<template>
    <div class="wrap">
        <div class="box">
            <h1>Реєстрація в NTI</h1>

            <div v-if="error" class="error">{{ error }}</div>

            <form @submit.prevent="handleRegister">
                <div class="field">
                    <label>Ім'я та прізвище</label>
                    <input v-model="form.name" type="text" placeholder="Іван Петренко" required />
                </div>
                <div class="field">
                    <label>Email</label>
                    <input v-model="form.email" type="email" placeholder="your@email.com" required />
                </div>
                <div class="field">
                    <label>Пароль</label>
                    <input v-model="form.password" type="password" placeholder="Мінімум 6 символів" required />
                </div>
                <div class="field">
                    <label>Я є</label>
                    <select v-model="form.role" required>
                        <option value="">Оберіть тип акаунту</option>
                        <option value="STUDENT">Студент</option>
                        <option value="FIRM">Компанія / Партнер</option>
                        <option value="MENTOR">Ментор</option>
                    </select>
                </div>
                <div class="field checkbox">
                    <input v-model="form.gdprConsent" type="checkbox" id="gdpr" />
                    <label for="gdpr">
                        Я погоджуюсь на обробку персональних даних
                    </label>
                </div>
                <button type="submit" :disabled="loading || !form.gdprConsent">
                    {{ loading ? 'Реєстрація...' : 'Зареєструватись' }}
                </button>
            </form>

            <p>
                Вже є акаунт?
                <router-link to="/login">Увійти</router-link>
            </p>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({
    name: '',
    email: '',
    password: '',
    role: '',
    gdprConsent: false
})

const loading = ref(false)
const error = ref('')

async function handleRegister() {
    error.value = ''
    loading.value = true
    try {
        await auth.register(form)
        router.push('/dashboard')
    } catch (e) {
        error.value =
            e.response?.data || 'Помилка реєстрації'
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.wrap {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f5f5;
}

.box {
    background: white;
    padding: 2rem;
    border-radius: 12px;
    width: 100%;
    max-width: 420px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

h1 {
    margin-bottom: 1.5rem;
    font-size: 1.5rem;
    text-align: center;
}

.field {
    margin-bottom: 1rem;
}

label {
    display: block;
    font-size: 0.875rem;
    margin-bottom: 4px;
    color: #555;
}

input,
select {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
    box-sizing: border-box;
}

input:focus,
select:focus {
    outline: none;
    border-color: #4f46e5;
}

.checkbox {
    display: flex;
    align-items: center;
    gap: 8px;
}

.checkbox input {
    width: auto;
}

.checkbox label {
    margin: 0;
    font-size: 0.875rem;
}

button {
    width: 100%;
    padding: 10px;
    background: #4f46e5;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 1rem;
    cursor: pointer;
    margin-top: 0.5rem;
}

button:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.error {
    background: #fee2e2;
    color: #dc2626;
    padding: 10px;
    border-radius: 8px;
    margin-bottom: 1rem;
    font-size: 0.875rem;
}

p {
    text-align: center;
    margin-top: 1rem;
    font-size: 0.875rem;
}

a {
    color: #4f46e5;
}
</style>