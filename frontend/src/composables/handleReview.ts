import { computed, ref } from "vue";
import { apiService } from "../services/api";
import { useRoute } from "vue-router";
import { useAuthStore } from "../stores/auth";
import { ERROR_MESSAGES } from '../constants/messages';

export function handleReview() {
    const route = useRoute();
    const authStore = useAuthStore();

    const idParam = Array.isArray(route.params.restaurantId)
        ? route.params.restaurantId[0]
        : route.params.restaurantId;
    const restaurantId = Number(idParam);
    const currentUserId = computed(() => authStore.currentUser?.userId ?? null);

    const reviews = ref<any[]>([]);
    const userReview = ref("");
    const userRating = ref(0);
    const hoverRating = ref(0);
    const isEditing = ref(false);
    const isLoading = ref(false);
    const saveError = ref('');
    const prevMemo = ref('');

    const setRating = (score: number) => {
        userRating.value = score;
    };

    const setHoverRating = (score: number) => {
        hoverRating.value = score;
    };


    const loadReviews = async () => {
        if (!currentUserId.value || restaurantId === null) return;
        const response = await apiService.getAllReviews(restaurantId);
        reviews.value = response.data;

    };

    const deleteReview = async (reviewId: number) => {
        await apiService.deleteReview(reviewId)
        reviews.value = reviews.value.filter(review => review.reviewId !== reviewId);
        await loadReviews()
        return true;
    };

    const addReview = async () => {
        try {
            if (!currentUserId.value || restaurantId === null) {
                saveError.value = ERROR_MESSAGES.SERVER_ERROR;
                return false;
            }
            if (userRating.value === 0) {
                saveError.value = "評価（★）を選択してください。";
                return false;
            }
            const reviewContent = userReview.value.trim() || "";
            await apiService.addReview(restaurantId, userRating.value, reviewContent)
            isEditing.value = false;
            isEditing.value = false;
            userReview.value = "";
            userRating.value = 0;
            await loadReviews()
            return true;
        } catch (error: any) {
            console.error(ERROR_MESSAGES.MEMO_SAVE_FAILED, error);
            saveError.value = ERROR_MESSAGES.MEMO_SAVE_FAILED;
            return false;
        } finally {
            isLoading.value = false;
        }
    };

    const startEdit = () => {
        prevMemo.value = userReview.value;
        isEditing.value = true;
        saveError.value = '';
    };

    const cancelEdit = () => {
        userReview.value = prevMemo.value;
        isEditing.value = false;
        saveError.value = '';
    };


    return {
        reviews,
        userReview,
        userRating,
        isEditing,
        isLoading,
        saveError,
        currentUserId,
        restaurantId,
        hoverRating,
        loadReviews,
        deleteReview,
        addReview,
        startEdit,
        cancelEdit,
        setRating,
        setHoverRating
    };
}

