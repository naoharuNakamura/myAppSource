<script setup lang="ts">
import { useRestaurantDetail } from '../composables/useRestaurantDetail';
import { handleReview } from '../composables/handleReview';
import { UI_TEXTS } from '../constants/messages';
import { computed, onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import StarRatings from 'vue3-star-ratings';
import { useFavorites } from '../composables/useFavorites';
import { GoogleMap, AdvancedMarker } from 'vue3-google-map';
const authStore = useAuthStore();
const text = UI_TEXTS.detail;
const MAPS_API_KEY = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;

const {
  restaurant,
  isLoading,
  isError,
  errorMessage,
  // isFavorite,
  goBack,
  loadRestaurant
} = useRestaurantDetail();

const {
  reviews,
  userReview,
  userRating,
  saveError,
  isEditing,
  hoverRating,
  loadReviews,
  deleteReview,
  addReview,
  startEdit,
  cancelEdit,
  setRating,
  setHoverRating
} = handleReview()

const {
  toggleFavorite,
  isFavorite
} = useFavorites()

const submitReview = async () => {
  const isSuccess = await addReview();
  if (isSuccess && loadRestaurant) {
    await loadRestaurant();
  }
};

const handleDeleteReview = async (reviewId: number) => {
  const isSuccess = await deleteReview(reviewId);
  if (isSuccess && loadRestaurant) {
    await loadRestaurant();
  }
};

const mapCenter = computed(() => {
  if (!restaurant.value?.latitude || !restaurant.value?.longitude) return null;
  return {
    center: { lat: restaurant.value.latitude, lng: restaurant.value.longitude },
    zoom: 16
  };
});


onMounted(async () => {
  try {
    await Promise.all([
      loadRestaurant(),
      loadReviews()
    ]);
  } catch (err: any) {
    throw err
  }
});

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
          <input type="checkbox" :id="'fav-' + restaurant.restaurantId" :checked="isFavorite(restaurant.restaurantId)"
            @click.stop="toggleFavorite(restaurant.restaurantId)" class="favorite-checkbox" />
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
              <span class="info-value">{{ Number(restaurant?.restaurantRating).toFixed(1) }}</span>
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
                <a :href="restaurant.restaurantUrl" target="_blank" rel="noopener noreferrer" class="official-link">
                  {{ restaurant.restaurantUrl }}
                </a>
              </span>
            </p>
          </div>


        </div>
        <div class="maps-container">
          <div class="maps-area">
            <h3 class="review-title">店舗Map</h3>
            <div v-if="mapCenter" class="map-wrapper">
              <GoogleMap :api-key="MAPS_API_KEY" style="width: 100%; height: 400px; border-radius: 12px;" map-id="DEMO_MAP_ID"
                :center="mapCenter.center" :zoom="mapCenter.zoom">
                <AdvancedMarker :options="{ position: mapCenter.center }" />
              </GoogleMap>
            </div>
            <div v-else class="no-map">
              <p>※こちらの店舗の位置情報は表示できません。</p>
            </div>
          </div>
        </div>
        <div class="review-container">

          <h3 class="review-title">店舗へのレビュー ({{ reviews.length }}件)</h3>
          <div class="memo-column">
            <div class="memo-area">
              <div v-if="isEditing" class="memo-body">
                <div class="rating-container">
                  <label class="rating-label">評価を選んでください：</label>
                  <div class="stars">
                    <span v-for="score in 5" :key="score" class="star"
                      :class="{ 'is-active': score <= (hoverRating || userRating) }" @mouseenter="setHoverRating(score)"
                      @mouseleave="setHoverRating(0)" @click="setRating(score)">
                      ★
                    </span>
                    <div v-if="saveError" class="error-text save-error">{{ saveError }}</div>
                  </div>
                </div>
                <textarea v-model="userReview" class="memo-textarea" rows="4"
                  :placeholder="text.memoPlaceholder"></textarea>
              </div>
              <div class="memo-header">
                <div class="memo-actions">
                  <button v-if="!isEditing" @click="startEdit" class="memo-button">{{ text.editButton }}</button>
                  <button v-else @click="submitReview" class="memo-button">{{ text.saveButton }}</button>
                  <button v-if="isEditing" @click="cancelEdit" class="memo-button secondary">{{ text.cancelButton
                  }}</button>
                </div>
              </div>
            </div>
          </div>
          <div v-if="reviews.length === 0" class="no-reviews">
            まだレビューがありません。
          </div>
          <div v-for="review in reviews" :key="review.reviewId" class="review-card">
            <div class="review-header">
              <span>{{ review.userName }}</span>
              <star-ratings v-model="review.userRating" :read-only="true" :star-size="20" :show-rating="false"
                inactive-color="#ccc" active-color="#ffcc00" />
            </div>

            <p class="review-text">{{ review.memo }}</p>
            <button v-if="review.userId === authStore.currentUser?.userId" @click="handleDeleteReview(review.reviewId)"
              class="delete-btn">削除 </button>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>


<style scoped>
.map-wrapper {
  width: 100%;
  height: 400px;
  overflow: hidden;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.map-wrapper:hover {
  transform: translateY(-2px);
}

#map {
  width: 100%;
}

.maps-container {
  width: 100%;
  padding: 10px;
  background-color: #ffffff;
  border-top: 1px solid #e2e8f0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-container {
  width: 100%;
  max-width: 960px;
  margin: 40px auto;
  padding: 0 24px;
  box-sizing: border-box;
}

.back-link-btn {
  background: none;
  border: none;
  color: #64748b;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 6px;
  margin-bottom: 12px;
  transition: all 0.2s ease;
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
  margin: 0;
  font-size: clamp(20px, 4vw, 26px);
  color: #1a202c;
  font-weight: 700;
}

.favorite-wrapper {
  position: relative;
  top: 0;
  right: 0;
}

.detail-card {
  overflow: hidden;
  background-color: var(--color-bg-card, #ffffff);
  border: 1px solid #e2e8f0;
  border-radius: var(--radius-lg, 16px);
  box-shadow: var(--shadow-md, 0 4px 6px -1px rgba(0, 0, 0, 0.05));
}

.image-wrapper {
  position: relative;
  width: 100%;
  height: 320px;
  background-color: #f1f5f9;
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

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
  min-width: 0;
  max-width: 100%;
}

.grid-column p {
  display: flex;
  align-items: flex-start;
  width: 100%;
  margin: 0;
  font-size: 14px;
  color: #334155;
  line-height: 1.6;
}

.info-label {
  position: relative;
  display: inline-block;
  width: 80px;
  font-weight: 700;
  color: #64748b;
}

.info-label::after {
  content: "：";
  position: absolute;
  right: 4px;
}

.info-value {
  flex: 1;
  min-width: 0;
  color: #1e293b;
}

.official-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 600;
  word-break: break-all;
  transition: color 0.2s ease;
}

.official-link:hover {
  color: #1d4ed8;
  text-decoration: underline;
}

.memo-area {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 100%;
  transition: transform 1.2s ease;
}

.memo-header {
  display: flex;
  flex-direction: row-reverse;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding-bottom: 8px;
}

.memo-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
}

.memo-sub-label {
  font-size: 12px;
  font-weight: 500;
  color: #94a3b8;
}

.memo-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rating-container {
  display: flex;
  align-items: center;
}

.star {
  font-size: 2rem;
  color: #ccc;
  cursor: pointer;
  transition: color 0.1s ease;
}

.star.is-active {
  color: #ffca28;
}

.rating-text {
  font-size: 0.9rem;
  color: #666;
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
  box-sizing: border-box;
  outline: none;
  transition: all 0.2s ease;
}

.memo-textarea:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.memo-text {
  display: block;
  width: 100%;
  min-height: 80px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background-color: #ffffff;
  color: #334155;
  white-space: pre-wrap;
  line-height: 1.6;
  word-break: break-all;
  overflow-wrap: break-word;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.02);
  box-sizing: border-box;
}

.memo-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
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

.review-container {
  width: 100%;
  padding: 32px;
  background-color: #ffffff;
  border-top: 1px solid #e2e8f0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-title {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

.no-reviews {
  padding: 24px;
  text-align: center;
  color: #94a3b8;
  font-size: 14px;
  background-color: #f8fafc;
  border-radius: 12px;
  border: 1px dashed #e2e8f0;
}

.review-card {
  padding: 20px;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.review-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.review-username {
  font-weight: 700;
  color: #334155;
  font-size: 15px;
}

.review-text {
  margin: 0;
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
  overflow-wrap: break-word;
}

.review-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 4px;
}

.delete-btn {
  background: none;
  border: 1px solid #fee2e2;
  color: #ef4444;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.delete-btn:hover {
  background-color: #fef2f2;
  border-color: #fca5a5;
}

/* レスポンシブ対応 */
@media screen and (min-width: 750px) {
  .detail-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
  }

  .memo-column {
    grid-column: 1 / -1;
  }

  .rating-container {
    flex-direction: row;
  }
}

@media screen and (max-width: 750px) {
  .detail-container {
    padding: 0 16px;
  }

  .detail-ribbon {
    align-items: flex-start;
    gap: 12px;
  }

  .detail-grid {
    padding: 16px;
  }

  .memo-header {
    align-items: flex-start;
  }

  .review-container {
    padding: 24px 16px;
  }

  .review-card {
    padding: 16px;
  }
}
</style>