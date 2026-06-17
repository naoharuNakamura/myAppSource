<script setup lang="ts">
import { onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import { ROUTE_NAMES } from '../constants/types';
import RestaurantCard from './RestaurantCard.vue';
import { UI_TEXTS } from '../constants/messages';
const authStore = useAuthStore();
const text = UI_TEXTS.mypage;

onMounted(async () => {
    if (!authStore.currentUser) return;
    const currentUserUserId = authStore.currentUser?.userId;
    try {
        await authStore.fetchFavoriteRestaurants(currentUserUserId);
    } catch (error) {
        console.error('お気に入りレストランの取得に失敗:', error);
    }
});

</script>

<template>

    <div class="mypage-container">
        <div class="mypage-header">
            <h2>{{ text.userInfoTitle }}</h2>
        </div>
        <div v-if="authStore.currentUser" class="user-info">
            <p><label>{{ text.memberIdLabel }}</label>：{{ authStore.currentUser.userId }}</p>
            <p><label>{{ text.nameLabel }}</label>：{{ authStore.currentUser.userName }}</p>
            <p><label>{{ text.emailLabel }}</label>：{{ authStore.currentUser.userEmail }}</p>
            <p><label>{{ text.phoneLabel }}</label>：{{ authStore.currentUser.userPhone }}</p>
        </div>
    </div>

    <div class="favorites-container">
        <div class="favorites-header">
            <h2>{{ text.favoritesTitle }}</h2>
        </div>
        <div class="card-grid">
            <RestaurantCard v-for="rest in authStore.favoriteRestaurants" :key="rest.restaurantId" :restaurant="rest" />
        </div>
    </div>

    <div class="btn-wrapper">
        <RouterLink :to="{ name: ROUTE_NAMES.SIGNUP }" class="back-btn">{{ text.settingsButton }}</RouterLink>
        <RouterLink :to="{ name: ROUTE_NAMES.LOGIN }" @click.prevent="authStore.logout" class="back-btn">{{ text.logoutButton }}</RouterLink>
    </div>
</template>

<style scoped>
.mypage-container,
.favorites-container {
    width: min(100%, 1200px);
    max-width: 1200px;
    margin: 40px auto;
    padding: 0 24px;
}

.mypage-header,
.favorites-header {
    border-bottom: 1px solid #e2e8f0;
    padding-bottom: 12px;
    margin-bottom: 24px;
}

.mypage-header h2,
.favorites-header h2 {
    margin: 0;
    font-size: 22px;
    font-weight: 800;
    color: #1e293b;
}

.user-info {
    background-color: #ffffff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 24px;
    max-width: 100%;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.user-info p {
    margin: 0;
    font-size: 14px;
    color: #334155;
    display: flex;
    border-bottom: 1px solid #f1f5f9;
    padding-bottom: 8px;
}

.user-info p:last-child {
    border-bottom: none;
    padding-bottom: 0;
}

.user-info p label {
    font-weight: 600;
    color: #64748b;
    width: 100px;
    flex-shrink: 0;
}

.card-grid {
    display: grid;
    grid-template-columns: repeat(1, 1fr);
    gap: 24px;
    max-height: 1000px;
    overflow-y: scroll;
}


@media screen and (min-width: 440px) {
    .card-grid {
        grid-template-columns: repeat(3, 1fr);
    }
}

.btn-wrapper {
    width: min(100%, 1200px);
    margin: 32px auto 64px auto;
    padding: 0 24px;
    display: flex;
    gap: 12px;
    justify-content: space-around;
    flex-wrap: wrap;
}

.back-btn {
    display: inline-block;
    padding: 10px 24px;
    font-size: 14px;
    font-weight: 600;
    border-radius: 8px;
    text-decoration: none;
    transition: all 0.2s ease;
    background-color: #ffffff;
    border: 1px solid #cbd5e1;
    color: #475569;
    width: auto;
}

.back-btn:hover {
    background-color: #f8fafc;
    border-color: #cbd5e1;
    color: #1e293b;
}

/* ログアウトボタンだけ少し強調を落としたり赤を混ぜたりできます（お好みで） */
.btn-wrapper .back-btn:last-child:hover {
    background-color: #fef2f2;
    border-color: #fca5a5;
    color: #dc2626;
}

@media screen and (max-width: 440px) {

    .mypage-container,
    .favorites-container {
        padding: 0 16px;
    }

    .user-info {
        padding: 20px;
    }

    .btn-wrapper {
        display: flex;
        flex-direction: row;
    }

    .card-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}
</style>