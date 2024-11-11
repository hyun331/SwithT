import { createApp } from 'vue';
import App from './App.vue';
import router from './router/index.js';
import vuetify from './plugins/vuetify.js';
import axios from 'axios';
import '@mdi/font/css/materialdesignicons.css';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import '@vuepic/vue-datepicker/dist/main.css'
import '@mdi/font/css/materialdesignicons.css'
import { useKakao } from 'vue3-kakao-maps/@utils';
import 'font-awesome/css/font-awesome.css';
import Toast, { POSITION } from 'vue-toastification';
import 'vue-toastification/dist/index.css';
const app = createApp(App);

axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axios.interceptors.response.use(
  response => response,
  async error => {
      if (error.response && error.response.status === 401) {
          
          const refreshToken = localStorage.getItem('refreshToken');
          
          try {

              localStorage.removeItem('token');
              const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member-service/refresh-token`, { refreshToken });
              localStorage.setItem('token', response.data.result.token);
              console.log("리프레쉬토큰으로 바꾼다~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
              window.location.reload();

          } catch (e) {
              console.log(e + "이거 인터셉터에서 나온거임여");
              localStorage.clear();
              window.location.href = "/member/main";

          }

      }
      return Promise.reject(error)
  }
);

useKakao('03a055c21377bee26ab1559dedf4af6f',['clusterer', 'services', 'drawing']);

app.use(router);
app.use(vuetify);
app.mount('#app');
app.use(Toast, {
  position: POSITION.TOP_RIGHT,
  timeout: 3000, // 토스트 알림 표시 시간
  closeOnClick: true, // 클릭하면 닫힘
});
