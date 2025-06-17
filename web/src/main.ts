import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import 'ant-design-vue/dist/reset.css'
import router from "./router";
import { createPinia } from "pinia";

createApp(App)
  .use(router)
  .use(createPinia())
  .mount('#app')

console.info('VITE_SERVER: %o', import.meta.env.VITE_SERVER)