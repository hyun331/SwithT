<template>
  <section v-if="isLogin" class="side-bar">
    <div class="profile">
      <div style="position: relative;">
        <v-avatar class="profile-image" style="height: 80px; width: 80px; border: 4px solid #6C97FD">
          <v-img :src="profileUrl"></v-img>
        </v-avatar>
        <div @click="openAlertListModal()" class="alert-badge">
          <span>{{ count }}</span>
        </div>
      </div>
      <v-overlay v-model="alertDialogSSE" width="400" content-class="custom-alert-position" absolute
        scrim="false" >
        <v-card 
        :style="{ height: '100vh', top: '0', right:'10px',overflowY: 'auto', boxShadow: 'inset -4px 0 10px rgba(0, 0, 0, 0.1)', backgroundColor:'#f1f3f5' }"
        class="border border-t-sm border-b-sm border-l-sm pa-5">
          <v-card-title style="text-align: center; padding-bottom: 0;">
            <v-row>
              <v-col :class="{ 'tab-active': selectedTab === 'payment' }"
                style="cursor: pointer; padding: 10px; font-weight: bold; position: relative;"
                @click="selectTab('payment')">
                결제 알림
              </v-col>
              <v-col :class="{ 'tab-active': selectedTab === 'general' }"
                style="cursor: pointer; padding: 10px; font-weight: bold; position: relative;"
                @click="selectTab('general')">
                일반 알림
              </v-col>
            </v-row>
          </v-card-title>

          <v-card-text class="mt-5">
            <div v-if="filteredEventList.length === 0">
              새로운 알림이 없습니다.
            </div>
            <div v-else>
              <div>
                <v-card v-for="event in filteredEventList.slice().reverse()" :key="event.id" class="alert-card" outlined
                  @click="goToAlertDetail(event)" :class="{ 'alert-card-active': selectedAlertId === event.id }">
                  <v-card-title>{{ event.title }}</v-card-title>
                  <v-card-subtitle>{{ event.contents }}</v-card-subtitle>
                </v-card>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-overlay>
      <div class="profile-name">
        <span style="font-weight: 700">{{ userName }}</span>
        <span> 님</span>
      </div>
    </div>
    <hr style="margin-bottom: 10px">
    <div class="side-menu">
      <div class="menu-list" v-for="(menu, index) in menuItems" :key="index" @click="navigateTo(menu.title)">
        <span class="material-icons menu-icon">{{ menu.icon }}</span>
        <div class="menu-title">{{ menu.title }}</div>
      </div>
    </div>
    <div class="logout" @click="logout">로그아웃</div>
  </section>

  <ChatModal
    v-model:dialog="chatModal"
    selectedChatRoomId=""
    persistent
    :style="{ position: 'fixed', right: '-9%', top: '10%', width: '0px' }"
  />
</template>

<script>
import axios from "axios";
import { EventSourcePolyfill } from 'event-source-polyfill'
import ChatModal from "../ChatModal.vue";
export default {
  components:{
    ChatModal
    },
  data() {
    return {
      isLogin: false,
      profileUrl: null,
      userName: null,
      userRole: null,
      menuItems: [],
      eventSource: null,
      alertDialogSSE: false,
      selectedTab: 'general',
      paymentEvents: [], // 결제 알림을 저장할 배열
      generalEvents: [], // 일반 알림을 저장할 배열
      count: 0,
      eventId: "",
      chatModal : false,
    };
  },
  watch: {
    paymentEvents: {
      handler() {
        this.setAlertCount();
      },
      deep: true, // 배열 내부의 요소 변화를 감지
    },
    generalEvents: {
      handler() {
        this.setAlertCount();
      },
      deep: true, // 배열 내부의 요소 변화를 감지
    },
  },
  async created() {
    await this.getMyInfo();
    const token = localStorage.getItem('token');
    if (token) {
      this.isLogin = true;
      this.userRole = localStorage.getItem('role');
      const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member-service/infoGet`);
      this.profileUrl = response.data.profileImage;
      this.profileUrl = localStorage.getItem('profileImage');
      this.userName = localStorage.getItem('name');

      this.setMenuItems();
    }

    await this.checkLoginStatus();

    // localStorage에서 알림 데이터를 불러옴
    const storedPaymentEvents = localStorage.getItem('paymentEvents');
    const storedGeneralEvents = localStorage.getItem('generalEvents');

    if (storedPaymentEvents) {
      this.paymentEvents = JSON.parse(storedPaymentEvents); // 결제 알림 불러오기
    }
    if (storedGeneralEvents) {
      this.generalEvents = JSON.parse(storedGeneralEvents); // 일반 알림 불러오기
    }

    this.count = this.paymentEvents.length + this.generalEvents.length;

    // SSE 연결 설정
    if (token) {
      let sse = new EventSourcePolyfill(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/subscribe`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      sse.addEventListener('notification', (event) => {
        if (event.data && event.data !== '""') {
          const newEvent = JSON.parse(event.data);

          // 중복 확인: 동일한 id의 알림이 있는지 확인
          const isDuplicatePayment = this.paymentEvents.some(e => e.id === newEvent.id);
          const isDuplicateGeneral = this.generalEvents.some(e => e.id === newEvent.id);

          // 결제 알림일 경우
          if (newEvent.messageType === '결제요청' && !isDuplicatePayment) {
            this.paymentEvents.push(newEvent);
            localStorage.setItem('paymentEvents', JSON.stringify(this.paymentEvents)); // 저장
          }
          // 일반 알림 처리
          else if (newEvent.messageType !== 'WAITING' && newEvent.messageType !== 'WAITING-SUCCESS' && newEvent.messageType !== '결제요청' && !isDuplicateGeneral) {
            this.generalEvents.push(newEvent);
            localStorage.setItem('generalEvents', JSON.stringify(this.generalEvents)); // 저장
          }
          else if (newEvent.messageType === '수강 신청' && !isDuplicateGeneral){
            this.generalEvents.push(newEvent);
            localStorage.setItem('generalEvents', JSON.stringify(this.generalEvents));
          }
          else if (newEvent.messageType === '수강 취소' && !isDuplicateGeneral){
            this.generalEvents.push(newEvent);
            localStorage.setItem('generalEvents', JSON.stringify(this.generalEvents));
          }

          // 전체 알림 개수 업데이트
          this.count = this.paymentEvents.length + this.generalEvents.length;
        }
      });
    } else {
      console.log("Token not found");
    }
  },

  computed: {
    filteredEventList() {
      if (this.selectedTab === 'payment') {
        return this.paymentEvents;
      } else {
        return this.generalEvents;
      }
    }
  },
  methods: {
    async getMyInfo() {
      const token = localStorage.getItem('token');
        if(token){
          try {
          const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member-service/infoGet`);
          this.profileUrl = response.data.profileImage;
          this.userName = response.data.name;
          this.userRole = response.data.role;
        } catch (e) {
          console.log(e);
        }
      }
    },
    setMenuItems() {
      if (this.userRole === 'TUTEE') {
        this.menuItems = [
          { title: '채팅', icon: 'chat', route: '/chat-room?chatRoomId=' },
          { title: '내 강의', icon: 'book', route: '/tutee-my-lecture-list' },
          { title: '내 스케줄', icon: 'calendar_today', route: '/schedule' },
          { title: '신청 내역', icon: 'assignment', route: '/tutee-applied-list' },
          { title: '내 정보', icon: 'person', route: '/member/mypage' },
          { title: '내 결제내역', icon: 'payments', route: '/payments/list' },
        ];
      } else if (this.userRole === 'TUTOR') {
        this.menuItems = [
          { title: '채팅', icon: 'chat', route: '/chat-room?chatRoomId=' },
          { title: '내 강의', icon: 'book', route: '/tutor-lecture-list' },
          { title: '내 스케줄', icon: 'calendar_today', route: '/schedule' },
          { title: '신청 내역', icon: 'assignment', route: '/tutor-applied-list' },
          { title: '정산금 관리', icon: 'monetization_on', route: '/balance/table' },
          { title: '내 정보', icon: 'person', route: '/member/mypage' },
        ];
      } else if (this.userRole === 'ADMIN') {
        this.menuItems = [
          { title: '승인 관리', icon: 'assignment', route: '/admin-applied-list' },
        ]
      }
    },
    navigateTo(title) {
      if(title ==='채팅'){
        this.chatModal = true;
      }else{
        const menuItem = this.menuItems.find(item => item.title === title);
        if (menuItem && menuItem.route) {
          this.$router.push(menuItem.route); // Vue Router를 통해 이동
        }
      }
      
    },
    openAlertListModal() {
      this.alertDialogSSE = true;
    },
    closeAlertListModal() {
      this.alertDialogSSE = false;
    },
    async checkLoginStatus() {
      const token = localStorage.getItem('token');
      if (token) {
        this.isLogin = true;
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member-service/infoGet`);
        this.member = response.data.result;
        const memberInfo = {
          name: this.member.name,
          profileImage: this.member.profileImage
        };
        localStorage.setItem('memberInfo', JSON.stringify(memberInfo));
      }
    },
    login() {
      this.$router.push("/member/main");
    },
    logout() {
      document.cookie.split(";").forEach(cookie => {
        const [name] = cookie.split("=");
        document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/;";
      });
      localStorage.clear();
      this.isLogin = false;
      window.location.href = '/home';
    },
    async goToAlertDetail(event) {
      const messageType = event.messageType;
      this.eventId = event.id;

      if (messageType === '결제요청') {
        try {
          this.$router.push('/tutee-applied-list');
        } catch (error) {
          console.error('결제 처리 중 오류 발생:', error);
        }
      }

      if (messageType === '강의 승인') {
        try {
          this.$router.push('/tutor-lecture-list');
          this.generalEvents = this.generalEvents.filter(e => e.id !== this.eventId);
          localStorage.setItem('generalEvents', JSON.stringify(this.generalEvents));
        } catch (error) {
          console.error('강의 승인 알림 처리 중 오류 발생:', error);
        }
      }
      else if(messageType === '수강 신청'){
        try{
          this.$router.push('/tutor-lecture-list');
          this.generalEvents = this.generalEvents.filter(e => e.id !== this.eventId);
          localStorage.setItem('generalEvents', JSON.stringify(this.generalEvents));
        } catch(error){
          console.error('강의 승인 알림 처리 중 오류 발생:', error);
        }
      }
      else if(messageType === '수강 취소'){
        try{
          this.$router.push('/tutor-lecture-list');
          this.generalEvents = this.generalEvents.filter(e => e.id !== this.eventId);
          localStorage.setItem('generalEvents', JSON.stringify(this.generalEvents));
        } catch(error){
          console.error('강의 승인 알림 처리 중 오류 발생:', error);
        }
      }

      this.alertDialogSSE = false;
    },
    setAlertCount() {
      this.count = this.paymentEvents.length + this.generalEvents.length;
    },
    selectTab(tab) {
      this.selectedTab = tab;
    },
  },
};
</script>

<style>
.side-bar {
  width: 120px;
  height: 100vh;
  background-color: #f8f9fa;
  padding: 20px;
  position: fixed;
  z-index: 2 !important;
  top: 0;
  right: 0;
}

.profile {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-direction: column;
}

.profile-image {
  object-fit: cover;
}

.profile-name {
  margin-top: 15px;
}

.menu-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 0;
  cursor: pointer;
}

.menu-list:hover {
  background-color: #EEE;
  border-radius: 10px;
  cursor: pointer;
}

.menu-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.menu-title {
  font-weight: bold;
  align-items: center;
  font-size: 14px;
}

.logout {
  position: absolute;
  bottom: 50px;
  width: 80px;
  align-items: center;
  cursor: pointer;
}

.logout:hover {
  font-weight: bold;
}

.tab-active {
  font-weight: bold;
  color: black;
}

.tab-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  width: 50%;
  left: 50%;
  transform: translateX(-50%);
  height: 14px;
  background-color: black;
}

.alert-card {
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-bottom: 10px;
  box-shadow: none;
  border-bottom: 1px solid #E0E0E0;
}

.alert-card-active {
  border-bottom: 3px solid #6C97FD;
}

.custom-alert-position {
  position: fixed !important;
  top: 0px !important;
  right: 100px !important;
  transform: none !important;
  width: 400px;
  height: 100vh !important;
  overflow-y: auto;
  z-index: 202;
}

.alert-badge {
  position: absolute;
  right: 0;
  bottom: 0;
  background-color: #6C97FD;
  color: #fff;
  width: 35px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
  cursor: pointer;
}

.alert-badge:hover {
  background-color: #5a82d8;
  /* Hover 시 약간 어두운 색으로 변경 */
}

@keyframes slideInPartial {
  0% {
    transform: translateX(100%);
    clip-path: inset(0 0 0 100%);
    opacity: 0;
  }

  50% {
    transform: translateX(0);
    clip-path: inset(0 0 0 20%);
    opacity: 1;
  }

  100% {
    transform: translateX(0);
    clip-path: inset(0 0 0 0);
  }
}

.custom-alert-position {
  position: fixed !important;
  top: 0px !important;
  right: 95px !important;
  width: 400px;
  height: 100vh;
  overflow-y: auto;
  z-index: 202;
  animation: slideInPartial 0.5s ease-out forwards;
  margin-top: 0px !important;
  padding: 1px;
  /* 슬라이드 애니메이션 */
}
.v-overlay__scrim {
  background-color: transparent !important;
}


</style>