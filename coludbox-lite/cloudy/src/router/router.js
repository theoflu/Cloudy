import { createRouter, createWebHistory } from 'vue-router'
import Login from "@/components/UserLogin.vue";
import UserRegister from "@/components/UserRegister.vue";
import DownloadFiles from "@/components/DownloadFiles.vue";
import FavouritePage from "@/components/FavouritePage.vue";
import MainMenu from "@/components/MainMenu.vue";
import DropDownComponent from "@/components/DropDownComponent.vue";

const routes = [
    {
        path: '/menu',
        name: 'menu',
        component: MainMenu
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
    , {
        path: '/favourite',
        name: 'favourite',
        component: FavouritePage
    }
    , {
        path: '/dropworncomp',
        name: 'dropworncomp',
        component: DropDownComponent
    }


]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})


export default router
