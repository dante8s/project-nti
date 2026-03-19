<template>
    <div class="wrap">
        <div class="box">
            <h1>Вхід до NTI</h1>

            <div v-if="error" class="error">{{ error }}</div>

            <form @submit.prevent="handleLogin">
                <div class="field">
                    <label>Email</label>
                    <input v-model="email" type="email" placeholder="your@email.com" required />
                </div>
                <div class="field">
                    <label>Пароль</label>
                    <input v-model="password" type="password" placeholder="••••••" required />
                </div>
                <button type="submit" :disabled="loading">
                    {{ loading ? 'Завантаження...' : 'Увійти' }}
                </button>
            </form>
            <p>
                <router-link to="/forgot-password">
                    Забули пароль?
                </router-link>
            </p>
            <p>
                Немає акаунту?
                <router-link to="/register">
                    Зареєструватись
                </router-link>
            </p>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const email = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function handleLogin() {
    error.value = ''
    loading.value = true
    try {
        const data = await auth.login(
            email.value,
            password.value
        )
        if (data.role === 'ADMIN') {
            router.push('/admin')
        } else {
            router.push('/dashboard')
        }
    } catch (e) {
        error.value = 'Невірний email або пароль'
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
    max-width: 400px;
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

input {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
    box-sizing: border-box;
}

input:focus {
    outline: none;
    border-color: #4f46e5;
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