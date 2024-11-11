<template>
  <!-- <div id="app">
    <router-view></router-view>
  </div>
  <img alt="Vue logo" src="./assets/logo.png">
  <HelloWorld msg="Welcome to Your Vue.js App"/> -->
  <v-app>
    <SidebarComponent/>
    <HeaderComponent/>  
      <!-- <v-container fluid class="custom-container"> -->
        <routerView />
      <!-- </v-container> -->
    <FooterComponent v-if="showFooter"/>
  </v-app>
</template>

<script>
import FooterComponent from '@/components/footer/FooterComponent.vue'
import SidebarComponent from '@/components/sideBar/SideBarComponent.vue'
import HeaderComponent from './components/header/HeaderComponent.vue';
import { computed } from 'vue';
import { useRoute } from 'vue-router';
export default {
  name: 'App',
  components: {
    HeaderComponent,
    FooterComponent,
    SidebarComponent
  },
  setup() {
    const route = useRoute();

    // 스케쥴 화면 footer 여부
    const showFooter = computed(() => {
      return route.path !== '/schedule';
    });
    return { showFooter };
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
.v-application {
  font-family: 'Pretendard', sans-serif !important;
}
@font-face {
  font-family: 'Pretendard';
  src: url('https://cdn.jsdelivr.net/gh/Project-Noonnu/noonfonts_2107@1.1/Pretendard-Regular.woff') format('woff');
  font-weight: 400; 
  font-style: normal;
}
</style>
