<template>
    <div>
      <h2>로그인</h2>
      <form @submit.prevent="handleLogin">
        <div>
          <label for="username">아이디:</label>
          <input type="text" v-model="email" id="email" required />
        </div>
        <div>
          <label for="password">비밀번호:</label>
          <input type="password" v-model="password" id="password" required />
        </div>
        <button type="submit">로그인</button>
      </form>
      <div v-if="errorMessage">{{ errorMessage }}</div>
    </div>

    <AlertModal
     v-model="alertModal" 
     @update:dialog="alertModal = $event"
     icon=mdi-alert-circle-outline
     :title=this.alertModalTtile
     :contents=this.alertModalContents
     />
  </template>
  
  <script>
  import axios from 'axios';
  import AlertModal from '@/components/AlertModal.vue';
  
  export default {
    components: {
        AlertModal
    },
    data() {
      return {
        email: '',
        password: '',
        errorMessage: '',
        alertModal: false,
        alertModalTtile: '',
        alertModalContents: '',
      };
    },
    methods: {
      async handleLogin() {
        try {
          const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member-service/doLogin`, {
            email: this.email,
            password: this.password,
          });
          console.log(this.email)
  
          localStorage.setItem('token', response.data.result.token);
        } catch (error) {
          this.errorMessage = '로그인 실패: 잘못된 아이디 또는 비밀번호.';
          this.alertModalTtile = "로그인 실패!!!";
          this.alertModalContents = error;
          this.alertModal = true;
        }
      },
    },
  };
  </script>
  