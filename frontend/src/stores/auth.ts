// src/stores/auth.ts

import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import { apiService } from '../services/api';
import { type UserResponse, STORAGE_KEYS } from '../constants/types';
import { ERROR_MESSAGES } from '../constants/messages';

export const useAuthStore = defineStore('auth', () => {

    const savedUser = localStorage.getItem(STORAGE_KEYS.CURRENT_USER);
    const currentUser = ref<UserResponse | null>(savedUser ? JSON.parse(savedUser) : null);


    const isLoggedIn = computed(() => currentUser.value !== null);

    const login = async (loginData: { userEmail: string; userPassword: string }) => {
        try {
            const response = await apiService.login(loginData);

            const { token, user } = response.data;

            localStorage.setItem(STORAGE_KEYS.TOKEN, token);

            currentUser.value = user;
            

            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(currentUser.value));
            return true;
        } catch (error) {
            console.error(ERROR_MESSAGES.LOGIN_FAILED_LOG, error);
            throw error;
        }
    };

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

    const logout = () => {
        currentUser.value = null;
        localStorage.removeItem(STORAGE_KEYS.CURRENT_USER);
        localStorage.removeItem(STORAGE_KEYS.TOKEN);
    };

    return {
        currentUser,
        isLoggedIn,
        login,
        logout,
        updateProfile,
    };
});