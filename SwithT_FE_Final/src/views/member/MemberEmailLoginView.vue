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
        <v-tab style="font-weight: bold; color: black;">TUTOR 로그인</v-tab>
        <v-tab style="font-weight: bold; color: black;">TUTEE 로그인</v-tab>
      </v-tabs>

      <v-tabs-window v-model="activeTab">
        <v-tabs-window-item value="tutor" >
        <!-- 튜터 로그인 -->
        <v-form @submit.prevent="doLogin" class="pa-4">
          
          <v-row class="mt-0 mr-2">
            
            <span class="material-icons mt-4 mr-4" style="font-size: 36px;">
              person_outline
              </span>

              <v-text-field
              clearable
              variant="outlined"
              label="Email"
              v-model="email"
              type="email"
              solo
              required
              hide-details
            ></v-text-field>
          </v-row>

          <v-row class="mr-2 mt-3">
            <span class="material-icons mt-4 mr-4" style="font-size: 36px;">
              lock_outline
              </span>
              <v-text-field
              :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
              clearable
              class="mt-2"
              variant="outlined"
              label="Password"
              v-model="password"
              :type="visible ? 'text' : 'password'" 
              solo
              required
              hide-details 
              @click:append-inner="visible = !visible"
            ></v-text-field>
          </v-row>


          <v-btn color="#6C97FD" class="mx-auto mt-5 ml-8" large style="width: 655px; " type="submit">
            로그인
          </v-btn>

        </v-form>
        <div class="text-center" style="margin-top: 20px; ">
          <a href="/member/email/register/tutor">TUTOR 회원가입</a>
        </div>
        </v-tabs-window-item>
      
        <v-tabs-window-item>
          <!-- 튜티 로그인 -->
          <v-form @submit.prevent="doLogin" class="pa-4">
            <v-row class="mt-0 mr-2">
            
              <span class="material-icons mt-4 mr-4" style="font-size: 36px;">
                person_outline
                </span>
  
                <v-text-field
                clearable
                variant="outlined"
                label="Email"
                v-model="email"
                type="email"
                solo
                required
                hide-details
              ></v-text-field>
            </v-row>
  
            <v-row class="mr-2 mt-3">
              <span class="material-icons mt-4 mr-4" style="font-size: 36px;">
                lock_outline
                </span>
                <v-text-field
                :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
                clearable
                class="mt-2"
                variant="outlined"
                label="Password"
                v-model="password"
                :type="visible ? 'text' : 'password'" 
                solo
                required
                hide-details 
                @click:append-inner="visible = !visible"
              ></v-text-field>
            </v-row>
  
  
            <v-btn color="#6C97FD" class="mx-auto mt-5 ml-5" large style="width: 660px; " type="submit">
              로그인
            </v-btn>
          </v-form>
          <div class="text-center" style="margin-top: 20px;">
            <a href="/member/email/register/tutee">TUTEE 회원가입</a>
          </div>
        </v-tabs-window-item>
      </v-tabs-window>
    </v-card>
  </v-container>

  <AlertModal
     v-model="alertModal" 
     @update:dialog="alertModal = $event"
     icon=mdi-alert-circle-outline
     :title=this.alertModalTtile
     :contents=this.alertModalContents
     />
</template>

<script>
import axios from 'axios'
import { jwtDecode } from 'jwt-decode'
import AlertModal from '@/components/AlertModal.vue';

export default {
  components: {
      AlertModal
  },
  data() {
    return {
      visible: false,
      activeTab: 0, // 기본 탭을 튜터 로그인으로 설정
      email: "",
      password: "",
      alertModal: false,
      alertModalTtile: '',
      alertModalContents: '',
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
                this.alertModalTtile = "로그인 실패!!!";
                this.alertModalContents = error_message;
                this.alertModal = true;
                // console.error(error_message);
            }
        },
  }
};
</script>

<style scoped>
.v-btn {
  font-weight: bold;
  border-radius: 8px;
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
::v-deep .v-input__icon--clear {
  margin-right: 10px; /* clearable 아이콘 간격 */
}

</style>
