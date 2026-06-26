import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";
import { API_ENDPOINTS } from "../constants/types";
import type { ApiErrorResponse } from "../constants/types";
import { toast } from 'vue3-toastify';
import { ERROR_MESSAGES } from "../constants/messages";

const apiClient = axios.create({
    // baseURL: 'http://localhost:8080',
    headers: {
        "Content-Type": "application/json",
    },
});

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token');

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
}, (error: any) => Promise.reject(error));

apiClient.interceptors.response.use(
    (response) => {

        const method = response.config.method?.toLowerCase();
        if (method !== 'get') {
            const successMessage = response.data?.message || "操作が完了しました";
            toast.success(successMessage);
        }
        return response;
    },
    (error: AxiosError) => {

        const skipToast = (error.config as any)?.skipToast;

        if (error.response) {
            const errorData = error.response.data as ApiErrorResponse;
            if (!skipToast) {
                toast.error(errorData.message);
            }
            if (errorData.errorCode === 'UNAUTHORIZED') {
                window.location.href = '/login';
            }
        } else {
            if (!skipToast) {
                toast.error(ERROR_MESSAGES.SERVER_ERROR);
            }
        }
        return Promise.reject(error);
    }
);

export default apiClient;

export const apiService = {

    login(data: any) {
        return apiClient.post(API_ENDPOINTS.USER.LOGIN, data, { skipToast: true } as any);
    },

    signup(data: any) {
        return apiClient.post(API_ENDPOINTS.USER.SIGNUP, data, { skipToast: true } as any);
    },

    getEmailExists(userEmail: string) {
        return apiClient.get(API_ENDPOINTS.USER.CHECK_EMAIL(userEmail), { skipToast: true } as any);
    },

    updateProfile(data: any) {
        return apiClient.put(API_ENDPOINTS.USER.UPDATE_PROFILE, data, { skipToast: true } as any);
    },

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

    getGenres() {
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

    toggleFavorite(restaurantId: number) {
        return apiClient.post(API_ENDPOINTS.FAVORITE.ITEM(restaurantId), { restaurantId: restaurantId });
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
        return apiClient.get(API_ENDPOINTS.REVIEW.REVIEW(restaurantId));
    },

    // メモの更新 (RESTfulの慣習に沿って put メソッドに変更)
    editMemoRestaurant(restaurantId: number, memo: string) {
        return apiClient.put(API_ENDPOINTS.REVIEW.REVIEW(restaurantId), { memo });
    },

    getAllReviews(restaurantId: number) {
        return apiClient.get(API_ENDPOINTS.REVIEW.REVIEW(restaurantId))
    },

    deleteReview(reviewId: number) {
        return apiClient.post(API_ENDPOINTS.REVIEW.DELETE(reviewId))
    },

    addReview(restaurantId: number, rating: number, memo: string) {
        return apiClient.put(API_ENDPOINTS.REVIEW.REVIEW(restaurantId), { rating, memo })
    },

};