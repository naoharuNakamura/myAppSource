<script setup lang="ts">
import StarRatings from 'vue3-star-ratings';
import { useAuthStore } from '../stores/auth';
import type { Restaurant } from '../constants/types';
import { ROUTE_NAMES } from '../constants/types';
import { UI_TEXTS } from '../constants/messages';

const authStore = useAuthStore();
const text = UI_TEXTS.common;
const props = defineProps<{
  restaurant: Restaurant
}>()
const restaurant = props.restaurant

</script>

<template>
  <div class="card-container">
    <div class="favorite-wrapper">
      <input type="checkbox" :id="'fav-' + restaurant.restaurantId"
        :checked="authStore.isFavorite(restaurant.restaurantId)"
        @click.stop="authStore.toggleFavorite(restaurant.restaurantId)" class="favorite-checkbox" />
      <label :for="'fav-' + restaurant.restaurantId" class="heart-icon"></label>
    </div>

    <RouterLink :to="{ name: ROUTE_NAMES.RESULT_DETAIL, params: { restaurantId: restaurant.restaurantId } }" class="card-link">
      <div class="image-wrapper">
        <img :src="restaurant.restaurantImg" :alt="text.restaurantImageAlt" />
      </div>

      <div class="card-label">
        <h4>{{ restaurant.restaurantName }}</h4>
      </div>

      <div class="info">
        <p>{{ restaurant.restaurantArea }} / {{ restaurant.restaurantGenre }}</p>
      </div>

      <div class="rating">
        <star-ratings v-model="restaurant.restaurantRating" :read-only="true" :star-size="20" :show-rating="false"
          inactive-color="#ccc" active-color="#ffcc00" />
        <p>{{ Number(restaurant.restaurantRating).toFixed(1) }}</p>
      </div>
    </RouterLink>
  </div>
</template>


<style scoped>
.card-container {
  background-color: var(--color-bg-card);
  border-radius: var(--radius-md);
  /* 共通の角丸 */
  box-shadow: var(--shadow-sm);
  /* 共通の影 */
  border: 1px solid var(--color-border);
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
}

.card-container:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05), 0 4px 6px -2px rgba(0, 0, 0, 0.03);
}

.favorite-wrapper {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 10;
}

.favorite-checkbox {
  display: none;
}

.heart-icon {
  display: block;
  width: 36px;
  height: 36px;
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}


.favorite-checkbox:checked+.heart-icon {
  background-color: #ffffff;
}

.heart-icon:hover {
  transform: scale(1.1);
}

.card-link {
  text-decoration: none;
  color: inherit;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.image-wrapper {
  width: 100%;
  padding-top: 60%;
  position: relative;
  background-color: #f1f5f9;
}

.image-wrapper img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-label {
  padding: 16px 16px 8px 16px;
}

.card-label h4 {

  font-size: 18px;
  padding: 0 12px;
  margin: 12px 0 8px 0;
  color: var(--color-text-main);
  line-height: 1.5;

  /* 🌟 標準プロパティ（今後の標準規格への互換性） */
  line-clamp: 2;

  /* 🌟 従来のブラウザ・Webkit系ブラウザ向けの互換性 */
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;

  /* 💡 指定行数を超えた部分を隠すために必須のプロパティ */
  overflow: hidden;
  height: 50px;
}

.card-link:hover .card-label h4 {
  color: #3b82f6;
}

.info {
  padding: 0 16px 12px 16px;
  font-size: 13px;
  color: #64748b;
}

.info p {
  padding: 0 12px;
  margin: 0;
  color: #b8b8b8;
  line-height: 1.5;

  /* 🌟 標準プロパティ（今後の標準規格への互換性） */
  line-clamp: 1;

  /* 🌟 従来のブラウザ・Webkit系ブラウザ向けの互換性 */
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;

  /* 💡 指定行数を超えた部分を隠すために必須のプロパティ */
  overflow: hidden;
}

.rating {
  margin-top: auto;
  padding: 12px 16px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  align-items: center;
  gap: 6px;
  color: #eab308;
  font-size: 14px;
  font-weight: 600;
}

.rating p {
  margin: 0;
  color: #475569;
}

.star {
  color: #ccc;
  /* 未評価時の色（グレー） */
  font-size: 20px;
}

.star.active {
  color: #ffcc00;
  /* 評価時の色（ゴールド） */
}
</style>