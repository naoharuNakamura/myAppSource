import { ref } from "vue";
import { ERROR_MESSAGES } from "../constants/messages";
import { STORAGE_KEYS, type Restaurant } from "../constants/types";
import { apiService } from "../services/api";
import { useAuthStore } from "../stores/auth";

const favoriteRestaurants = ref<Restaurant[]>([]);

export const useFavorites = () => {
    const authStore = useAuthStore()
    const isFavorite = (restaurantId: number) => {
        return authStore.currentUser?.favoriteIds?.includes(restaurantId) || false;
    };

    const fetchFavoriteIds = async () => {
        if (!authStore.currentUser) return;
        try {
            const response = await apiService.getFavorites();
            authStore.currentUser.favoriteIds = response.data;
            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(authStore.currentUser));
        } catch (error) {
            console.error(ERROR_MESSAGES.FAVORITE_ID_FETCH_FAILED, error);
        }
    };
    const fetchFavoriteRestaurants = async () => {
        try {
            const response = await apiService.getFavoriteDetails();
            favoriteRestaurants.value = response.data;
        } catch (error) {
            console.error(ERROR_MESSAGES.FAVORITE_DETAILS_FETCH_FAILED, error);
        }
    };

    const toggleFavorite = async (restaurantId: number) => {
        if (!authStore.currentUser) return;

        try {
            const response = await apiService.toggleFavorite(restaurantId);

            if (!authStore.currentUser.favoriteIds) {
                authStore.currentUser.favoriteIds = [];
            }

            if (response.data.status === 'added') {
                if (!authStore.currentUser.favoriteIds.includes(restaurantId)) {
                    authStore.currentUser.favoriteIds.push(restaurantId);
                }
            } else {
                authStore.currentUser.favoriteIds = authStore.currentUser.favoriteIds.filter(id => id !== restaurantId);
            }

            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(authStore.currentUser));

            await fetchFavoriteIds();
            await fetchFavoriteRestaurants();

        } catch (error) {
            console.error("お気に入り更新失敗:", error);
        }
    };

    return {
        favoriteRestaurants,
        isFavorite,
        fetchFavoriteIds,
        fetchFavoriteRestaurants,
        toggleFavorite
    }
}