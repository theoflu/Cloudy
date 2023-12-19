import { createApp } from 'vue'
import App from './App.vue'
import axios from "axios";
import {BASE_URL} from "@/common/config";
import VueAxios from "vue-axios";
import router from "@/router/router";




axios.defaults.baseURL=BASE_URL;

createApp(App).use(router).use(VueAxios,axios).mount('#app');
