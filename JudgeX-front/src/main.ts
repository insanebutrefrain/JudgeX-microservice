import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ArcoVue from '@arco-design/web-vue'
import '@arco-design/web-vue/dist/arco.css'
import "@/plugins/axios.ts";
import "@/access/index.ts";
import "bytemd/dist/index.css";

const app = createApp(App);
app.use(store);
app.use(router);
app.use(ArcoVue);
app.mount('#app');

// openapi --input http://localhost:8101/api/v2/api-docs --output ./generated --client axios


