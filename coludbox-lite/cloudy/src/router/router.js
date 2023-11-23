import { createRouter, createWebHistory } from 'vue-router'
import HelloWorld from "@/components/HelloWorld.vue";
import Login from "@/components/UserLogin.vue";

const routes = [
    {
        path: '/menu',
        name: 'menu',
        component: HelloWorld
    }
    , {
        path: '/',
        name: 'login',
        component: Login
    }

]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})


export default router
