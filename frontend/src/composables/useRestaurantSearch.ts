import { ref, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { apiService } from '../services/api';
import { type Restaurant, SEARCH_CONFIG, ROUTE_NAMES } from '../constants/types';
import { ERROR_MESSAGES } from '../constants/messages';

export function useRestaurantSearch() {
  const route = useRoute();
  const router = useRouter();

  // --- 状態管理 ---
  const allResults = ref<Restaurant[]>([]);
  const isLoading = ref(false);
  const isAndSearch = ref(false);
  const isOpen = ref(false); // アコーディオン用

  // ページネーション用
  const pageSize = ref<number>(SEARCH_CONFIG.DEFAULT_PAGE_SIZE);
  const currentPage = ref<number>(SEARCH_CONFIG.INITIAL_PAGE);
  const totalPages = ref<number>(0);
  const totalElements = ref<number>(0);
  const isFirstPage = ref<boolean>(true);
  const isLastPage = ref<boolean>(true);

  // マスタデータ用
  const restGenres = ref<string[]>([]);
  const restAreas = ref<string[]>([]);
  const restPriceRanges = ref<string[]>([]);
  const restRatings = ref<number[]>([]);

  // --- 検索入力フォーム用の同期リアクティブデータ ---
  // クエリパラメータ、または初期値（空文字）をセット
  const searchForm = ref({
    restaurantName: (route.query.restaurantName as string) || '',
    restaurantGenre: (route.query.restaurantGenre as string) || '',
    restaurantArea: (route.query.restaurantArea as string) || '',
    restaurantRating: (route.query.restaurantRating as string) || '',
    restaurantPriceRange: (route.query.restaurantPriceRange as string) || '',
  });

  const fetchMasterData = async () => {
    try {
      const [genres, areas, prices, ratings] = await Promise.all([
        apiService.getGenres(),
        apiService.getAreas(),
        apiService.getPriceRanges(),
        apiService.getRatings(),
      ]);
      restGenres.value = genres.data;
      restAreas.value = areas.data;
      restPriceRanges.value = prices.data;
      restRatings.value = ratings.data;
    } catch (error) {
      console.error(ERROR_MESSAGES.MASTER_DATA_FETCH_FAILED_LOG, error);
    }
  };

  const executeSearch = async (page: number = SEARCH_CONFIG.INITIAL_PAGE) => {
    let targetPage = page;

    if (targetPage < 1) {
      targetPage = 1;
    }

    if (totalPages.value > 0) {

      if (targetPage > totalPages.value) {
        targetPage = totalPages.value;
      }
    }

    isLoading.value = true;
    currentPage.value = targetPage; 

    try {
      const searchParams = {
        restaurantName: (route.query.restaurantName as string) || '',
        restaurantGenre: (route.query.restaurantGenre as string) || '',
        restaurantArea: (route.query.restaurantArea as string) || '',
        restaurantRating: (route.query.restaurantRating as string) || '',
        restaurantPriceRange: (route.query.restaurantPriceRange as string) || '',
        isAndSearch: isAndSearch.value,
        page: targetPage,
        size: pageSize.value,
        sort: (route.query.sort as string) || SEARCH_CONFIG.DEFAULT_SORT,
      };

      const response = await apiService.searchRestaurants(searchParams);

      allResults.value = response.data.list;
      totalPages.value = response.data.pages;      
      totalElements.value = response.data.total;     

      isFirstPage.value = response.data.isFirstPage;
      isLastPage.value = response.data.isLastPage;
    } catch (error) {
      console.error(ERROR_MESSAGES.SEARCH_FAILED_LOG, error);
      allResults.value = [];
      totalPages.value = 0;
      totalElements.value = 0;
      isFirstPage.value = true;
      isLastPage.value = true;
    } finally {
      isLoading.value = false;
    }
  };

  const handleSearchSubmit = () => {

    const activeConditions = Object.fromEntries(
      Object.entries(searchForm.value).filter(([_, value]) => value !== '')
    );
 
    activeConditions.isAndSearch = isAndSearch.value ? 'true' : 'false';
    router.push({ name: ROUTE_NAMES.RESULT, query: activeConditions });
  };

  const handleSortChange = (event: Event) => {
    const target = event.target as HTMLSelectElement;
    router.push({
      name: ROUTE_NAMES.RESULT,
      query: {
        ...route.query,
        sort: target.value,
        page: SEARCH_CONFIG.INITIAL_PAGE,
      },
    });
  };


  const clearQuery = () => {
    searchForm.value = {
      restaurantName: '',
      restaurantGenre: '',
      restaurantArea: '',
      restaurantRating: '',
      restaurantPriceRange: '',
    };
  };


  
  const toggleRating = (value: string) => {
    searchForm.value.restaurantRating =
      searchForm.value.restaurantRating === value ? '' : value;
  };

  const togglePrice = (value: string) => {
    searchForm.value.restaurantPriceRange =
      searchForm.value.restaurantPriceRange === value ? '' : value;
  };

  const toggleAccordion = () => {
    isOpen.value = !isOpen.value;
  };

  const toggleSearchMode = () => {
    isAndSearch.value = !isAndSearch.value;
  }

  // --- 算出プロパティ ---
  const noResult = computed(() => !isLoading.value && totalElements.value === 0);

  // クエリの変更を監視してフォームに同期し、検索を自動実行する
  watch(
    () => route.query,
    (newQuery, oldQuery) => {
      // 1. フォームへの同期
      searchForm.value = {
        restaurantName: (newQuery.restaurantName as string) || '',
        restaurantGenre: (newQuery.restaurantGenre as string) || '',
        restaurantArea: (newQuery.restaurantArea as string) || '',
        restaurantRating: (newQuery.restaurantRating as string) || '',
        restaurantPriceRange: (newQuery.restaurantPriceRange as string) || '',
      };
      isAndSearch.value = (newQuery.isAndSearch as string) === 'true';

      // 2. 検索実行（クエリが変化した時のみ実行）
      // JSON.stringifyでクエリの変化を検知します
      if (JSON.stringify(newQuery) !== JSON.stringify(oldQuery)) {
        // ページ番号をクエリから取得（なければ初期値 0）
        const page = parseInt(newQuery.page as string) || SEARCH_CONFIG.INITIAL_PAGE;
        executeSearch(page);
      }
    },
    { deep: true }
  );

  return {
    // 状態
    allResults,
    isLoading,
    isAndSearch,
    isOpen,
    searchForm,
    // ページネーション
    pageSize,
    currentPage,
    totalPages,
    totalElements,
    isFirstPage,
    isLastPage,
    noResult,
    // マスタデータ
    restGenres,
    restAreas,
    restPriceRanges,
    restRatings,
    // 関数
    fetchMasterData,
    executeSearch,
    handleSearchSubmit,
    handleSortChange,
    clearQuery,
    toggleRating,
    togglePrice,
    toggleAccordion,
    toggleSearchMode,
  };
}