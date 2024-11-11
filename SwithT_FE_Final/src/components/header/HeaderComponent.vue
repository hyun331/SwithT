<template>
    <v-app-bar :elevation="0" scroll-behavior="hide" style="z-index: 1;">
        <router-link to="/">
            <img src="@/assets/logo.png" alt="logo" style="width: 150px; margin-left: 20px;">
        </router-link>
        <!-- <v-btn @click="$router.push('/')">SwithT</v-btn> -->
        <!-- <v-btn @click="LecturePageBtn()">과외</v-btn>
        <v-btn @click="LessonPageBtn()">강의</v-btn> -->
        <!-- <v-btn @click="getPrice()">gdgdgdgdgd</v-btn> -->
        <!-- <v-btn @click="openAlertListModal()" style="margin-left: 60%;">
            <v-icon>mdi-bell</v-icon>
            <span>{{ count }}</span>
        </v-btn> -->
        <v-btn style="margin: 0 20px 0 auto; font-size: 16px;" v-if="!isLogin" @click="login()">로그인</v-btn>
        <!-- <v-btn style="margin-left: auto; " v-if="isLogin" @click="logout()">로그아웃</v-btn> -->
        <v-dialog v-model="alertDialogSSE">
            <v-card-title>New Alert</v-card-title>
            <v-card-text>
                <div v-if="count === 0">새로운 알림이 없습니다.</div>
                <div v-else>
                    <ul>
                        <li v-for="event in eventList" :key="event.id" @click="goToAlertDetail(event)">
                            {{ event.title }}
                        </li>
                    </ul>
                </div>
            </v-card-text>
        </v-dialog>
    </v-app-bar>
</template>

<script>
import axios from "axios"
// import { EventSourcePolyfill } from 'event-source-polyfill'
import { jwtDecode } from 'jwt-decode'

export default {
    data() {
        return {
            isLogin: false,
            count: 0,
            eventSource: null,
            member: "",
            title: "",
            price: "",
            applyId: "",
            eventList: [],
            alertDialogSSE: false,
            eventId: "",
        };
    },
    // async created() {
    //     await this.checkLoginStatus() // checkLoginStatus 메서드 호출
    //     const token = localStorage.getItem('token')
    //     if (token) {
    //         // EventSource 생성 확인
    //         const token = localStorage.getItem('token')
    //         let sse = new EventSourcePolyfill(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/subscribe`, {
    //         headers: { 'Authorization': `Bearer ${token}` }
    //         })
    //         console.log("EventSource created"); // 디버깅용 로그
    //         sse.addEventListener('connect', (event) => {
    //             console.log(event)
    //         });
    //         sse.addEventListener('notification', (event) => { // 'notification' 이벤트
    //             console.log('Notification event received:', event) // 이벤트 수신 확인
    //             if (event.data && event.data !== '""') {
    //                 // const data = JSON.parse(event.data)
    //                 // const messageType = data.messageType
    //                 // const contents = data.contents
    //                 // const title = data.title
    //                 // console.log('Message Type:', messageType)
    //                 // console.log('Contents:', contents)
    //                 // console.log('Title:', title)
    //                 this.count++
    //                 this.eventList.push(JSON.parse(event.data))
    //                 localStorage.setItem('eventList', JSON.stringify(this.eventList))
    //             }
    //         })
    //     } else {
    //         console.log("Token not found") // 토큰이 없을 경우
    //     }
    // },
    async created() {
        await this.checkLoginStatus()
        // const storedEvents = localStorage.getItem('eventList')
        // this.member = JSON.parse(localStorage.getItem('memberInfo'))
        // if (storedEvents) {
        //     this.eventList = JSON.parse(storedEvents) // localStorage에서 불러오기
        //     this.count = this.eventList.length // 이벤트 리스트의 길이에 따라 카운트 설정
        // }
        // // const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/payment-service/hello`)

        // // console.log(response) 헬스체크 메서드 주석처리.
        
        // const token = localStorage.getItem('token')
        // if (token) {
        //     let sse = new EventSourcePolyfill(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/subscribe`, {
        //         headers: { 'Authorization': `Bearer ${token}` }
        //     })
        //     console.log("EventSource created") // 디버깅용 로그
        //     sse.addEventListener('connect', (event) => {
        //         console.log(event)
        //     });
        //     sse.addEventListener('notification', (event) => { // 'notification' 이벤트
        //         console.log('Notification event received:', event) // 이벤트 수신 확인
        //         if (event.data && event.data !== '""') {
        //             const newEvent = JSON.parse(event.data)

        //             // 중복 확인: 동일한 id의 알림이 있는지 확인
        //             const isDuplicate = this.eventList.some(e => e.id === newEvent.id)

        //             if (!isDuplicate) {
        //                 this.eventList.push(newEvent) // 중복이 아니면 리스트에 추가
        //                 this.count = this.eventList.length // 카운트 증가
        //                 localStorage.setItem('eventList', JSON.stringify(this.eventList)) // localStorage에 저장
        //             }
        //         }
        //     });
        // } else {
        //     console.log("Token not found"); // 토큰이 없을 경우
        // }
    },

    methods: {
        openAlertListModal() {
            this.alertDialogSSE = true
        },
        closeAlertListModal() {
            this.alertDialogSSE = false
        },
        async checkLoginStatus() {
            const token = localStorage.getItem('token')
            if (token) {
            
            this.isLogin = true;
            const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member-service/infoGet`)
            
            this.member = response.data.result
            
            // const memberInfo = {
            //     name: this.member.name,
            //     profileImage: this.member.profileImage
            // }
            // localStorage.setItem('memberInfo',JSON.stringify(memberInfo))
            localStorage.setItem('profileImage', this.member.profileImage)
            localStorage.setItem('name', this.member.name)
        
            }

        },
        login() {
            this.$router.push("/member/main") //김민성 수정
        },
        logout() {
            localStorage.clear();
            this.isLogin = false
            window.redirect
            this.$router.push("/home")
        },
        LecturePageBtn() {
            this.$router.push("/lecture")
        },
        LessonPageBtn() {
            this.$router.push("/lesson")
        },
        async goToAlertDetail(event){
            const messageType = event.messageType
            this.eventId = event.id
            // console.log(this.eventId)
            if(messageType === '결제요청'){
                try{
                    // 결제에 필요한 데이터 추출
                    const lectureApplyId = event.contents
                    await this.handlePaymentRequest(lectureApplyId)

                    // 결제 시작
                    this.initiatePayment() // initiatePayment()로 결제 요청 시작
                } catch(error){
                    console.error('결제 처리 중 오류 발생:', error)
                }
            }
        },
        async handlePaymentRequest(applyId) {
            try {
                const lectureApplyId = Number(applyId)
                this.applyId = lectureApplyId 

                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/group/${lectureApplyId }`)
                this.title = response.data.result.lectureName
                this.price = response.data.result.price
                console.log('결제 요청 처리 결과:', response.data)
            } catch (error) {
                console.error('결제 요청 처리 중 오류 발생:', error)
            }
        },
        initiatePayment() {
            
            const IMP = window.IMP  // 아임포트 전역 객체
            IMP.init("imp00575764") // 아임포트 상점 고유코드로 초기화

            const paymentData = {
                pg: "html5_inicis", // 결제 PG사
                pay_method: "card", // 결제 방법
                merchant_uid: `merchant_${new Date().getTime()}`, // 주문번호
                name: this.title, // 결제 내역
                amount: this.price, // 결제 금액
                buyer_email: this.member.email,
                buyer_name: this.member.name,
                buyer_tel: this.member.phoneNumber,
            }

            IMP.request_pay(paymentData, this.processPayment) // 결제 요청
        },
        async processPayment(rsp){
            const token = localStorage.getItem('token')
            const decodedToken = jwtDecode(token)
            if(token){
                this.memberId = decodedToken.sub
            }
            if(rsp.success) {
                const data = {
                    impUid: rsp.imp_uid, // 아임포트 거래 고유번호
                    title: this.title,
                    price: this.price,
                    memberId: this.memberId,
                    id: this.applyId
                }

                const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/payment-service/complete`, data)
                alert(response.data.responseMessage)

                const eventList = JSON.parse(localStorage.getItem('eventList')) || []
                const updatedEventList = eventList.filter(event => event.id !== this.eventId) // 결제된 알림 제거
                localStorage.setItem('eventList', JSON.stringify(updatedEventList))

            
                this.count = updatedEventList.length
                this.closeAlertListModal()
            }
        },
    }
};
</script>
