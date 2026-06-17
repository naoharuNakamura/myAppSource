const API_BASE = '/api'

export const ROUTES = {
    HOME: '/',
    LOGIN: '/login',
    SIGNUP: '/signup',
    MYPAGE: '/mypage',
    RESULT: '/result',
    RESULT_DETAIL_PATH: '/result-detail/:restaurantId',
    RESULT_DETAIL: (restaurantId: number | string) => `/result-detail/${restaurantId}`,
    SEARCH: '/search'
} as const;

export const ROUTE_NAMES = {
    LOGIN: 'Login',
    SIGNUP: 'SignUp',
    SEARCH: 'Search',
    RESULT: 'Result',
    RESULT_DETAIL: 'ResultDetail',
    MYPAGE: 'MyPage'
} as const;

export const API_ENDPOINTS = {
    USER: {
        LOGIN: `${API_BASE}/users/login`,
        CHECK_EMAIL: (userEmail: string) => `${API_BASE}/users/check-email?userEmail=${encodeURIComponent(userEmail)}`,
        SIGNUP: `${API_BASE}/users/signup`,
        UPDATE_PROFILE: `${API_BASE}/users/profile`
    },
    RESTAURANT: {
        SEARCH: `${API_BASE}/restaurants/search`,
        GENRES: `${API_BASE}/restaurants/genres`,
        PRICE_RANGES: `${API_BASE}/restaurants/price-ranges`,
        AREAS: `${API_BASE}/restaurants/areas`,
        RATINGS: `${API_BASE}/restaurants/ratings`,
        DETAIL: (restaurantId: number | string) => `${API_BASE}/restaurants/${restaurantId}`,
    },

    FAVORITE: {
        LIST: `${API_BASE}/favorites`,

        DETAILS: `${API_BASE}/favorites/details`,

        ITEM: (restaurantId: number | string) => `${API_BASE}/favorites/${restaurantId}`,
        
        // メモの取得(GET)・編集(PUT) (旧: GET_MEMO, EDIT_MEMO)
        MEMO: (restaurantId: number | string) => `${API_BASE}/favorites/${restaurantId}/memo`
    }
} as const;

export interface Restaurant {
    restaurantId: number;
    restaurantName: string;
    restaurantImg: string;
    restaurantRating: number;
    restaurantGenre: string;
    restaurantPriceRange: string;
    restaurantArea: string;
    restaurantOpenHour: string;
    restaurantCloseHour: string;
    restaurantAddress: string;
    restaurantPhone: string;
    restaurantUrl: string;
    restaurantClosedDays: string;
}

export interface UserResponse {
    userId: number;
    userName: string;
    userEmail: string;
    userPhone: string;
    favoriteIds?: number[]; // お気に入りの管理用
}

export const STORAGE_KEYS = {
    TOKEN: 'token',
    CURRENT_USER: 'currentUser'
} as const;

export const HTTP_STATUS_CODES = {
    CONFLICT: 409,
    SERVER_ERROR: 500,
} as const;

export const SEARCH_CONFIG = {
    DEFAULT_PAGE_SIZE: 50,
    INITIAL_PAGE: 0,
    SHOW_ALL_SIZE: 99999,
    PAGE_SIZE_OPTIONS: [30, 50, 100, 99999] as const,
    SORT_OPTIONS: [
        { label: '標準', value: '' },
        { label: '評価の高い順', value: 'restaurantRating,desc' },
        { label: '評価の低い順', value: 'restaurantRating,asc' },
    ] as const,
    DEFAULT_SORT: 'restaurantId,asc',
} as const;

export const SEARCH_OPTIONS = {
    RATINGS: ['0~1.5', '1.5~2.5', '2.5~3.5', '3.5~4.5', '4.5~5.0'] as const,
    PRICE_RANGES: ['¥1,000〜¥2,000', '¥2,000〜¥4,000', '¥4,000〜¥6,000', '¥6,000〜'] as const,
} as const;

export const REGEX = {
    PASSWORDREGEX: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/,
    EMAILREGEX:  /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    PHONEREGEX: /^0\d{1,4}-\d{1,4}-\d{4}$|^0\d{9,10}$/,
} 
