import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";
import Home from "@/view/Home.vue";
import Login from "@/view/Login.vue";

const routes: RouteRecordRaw[] = [
  { path: '/home', component: Home },
  { path: '/login', component: Login},
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router