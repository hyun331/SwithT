<template>
    <div class="info-container d-flex flex-column justify-content-center align-items-center text-center">
      <h1 class="welcome-message mb-4">Welcome To SwithT!</h1>
      <v-btn 
        class="continue-btn" 
        @click="navigateHome"
        color="#6C97FD"
        dark
        large
      >
        계속하기
      </v-btn>
    </div>
  </template>
  
  <script>
  export default {
    mounted() {
      // 5초 후에 자동으로 /home으로 이동
      setTimeout(() => {
        this.navigateHome();
      }, 5000);
    },
    methods: {
      navigateHome() {
        
        window.location.href = '/home';
        // this.$router.push('/home'); // /home 경로로 라우팅
      
    }
    }
  };
  </script>
  
  <style scoped>
  .info-container {
    height: 85vh; /* 화면 전체 높이 설정 */
    background-color: #f8f9fa; /* 밝은 회색 배경 */
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    text-align: center;
    padding: 20px;
  }
  
  .welcome-message {
    font-size: 2.5rem;
    font-weight: bold;
    color: #2c3e50; /* 어두운 블루 컬러 */
  }
  
  .info-text {
    font-size: 1.25rem;
    color: #34495e; /* 중간 블루 컬러 */
  }
  
  .continue-btn {
    width: 200px;
  }
  </style>
  