import axios, { type InternalAxiosRequestConfig, type AxiosResponse } from "axios";
import { useAuthStore } from "../stores/auth";
// パスは実際のプロジェクト構造に合わせてください（必要に応じて '../constants' 等へ）
import { API_ENDPOINTS } from "../constants/types";

const apiClient = axios.create({
    // baseURL: 'http://localhost:8080',
    headers: {
        "Content-Type": "application/json",
    },
});

// 💡 リクエストインターセプターを追加（TypeScriptのエラー解消のため型を明記）
apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    // localStorage からトークンを取得
    const token = localStorage.getItem('token');

    // トークンがあればヘッダーにセット
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
}, (error: any) => {
    console.error('API request error:', error);
    return Promise.reject(error);
});

// 💡 レスポンスインターセプターを追加（TypeScriptのエラー解消のため型を明記）
apiClient.interceptors.response.use(
    (response: AxiosResponse) => response,
    (error: any) => {
        console.error('API response error:', error);
        if (error.response?.status === 401) {
            // トークンが無効な場合、ログアウト処理を実行
            const authStore = useAuthStore();
            authStore.logout();
            // window.location.href = '/login'; // 必要に応じてリダイレクト
        }
        return Promise.reject(error);
    }
);

export default apiClient;

export const apiService = {
    // ==========================================
    // USER 関連
    // ==========================================
    login(data: any) {
        return apiClient.post(API_ENDPOINTS.USER.LOGIN, data);
    },

    signup(data: any) {
        return apiClient.post(API_ENDPOINTS.USER.SIGNUP, data);
    },

    getEmailExists(userEmail: string) {
        return apiClient.get(API_ENDPOINTS.USER.CHECK_EMAIL(userEmail));
    },
    
    updateProfile(data: any) {
        return apiClient.put(API_ENDPOINTS.USER.UPDATE_PROFILE, data);
    },

    // ==========================================
    // RESTAURANT 関連
    // ==========================================
    searchRestaurants(searchParams: {
        restaurantName?: string;
        restaurantRating?: string;
        restaurantGenre?: string;
        restaurantPriceRange?: string;
        restaurantArea?: string;
        isAndSearch: boolean;
    }) {
        return apiClient.get(API_ENDPOINTS.RESTAURANT.SEARCH, { params: searchParams });
    },

    getRestaurantDetail(restaurantId: number) {
        return apiClient.get(API_ENDPOINTS.RESTAURANT.DETAIL(restaurantId));
    },

    getGenres() { // 💡 タイポ修正 (getgenres -> getGenres)
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

    // ==========================================
    // FAVORITE (旧: USER_RESTAURANT) 関連
    // 💡 全て userId の引数を削除し、シンプルにしました
    // ==========================================
    
    // お気に入りの追加・解除 (トグル)
    toggleFavorite(restaurantId: number) {
        return apiClient.post(API_ENDPOINTS.FAVORITE.ITEM(restaurantId), {restaurantId: restaurantId});
    },

    // お気に入り一覧取得
    getFavorites() {
        return apiClient.get(API_ENDPOINTS.FAVORITE.LIST);
    },

    // お気に入り詳細一覧取得
    getFavoriteDetails() {
        return apiClient.get(API_ENDPOINTS.FAVORITE.DETAILS);
    },

    // メモの取得
    getMemoRestaurant(restaurantId: number) {
        return apiClient.get(API_ENDPOINTS.FAVORITE.MEMO(restaurantId));
    },

    // メモの更新 (RESTfulの慣習に沿って put メソッドに変更)
    editMemoRestaurant(restaurantId: number, memo: string) {
        return apiClient.put(API_ENDPOINTS.FAVORITE.MEMO(restaurantId), { memo });
    }
};