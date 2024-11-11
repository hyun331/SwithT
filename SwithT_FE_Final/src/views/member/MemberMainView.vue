<template>
  <v-container class="d-flex justify-center align-center" style="height: 65vh">
    <v-card
      class="pa-16"
      outlined
      style="width: 800px; height: 500px; 
          background-color: white; border-width: 1px; margin-top: 90px; border-radius: 25px;"
    >
      <v-tabs v-model="activeTab" background-color="#fff9db" grow centered>
        <v-tab style="font-weight: bold; color: black">TUTOR 로그인</v-tab>
        <v-tab style="font-weight: bold; color: black">TUTEE 로그인</v-tab>
      </v-tabs>

      <v-tabs-window v-model="activeTab">
        <v-tabs-window-item value="tutor">
          <br /><br />
          <v-btn
            :to="{ path: '/member/email/login' }"
            block
            style="height: 50px; width: 100%; border-width: 1px"
            outlined
          >
            <v-icon
              color="black"
              icon="mdi-email"
              size="large"
              style="margin-right: 10px"
            ></v-icon>
            이메일로 로그인
          </v-btn>

          <!-- <br /> -->

          <!-- <v-btn
            block
            style="
              height: 50px;
              width: 100%;
              background-color: #fee500;
              color: black;
              border-width: 1px;
            "
            outlined
            @click="redirectToKakao('TUTOR')"
          >
            <v-icon left style="margin-right: 10px"> mdi-chat </v-icon>
            카카오 계정으로 로그인
          </v-btn> -->

          <br />

          <v-btn
            block
            style="
              height: 50px;
              width: 100%;
              background-color: white;
              color: #4285f4;
              border-width: 1px;
            "
            outlined
            @click="redirectToGoogle('TUTOR')"
          >
            <v-icon left color="#4285F4" style="margin-right: 10px">
              mdi-google
            </v-icon>
            <span style="color: black">구글 계정으로 로그인</span>
          </v-btn>

          <v-divider class="my-4"></v-divider>

          <v-btn
            :to="{ path: '/member/email/register/tutor' }"
            block
            outlined
            style="
              height: 50px;
              width: 100%;
              background-color: white;
              color: black;
              display: flex;
              align-items: center;
              justify-content: center;
              font-weight: bold;
              border-width: 1px;
            "
          >
            <v-icon style="margin-right: 8px" :icon="mdiAccount"></v-icon>

            <svg-icon type="mdi" :path="path"></svg-icon>
            TUTOR 회원가입
          </v-btn>
          <br />
        </v-tabs-window-item>

        <v-tabs-window-item value="tutee">
          <br /><br />
          <v-btn
            :to="{ path: '/member/email/login' }"
            block
            style="height: 50px; width: 100%; border-width: 1px"
            outlined
          >
            <v-icon
              color="black"
              icon="mdi-email"
              size="large"
              style="margin-right: 10px"
            ></v-icon>
            이메일로 로그인
          </v-btn>

          <br />

          <!-- <v-btn
            block
            style="
              height: 50px;
              width: 100%;
              background-color: #fee500;
              color: black;
              border-width: 1px;
            "
            outlined
            @click="redirectToKakao('TUTEE')"
          >
            <v-icon left style="margin-right: 10px">mdi-chat</v-icon>
            카카오 계정으로 로그인
          </v-btn>

          <br /> -->

          <v-btn
            block
            style="
              height: 50px;
              width: 100%;
              background-color: white;
              color: #4285f4;
              border-width: 1px;
            "
            outlined
            @click="redirectToGoogle('TUTEE')"
          >
            <v-icon left style="margin-right: 10px">mdi-google</v-icon>
            <span style="color: black">구글 계정으로 로그인</span>
          </v-btn>
          <v-divider class="my-4"></v-divider>

          <v-btn
            :to="{ path: '/member/email/register/tutee' }"
            block
            outlined
            style="
              height: 50px;
              width: 100%;
              background-color: white;
              color: black;
              display: flex;
              align-items: center;
              justify-content: center;
              font-weight: bold;
              border-width: 1px;
            "
          >

            <svg-icon type="mdi" :path="path"></svg-icon>
     
            TUTEE 회원가입
          </v-btn>
          <br />
        </v-tabs-window-item>
      </v-tabs-window>
    </v-card>
  </v-container>
</template>

<script>
import Cookies from "js-cookie"; // js-cookie 라이브러리 import
import SvgIcon from "@jamescoyle/vue-icon";
import { mdiAccount } from "@mdi/js";
export default {
  name: "my-component",
  components: {
    SvgIcon,
  },
  data() {
    return {
      path: mdiAccount,
      activeTab: 0,
    };
  },
  methods: {
    redirectToGoogle(role) {
      // 쿠키에 role 저장
      Cookies.set("role", role);
      
      // Google OAuth URL로 리다이렉트
      const googleAuthUrl = `${process.env.VUE_APP_API_BASE_URL}/member-service/oauth2/authorization/google`;
      let retryCount = 0; // 재시도 횟수 초기화
      const maxRetries = 3; // 최대 재시도 횟수 설정

      function attemptRedirect() {
        // 타임아웃 후 /member/main 페이지로 이동
        const timeout = setTimeout(() => {
          window.location.href = "/member/main";
        }, 5000); // 5초 타임아웃 설정

        // 실제 리다이렉트 시도
        window.location.href = googleAuthUrl;

        // beforeunload 이벤트로 리다이렉션 성공 시 타임아웃 취소
        window.addEventListener("beforeunload", () => clearTimeout(timeout));
      }

      // 재시도 로직 설정
      attemptRedirect();

      window.addEventListener("error", () => {
        if (retryCount < maxRetries) {
          console.warn(`Google 로그인 재시도 중 (${retryCount + 1}/${maxRetries})`);
          retryCount++;
          setTimeout(attemptRedirect, 2000); // 2초 간격으로 재시도
        } else {
          console.error("Google 로그인 재시도 실패. /member/main으로 이동합니다.");
          window.location.href = "/member/main";
        }
      });
    },
    // redirectToKakao(role) {
    //   Cookies.set("role", role); // 쿠키에 role 저장
    //   window.location.href = 
    //     `${process.env.VUE_APP_API_BASE_URL}/member-service/oauth2/authorization/kakao`;
    // },
  },
};
</script>

<style scoped>
.v-btn {
  font-weight: bold;
}
</style>
