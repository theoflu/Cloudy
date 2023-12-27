import { createRouter, createWebHistory } from 'vue-router'
import Login from "@/components/UserLogin.vue";
import UserRegister from "@/components/UserRegister.vue";
import DownloadFiles from "@/components/DownloadFiles.vue";
import FavouritePage from "@/components/FavouritePage.vue";
import MainMenu from "@/components/MainMenu.vue";
import TrashcanComponent from "@/components/TrashcanComponent.vue";
import PhotoVideoComp from "@/components/PhotoVideoComp.vue";
import SearchItem from "@/components/SearchItem.vue";

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
    ,{
        path: '/trash',
        name: 'trash',
        component: TrashcanComponent
    }
    , {
        path: '/vid',
        name: 'vid',
        component: PhotoVideoComp
    },{
        path: '/search',
        name: 'search',
        component: SearchItem
    }


]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})


export default router
