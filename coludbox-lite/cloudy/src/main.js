import { createApp } from 'vue'
import App from './App.vue'
import axios from "axios";
import {BASE_URL} from "@/common/config";


axios.defaults.baseURL=BASE_URL;

createApp(App).mount('#app')
