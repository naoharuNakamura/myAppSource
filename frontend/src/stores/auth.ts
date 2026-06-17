// src/stores/auth.ts

import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import { apiService } from '../services/api';
import { type UserResponse, type Restaurant, STORAGE_KEYS } from '../constants/types';
import { ERROR_MESSAGES } from '../constants/messages';

export const useAuthStore = defineStore('auth', () => {
    // ==========================================
    // ① 状態（State）
    // ==========================================
    const savedUser = localStorage.getItem(STORAGE_KEYS.CURRENT_USER);
    const currentUser = ref<UserResponse | null>(savedUser ? JSON.parse(savedUser) : null);
    const favoriteRestaurants = ref<Restaurant[]>([]);

    // ==========================================
    // ② ゲッター（Getters）
    // ==========================================
    const isLoggedIn = computed(() => currentUser.value !== null);

    const isFavorite = (restaurantId: number) => {
        return currentUser.value?.favoriteIds?.includes(restaurantId) || false;
    };

    // ==========================================
    // ③ アクション（Actions）
    // ==========================================

    // 🌟 お気に入りIDの配列だけを取得（userId の引数を削除）
    const fetchFavoriteIds = async () => {
        if (!currentUser.value) return;
        try {
            // 新しい apiService.getFavorites() を呼び出し
            const response = await apiService.getFavorites();
            currentUser.value.favoriteIds = response.data;
            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(currentUser.value));
        } catch (error) {
            console.error(ERROR_MESSAGES.FAVORITE_ID_FETCH_FAILED, error);
        }
    };

    // 🌟 お気に入り詳細一覧を取得（userId の引数を削除）
    const fetchFavoriteRestaurants = async () => {
        try {
            const response = await apiService.getFavoriteDetails();
            console.log(response.data)
            favoriteRestaurants.value = response.data;
        } catch (error) {
            console.error(ERROR_MESSAGES.FAVORITE_DETAILS_FETCH_FAILED, error);
        }
    };


    const login = async (loginData: {userEmail: string; userPassword: string}) => {
        try {
            const response = await apiService.login(loginData);

            // サーバーからトークンとユーザー情報が返ってくる想定
            const { token, user } = response.data;

            // トークンを保存（リクエストインターセプター用）
            localStorage.setItem(STORAGE_KEYS.TOKEN, token);

            currentUser.value = user;

            // 💡 userIdを渡さずに、ログインユーザー自身のお気に入りを取得するように変更
            await fetchFavoriteIds();
            await fetchFavoriteRestaurants();

            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(currentUser.value));
            return true;
        } catch (error) {
            console.error(ERROR_MESSAGES.LOGIN_FAILED_LOG, error);
            throw error;
        }
    };


    // プロフィール更新
    const updateProfile = async (updateData: UserResponse) => {
        try {
            const response = await apiService.updateProfile(updateData);
            const updatedUserFromServer: UserResponse = response.data;

            updatedUserFromServer.favoriteIds = currentUser.value?.favoriteIds || [];
            currentUser.value = updatedUserFromServer;

            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(currentUser.value));
            return true;
        } catch (error) {
            console.error(ERROR_MESSAGES.PROFILE_UPDATE_FAILED_LOG, error);
            throw error;
        }
    };

    // ログアウト処理
    const logout = () => {
        currentUser.value = null;
        favoriteRestaurants.value = [];
        localStorage.removeItem(STORAGE_KEYS.CURRENT_USER);
        localStorage.removeItem(STORAGE_KEYS.TOKEN);
    };

    // 🌟 お気に入りの追加・解除トグル（userId を排除し、超シンプル化）
    const toggleFavorite = async (restaurantId: number) => {
        if (!currentUser.value) return;

        try {
            // 💡 引数は restaurantId だけに！
            const response = await apiService.toggleFavorite(restaurantId);

            if (!currentUser.value.favoriteIds) {
                currentUser.value.favoriteIds = [];
            }

            // サーバーのレスポンスに応じてフロントの状態を更新
            if (response.data.status === 'added') {
                if (!currentUser.value.favoriteIds.includes(restaurantId)) {
                    currentUser.value.favoriteIds.push(restaurantId);
                }
            } else {
                currentUser.value.favoriteIds = currentUser.value.favoriteIds.filter(id => id !== restaurantId);
            }

            // localStorage に同期
            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(currentUser.value));

            // 最新状態をバックエンドと再同期
            await fetchFavoriteIds();
            await fetchFavoriteRestaurants();

        } catch (error) {
            console.error("お気に入り更新失敗:", error);
        }
    };

    return {
        currentUser,
        isLoggedIn,
        fetchFavoriteIds,
        fetchFavoriteRestaurants,
        favoriteRestaurants,
        login,
        logout,
        updateProfile,
        isFavorite,
        toggleFavorite
    };
});