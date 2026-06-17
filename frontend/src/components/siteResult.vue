<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';
import RestaurantCard from './RestaurantCard.vue';
import { useRestaurantSearch } from '../composables/useRestaurantSearch';
import { ROUTE_NAMES, SEARCH_CONFIG, SEARCH_OPTIONS } from '../constants/types';
import { UI_TEXTS } from '../constants/messages';

const route = useRoute();
const restaurantSearch = useRestaurantSearch();
const text = UI_TEXTS.result;

// コンポーザブルから必要な状態や関数をすべて展開（テンプレートの記述をシンプルにするため）
const {
  allResults,
  isLoading,
  isAndSearch,
  isOpen,
  searchForm,
  pageSize,
  currentPage,
  totalPages,
  totalElements,
  isFirstPage,
  isLastPage,
  noResult,
  restGenres,
  restAreas,
  fetchMasterData,
  executeSearch,
  handleSearchSubmit,
  handleSortChange,
  clearQuery,
  toggleRating,
  togglePrice,
  toggleAccordion,
  toggleSearchMode
} = restaurantSearch;

onMounted(() => {
  executeSearch(SEARCH_CONFIG.INITIAL_PAGE);
  fetchMasterData();
});


// 💡 修正ポイント②: 「この条件で再検索」ボタンが押された時の専用処理
const onSearchSubmit = () => {
  // 現在のクエリ文字列を一時保存
  const currentQueryStr = JSON.stringify(route.query);

  // URLを更新する（コンポーザブルの処理を呼び出し）
  handleSearchSubmit();

  // 覆いかぶさっているアコーディオンを自動で閉じ、背後のローディングや結果を見せる
  isOpen.value = false;

  // ボタン押下後に生成される予定の新しいクエリを計算
  const nextQuery: Record<string, string> = Object.fromEntries(
    Object.entries(searchForm.value).filter(([_, value]) => value !== '')
  );
  // 検索モードもクエリ比較に含める
  nextQuery.isAndSearch = isAndSearch.value ? 'true' : 'false';
  if (route.query.sort) {
    nextQuery.sort = route.query.sort as string; // 現在の並び替え順を維持
  }

  // もし前回と「完全に同じ条件」でURLが変わらなかった場合、
  // watchが動かないため、ここで手動で強制的に再検索を走らせる
  if (currentQueryStr === JSON.stringify(nextQuery)) {
    executeSearch(SEARCH_CONFIG.INITIAL_PAGE);
  }
};

const onToggleSearchMode = () => {
  toggleSearchMode();
  // executeSearch(SEARCH_CONFIG.INITIAL_PAGE);
};
</script>

<template>
  <div class="result-container">

    <div v-if="isLoading" class="simple-loading-container">
      <div class="loading-spinner"></div>
      <p class="loading-text">{{ text.loadingText }}</p>
    </div>

    <div v-else>
      <div class="result-header">
        <div class="header-left">
          <h2>{{ text.headerText }} ({{ totalElements }}件)</h2>
        </div>
        <div class="actions-wrapper">
          <div class="size-selector">
            <label for="page-size">表示件数：</label>
            <select id="page-size" v-model="pageSize" @change="executeSearch(SEARCH_CONFIG.INITIAL_PAGE)">
              <option v-for="size in SEARCH_CONFIG.PAGE_SIZE_OPTIONS" :key="size" :value="size">
                {{ size === SEARCH_CONFIG.SHOW_ALL_SIZE ? text.showAllText : `${size}件ずつ` }}
              </option>
            </select>
          </div>
          <RouterLink :to="{ name: ROUTE_NAMES.SEARCH }" class="back-btn">{{ text.backToSearch }}</RouterLink>
        </div>
      </div>

      <div class="sort-wrapper">
        <div class="accordion-container">
          <button @click="toggleAccordion()" type="button" class="accordion-btn" :aria-expanded="isOpen"
            aria-controls="filter-accordion-content">
            {{ text.refineButton }}
          </button>
          <div v-show="isOpen" id="filter-accordion-content" class="accordion-content-box">
            <form @submit.prevent="onSearchSubmit" class="inner-search-form">

              <div class="form-group-block">
                  <label for="filter-res-name">{{ text.storeNameLabel }}</label>
                  <input id="filter-res-name" v-model="searchForm.restaurantName" type="text" :placeholder="text.storeNamePlaceholder">
              </div>

              <div class="form-row-line">
                <div class="form-group-block">
                  <label for="filter-genre">{{ UI_TEXTS.search.genreLabel }}</label>
                  <select id="filter-genre" v-model="searchForm.restaurantGenre">
                    <option value="">{{ UI_TEXTS.search.allLabel }}</option>
                    <option v-for="gen in restGenres" :value="gen" :key="gen">{{ gen }}</option>
                  </select>
                </div>

                <div class="form-group-block">
                  <label for="filter-area">{{ UI_TEXTS.search.areaLabel }}</label>
                  <select id="filter-area" v-model="searchForm.restaurantArea">
                    <option value="">{{ UI_TEXTS.search.allLabel }}</option>
                    <option v-for="area in restAreas" :value="area" :key="area">{{ area }}</option>
                  </select>
                </div>
              </div>

              <div class="form-group-block">
                <label>評価</label>
                <div class="mini-kpi-group">
                  <button v-for="rate in SEARCH_OPTIONS.RATINGS" :key="rate"
                    type="button" :class="['mini-btn', searchForm.restaurantRating === rate ? 'active' : '']"
                    :aria-pressed="searchForm.restaurantRating === rate" @click="toggleRating(rate)">
                    {{ rate }}
                  </button>
                </div>
              </div>

              <div class="form-group-block">
                <label>価格帯</label>
                <div class="mini-kpi-group">
                  <button v-for="price in SEARCH_OPTIONS.PRICE_RANGES" :key="price"
                    type="button" :class="['mini-btn', searchForm.restaurantPriceRange === price ? 'active' : '']"
                    :aria-pressed="searchForm.restaurantPriceRange === price" @click="togglePrice(price)">
                    {{ price }}
                  </button>
                </div>
              </div>

              <div class="form-action-line">
                <button @click="onToggleSearchMode" type="button"
                  :class="['toggle-search-btn', isAndSearch ? 'and-mode' : 'or-mode']">
                  {{ text.modeLabel }} {{ isAndSearch ? text.modeAnd : text.modeOr }}
                </button>

                <div class="submit-btns">
                  <button @click="clearQuery" class="inner-clear-btn" type="button">{{ text.clearText }}</button>
                  <button class="inner-submit-btn" type="submit">{{ text.infoSearchButton }}</button>
                </div>
              </div>

            </form>
          </div>
        </div>
        <div v-if="totalPages > 1 && pageSize !== SEARCH_CONFIG.SHOW_ALL_SIZE" class="pagination-container">
          <button class="back-btn" :disabled="isFirstPage" @click="executeSearch(currentPage - 1)">
            {{ text.prevPage }}
          </button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }} ページ</span>
          <button class="back-btn" :disabled="isLastPage" @click="executeSearch(currentPage + 1)">
            {{ text.nextPage }}
          </button>
        </div>
        <div v-if="allResults.length" class="sort-selector">
          <label for="sort-select">{{ text.sortLabel }}</label>
          <select id="sort-select" :value="route.query.sort || ''" @change="handleSortChange">
            <option v-for="option in SEARCH_CONFIG.SORT_OPTIONS" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </div>
      </div>
      <div v-if="!noResult" class="card-container">
        <div class="card-grid">
          <RestaurantCard v-for="rest in allResults" :key="rest.restaurantId" :restaurant="rest" />
        </div>
      </div>
    </div>
    <div v-if="noResult" class="no-result-message">
      <h3>{{ text.noResultText }}</h3>
    </div>
  </div>
</template>

<style scoped>
.result-container {
  margin: 40px auto;
  padding: 0 24px;
}

/* --- ヘッダーエリアの安定化 --- */
.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 20px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  /* 💡 タイトルとボタンを横一列に綺麗に並べる */
  flex-wrap: wrap;
  gap: 20px;
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.count-badge {
  font-size: 16px;
  font-weight: 600;
  color: #64748b;
  background-color: #f1f5f9;
  padding: 2px 10px;
  border-radius: 20px;
}

.actions-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

/* --- 🔍 アコーディオン（覆いかぶさり・レスポンシブガード） --- */
.accordion-container {
  position: relative;
  /* absoluteの基準点 */
}

.accordion-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 600;
  color: #2563eb;
  background-color: #eff6ff;
  border: 1px solid #bfdbfe;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.accordion-btn:hover {
  background-color: #dbeafe;
  border-color: #93c5fd;
}

.btn-icon {
  font-size: 12px;
}

.accordion-content-box {
  position: absolute;
  top: calc(100% + 8px);
  left: 260px;
  transform: translateX(-50%);
  z-index: 150;
  /* 確実に最前面に出す */
  padding: 24px;
  background-color: var(--color-bg-main);

  /* 💡 スマホ等で画面外にはみ出さないようにしつつ、PCでは適切な幅を確保 */
  width: min(calc(100vw - 48px), 540px);
  border: 1px solid #e2e8f0;
  border-radius: 12px;

  /* 立体的なプレミアムシャドウ */
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

/* --- 💡 コントロールバー（段崩れ・ワープ防止設計） --- */
.sort-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  min-height: 38px;
  /* 💡 ページネーションの有無で全体の高さがガタつかないように高さを確保 */
  width: 100%;
  flex-wrap: wrap;
  gap: 12px;
}

.pagination-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #475569;
}

/* --- 内部フォームのスタイリング --- */
.inner-search-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group-block {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group-block label {
  font-size: 13px;
  font-weight: 700;
  color: #475569;
}

.form-group-block input,
.form-group-block select,
.size-selector select,
.sort-selector select {
  padding: 8px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  font-size: 14px;
  color: #334155;
  background-color: #ffffff;
  outline: none;
  transition: all 0.2s ease;
}

.form-group-block input:focus,
.form-group-block select:focus,
.size-selector select:focus,
.sort-selector select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.form-row-line {
  display: flex;
  gap: 16px;
}

.form-row-line .form-group-block {
  flex: 1;
}

.mini-kpi-group {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.mini-btn {
  padding: 6px 14px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid #cbd5e1;
  background-color: #ffffff;
  color: #64748b;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.mini-btn:hover {
  background-color: #f1f5f9;
  color: #334155;
}

.mini-btn.active {
  background-color: #2563eb;
  border-color: #2563eb;
  color: #ffffff;
  font-weight: 600;
}

.form-action-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #e2e8f0;
  padding-top: 16px;
  margin-top: 8px;
  flex-wrap: wrap;
  gap: 12px;
}

.toggle-search-btn {
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
}

.or-mode {
  background-color: #f0fdf4;
  color: #166534;
  border-color: #bbf7d0;
}

.and-mode {
  background-color: #eff6ff;
  color: #1e40af;
  border-color: #bfdbfe;
}

.submit-btns {
  display: flex;
  gap: 8px;
}

.inner-clear-btn {
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 500;
  background: transparent;
  border: 1px solid #cbd5e1;
  color: #64748b;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.inner-clear-btn:hover {
  background-color: #f8fafc;
  color: #334155;
}

.inner-submit-btn {
  padding: 8px 20px;
  font-size: 13px;
  font-weight: 600;
  background-color: #2563eb;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.inner-submit-btn:hover {
  background-color: #1d4ed8;
}

/* --- ボタン共通（戻る・ページネーション） --- */
.back-btn {
  display: inline-block;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  color: #475569;
  background-color: #ffffff;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  text-decoration: none;
  white-space: nowrap;
  transition: all 0.2s ease;
  cursor: pointer;
}

.back-btn:hover:not(:disabled) {
  background-color: #f8fafc;
  color: #1e293b;
  border-color: #cbd5e1;
}

.back-btn:disabled {
  opacity: 0.5;
  background-color: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #475569;
  font-weight: 600;
}

.size-selector {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #475569;
}

.size-selector select {
  padding: 4px 8px;
}

/* --- ❌ 0件メッセージ --- */
.no-result-message {
  text-align: center;
  padding: 80px 0;
  color: #64748b;
  background-color: #f8fafc;
  border-radius: 12px;
  border: 1px dashed #cbd5e1;
  margin-top: 16px;
}

/* --- 📋 カードグリッド --- */
.card-grid {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 24px;
}

@media screen and (max-width: 440px) {
  .result-header{
    flex-direction: column;
  }

  .sort-wrapper {
    flex-direction: column;
    align-items: center;
  }

  .card-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (min-width: 440px) {
  .card-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .accordion-content-box {
    top: calc(100% + 12px);
    left: 260px;
  }
}

/* --- 🔄 スピナー --- */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.simple-loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120px 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #2563eb;
  border-radius: 50%;
  animation: spin 0.8s infinite linear;
  margin-bottom: 16px;
}

.loading-text {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
}
</style>