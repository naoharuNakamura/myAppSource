<script setup lang="ts">

import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import { ROUTES, ROUTE_NAMES } from "../constants/types";
import { UI_TEXTS } from "../constants/messages";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const text = UI_TEXTS.login;

const userEmail = ref("");
const userPassword = ref("");
const hasError = ref(false);

const handleLogin = async () => {
    try {
        await authStore.login({
            userEmail: userEmail.value,
            userPassword: userPassword.value
        });

        const redirectPath = (route.query.redirect as string) || ROUTES.SEARCH;
        router.push(redirectPath);
    } catch (error) {
        hasError.value = true;
        console.error("Login failed:", error);
    }
};

</script>

<template>

    <div class="login-container">

        <div class="login-card">

            <div v-if="hasError" class="error-banner">⚠️ メールアドレスまたはパスワードが間違っています</div>

            <form class="login-form" @submit.prevent>

                <div class="login-group">
                    <label for="userEmail">{{ text.emailLabel }}</label>
                    <input v-model="userEmail" id="userEmail" type="text" :placeholder="text.emailPlaceholder" class="form-control">
                </div>

                <div class="form-group">
                    <label for="userPassword">{{ text.passwordLabel }}</label>
                    <input v-model="userPassword" id="userPassword" type="password" :placeholder="text.passwordPlaceholder"
                        class="form-control" />
                </div>

                <button @click="handleLogin" type="submit" class="btn-primary">
                    {{ text.submitButton }}
                </button>

            </form>

            <div class="signup-redirect">

                <p>{{ text.signupHint }}</p>
                <RouterLink :to="{ name: ROUTE_NAMES.SIGNUP }" class="signup-link-btn">{{ text.signupLink }}</RouterLink>
            </div>

        </div>

    </div>

</template>

<style scoped>
/* 共通ヘッダーの下に綺麗に収まるよう、少し上の余白を調整したスタイル */
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 70vh;
    /* ヘッダーの分、高さを少し調整 */
    background-color: #fafafa;
}

.login-card {
    width: min(100%, 400px);
    padding: 32px;
    background-color: #ffffff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    box-sizing: border-box;
}

.error-banner {
    padding: 12px;
    background-color: #fff5f5;
    border: 1px solid #fed7d7;
    border-radius: 6px;
    color: #c53030;
    font-size: 14px;
    margin-bottom: 20px;
    text-align: center;
    font-weight: 500;
}

.login-form {
    display: flex;
    flex-direction: column;
    gap: 18px;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.form-group label {
    font-size: 13px;
    font-weight: 600;
    color: #4a5568;
}

.form-control {
    padding: 12px;
    font-size: 15px;
    border: 1px solid #cbd5e1;
    border-radius: 6px;
    outline: none;
    box-sizing: border-box;
    width: 100%;
}

.form-control:focus {
    border-color: #3182ce;
}

.login-btn {
    margin-top: 10px;
    padding: 14px;
    font-size: 16px;
    font-weight: bold;
    color: #ffffff;
    background-color: #3182ce;
    border: none;
    border-radius: 6px;
    cursor: pointer;
}

.login-btn:hover {
    background-color: #2b6cb0;
}

.signup-redirect {
    margin-top: 24px;
    text-align: center;
    border-top: 1px solid #e2e8f0;
    padding-top: 16px;
}

.signup-redirect p {
    font-size: 13px;
    color: #718096;
    margin-bottom: 8px;
}

.signup-link-btn {
    background: none;
    border: none;
    color: #3182ce;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
}

.signup-link-btn:hover {
    text-decoration: underline;
}

@media (max-width: 600px) {
    .login-container {
        padding: 20px;
        min-height: auto;
    }

    .login-card {
        padding: 24px;
    }
}
</style>