<template>
    <LectureDetailInfoComponent :lectureId=this.lectureId />

    <v-container>
        <v-row>
            <v-col cols="8">
                <v-tabs v-model="activeTab" style="margin-bottom: 30px;">
                    <v-tab value="lecture-info" style="font-size: 18px;">강의 정보</v-tab>
                    <v-tab value="tutor-info" style="font-size: 18px;">강사 정보</v-tab>
                    <v-tab value="tutor-review" style="font-size: 18px;">리뷰</v-tab>
                </v-tabs>
                <div class="contents-section">
                    <v-tabs-window v-model="activeTab">
                        <v-tabs-window-item value="lecture-info">

                            <div class="contents-title">강의 소개</div>

                            <div style="font-size: 18px; margin: 0px 10px 50px; color: #333; text-align: left;">
                                {{ lectureInfo?.contents }}
                            </div>
                            <hr />
                            <div class="contents-title">강의 시간</div>
                            <div v-if="lectureInfo?.lectureType === 'LECTURE'" class="date-info">
                                <span style="font-weight: 800; margin-right: 10px; ">📅 진행기간 </span> {{
                                    lectureGroups[0]?.startDate }} ~ {{ lectureGroups[0]?.endDate }}
                            </div>
                            <v-row v-for="(group, index) in lectureGroups" :key="index">
                                <v-col>
                                    <div class="pa-3 groups-info"
                                        :style="{ backgroundColor: group.isAvailable === 'N' || group.remaining === 0 ? '#f0efef' : '' }">
                                        <v-row style="padding: 20px 0">
                                            <v-col cols="3" class="align-content-center">
                                                <v-row v-if="group.isAvailable === 'N' || group.remaining === 0"
                                                    class="align-center justify-center">
                                                    <div class="soldout">마감</div>
                                                </v-row>
                                                <v-row class="align-center justify-center">
                                                    <div style="font-weight: bold; font-size: 17px;">
                                                        {{ `강의 그룹 ${index + 1}` }}
                                                    </div>
                                                </v-row>
                                            </v-col>
                                            <v-col cols="9" style="text-align: left; font-size: 15px">
                                                <v-row>
                                                    <v-col cols="4" class="align-center justify-start"
                                                        style="padding: 10px">
                                                        <strong>강의료</strong>
                                                    </v-col>
                                                    <v-col class="d-flex align-center justify-start"
                                                        style="padding: 10px">
                                                        <span v-if="lectureInfo?.lectureType === 'LESSON'"
                                                            style="margin-right: 7px;">1개월 </span>
                                                        {{ formatPrice(group.price) }}원
                                                    </v-col>
                                                </v-row>
                                                <v-row>
                                                    <v-col cols="4" class="align-center justify-start"
                                                        style="padding: 10px">
                                                        <strong>모집인원</strong>
                                                    </v-col>
                                                    <v-col class="d-flex align-center justify-start"
                                                        style="padding: 10px">
                                                        {{ group.participants }}명
                                                    </v-col>
                                                </v-row>

                                                <v-row>
                                                    <v-col cols="4" class="d-flex align-center justify-start"
                                                        style="padding: 10px">
                                                        <strong>강의시간</strong>
                                                    </v-col>
                                                    <v-col class="d-flex align-center justify-start"
                                                        style="padding: 10px">
                                                        <div>
                                                            <div v-for="time in group.groupTimes"
                                                                :key="time.groupTimeId">
                                                                <span style="font-weight: bold; color: #6C97FD">{{
                                                                    time.day }}요일</span>
                                                                {{ formatTime(time.startTime) }} ~ {{
                                                                    formatTime(time.endTime) }}
                                                            </div>
                                                        </div>
                                                    </v-col>
                                                </v-row>
                                            </v-col>
                                        </v-row>
                                    </div>
                                </v-col>
                            </v-row>

                        </v-tabs-window-item>

                        <v-tabs-window-item value="tutor-info">
                            <!-- 강사 정보 내용 -->
                            <v-col>
                                <v-row class="d-flex align-center justify-center">
                                    <div class="tutor-profile">
                                        <img :src="tutorInfo?.profileImage" alt="tutor profile" />
                                    </div>
                                </v-row>

                                <v-row class="d-flex align-center justify-center">
                                    <div style="font-style: italic; font-size: 24px; margin: 20px 0"> 안녕하세요.<br />
                                        <span
                                            style="font-size: 27px; font-style: normal; font-weight: 700; color:#6C97FD;">
                                            {{ tutorInfo.name }}
                                        </span>
                                        튜터입니다.
                                    </div>
                                </v-row>

                                <v-row class="d-flex align-center justify-center">
                                    <div class="tutor-info" style="padding: 20px">
                                        <v-row>
                                            <v-col cols="3" class="d-flex align-center justify-center">
                                                <span style="font-weight: bold; margin-right: 15px">평점</span>
                                            </v-col>
                                            <v-col>
                                                <span class="mdi mdi-star" style="font-size: 18px;"> </span>
                                                {{ tutorInfo.avgScore }} / 5.0
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col cols="3" class="d-flex align-center justify-center">
                                                <span style="font-weight: bold; margin-right: 15px">최종 학력</span>
                                            </v-col>
                                            <v-col>
                                                {{ tutorInfo.education }}
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col cols="3" class="d-flex align-center justify-center">
                                                <span style="font-weight: bold; margin-right: 15px">성별</span>
                                            </v-col>
                                            <v-col>
                                                {{ convertGender(tutorInfo.gender) }}
                                            </v-col>
                                        </v-row>
                                        <v-row>
                                            <v-col cols="3" class="d-flex align-center justify-center">
                                                <span style="font-weight: bold; margin-right: 15px">연락처</span>
                                            </v-col>
                                            <v-col>
                                                {{ tutorInfo.email }}
                                            </v-col>
                                        </v-row>
                                    </div>
                                </v-row>

                                <v-row style="margin: 20px 50px">
                                    <div style="font-size: 18px; text-align: left; padding: 30px">
                                        {{ tutorInfo.introduce }}</div>
                                </v-row>
                            </v-col>
                        </v-tabs-window-item>
                        <v-tabs-window-item value="tutor-review">
                            <ReviewListComponent :tutorId="tutorId" />
                            <!-- 리뷰 내용 -->
                        </v-tabs-window-item>
                    </v-tabs-window>
                </div>
            </v-col>

            <v-col cols="4">
                <aside class="float-info">
                    <table border="3" class="time-table">
                        <thead>
                            <tr>
                                <th>시간</th>
                                <th>월</th>
                                <th>화</th>
                                <th>수</th>
                                <th>목</th>
                                <th>금</th>
                                <th>토</th>
                                <th>일</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(hour, index) in hours" :key="index">
                                <td>{{ hour }}</td>
                                <td v-for="day in days" :key="day"
                                    :style="{ backgroundColor: schedule[day] && schedule[day][index] ? schedule[day][index].color : '' }">
                                    <div v-if="schedule[day] && schedule[day][index]"
                                        style="color: #fff; font-weight: bold;">
                                        {{ schedule[day][index].index }}
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <v-btn v-if="isLogin === true && userRole === 'TUTEE'" @click="openApplyModal"
                        class="btn-transition"
                        style="width: 90%; margin: 20px 0 10px; background-color: #0d6efd; color: #fff; font-weight: 700;">신청하기</v-btn>
                </aside>
            </v-col>
        </v-row>
        <v-snackbar v-model="snackbar.show" :color="snackbar.color">
            {{ snackbar.message }}
        </v-snackbar>
    </v-container>

    <v-dialog v-model="isApplyModalOpen" max-width="600px">
        <v-card style="padding: 40px 20px 50px; border-radius: 10px;">
            <div style="font-size: 24px; font-weight: 700; margin: auto;">강의 신청</div>
            <v-card-text>
                <div v-for="group in lectureGroups" :key="group.lectureGroupId" @click="checkAndSelectGroup(group)"
                    :class="[
                        'custom-option', 'btn-transition',
                        {
                            'selected': selectedLectureGroup && selectedLectureGroup.lectureGroupId === group.lectureGroupId,
                            'disabled-group': group.isAvailable === 'N' || group.remaining === 0
                        }]" :style="{
                            backgroundColor: group.isAvailable === 'N' || group.remaining === 0 ? '#f0efef' : (selectedLectureGroup && selectedLectureGroup.lectureGroupId === group.lectureGroupId ? '#e7f0ff' : ''),
                            borderColor: selectedLectureGroup && selectedLectureGroup.lectureGroupId === group.lectureGroupId ? '#007bff' : '#ccc'
                        }" style="margin-top: 10px;">

                    <div>
                        <span v-if="group.isAvailable === 'N' || group.remaining === 0" class="soldout">
                            마감
                        </span>
                        <span style="color: #000; font-weight: 700; margin: 10px">강의 그룹 {{ group.groupIndex }}</span>
                    </div>
                </div>

                <!-- 강의 그룹 선택 시 추가 정보 입력 폼 -->
                <transition name="fade">
                    <div v-if="selectedLectureGroup" style="margin-top: 20px;">
                        <div v-if="lectureInfo?.lectureType === 'LESSON'" style="padding: 0 10px;">
                            <hr style="margin: 30px 0" />
                            <div style="font-size: 18px; font-weight: 700; color: #5d8dfc; margin: 10px 0;">추가 정보 입력
                            </div>
                            <v-row>
                                <v-col>
                                    <label for="startDate" class="form-label">시작일</label>
                                    <input v-model="startDate" id="startDate" class="form-control" type="date" />
                                </v-col>
                                <!-- <v-col>
                                    <label for="endDate" class="form-label">종료일</label>
                                    <input v-model="endDate" class="form-control" type="date" />
                                </v-col> -->
                            </v-row>
                            <v-row>
                                <v-col>
                                    <label for="location" class="form-label">강의 위치</label><br />
                                    <v-btn style="border: 1px solid #ccc;" variant="outlined" @click="updateAddress()">
                                        주소 검색</v-btn>
                                    <span style="margin: 5px 10px; font-weight: 700;">{{ this.location }}</span>
                                    <input v-model="detailAddress" id="detailAddress" class="form-control"
                                        placeholder="상세 주소를 입력해주세요" type="text" style="margin-top: 10px;" />
                                </v-col>
                            </v-row>
                        </div>
                    </div>

                </transition>
            </v-card-text>
            <v-card-actions style="justify-content: flex-end;">
                <transition name="fade">
                    <v-btn v-if="selectedLectureGroup" class="btn-transition"
                        style="background-color: #0d6efd; color: #fff; font-weight: 700; margin-right: 10px;"
                        @click="submitApplication();">신청하기</v-btn>
                </transition>
                <v-btn @click="closeApplyModal" class="btn-transition">취소</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <!-- 대기열 모달 -->
    <v-dialog v-model="waitingDialog" persistent max-width="600px">
        <v-card style="padding: 40px 20px 50px; border-radius: 10px; text-align: center;">
            <div>
                <span class="material-icons btn-transition" @click="confirmExitQueue"
                    style="cursor: pointer; float: right; margin-right: 10px;">
                    close
                </span>
            </div>
            <div class="spinner-grow text-primary" role="status" style="margin: 20px auto;">
                <span class="visually-hidden">Loading...</span>
            </div>
            <v-card-text style="font-size: 20px;">

                <div v-if="queueStatusMessage">{{ queueStatusMessage }}</div> <!-- 대기열 상태 메시지 표시 -->

                <div v-if="this.rank !== -1 && this.rank !== null && this.rank !== 0">
                    현재 대기 순번은 <strong>{{ this.rank }}</strong>번 입니다.
                </div>

                <div v-if="this.rank == 0">잠시 후 결제 페이지로 넘어갑니다.</div>

                <div v-else-if="this.rank === -1 || this.rank === null">
                    대기열 정보를 불러오는 중입니다.
                </div>

                <div class="caution">창을 벗어날 시 대기열에서 순번이 뒤로 밀리게 됩니다.</div>
            </v-card-text>
        </v-card>
    </v-dialog>

    <!-- YesOrNoModal -->
    <YesOrNoModal v-model:dialog="showPaymentModal" :title="paymentModalTitle" :contents="paymentModalContents"
        yesBtnName="결제" @confirmed="proceedPayment" />

    <AlertModal v-model="alertModal" @update:dialog="alertModal = $event" icon=mdi-alert-circle-outline
        :title=this.alertModalTtile :contents=this.alertModalContents />

</template>

<script>
/* global kakao */

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css'

import axios from 'axios';
import LectureDetailInfoComponent from '@/components/LectureDetailInfoComponent.vue';
import ReviewListComponent from '@/components/ReviewListComponent.vue';
import YesOrNoModal from "@/components/YesOrNoModal.vue";
import { jwtDecode } from 'jwt-decode'
import AlertModal from '@/components/AlertModal.vue';

import { useToast } from "vue-toastification";

export default {
    components: {
        LectureDetailInfoComponent,
        ReviewListComponent,
        YesOrNoModal,
        AlertModal
    },
    data() {
        return {
            isLogin: false,
            userRole: null,
            activeTab: 'lecture-info',
            isApplyModalOpen: false, // 모달 열림 상태
            availableLectureGroups: [],
            selectedLectureGroup: null,
            startDate: null,
            endDate: null,
            location: null,
            detailAddress: null,
            days: ['월', '화', '수', '목', '금', '토', '일'],
            hours: [
                '06:00', '07:00', '08:00', '09:00', '10:00', '11:00',
                '12:00', '13:00', '14:00', '15:00', '16:00', '17:00',
                '18:00', '19:00', '20:00', '21:00', '22:00', '23:00', '24:00'
            ],
            schedule: {},
            lectureInfo: null,
            lectureGroups: [], // 강의 그룹 데이터
            lectureLocation: '', // 강의 위치
            tutorInfo: null,
            tutorId: null,
            lectureId: this.$route.params.id,
            memberId: null,
            memberName: null,
            snackbar: {
                show: false,
                message: "",
                color: ""
            },
            waitingDialog: false,
            isExitingQueue: false,  // 사용자가 자발적으로 대기열을 벗어나려고 할 때 이를 추적하는 불리언 변수
            rank: null,
            queueStatusMessage: '', // 대기열 상태 메시지
            showPaymentModal: false, // 모달 표시 여부
            paymentModalTitle: '',
            paymentModalContents: '',
            queueRank: -1,
            getOrderData: null,
            lectureGroupId: null,

            alertModal: false,
            alertModalTtile: '',
            alertModalContents: '',
        };
    },
    created() {
        this.fetchLectureGroupInfo();
        const token = localStorage.getItem('token');
        if (token) {
            this.isLogin = true;
            this.userRole = localStorage.getItem('role');
        }
    },
    async mounted() {
        await this.fetchLectureDetail(); // 강의 세부 정보를 먼저 가져옵니다.
        await this.fetchTutorInfo(); // 이후 강사 정보를 가져옵니다.
        this.loadDaumPostcodeScript();
        this.loadKakaoMapScript();
    },

    methods: {
        loadDaumPostcodeScript() {
            const script = document.createElement('script');
            script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
            script.onload = () => {
                this.isDaumScriptLoaded = true;
            };
            document.head.appendChild(script);
        },
        loadKakaoMapScript() {
            const script = document.createElement('script');
            script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=03a055c21377bee26ab1559dedf4af6f&libraries=services&autoload=false`;
            script.onload = () => {
                window.kakao.maps.load(() => {
                    this.isKakaoScriptLoaded = true;
                });
            };
            document.head.appendChild(script);
        },
        updateAddress() {
            if (window.daum && window.daum.Postcode) {
                // eslint-disable-next-line no-undef
                new daum.Postcode({
                    oncomplete: (data) => {
                        this.location = data?.roadAddress;

                        // 주소 검색한 거 기반으로 위도 경도
                        // 좌표 검색을 위해 Kakao 지도 Geocoder 사용
                        const geocoder = new kakao.maps.services.Geocoder();
                        geocoder.addressSearch(this.roadAddress, (result, status) => {
                            if (status === kakao.maps.services.Status.OK) {
                                console.log('위도 : ' + result[0].y);
                                console.log('경도 : ' + result[0].x);

                                // 지도에 마커를 추가하는 로직
                                this.initMap(result[0].y, result[0].x);
                            }
                        });
                    }
                }).open();
            } else {
                console.error("Daum Postcode 스크립트가 로드되지 않았습니다.");
            }
        },
        async getIpAddress() {
            try {
                const res = await axios.get('https://api64.ipify.org?format=json'); // IP 주소 조회 API
                return res.data.ip;
            } catch (error) {
                console.error("IP 주소를 가져오는 데 실패했습니다:", error);
                return '';
            }
        },
        async fetchLectureDetail() {
            const lectureId = this.$route.params.id; // URL에서 강의 ID 가져오기
            const userId = localStorage.getItem('id'); // 로그인된 사용자 ID 가져오기
            const ipAddress = await this.getIpAddress(); // IP 주소 가져오기
            const userAgent = navigator.userAgent; // 사용자 에이전트 가져오기

            try {
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture-detail/${lectureId}`, {
                    headers: {
                        'X-Forwarded-For': ipAddress, // IP 주소 헤더로 전달
                        'User-Agent': userAgent, // User-Agent 헤더로 전달
                    },
                    params: {
                        userId: userId || '', // 로그인한 사용자 ID를 쿼리 파라미터로 전달
                    }
                });
                this.lectureInfo = response.data.result; // 가져온 강의 정보를 저장
                this.tutorId = this.lectureInfo.memberId;
            } catch (error) {
                console.error('강의 정보를 가져오는 데 실패했습니다:', error);
            }
        },
        async fetchLectureGroupInfo() {
            try {
                const id = this.$route.params.id; // URL에서 강의 ID 가져오기
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture-group-info/${id}`);
                const data = response.data;
                console.log(data);

                if (data.result && Array.isArray(data.result) && data.result.length > 0) {
                    // 강의 그룹 데이터를 lectureGroups에 저장
                    this.lectureGroups = data.result.map((group, index) => ({
                        lectureGroupId: group.lectureGroupId,
                        startDate: group.startDate,
                        endDate: group.endDate,
                        price: group.price || 0,
                        participants: group.participants || 1,
                        isAvailable: group.isAvailable,
                        remaining: group.remaining,
                        groupIndex: index + 1,
                        groupTimes: group.groupTimes.map(time => ({
                            day: this.convertDayToKorean(time.lectureDay), // 요일을 한글로 변환
                            startTime: time.startTime,
                            endTime: time.endTime,
                            groupIndex: index + 1 // 인덱스를 강의 그룹 순서대로 부여
                        }))
                    }));


                    // 시간표에 맞는 스케줄 데이터 포맷
                    const allGroupTimes = data.result.flatMap((group, index) =>
                        group.groupTimes.map(time => ({ ...time, groupIndex: index + 1 })) // 그룹 인덱스를 추가로 부여
                    );
                    this.schedule = this.formatSchedule(allGroupTimes);
                }
            } catch (error) {
                console.error('강의 그룹 정보를 가져오는 데 실패했습니다:', error);
            }
        },
        formatTime(time) {
            // 시간에서 앞의 5자리만 잘라서 반환 (HH:MM)
            return time.substring(0, 5);
        },

        deleteLectureGroup(index) {
            // 강의 그룹 삭제 기능 (단순히 배열에서 삭제)
            this.lectureGroups.splice(index, 1);
        },
        convertDayToKorean(day) {
            const dayMap = {
                MONDAY: '월',
                TUESDAY: '화',
                WEDNESDAY: '수',
                THURSDAY: '목',
                FRIDAY: '금',
                SATURDAY: '토',
                SUNDAY: '일'
            };
            return dayMap[day] || '요일 미상';
        },
        formatSchedule(times) {
            const schedule = {};
            const dayMap = {
                MONDAY: '월',
                TUESDAY: '화',
                WEDNESDAY: '수',
                THURSDAY: '목',
                FRIDAY: '금',
                SATURDAY: '토',
                SUNDAY: '일'
            };

            const groupColors = {}; // 그룹 인덱스별 색상을 저장

            times.forEach((time) => {
                const day = dayMap[time.lectureDay]; // 요일 변환
                const startHourIndex = this.hours.indexOf(time.startTime.split(':')[0] + ':00'); // 시작 시간의 index
                const endHourIndex = this.hours.indexOf(time.endTime.split(':')[0] + ':00'); // 종료 시간의 index

                // group.isAvailable 값에 따라 색상을 결정
                const group = this.lectureGroups[time.groupIndex - 1]; // 그룹 정보 가져오기 (인덱스 조정)
                let color;

                if (group.isAvailable === 'N' || group.remaining === 0) {
                    color = '#f0efef'; // 사용 불가한 경우
                } else {
                    // 그룹 인덱스로 색을 할당하고, 해당 색상이 없으면 새롭게 생성
                    if (!groupColors[time.groupIndex]) {
                        groupColors[time.groupIndex] = this.getRandomColor(); // 그룹 인덱스별로 고유 색상 할당
                    }
                    color = groupColors[time.groupIndex]; // 해당 그룹의 색상 가져오기
                }

                if (!schedule[day]) {
                    schedule[day] = Array(this.hours.length).fill(null); // 해당 요일에 스케줄 배열 초기화
                }

                // 시작 시간부터 종료 시간까지 색상 및 그룹 인덱스 설정
                for (let hour = startHourIndex; hour < endHourIndex; hour++) {
                    schedule[day][hour] = {
                        name: '강의',
                        color: color, // 조건에 따른 색상 적용
                        index: time.groupIndex // 그룹 인덱스
                    };
                }
            });
            return schedule;
        },
        getRandomColor() {
            const colors = ['#d0e2ff', '#9ec5fe', '#6ea8fe', '#3d8bfd', '#0d6efd', '#2f6fd4', '#bad2f8', '#abc3ea', '#7fa3dd', '#5982c4', '#426caf'];
            const randomIndex = Math.floor(Math.random() * colors.length);
            return colors[randomIndex];
        },
        async fetchTutorInfo() {
            try {
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member-service/public-infoGet/${this.tutorId}`);
                this.tutorInfo = response.data.result; // 강사 정보를 저장
                console.log(this.tutorInfo);
            } catch (error) {
                console.error('강사 정보를 가져오는 데 실패했습니다:', error);
            }
        },
        convertGender(gender) {
            return gender === 'MAN' ? '남성' : '여성';
        },
        formatPrice(value) {
            if (!value) return '0';
            return new Intl.NumberFormat('ko-KR').format(value);
        },
        openApplyModal() {
            this.isApplyModalOpen = true;
        },
        closeApplyModal() {
            this.isApplyModalOpen = false;
            this.selectedLectureGroup = null;
            this.startDate = null;
            this.endDate = null;
            this.lectureLocation = '';
            this.location = null;
            this.detailAddress = null;
        },
        closeWaitingDialog() {
            this.waitingDialog = false;
            this.sendDeleteQueue();
        },
        confirmExitQueue() {
            if (confirm("대기열을 벗어나시겠습니까?")) {
                this.isExitingQueue = true;  // 플래그 설정
                this.closeWaitingDialog();
                this.closeApplyModal();
                this.snackbar = { show: true, message: "강의 신청을 취소하였습니다.", color: "success" };
            }
        },
        sendDeleteQueue() {
            axios.post(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture-delete-queue`, null, {
                params: this.getOrderData
            });
            this.rank = null;
        },
        selectLectureGroup(group) {
            this.selectedLectureGroup = group;
            console.log(this.selectedLectureGroup.lectureGroupId) // 잘 들어옴
            this.lectureGroupId = group.lectureGroupId;
            this.price = group.price;
            console.log("선택한 강의 그룹 남은 인원:" + this.selectedLectureGroup.remaining);
            console.log("선택한 강의 그룹 가격:" + this.price);

        },

        async submitApplication() {
            const toast = useToast(); // Toast 인스턴스 생성

            this.token = localStorage.getItem('token');
            this.memberId = localStorage.getItem('id');
            this.memberName = localStorage.getItem('name');

            console.log(this.lectureInfo.lectureType);

            if (this.selectedLectureGroup) {
                this.lectureGroupId = this.selectedLectureGroup.lectureGroupId; // 선택된 강의 그룹 ID를 다시 확인 및 설정
            }

            // LECTURE 신청 로직
            if (this.lectureInfo.lectureType === "LECTURE") {

                const requestData = {
                    lectureGroupId: this.selectedLectureGroup.lectureGroupId, // 선택된 강의 그룹 ID
                    memberId: this.memberId, // 요청할 때 필요한 memberId
                    memberName: this.memberName, // 요청할 때 필요한 memberName
                };

                try {
                    // 대기열에 넣기 (lecture-add-queue)
                    await axios.post(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture-add-queue`, null, {
                        params: requestData // 쿼리 파라미터로 전달
                    });

                    console.log("this.rank:" + this.rank);
                    this.waitingDialog = true;  // 대기열 모달 열기

                    this.getOrderData = {
                        lectureGroupId: this.selectedLectureGroup.lectureGroupId,
                        memberId: this.memberId,
                    };


                    try {
                        while (!this.isExitingQueue && this.rank !== 0 && this.rank !== -1) {
                            const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture-get-order`, {
                                params: this.getOrderData
                            });

                            this.rank = response.data.result;
                            console.log("현재 대기열 순위: " + this.rank);

                            if (this.rank > 500 && this.rank < 1000) {
                                // 3초 대기
                                await new Promise(resolve => setTimeout(resolve, 3000));
                            } else if (this.rank > 1000 && this.rank < 3000) {
                                // 5초 대기
                                await new Promise(resolve => setTimeout(resolve, 5000));
                            } else if (this.rank > 3000) {
                                // 9초 대기
                                await new Promise(resolve => setTimeout(resolve, 9000));
                            }
                        }

                        if (!this.isExitingQueue) {
                            if (this.price === 0) {
                                this.processFreeLesson();
                                this.closeWaitingDialog();
                            } else {
                                this.confirmPayment();
                                this.closeWaitingDialog();
                            }
                        }

                    } catch (error) {
                        if (!this.isExitingQueue) {  // 의도된 종료가 아닐 때만 오류 처리
                            this.snackbar = { show: true, message: "강의 신청 중 오류가 발생했습니다", color: "error" };
                        }

                        this.closeApplyModal();
                        this.waitingDialog = false;
                    }

                } catch (error) {
                    if (!this.isExitingQueue) {  // 의도된 종료가 아닐 때만 오류 처리
                        this.snackbar = { show: true, message: "강의 신청 중 오류가 발생했습니다", color: "error" };
                    }
                    if (error && error.response.status === '500') {
                        // 예외 발생: 알림 시간이 현재 시간으로부터 10분 미만일 경우
                        toast.error("이미 신청한 강의입니다");
                        return;
                    }
                    this.closeApplyModal();
                    this.waitingDialog = false;
                }


            }

            // LESSON 신청 로직
            else if (this.lectureInfo.lectureType === "LESSON") {

                // 필수 입력 값 체크
                if (!this.startDate || !this.location) {
                    this.snackbar = { show: true, message: "시작일과 수업 위치를 입력해 주세요.", color: "error" };
                    return;
                }

                // endDate를 startDate의 한 달 뒤로 설정
                const start = new Date(this.startDate);
                const end = new Date(start);
                end.setMonth(start.getMonth() + 1); // 한 달 뒤로 설정
                this.endDate = end.toISOString().split('T')[0]; // YYYY-MM-DD 형식으로 변환

                console.log(this.location + this.detailAddress)
                const requestData = {
                    lectureGroupId: this.selectedLectureGroup.lectureGroupId, // 선택된 강의 그룹 ID
                    startDate: this.startDate, // 시작일
                    endDate: this.endDate, // 종료일
                    location: this.location, // 강의 위치
                    detailAddress: this.detailAddress, // 강의 위치
                };

                try {
                    await axios.post(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/single-lecture-apply`, requestData, {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    });
                    this.snackbar = { show: true, message: "강의 신청이 완료되었습니다.", color: "success" };
                    this.closeApplyModal();
                    // location.reload(); // 페이지 새로 고침
                } catch (error) {
                    console.error("강의 신청 중 오류가 발생했습니다:", error);
                    this.snackbar = { show: true, message: "강의 신청에 실패했습니다.", color: "error" };
                }
            }
        },
        checkAndSelectGroup(group) {
            console.log('강의 그룹 상태:', group.isAvailable, '잔여석:', group.remaining);

            if (group.isAvailable !== 'N' && group.remaining > 0) {
                this.selectLectureGroup(group);
            } else {
                console.log('선택할 수 없는 강의 그룹입니다.');
            }
        },

        confirmPayment() {
            this.paymentModalTitle = `<${this.lectureInfo.title}> 결제하시겠습니까?`;
            this.paymentModalContents = "결제를 진행하려면 결제 버튼을 클릭하세요.";
            this.showPaymentModal = true; // 결제 확인 모달을 엶
        },
        async proceedPayment() {
            this.showPaymentModal = false; // 모달 닫기
            try {
                this.initiatePayment(); // 결제 진행
            } catch (error) {
                console.error('결제 요청 중 오류 발생:', error);
            }
        },
        initiatePayment() {

            const IMP = window.IMP;  // 아임포트 전역 객체
            IMP.init("imp00575764"); // 아임포트 상점 고유코드로 초기화

            const paymentData = {
                pg: "html5_inicis", // 결제 PG사
                pay_method: "card", // 결제 방법
                merchant_uid: `merchant_${new Date().getTime()}`, // 주문번호
                name: this.lectureInfo.title, // 결제 내역
                amount: this.price, // 결제 금액
                buyer_email: jwtDecode(localStorage.getItem('token')).email,
                buyer_name: jwtDecode(localStorage.getItem('token')).name,
                buyer_tel: "",
            };

            this.closeApplyModal();
            console.log(paymentData);

            IMP.request_pay(paymentData, this.processPayment); // 결제 요청
        },
        async processPayment(rsp) {

            console.log("processPayment");
            try {
                this.memberId = localStorage.getItem('id');
                if (rsp.success) {
                    const data = {
                        impUid: rsp.imp_uid, // 아임포트 거래 고유번호
                        title: this.lectureInfo.title,
                        price: this.price,
                        memberId: this.memberId,
                        lectureGroupId: this.lectureGroupId,
                    };
                    console.log(data);

                    await axios.post(`${process.env.VUE_APP_API_BASE_URL}/payment-service/complete`, data);
                    this.alertModalTtile = "결제 완료";
                    this.alertModalContents = "결제가 완료되었습니다!";
                    this.alertModal = true;
                }
            } catch (error) {
                console.log("결제 처리 중 오류 발생:", error);
            }
        },
        async processFreeLesson() {
            console.log("무료 강연")
            const lectureGroupId = this.selectedLectureGroup.lectureGroupId
            try {
                const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/after-paid?lectureGroupId=${lectureGroupId}&memberId=${this.memberId}`);
                // console.log("신청됨", response.data)
                this.alertModalTtile = response.data.status_message
                this.alertModalContents = "강의 신청이 완료되었습니다!"
                this.alertModal = true;
                this.isApplyModalOpen = false;
                // alert(response.data.status_message)
            } catch (error) {
                console.log("결제 처리 중 오류 발생:", error);
            }
        },
    }
}
</script>


<style scoped>
.v-container {
    width: 70vw;
}

.contents-section {
    /*display: flex;*/
    justify-content: space-between;
    gap: 20px;
}

.float-info {
    width: 300px;
    background-color: #fff;
    position: sticky;
    border: 2px solid #f0efef;
    border-radius: 10px;
    padding: 30px 5px;
    margin-top: 60px;
    /* 화면 상단과 40px 거리 */
    top: 60px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    /* 그림자 추가 */
}

.time-table {
    margin: auto;
    width: 250px;

}

table {
    width: 100%;
    border-collapse: collapse;
    border: 1px solid #cdcdcd !important;
}

th,
td {
    padding: 5px;
    text-align: center;
    font-size: 11px;
    border: 1px solid #e6e4e4 !important;
}

td {
    height: 10px;
}

.groups-info {
    border: 2px solid #f0efef;
    border-radius: 10px;
}

.contents-title {
    text-align: left;
    margin: 20px 10px;
    font-size: 22px;
    font-weight: 700;
}

.tutor-profile img {
    width: 200px;
    height: 200px;
    border-radius: 50%;
}

.tutor-info {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    /* 그림자 추가 */
    background-color: #e1e8fa;
    border-radius: 10px;
    width: 80%;
    padding: 20px 50px;
    font-size: 17px;
    text-align: left;
}

.date-info {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    /* 그림자 추가 */
    background-color: #d0e2ff;

    border-radius: 10px;
    padding: 20px;
    margin: 20px 0;
    font-size: 18px;
    color: #333;
    text-align: left;

}

.soldout {
    display: inline-block;
    text-align: center;
    width: 50px;
    border-radius: 5px;
    font-weight: bold;
    font-size: 15px;
    color: #fff;
    background-color: #666;
}

.custom-option {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.custom-option.selected {
    border-color: #007bff !important;
    /* 선택된 경우 테두리 색상 */
    background-color: #e7f0ff !important;
    /* 선택된 경우 배경 색상 */
    transform: scale(1.01) !important;
    /* 선택된 경우 살짝 확대 */
}

.disabled-group {
    opacity: 0.5;
    /* 비활성화된 그룹에 대한 스타일 */
    pointer-events: none;
    /* 클릭 비활성화 */
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.4s ease;
}

.fade-enter,
.fade-leave-to

/* .fade-leave-active in <2.1.8 */
    {
    opacity: 0;
}

.caution {
    font-size: 14px;
    color: red;
}

.btn-transition {
    transition: all 0.3s ease;
}

.btn-transition:hover {
    transform: scale(1.05);
}
</style>