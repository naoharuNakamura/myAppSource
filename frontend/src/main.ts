import { createApp } from 'vue'
import './assets/style.css'
import App from './App.vue'
import { router } from "./router";
import { createPinia } from "pinia";
import Vue3Toastify, { type ToastContainerOptions } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

const app = createApp(App);
const pinia = createPinia();
app.use(Vue3Toastify, {
  autoClose: 3000,
  position: 'top-center',
} as ToastContainerOptions);

app.use(pinia)
app.use(router)

app.mount('#app')

