import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { apiService } from '../services/api';
import type { Restaurant } from '../constants/types';
import { ERROR_MESSAGES } from '../constants/messages';

export function useRestaurantDetail() {
  const route = useRoute();
  const router = useRouter();
  const authStore = useAuthStore();

  const restaurant = ref<Restaurant | null>(null);
  const restaurantMemo = ref('');
  const isEditing = ref(false);
  const isLoading = ref(false);
  const isError = ref(false);
  const errorMessage = ref('');
  const saveError = ref('');
  const prevMemo = ref('');

  const restaurantId = computed<number | null>(() => {
    const rawId = route.params.restaurantId;
    const parsed = Number(rawId);
    return Number.isInteger(parsed) && parsed > 0 ? parsed : null;
  });

  const currentUserId = computed(() => authStore.currentUser?.userId ?? null);
  const isFavorite = computed(() => !!restaurant.value && authStore.isFavorite(restaurant.value.restaurantId));

  const goBack = () => router.back();

  const loadMemo = async () => {
    if (!currentUserId.value || restaurantId.value === null) return;
    try {
      const response = await apiService.getMemoRestaurant({
        userId: currentUserId.value,
        restaurantId: restaurantId.value,
      });
      restaurantMemo.value = response.data.memo ?? '';
    } catch (error) {
      console.error('メモの取得に失敗:', error);
    }
  };

  const loadRestaurant = async () => {
    if (restaurantId.value === null) {
      isError.value = true;
      errorMessage.value = ERROR_MESSAGES.INVALID_RESTAURANT_ID;
      return;
    }

    if (!currentUserId.value) {
      isError.value = true;
      errorMessage.value = ERROR_MESSAGES.LOGIN_REQUIRED;
      return;
    }

    isLoading.value = true;
    isError.value = false;
    errorMessage.value = '';

    try {
      const response = await apiService.getRestaurantDetail(restaurantId.value);
      restaurant.value = response.data;
      await loadMemo();
    } catch (error) {
      console.error(ERROR_MESSAGES.RESTAURANT_LOAD_FAILED, error);
      isError.value = true;
      errorMessage.value = ERROR_MESSAGES.RESTAURANT_LOAD_FAILED;
    } finally {
      isLoading.value = false;
    }
  };

  const startEdit = () => {
    prevMemo.value = restaurantMemo.value;
    isEditing.value = true;
    saveError.value = '';
  };

  const cancelEdit = () => {
    restaurantMemo.value = prevMemo.value;
    isEditing.value = false;
    saveError.value = '';
  };

  const saveMemo = async () => {
    if (!currentUserId.value || restaurantId.value === null || !restaurant.value) {
      saveError.value = ERROR_MESSAGES.SERVER_ERROR;
      return false;
    }

    isLoading.value = true;
    saveError.value = '';

    try {
      await apiService.editMemoRestaurant({
        userId: currentUserId.value,
        restaurantId: restaurant.value.restaurantId,
        memo: restaurantMemo.value,
      });
      isEditing.value = false;
      return true;
    } catch (error) {
      console.error(ERROR_MESSAGES.MEMO_SAVE_FAILED, error);
      saveError.value = ERROR_MESSAGES.MEMO_SAVE_FAILED;
      return false;
    } finally {
      isLoading.value = false;
    }
  };

  const toggleFavorite = async () => {
    if (!restaurant.value) return;
    await authStore.toggleFavorite(restaurant.value.restaurantId);
  };

  onMounted(loadRestaurant);

  return {
    restaurant,
    restaurantMemo,
    isEditing,
    isLoading,
    isError,
    errorMessage,
    saveError,
    currentUserId,
    restaurantId,
    isFavorite,
    startEdit,
    cancelEdit,
    saveMemo,
    toggleFavorite,
    goBack,
  };
}
