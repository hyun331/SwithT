<template>
    <v-container 
    class="d-flex justify-center align-center" 
    style="height:65vh; margin-top:80px;">
      <v-card 
      class="pa-8" 
      outlined
      style="background-color: white; width: 800px; height: 500px; border-width: 1px; border-radius: 10px;">
        <!-- 탭 메뉴 -->
        <h3><strong>SwithT</strong></h3>
        <h5 style="color: gray;">스위티에 오신걸 환영합니다.</h5>
        <br>
        <v-tabs v-model="activeTab" background-color="#FFF490" grow centered>
          <v-tab style="font-weight: bold; color: black;">ADMIN 로그인</v-tab>
        </v-tabs>
  
        <v-tabs-window v-model="activeTab">
          <v-tabs-window-item value="tutor" >
          <!-- 튜터 로그인 -->
          <v-form @submit.prevent="doLogin" class="pa-4">
            <v-text-field
              label="이메일"
              v-model="email"
              type="email"
              solo
              required
              hide-details
            ></v-text-field>
            <v-text-field
              label="비밀번호"
              v-model="password"
              type="password"
              solo
              required
              hide-details
            ></v-text-field>
            
            <v-btn color="#6C97FD" class="mx-auto mt-3 mr-1" large style="width: 700px; " type="submit">
              로그인
            </v-btn>
          </v-form>
          </v-tabs-window-item>
        </v-tabs-window>
      </v-card>
    </v-container>
  </template>
  
  <script>
  import axios from 'axios'
  import { jwtDecode } from 'jwt-decode'
  
  export default {
    data() {
      return {
        
        activeTab: 0, // 기본 탭을 튜터 로그인으로 설정
        email: "",
        password: ""
        
      };
    },
    methods: {
      async doLogin() {
              try {
  
  
                  const loginData = {
                      email: this.email,
                      password: this.password,
                  }
  
  
                  const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member-service/doLogin`, loginData);
                  
                  console.log("로그인 성공");
                  
                  const token = response.data.result.token;
                  const refreshToken = response.data.result.refreshToken;
                  const role = jwtDecode(token).role;
                  const id = response.data.result.id;
  
                  localStorage.setItem('token', token)
                  localStorage.setItem('refreshToken', refreshToken)
                  localStorage.setItem('role', role)
                  localStorage.setItem('id', id)
              
                
  
                  window.location.href = "/home"
  
              } catch (e) {
                  const error_message = e.response.data.error_message
                  console.error(error_message);
              }
          },
    }
  };
  </script>
  
  <style scoped>
  .v-btn {
    font-weight: bold;
    border-radius: 20px;
  }
  .v-text-field input {
    border-radius: 10px;
  }
  a {
    color: black;
    font-weight: bold;
    text-decoration: none; /* 기본적으로 언더라인 없애기 */
  }
  
  a:hover {
    text-decoration: underline; /* 마우스가 올라가면 언더라인 표시 */
  }
  </style>
  