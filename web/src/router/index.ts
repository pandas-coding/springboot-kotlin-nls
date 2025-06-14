import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";
import Home from "@/view/Home.vue";
import Login from "@/view/Login.vue";
import Register from "@/view/Register.vue";
import ResetPassword from "@/view/ResetPassword.vue";

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/reset', component: ResetPassword },
  { path: '/home', component: Home },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router