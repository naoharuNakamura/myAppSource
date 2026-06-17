<script setup lang="ts">
import { onMounted } from 'vue';
import { useRestaurantSearch } from '../composables/useRestaurantSearch'; // パスは環境に合わせて調整してください
import { SEARCH_OPTIONS } from '../constants/types';
import { UI_TEXTS } from '../constants/messages';

const text = UI_TEXTS.search;

const {
  searchForm,
  restGenres,
  restAreas,
  fetchMasterData,
  handleSearchSubmit,
  clearQuery,
  toggleRating,
  togglePrice
} = useRestaurantSearch();

onMounted(() => {
  // マスタデータの取得を一括実行
  fetchMasterData();
});
</script>

<template>
  <form @submit.prevent="handleSearchSubmit" class="search-form-container">
    <div class="name-search-form">
      <label>{{ text.nameLabel }}</label>
      <input @keydown.enter="handleSearchSubmit" v-model="searchForm.restaurantName" type="text" :placeholder="text.namePlaceholder">
    </div>
    <div class="detail-search-form">
      <label class="section-label">{{ text.detailLabel }}</label>
      <div class="form-layout-rows">

        <div class="form-row">
          <div class="form-group">
            <label>{{ text.genreLabel }}</label>
            <select v-model="searchForm.restaurantGenre" class="select-field">
              <option value="">{{ text.allLabel }}</option>
              <option v-for="gen in restGenres" :value="gen">{{ gen }}</option>
            </select>
          </div>

          <div class="form-group">
            <label>{{ text.areaLabel }}</label>
            <select v-model="searchForm.restaurantArea" class="select-field">
              <option value="">{{ text.allLabel }}</option>
              <option v-for="area in restAreas" :value="area">{{ area }}</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>{{ text.ratingLabel }}</label>
            <div class="radio-button-group">
              <template v-for="(rating, index) in SEARCH_OPTIONS.RATINGS" :key="rating">
                <input :checked="searchForm.restaurantRating === rating" type="radio" :id="`rating-${index}`" name="rating">
                <label @click.prevent="toggleRating(rating)" :for="`rating-${index}`">
                  {{ rating === '0~1.5' ? '1.5未満' : rating === '4.5~5.0' ? '4.5以上' : rating }}
                </label>
              </template>
            </div>
          </div>
          <div class="form-group">
            <label>{{ text.priceLabel }}</label>
            <div class="radio-button-group">
              <template v-for="(price, index) in SEARCH_OPTIONS.PRICE_RANGES" :key="price">
                <input :checked="searchForm.restaurantPriceRange === price" type="radio" :id="`price-${index}`" name="price">
                <label @click.prevent="togglePrice(price)" :for="`price-${index}`">
                  {{ price }}
                </label>
              </template>
            </div>
          </div>
        </div>

      </div>
    </div>
    <div class="btn-wrapper">
      <button @click="clearQuery" class="clear-btn" type="button">{{ text.clearButton }}</button>
      <button class="search-btn" type="submit">{{ text.submitButton }}</button>
    </div>
  </form>
</template>

<style scoped>
.search-form-container {
  width: min(100%, 800px);
  margin: 40px auto;
  padding: 24px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
  border: 1px solid #f1f5f9;
  box-sizing: border-box;
}

.name-search-form,
.detail-search-form {
  margin-bottom: 24px;
  
  overflow: hidden;
}

.name-search-form label,
.detail-search-form>label,
.form-grid label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  margin-bottom: 8px;
  overflow: hidden;
}

input[type="text"],
select {
  width: 100%;
  flex-shrink: 0;
  padding: 10px 14px;
  font-size: 14px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  background-color: #ffffff;
  outline: none;
  box-sizing: border-box;
  transition: all 0.2s ease;
  color: #1e293b;
}

input[type="text"]:focus,
select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.form-layout-rows {
  display: flex;
  flex-direction: column;
  gap: 24px;
  /* 1段目と2段目のすき間 */
}

.form-row {
  display: flex;
  flex-direction: row;
  /* 横並びにする */
  gap: 24px;
  /* 隣り合う項目のすき間 */
  flex-wrap: wrap;
  /* 画面が狭いときは自動で折り返す（スマホ対応） */
}

.form-row .form-group {
  flex: 1;
  /* 横幅を均等（50%:50%）に分割する */
  min-width: 0;
  width: 100%;
  /* 画面が狭くなったときに潰れすぎないようにする限界幅 */
  overflow: hidden;
}

@media (min-width: 768px) {
  .form-grid {
    flex-direction: column;
    gap: 32px;
  }

  .grid-column {
    flex: 0;
  }
}

.radio-button-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.radio-button-group input[type="radio"] {
  display: none;
}

.radio-button-group label {
  display: inline-block;
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 500;
  color: #475569;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.radio-button-group label:hover {
  background-color: #f1f5f9;
  border-color: #cbd5e1;
}

/* ラジオボタンが選択されている（前方のinput要素がchecked）時のデザイン */
.radio-button-group input[type="radio"]:checked+label {
  background-color: #eff6ff;
  border-color: #3b82f6;
  color: #2563eb;
  font-weight: 600;
}

.btn-wrapper {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  border-top: 1px solid #f1f5f9;
  padding-top: 24px;
}

.clear-btn,
.search-btn {
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.clear-btn {
  background-color: #ffffff;
  border: 1px solid #cbd5e1;
  color: #475569;
}

.clear-btn:hover {
  background-color: #f8fafc;
  border-color: #ebd5e1;
  color: #1e293b;
}

.search-btn {
  background-color: #3b82f6;
  border: none;
  color: #ffffff;
}

.search-btn:hover {
  background-color: #2563eb;
}
</style>