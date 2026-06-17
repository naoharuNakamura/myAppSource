// src/stores/auth.ts

import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import { apiService } from '../services/api';
import { type UserResponse, type Restaurant, STORAGE_KEYS } from '../constants/types';
import { ERROR_MESSAGES } from '../constants/messages';

export const useAuthStore = defineStore('auth', () => {
    // ① 状態（State）

    const savedUser = localStorage.getItem(STORAGE_KEYS.CURRENT_USER);
    const currentUser = ref<UserResponse | null>(savedUser ? JSON.parse(savedUser) : null);
    const favoriteRestaurants = ref<Restaurant[]>([]);

    // ② ゲッター（Getters）
    const isLoggedIn = computed(() => currentUser.value !== null);

    // 🌟 復活: お気に入りIDの配列だけを取得する（星マークの点灯判定用）
    const fetchFavoriteIds = async (userId: number) => {
        if (!currentUser.value) return;
        try {
            const response = await apiService.getFavoritesByUserId(userId);
            currentUser.value.favoriteIds = response.data;
            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(currentUser.value));
        } catch (error) {
            console.error(ERROR_MESSAGES.FAVORITE_ID_FETCH_FAILED, error);
        }
    };

    // お気に入り詳細一覧を取得する
    const fetchFavoriteRestaurants = async (userId: number) => {
        try {
            const response = await apiService.getFavoriteDetails(userId);
            favoriteRestaurants.value = response.data;
        } catch (error) {
            console.error(ERROR_MESSAGES.FAVORITE_DETAILS_FETCH_FAILED, error);
        }
    };

    // ③ アクション（Actions）

    // 💡 ログイン処理
    // ここで受け取るデータの型を明確に指定します（コンポーネント側で間違えないようにするため）
    const login = async (loginData: { userEmail: string; userPassword: string }) => {
        try {
            const response = await apiService.login(loginData);

            // 💡 サーバーからトークンが返ってくる想定（例: response.data.token）
            // ログインAPIのレスポンス形式を確認してください
            const { token, user } = response.data;

            // トークンを保存（これがないとインターセプターで送信されません）
            localStorage.setItem(STORAGE_KEYS.TOKEN, token);

            currentUser.value = user;

            await fetchFavoriteIds(user.userId);
            await fetchFavoriteRestaurants(user.userId);

            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(currentUser.value));
            return true;
        } catch (error) {
            console.error(ERROR_MESSAGES.LOGIN_FAILED_LOG, error);
            throw error;
        }
    };

    const updateProfile = async (updateData: UserResponse) => {
        try {
            // ※api.ts の updateProfile がこのデータを送る想定
            const response = await apiService.updateProfile(updateData);
            const updatedUserFromServer: UserResponse = response.data;

            // サーバーから返ってきた最新データをセットし、お気に入りIDをマージ
            updatedUserFromServer.favoriteIds = currentUser.value?.favoriteIds || [];
            currentUser.value = updatedUserFromServer;

            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(currentUser.value));
            return true;
        } catch (error) {
            console.error(ERROR_MESSAGES.PROFILE_UPDATE_FAILED_LOG, error);
            throw error;
        }
    };

    const logout = () => {
        currentUser.value = null;
        favoriteRestaurants.value = [];
        localStorage.removeItem(STORAGE_KEYS.CURRENT_USER);
        localStorage.removeItem(STORAGE_KEYS.TOKEN);
    };

    const isFavorite = (restaurantId: number) => {
        return currentUser.value?.favoriteIds?.includes(restaurantId) || false;
    };

    const toggleFavorite = async (restaurantId: number) => {
        if (!currentUser.value) return;

        try {
            const response = await apiService.toggleFavorite({
                userId: currentUser.value.userId,
                restaurantId: restaurantId
            });

            // 2. サーバーのレスポンス (added/removed) に応じて、フロントのID配列を直接書き換える
            // サーバーから再取得するのを待たず、今の状態を正とする
            if (!currentUser.value.favoriteIds) {
                currentUser.value.favoriteIds = [];
            }

            if (response.data.status === 'added') {
                if (!currentUser.value.favoriteIds.includes(restaurantId)) {
                    currentUser.value.favoriteIds.push(restaurantId);
                }
            } else {
                currentUser.value.favoriteIds = currentUser.value.favoriteIds.filter(id => id !== restaurantId);
            }

            // 3. localStorage を最新状態に同期
            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(currentUser.value));

            // （任意）バックエンドとの完全な整合性を取るために、念のため再取得を行う
            await fetchFavoriteIds(currentUser.value.userId);
            await fetchFavoriteRestaurants(currentUser.value.userId);

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