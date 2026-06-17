import axios from "axios";
import { useAuthStore } from "../stores/auth";
import { API_ENDPOINTS } from "../constants/types";

const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        "Content-Type": "application/json",
    },
});

// 💡 リクエストインターセプターを追加
apiClient.interceptors.request.use((config) => {
    // localStorage からトークンを取得
    const token = localStorage.getItem('token');

    // トークンがあればヘッダーにセット
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
}, (error) => {
    console.error('API request error:', error);
    return Promise.reject(error);
});

// 💡 レスポンスインターセプターを追加（401エラー時の対応）
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API response error:', error);
        if (error.response?.status === 401) {
            // トークンが無効な場合、ログアウト処理を実行
            const authStore = useAuthStore();
            authStore.logout();
            //   // ログインページへリダイレクト（必要に応じて）
            //   window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default apiClient;

export const apiService = {
    getRestaurantDetail(restaurantId: number) {
        return apiClient.get(API_ENDPOINTS.RESTAURANT.DETAIL(restaurantId));
    },

    login(data: any) {
        return apiClient.post(API_ENDPOINTS.USER.LOGIN, data);
    },

    getEmailExists(userEmail: string) {
        return apiClient.get(API_ENDPOINTS.USER.CHECK_EMAIL(userEmail));
    },
    
    signup(data: any) {
        return apiClient.post(API_ENDPOINTS.USER.SIGNUP, data);
    },

    updateProfile(data: any) {
        return apiClient.put(API_ENDPOINTS.USER.UPDATE_PROFILE, data);
    },

    toggleFavorite({ userId, restaurantId }: { userId: number; restaurantId: number }) {
        return apiClient.post(API_ENDPOINTS.USER_RESTAURANT.TOGGLE, { userId, restaurantId });
    },

    getFavoritesByUserId(userId: number) {
        return apiClient.get(API_ENDPOINTS.USER_RESTAURANT.GET_USER_FAVORITES(userId));
    },

    getFavoriteDetails(userId: number) {
        return apiClient.get(API_ENDPOINTS.USER_RESTAURANT.GET_USER_FAVORITE_DETAILS(userId));
    },

    searchRestaurants: (searchParams: {
        restaurantName?: string;
        restaurantRating?: string;
        restaurantGenre?: string;
        restaurantPriceRange?: string;
        restaurantArea?: string;
        isAndSearch: boolean;
    }) => {
        return apiClient.get(API_ENDPOINTS.RESTAURANT.SEARCH, { params: searchParams });
    },

    getgenres() {
        return apiClient.get(API_ENDPOINTS.RESTAURANT.GENRES);
    },

    getPriceRanges() {
        return apiClient.get(API_ENDPOINTS.RESTAURANT.PRICE_RANGES);
    },

    getAreas() {
        return apiClient.get(API_ENDPOINTS.RESTAURANT.AREAS);
    },

    getRatings() {
        return apiClient.get(API_ENDPOINTS.RESTAURANT.RATINGS);
    },

    getMemoRestaurant({ userId, restaurantId }: { userId: number; restaurantId: number }) {
        return apiClient.get(API_ENDPOINTS.USER_RESTAURANT.GET_MEMO(userId, restaurantId));
    },

    editMemoRestaurant({ userId, restaurantId, memo }: { userId: number; restaurantId: number; memo: string }) {
        return apiClient.post(API_ENDPOINTS.USER_RESTAURANT.EDIT_MEMO(userId, restaurantId), { userId, memo });
    }
};