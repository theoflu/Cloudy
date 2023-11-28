import { createRouter, createWebHistory } from 'vue-router'
import HelloWorld from "@/components/HelloWorld.vue";
import Login from "@/components/UserLogin.vue";
import UserRegister from "@/components/UserRegister.vue";
import DownloadFiles from "@/components/DownloadFiles.vue";

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
    }, {
        path: '/register',
        name: 'register',
        component: UserRegister
    }
    , {
        path: '/downloadfile',
        name: 'downloadfile',
        component: DownloadFiles
    }


]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})


export default router
