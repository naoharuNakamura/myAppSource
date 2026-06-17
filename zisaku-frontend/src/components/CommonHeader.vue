<script setup ts>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth';
import { ROUTES, ROUTE_NAMES } from '../constants/types';

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// ① 現在のルート名を見て、ログイン・新規登録画面かどうかを判定
const isAuthPage = computed(() => {
    return route.name === ROUTE_NAMES.LOGIN || route.name === ROUTE_NAMES.SIGNUP
})

// 会員情報を右上に表示するかどうかの判定（ログイン・登録画面以外、かつログイン中のみ表示）
const showUserInfo = computed(() => {
    return !isAuthPage.value && authStore.isLoggedIn
})

const memberName = computed(() => {
    return authStore.currentUser?.userName || ''
})
</script>

<template>

    <header class="app-header" :class="{ 'is-large': isAuthPage }">
        <RouterLink :to="{ name: ROUTE_NAMES.SEARCH }" class="logo"><h1>My Gourmet Map</h1></RouterLink> 
        <RouterLink :to="{ name: ROUTE_NAMES.MYPAGE }" v-if="showUserInfo" class="user-info">
            <span class="user-icon">👤</span>
            <span class="user_name">{{memberName}}</span>
        </RouterLink>
    </header>

</template>

<style scoped>
.app-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05), 0 1px 2px rgba(0, 0, 0, 0.02);
    border-bottom: 1px solid #f1f5f9;
    background-color: #ffffff;
    box-sizing: border-box;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    min-height: 64px;
    position: sticky;
    top: 0;
    z-index: 100;
    width: 100%;
    min-width: 0;
}

.app-header.is-large {
    height: 80px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.logo {
    text-decoration: none;
    color: #3b82f6;
    transition: opacity 0.2s;
    min-width: 0;
}

.logo:hover {
    opacity: 0.85;
}

.logo h1 {
    font-size: 20px;
    margin: 0;
    font-weight: 800;
    letter-spacing: -0.5px;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    text-decoration: none;
    color: #475569;
    padding: 6px 12px;
    border-radius: 20px;
    background-color: #f8fafc;
    transition: all 0.2s ease;
    font-size: 14px;
    font-weight: 500;
}

.user-info:hover {
    background-color: #f1f5f9;
    color: #1e293b;
}

.user-icon {
    font-size: 16px;
}


@media screen and (max-width: 440px) {
    .app-header {
        flex-wrap: wrap;
        justify-content: center;
        gap: 12px;
        padding: 12px 16px;
        text-align: center;
    }

    .logo {
        width: 100%;
    }

    .logo h1 {
        font-size: 18px;
    }

    .user-info {
        width: 100%;
        justify-content: center;
        padding: 10px 14px;
    }
}
</style>