import { ref, computed} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { apiService } from '../services/api';
import { HTTP_STATUS_CODES, type Restaurant } from '../constants/types';
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

  const goBack = () => router.back();

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
    } catch (error: any) {
      isError.value = true;
      if (error.response?.status === HTTP_STATUS_CODES.NOT_FOUND) {
        errorMessage.value = ERROR_MESSAGES.RESTAURANT_LOAD_FAILED;
      }else{
        errorMessage.value = ERROR_MESSAGES.SERVER_ERROR
      }
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
    startEdit,
    cancelEdit,
    goBack,
    loadRestaurant
  };
}
