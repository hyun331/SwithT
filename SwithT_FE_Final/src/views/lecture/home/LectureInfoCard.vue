<template>
    <section class="top-info">
        <v-row class="align-center">
            <v-col cols="7">
                <v-row v-if="isLoading">
                    <v-progress-circular color="primary" indeterminate></v-progress-circular>
                </v-row>
                <v-row v-else>
                    <div>
                        <span class="lectureTypeStyle">
                            {{ this.lectureType }}
                        </span>
                        <span class="category" style="font-size: 18px;">
                            {{ this.category }}
                        </span>
                    </div>
                </v-row>
                <v-row class="d-flex align-center justify-start mb-2">
                    <div class="title mr-2">{{ infoData.title }} <v-icon style="font-size:30px;" v-if="isChat" @click="clickChatRoom()">mdi-chat</v-icon><v-icon v-if="isTutor && !isTuteeExist" style="font-size:30px;" @click="this.$router.push(`/lecture-group/${this.lectureGroupId}`)">mdi-pencil</v-icon></div>

                    
                    <!-- <v-btn v-if="isTutor" variant="outlined" class="ml-2">연장하기</v-btn> -->
                </v-row>

                <div class="memberName" v-if="lectureType=='강의'"> <strong>{{ infoData.memberName }} 튜터</strong>
                </div>
                <div class="memberName" v-else> <strong>{{ infoData.tuteeName ?? "신청 및 승인된 튜티가 없습니다" }} 튜티</strong>
                </div>
                <div class="detailInfo">시작 일자 : {{ infoData.startDate ?? "아직 입력되지 않았습니다." }}</div>
                <div class="detailInfo">강의 일정 : </div>
                <div v-html="lectureSchedules" class="detailInfo"></div>
                <div class="detailInfo">위치 : {{ infoData.address }} {{infoData.detailAddress=="" ? "아직 입력되지 않았습니다.":infoData.detailAddress}}<v-icon @click="showMap()" v-if="infoData.address">mdi-google-maps</v-icon>
                </div>
                <!-- <div class="detailInfo" v-if="lectureType=='과외'"> 튜티 : {{ infoData.tuteeName ?? "신청 및 승인된 튜티가 없습니다" }}</div> -->


            </v-col>
            <v-col cols="5" class="d-flex align-center justify-center">
                <div class="thumbnail">
                    <img :src="infoData.image" alt="Lecture Thumbnail" />
                </div>
            </v-col>
        </v-row>
    </section>
    <v-dialog v-model="mapModal" max-width="500px">
        <v-card>
            <v-card-title class="text-h4 pa-4 d-flex justify-center">
                주소 확인
            </v-card-title>
            <v-divider class="mb-4" style="height: 2px; background-color: black;"></v-divider>

            <v-card-text class="pa-4 pt-0">
                <div>
                    <div id="map" style="width:100%;height:350px;"></div>
                </div>
            </v-card-text>
            <v-card-actions class="pa-4">
                <v-row justify="center">
                    <v-btn variant="outlined" @click="mapModal = false" class="mr-3">확인</v-btn>
                </v-row>
            </v-card-actions>
            <v-divider class="mt-2 mb-10"></v-divider>

        </v-card>
    </v-dialog>
    <v-dialog v-model="infoModal" max-width="800px">
        <v-card>
            <v-divider class="mt-10 mb-2"></v-divider>

            <v-card-text class="pa-4 pt-0 mt-5">
                <h4 class="mb-1 ml-6 mr-2"> <strong>강의 그룹 수정 및 삭제</strong> </h4>
                <!-- 제목  -->
                <v-row></v-row>
                <v-divider></v-divider>
                <v-row class="d-flex align-center justify-start ml-7 mt-5">
                    <h6 class="d-flex align-center mt-6 mb-1 ml-6 mr-2"> <strong>주소</strong> </h6>
                    <v-btn variant="outlined" @click="updateAddress()" class="align-center mt-3">수정</v-btn>
                </v-row>

                <div class="mb-2 ml-15 mr-6 mt-6"> {{ address }}</div>

                <v-row class="d-flex align-center justify-start ml-7 mt-5">
                    <h6 class="d-flex align-center mt-6 mb-1 ml-6 mr-2"> <strong>가격</strong> </h6>
                    <v-text-field variant="outlined" v-model="price" class="d-flex align-center"></v-text-field>
                </v-row>

                <v-row class="d-flex align-center justify-start ml-7 mt-5">
                    <h6 class="d-flex align-center mt-6 mb-1 ml-6 mr-2"> <strong>인원수</strong> </h6>
                    <v-text-field variant="outlined" v-model="limitPeople" class="d-flex align-center"></v-text-field>
                </v-row>

                <v-row class="d-flex align-center justify-start ml-7 mt-5">
                    <h6 class="d-flex align-center mt-6 mb-1 ml-6 mr-2"> <strong>시작 일자</strong> </h6>
                    <input class="d-flex align-center mb-2 ml-6 mr-6 mt-3" v-model="startDate" type="date" outlined>
                </v-row>

                <v-row class="d-flex align-center justify-start ml-7 mt-5">
                    <h6 class="d-flex align-center mt-6 mb-1 ml-6 mr-2"> <strong>종료 일자</strong> </h6>
                    <input class="d-flex align-center mb-2 ml-6 mr-6 mt-3" v-model="endDate" type="date" outlined>
                </v-row>

                        <h6 class="d-flex align-center mt-6 mb-1 ml-13 mr-2"><strong>강의 일정 :</strong> </h6>
                        <div v-html="lectureSchedules" class="d-flex align-center mt-6 mb-1 ml-13 mr-2 detailInfo"></div>

            </v-card-text>

            <v-card-actions class="pa-4">
                <v-row justify="center" class="flex">
                    <v-btn variant="outlined" class="cancel-btn mr-3" @click="renewInfo()">취소하기</v-btn>
                    <v-btn variant="outlined" class="delete-btn mr-3" @click="deleteGroup()">삭제하기</v-btn>
                    <v-btn variant="outlined" class="submit-btn" @click="updateInfo()">등록하기</v-btn>
                </v-row>
            </v-card-actions>

            <v-divider class="mt-2 mb-10"></v-divider>
        </v-card>
    </v-dialog>
    <ChatModal
    v-model:dialog="chatModal"
    :selectedChatRoomId="this.chatRoomId"
    persistent
    :style="{ position: 'fixed', right: '-9%', top: '10%', width: '0px' }"
    />
    <!-- <v-card class="custom-card">
        <v-row>
            <v-col cols="3" class="thumbnail-container rounded-circle">
                <v-img :src="infoData.image" alt="썸네일" class="thumbnail-image rounded-circle"></v-img>
            </v-col>

            <v-col cols="9" class="pa-4">
                    <div class="text-left">
                        <div class="ml-2" justify="start">{{infoData.memberName}} 선생님 <v-icon @click="clickChatRoom()">mdi-chat</v-icon> </div> 
                        <h2>{{ infoData.title }}</h2>
                    </div>
                <v-row>
                    <v-col cols="6" class="text-left">
                        <v-chip label>{{ infoData.category }}</v-chip> 
                        <div style="margin-top: 10px;margin-bottom: 10px;"><strong>시작 일자:</strong> {{ infoData.startDate }}</div>
                        <div style="margin-bottom: 1px;"><strong>강의 일정:</strong></div>
                        <div v-html="lectureSchedules" style="margin-bottom: 5px;"></div>
                    </v-col>
                </v-row>
            </v-col>
        </v-row>
    </v-card> -->
</template>

<script>
/* global kakao */
import { useRoute } from 'vue-router';
import axios from 'axios';
import ChatModal from '@/components/ChatModal.vue';

export default {
    components:{
        ChatModal,
    },
    props: {
        isTutor: Boolean,
        infoData: Object,
        lectureSchedules: String,
        isTuteeExist:Boolean
    },
    data() {
        return {
            mapModal: false,
            price: 0,
            limitPeople: 0,
            startDate: "",
            endDate: "",
            isLoading: true,
            lectureType: "",
            category: "",
            infoModal: false,
            address: "",
            detailAddress:"",
            lectureGroupId: 0,
            days: ['월', '화', '수', '목', '금', '토', '일'],
            hours: [
                '06:00', '07:00', '08:00', '09:00', '10:00', '11:00',
                '12:00', '13:00', '14:00', '15:00', '16:00', '17:00',
                '18:00', '19:00', '20:00', '21:00', '22:00', '23:00', '24:00'
            ],
            groupTimes: [],
            chatModal: false,
            chatRoomId: '',
            isChat:false,
        };
    },
    mounted() {
        this.loadDaumPostcodeScript();
        this.loadKakaoMapScript();
        this.lectureType = this.formattedLectureType();
        this.category = this.formattedCategory();
        this.price = this.infoData.price
        this.limitPeople = this.infoData.limitPeople

    },
    created() {
        const route = useRoute();
        this.lectureGroupId = route.params.lectureGroupId;
        this.isLoading = false;
        this.loadKakaoMapScript();
        this.address = this.infoData.address;
        this.detailAddress=this.infoData.detailAddress;
        this.startDate = this.infoData.startDate;
        this.endDate = this.infoData.endDate;
    },
    watch: {
        infoData: {
            handler() {
                console.log(this.infoData)
                this.lectureType = this.formattedLectureType();
                this.category = this.formattedCategory();
                this.startDate = this.infoData.startDate;
                this.endDate = this.infoData.endDate;
                this.address = this.infoData.address;
                this.detailAddress=this.infoData.detailAddress;
                this.price = this.infoData.price
                this.limitPeople = this.infoData.limitPeople
                this.groupTimes = this.infoData.lectureGroupTimes
                this.chatRoomId=this.infoData.chatRoomId
                if(this.infoData.chatRoomId === null) this.isChat = false;
                else this.isChat = true;
            },
            immediate: true,
            deep: true
        }
    },
    methods: {
        renewInfo() {
            this.address = this.infoData.address;
            this.infoModal = false;
        },
        async deleteGroup() {
            try {
                await axios.put(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/delete/lecture-group/${this.lectureGroupId}`)
            }
            catch (e) {
                alert(e?.response?.data?.error_message)
            }
        },
        async updateInfo() {
            const body = {
                address: this.address,
                price: this.price,
                limitPeople: this.limitPeople,
                startDate: this.startDate,
                endDate: this.endDate,
            };
            try {
                await axios.put(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/update/lecture-group/${this.lectureGroupId}`, body)
            } catch (e) {
                alert(e?.response?.data?.error_message)
            }

        },
        groupEditClick(){
            this.$router.push(`/lecture-group/${this.lectureGroupId}`);
        },
         clickChatRoom() {
            console.log("채팅방 입장" + this.infoData.chatRoomId);
            this.chatModal = true;
        },
        formattedCategory() {
            if (this.infoData?.category) {
                const category = this.infoData?.category;
                switch (category) {
                    case 'CAREER':
                        return '취업&직무';
                    case 'HOBBY':
                        return '취미';
                    case 'ADDMISSION':
                        return '입시';
                    case 'DEVELOPMENT':
                        return '자기계발';
                    default:
                        return this.infoData?.category;
                }
            }
        },
        changeDay(day) {
            switch (day) {
                case 'MONDAY':
                    return '월요일';
                case 'TUESDAY':
                    return '화요일';
                case 'WEDNESDAY':
                    return '수요일';
                case 'THURSDAY':
                    return '목요일';
                case 'FRIDAY':
                    return '금요일';
                case 'SATURDAY':
                    return '토요일';
                case 'SUNDAY':
                    return '일요일';
            }
        },
        formattedLectureType() {
            if (this.infoData?.lectureGroupTimes && this.infoData.lectureGroupTimes.length > 0) {
                const lectureType = this.infoData.lectureGroupTimes[0].lectureType;
                console.log("lectureType" + lectureType)
                switch (lectureType) {
                    case 'LECTURE':
                        return '강의';
                    case 'LESSON':
                        return '과외';
                    default:
                        return this.infoData.lectureGroupTimes[0].lectureType;
                }
            }
        },
        showMap() {
            this.execDaumPostcode();
            this.mapModal = true;
        },
        loadDaumPostcodeScript() {
            const script = document.createElement('script');
            script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
            script.onload = () => {
                this.isDaumScriptLoaded = true;
            };
            document.head.appendChild(script);
        },
        updateAddress() {
            if (window.daum && window.daum.Postcode) {
                // eslint-disable-next-line no-undef
                new daum.Postcode({
                    oncomplete: (data) => {
                        this.address = data?.roadAddress;

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
        // 주소 검색 api
        execDaumPostcode() {
            // 주소 검색한 거 기반으로 위도 경도
            // 좌표 검색을 위해 Kakao 지도 Geocoder 사용
            const geocoder = new kakao.maps.services.Geocoder();
            geocoder.addressSearch(this.infoData.address, (result, status) => {
                if (status === kakao.maps.services.Status.OK) {
                    console.log('위도 : ' + result[0].y);
                    console.log('경도 : ' + result[0].x);

                    // 지도에 마커를 추가하는 로직
                    this.initMap(result[0].y, result[0].x);
                }
            });
        },
        initMap(latitude, longitude) {
            const mapContainer = document.getElementById('map');
            const mapOption = {
                center: new kakao.maps.LatLng(latitude, longitude),
                level: 3
            };
            const map = new kakao.maps.Map(mapContainer, mapOption);

            const markerPosition = new kakao.maps.LatLng(latitude, longitude);
            const marker = new kakao.maps.Marker({
                position: markerPosition
            });
            marker.setMap(map);
        },
    },
};
</script>

<style scoped>
.v-container {
    padding: 0 !important;
}

.top-info {
    background-color: #6C97FD;
    color: white;
    padding: 100px 20vw 40px;
    text-align: left;
}

.lectureTypeStyle {
    display: inline-block;
    margin-right: 5px;
    background-color: #121212;
    width: 50px;
    border-radius: 5px;
    font-size: 18px;
    font-weight: bold;
    text-align: center;
}

.title {
    font-size: 40px;
    font-weight: bold;
    word-wrap: break-word;
    word-break: break-all;
    max-width: 80%; /* 제목의 최대 너비를 조절하기 위해 이 값을 설정 */
    overflow-wrap: break-word;
    white-space: normal; /* 제목 안에서 줄바꿈이 가능하도록 설정 */
}

.memberName {
    font-size: 20px;
}

.detailInfo {
    font-size: 15px;
    margin-bottom: 5px;
}

.avgScore {
    font-size: 1.5rem;
}
.thumbnail img {
    border-radius: 8px;
    height: 300px;
    object-fit: contain;
}
.thumbnail img {
    width: 80%;
    border-radius: 8px;
}

.custom-card {
    border: 1px solid #E0E0E0;
    padding: 20px;
    background-color: #FAFAFA;
}

.thumbnail-container {

    width: 100px;
    height: 100px;
    border-radius: 100%;
    overflow: hidden;
    display: flex;
    justify-content: flex-start;
    align-items: flex-start;
}
.thumbnail img {
    border-radius: 8px;
    height: 300px;
    object-fit: contain;
}

.pa-4 {
    padding: 16px;
}

.text-left {
    text-align: left;
}

.d-flex {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.cancel-btn {
    color: black;
    border-color: #e0e0e0;
    width: 30%;
    height: 40px;
}

.delete-btn {
    color: red;
    border-color: red;
    width: 30%;
    height: 40px;
}

.submit-btn {
    background-color: #0066ff;
    color: white;
    width: 30%;
    height: 40px;
}
</style>
