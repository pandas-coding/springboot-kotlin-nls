import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";
import Home from "@/view/Home.vue";
import Login from "@/view/Login.vue";
import HomeWelcome from "@/view/home/HomeWelcome.vue";
import HomeHelp from "@/view/home/HomeHelp.vue";
import FileTransfer from "@/view/file-transfer/FileTransfer.vue";

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  {
    path: '/home',
    component: Home,
    children: [
      { path: 'file-transfer', component: FileTransfer },
      { path: 'welcome', component: HomeWelcome },
      { path: 'help', component: HomeHelp },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router