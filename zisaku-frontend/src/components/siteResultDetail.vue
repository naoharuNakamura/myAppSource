<script setup lang="ts">
import { useRestaurantDetail } from '../composables/useRestaurantDetail';
import { UI_TEXTS } from '../constants/messages';

const text = UI_TEXTS.detail;

const {
  restaurant,
  restaurantMemo,
  isEditing,
  isLoading,
  isError,
  errorMessage,
  saveError,
  isFavorite,
  startEdit,
  cancelEdit,
  saveMemo,
  toggleFavorite,
  goBack,
} = useRestaurantDetail();
</script>

<template>
  <div class="detail-container">
    <div v-if="isLoading" class="simple-loading-container">
      <div class="loading-spinner"></div>
      <p class="loading-text">{{ text.loadingText }}</p>
    </div>

    <div v-else-if="isError" class="error-banner">
      <p>{{ errorMessage }}</p>
      <button type="button" class="back-link-btn" @click="goBack">{{ text.backToListButton }}</button>
    </div>

    <div v-else>
      <button type="button" class="back-link-btn" @click="goBack">{{ text.backButton }}</button>
      <div class="detail-ribbon">
        <h2>{{ restaurant?.restaurantName }}</h2>
        <div v-if="restaurant" class="favorite-wrapper">
          <input
            type="checkbox"
            :id="'fav-' + restaurant.restaurantId"
            :checked="isFavorite"
            @click.stop="toggleFavorite"
            class="favorite-checkbox"
          />
          <label :for="'fav-' + restaurant.restaurantId" class="heart-icon"></label>
        </div>
      </div>

      <div class="detail-card">
        <div class="image-wrapper">
          <img :src="restaurant?.restaurantImg" :alt="restaurant?.restaurantName" />
        </div>

        <div class="detail-grid">
          <div class="grid-column">
            <p>
              <span class="info-label">評価</span>
              <span class="info-value">{{ restaurant?.restaurantRating }}</span>
            </p>
            <p>
              <span class="info-label">ジャンル</span>
              <span class="info-value">{{ restaurant?.restaurantGenre }}</span>
            </p>
            <p>
              <span class="info-label">価格帯</span>
              <span class="info-value">{{ restaurant?.restaurantPriceRange }}</span>
            </p>
          </div>

          <div class="grid-column">
            <p>
              <span class="info-label">営業時間</span>
              <span class="info-value">
                {{ restaurant?.restaurantOpenHour }} - {{ restaurant?.restaurantCloseHour }}
              </span>
            </p>
            <p>
              <span class="info-label">休業日</span>
              <span class="info-value">{{ restaurant?.restaurantClosedDays }}</span>
            </p>
            <p>
              <span class="info-label">電話番号</span>
              <span class="info-value">{{ restaurant?.restaurantPhone }}</span>
            </p>
          </div>

          <div class="grid-column">
            <p>
              <span class="info-label">住所</span>
              <span class="info-value">{{ restaurant?.restaurantAddress }}</span>
            </p>
            <p v-if="restaurant?.restaurantUrl">
              <span class="info-label">公式サイト</span>
              <span class="info-value">
                <a
                  :href="restaurant.restaurantUrl"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="official-link"
                >
                  {{ restaurant.restaurantUrl }}
                </a>
              </span>
            </p>
          </div>

          <div class="memo-column">
            <div class="memo-area">
              <div class="memo-header">
                <span class="memo-label">{{ text.memoLabel }}</span>
                <div class="memo-actions">
                  <button v-if="!isEditing" @click="startEdit" class="memo-button">{{ text.editButton }}</button>
                  <button v-else @click="saveMemo" class="memo-button">{{ text.saveButton }}</button>
                  <button v-if="isEditing" @click="cancelEdit" class="memo-button secondary">{{ text.cancelButton }}</button>
                </div>
              </div>

              <div v-if="saveError" class="error-text save-error">{{ saveError }}</div>

              <div class="memo-body">
                <span v-if="!isEditing" class="memo-text">
                  {{ restaurantMemo || text.emptyMemo }}
                </span>
                <textarea
                  v-else
                  v-model="restaurantMemo"
                  class="memo-textarea"
                  rows="4"
                  :placeholder="text.memoPlaceholder"
                ></textarea>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>
/* 既存の変数（変数がない場合のフォールバック値も安全のために指定） */
.detail-card {
  background-color: var(--color-bg-card, #ffffff);
  border-radius: var(--radius-lg, 16px);
  box-shadow: var(--shadow-md, 0 4px 6px -1px rgba(0, 0, 0, 0.05));
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.detail-container {
  width: min(100%, 960px);
  max-width: 960px;
  margin: 40px auto;
  padding: 0 24px;
}

/* 戻るボタンの洗練化 */
.back-link-btn {
  background: none;
  border: none;
  color: #64748b;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.2s ease;
  margin-bottom: 12px;
}

.back-link-btn:hover {
  color: #1e293b;
  background-color: #f1f5f9;
}

.detail-ribbon {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #edf2f7;
  padding-bottom: 12px;
  margin-bottom: 20px;
}

.detail-ribbon h2 {
  font-size: clamp(20px, 4vw, 26px);
  color: #1a202c;
  margin: 0;
  font-weight: 700;
}

.image-wrapper {
  width: 100%;
  height: 320px;
  position: relative;
  background-color: #f1f5f9;
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.favorite-wrapper {
  position: relative;
  top: 0;
  right: 0;
}

/* グリッドレイアウト */
.detail-grid {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 16px;
  padding: 32px;
  background-color: #f8fafc;
}

.grid-column {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 12px;
  max-width: 100%;
  min-width: 0;
}

/* 💡 PCでの見栄えを最適化 */
@media screen and (min-width: 440px) {
  .detail-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
  }

  /* 💡 メモエリアを3列分横いっぱいに広げる指定がこれで正常に動作します */
  .memo-column {
    grid-column: 1 / -1;
    border-top: 1px solid #e2e8f0;
    padding-top: 16px;
  }
}

/* 💡 コロン（：）の位置を縦に綺麗に揃えるためのラベル記述 */
.info-label {
  display: inline-block;
  width: 80px;
  font-weight: 700;
  color: #64748b;
  position: relative;
}

.info-label::after {
  content: "：";
  position: absolute;
  right: 4px;
}

.grid-column p {
  margin: 0;
  font-size: 14px;
  color: #334155;
  line-height: 1.6;
  display: flex;            /* 💡 左右に並べる */
  align-items: flex-start;  /* 💡 住所が2行になっても、ラベルは上に固定する */
  width: 100%;
}

/* マイメモエリア全体の見た目向上 */
.memo-area {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 8px 0 0 0;
  max-width: 100%;
}

.memo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding-bottom: 8px;
}

.memo-label {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 6px;
}

.memo-sub-label {
  font-size: 12px;
  font-weight: 500;
  color: #94a3b8;
}

.memo-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.memo-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.memo-text {
  display: block;
  min-height: 80px;
  padding: 16px;
  border-radius: 12px;
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  color: #334155;
  white-space: pre-wrap;
  line-height: 1.6;
  width: 100%;
  box-sizing: border-box;
  word-break: break-all;
  overflow-wrap: break-word;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.02);
}

.memo-textarea {
  width: 100%;
  padding: 16px;
  border: 1px solid #cbd5e1;
  border-radius: 12px;
  background-color: #ffffff;
  color: #1e293b;
  font-size: 14px;
  line-height: 1.6;
  transition: all 0.2s ease;
  box-sizing: border-box;
  outline: none;
}

.memo-textarea:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.memo-button {
  border: none;
  border-radius: 20px;
  padding: 6px 16px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 700;
  color: #ffffff;
  background-color: #3b82f6;
  transition: background-color 0.2s ease;
}

.memo-button:hover {
  background-color: #2563eb;
}

.memo-button.secondary {
  background-color: #64748b;
}

.memo-button.secondary:hover {
  background-color: #475569;
}

/* 右側の値（テキストやURL）のスタイル */
.info-value {
  flex: 1;                  /* 💡 残りの横幅をいっぱいに使う */
  min-width: 0;             /* 💡 これがあると、中の文字が枠を突き破らなくなる */
  color: #1e293b;
}

/* 公式サイトのURLリンク */
.official-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 600;
  word-break: break-all;    /* 💡 【重要】URLが枠の右端にきたら綺麗に自動改行させる */
  transition: color 0.2s ease;
}

.official-link:hover {
  color: #1d4ed8;
  text-decoration: underline;
}

@media screen and (max-width: 440px) {
  .detail-ribbon {
    align-items: flex-start;
    gap: 12px;
  }
  .detail-container {
    max-width: 100%;
    padding: 0 16px;
  }

  .detail-grid {
    padding: 16px;
  }

  .memo-header {
    align-items: flex-start;
  }
}

</style>