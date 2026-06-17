import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ROUTES, ROUTE_NAMES } from '../constants/types'

// components から各画面（コンポーネント）をインポート
import siteLogin from '../components/siteLogin.vue'
import siteSignUp from '../components/siteSignUp.vue'
import siteSearch from '../components/siteSearch.vue'
import siteResult from '../components/siteResult.vue'
import siteResultDetail from '../components/siteResultDetail.vue'
import siteMyPage from '../components/siteMyPage.vue'

// パスと画面の紐付け定義
const routes: Array<RouteRecordRaw> = [
  {
    path: ROUTES.HOME,
    redirect: { name: ROUTE_NAMES.LOGIN } // 初期アクセス時はログイン画面にリダイレクト
  },
  {
    path: ROUTES.LOGIN,
    name: ROUTE_NAMES.LOGIN,
    component: siteLogin
  },
  {
    path: ROUTES.SIGNUP,
    name: ROUTE_NAMES.SIGNUP,
    component: siteSignUp
  },
  {
    path: ROUTES.SEARCH,
    name: ROUTE_NAMES.SEARCH,
    component: siteSearch
  },
  {
    path: ROUTES.RESULT,
    name: ROUTE_NAMES.RESULT,
    component: siteResult
  },
  {
    path: ROUTES.RESULT_DETAIL_PATH, // 店舗IDをURLパラメータとして受け取れるように設定
    name: ROUTE_NAMES.RESULT_DETAIL,
    component: siteResultDetail
  },
  {
    path: ROUTES.MYPAGE,
    name: ROUTE_NAMES.MYPAGE,
    component: siteMyPage
  }
]



export const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from) => {
  const authStore = useAuthStore();
  // マイページへのアクセス、かつ未ログインの場合
  if (to.name !== ROUTE_NAMES.LOGIN && to.name !== ROUTE_NAMES.SIGNUP && !authStore.isLoggedIn) {
    return { name: ROUTE_NAMES.LOGIN };
  }
});

export const goToResult = () => {
  router.push({ name: ROUTE_NAMES.RESULT });
}


