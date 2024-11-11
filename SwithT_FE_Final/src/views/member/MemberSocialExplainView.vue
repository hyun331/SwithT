<template>
  <div class="info-container d-flex flex-column justify-content-center align-items-center text-center">
    <h1 class="welcome-message mb-4">스위티에 처음 오셨군요!</h1>
    <p class="info-text mb-5">
      회원님의 정보를 완성하기 위해 추가 정보입력이 필요해요! 이동하시겠습니까?
    </p>
    <v-btn 
      class="continue-btn" 
      @click="navigateToRegister"
      color="#6C97FD"
      dark
      large
    >
      계속하기
    </v-btn>
  </div>
</template>

<script>
import Cookies from 'js-cookie'; // js-cookie 라이브러리 import

export default {
  methods: {
    navigateToRegister() {
      const role = Cookies.get('role'); // 쿠키에서 role을 읽어옴

      // role 값에 따라 라우팅 처리
      if (role === 'TUTOR') {
        this.$router.push('/member/social/register/tutor'); // 튜터 추가 정보 입력 화면으로 이동
      } else if (role === 'TUTEE') {
        this.$router.push('/member/social/register/tutee'); // 튜티 추가 정보 입력 화면으로 이동
      } else {
        // role이 없거나 다른 경우 기본 경로로 이동
        this.$router.push('/home');
      }
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
