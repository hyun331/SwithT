<template>
    <v-container justify="center">
        <v-row justify="center">
            <v-col class="main-title" cols="12" align="center" style="font-size: 28px; font-weight: 700">
                <p>신청 내역</p>
            </v-col>
        </v-row>

        <v-row justify="center">
            <v-col cols="12" style="max-width: 1300px;">
                <v-row class="header" style="padding: 0 10px;">
                    <v-col cols="2" class="text-center">순서</v-col>
                    <v-col cols="2" class="text-center">상태</v-col>
                    <v-col cols="3" class="text-center">강의명</v-col>
                    <v-col cols="2" class="text-center">신청날짜</v-col>
                    <v-col cols="2" class="text-center ml-5">결과 처리</v-col>
                </v-row>
            </v-col>
        </v-row>

        <v-row justify="center">
            <v-col v-if="allLectures.length" cols="12" style="max-width: 1300px;">
                <v-row v-for="(lecture, index) in allLectures" :key="lecture.applyId" class="item"
                    style="padding: 5px 10px; cursor:pointer;" @click="goToLectureDetail(lecture.id)">
                    <v-col cols="2" class="text-center">{{ index + 1 + page * size }}</v-col> <!-- 순서에 페이지 값 반영 -->
                    <v-col cols="2" class="text-center">
                        <span :class="getStatusClass(lecture.status)" style="font-weight: bold;">{{
                            getStatusText(lecture.status) }}</span>
                    </v-col>
                    <v-col cols="3" class="text-center">
                        {{ lecture.title }}
                    </v-col>
                    <v-col cols="2" class="text-center">{{ formatDate(lecture.createdTime) }}</v-col>
                    <v-col cols="2" class="text-center ml-5">
                        <v-btn color="#6C97FD" @click.stop="updateLectureStatus(lecture.id, 'ADMIT')">
                            <strong>승인</strong>
                        </v-btn>
                        <v-btn color="#FD6C6C" class="ml-2" @click.stop="updateLectureStatus(lecture.id, 'REJECT')">
                            <strong>거절</strong>
                        </v-btn>
                    </v-col>
                </v-row>
            </v-col>
            <v-col v-else>
                <div style="margin: 20px 0; text-align: center;"> 개설 요청된 강의가 없습니다.</div>
            </v-col>
        </v-row>

        <v-row justify="center" class="mr-2">
            <v-col cols="auto">
                <!-- 이전 페이지로 이동하는 클릭 가능한 텍스트 -->
                <span @click="goToPreviousPage" :class="{ 'disabled-text': page === 0 }"
                    style="cursor: pointer; color: #000000;" v-if="page !== 0">
                    이전
                </span>
                <span v-else style="color: #B0BEC5;">이전</span> <!-- 비활성화된 경우 -->
            </v-col>

            <v-col cols="auto">
                <span>{{ page + 1 }} / {{ totalPages }}</span> <!-- 현재 페이지 및 전체 페이지 표시 -->
            </v-col>

            <v-col cols="auto">
                <!-- 다음 페이지로 이동하는 클릭 가능한 텍스트 -->
                <span @click="goToNextPage" :class="{ 'disabled-text': page + 1 >= totalPages }"
                    style="cursor: pointer; color: #000000;" v-if="page + 1 < totalPages">
                    다음
                </span>
                <span v-else style="color: #B0BEC5;">다음</span> <!-- 비활성화된 경우 -->
            </v-col>
        </v-row>
        <AlertModal v-model="alertModal" @update:dialog="alertModal = $event" icon=mdi-alert-circle-outline
            :title=this.alertModalTitle :contents=this.alertModalContents />
    </v-container>
</template>


<script>
import axios from "axios";
import AlertModal from "@/components/AlertModal.vue";

export default {
    components: {
        AlertModal
    },
    data() {
        return {
            allLectures: [],   // 모든 강의 목록
            page: 0,           // 현재 페이지
            size: 10,          // 페이지 당 불러올 강의 수
            totalPages: 0,     // 전체 페이지 수
            isLoading: false,  // 로딩 상태
            alertModal: false,
            alertModalTitle: "",
            alertModalContents: "",
        };
    },
    methods: {
        // 강의 상태 변경 메서드
        async updateLectureStatus(id, newStatus) {
            try {
                const response = await axios.put(`${process.env.VUE_APP_API_BASE_URL}/member-service/lectures/${id}/status`, null, {
                    params: { newStatus: newStatus },
                });
                if (response.status === 200) {
                    this.alertModalTtile = `${newStatus} 완료`;
                    this.alertModalContents = `${newStatus} 완료되었습니다`;
                    this.alertModal = true;
                    this.fetchLectures();  // 상태 변경 후 강의 목록을 다시 불러옴
                }
            } catch (error) {
                console.error("상태 변경:", error);

            }
            this.fetchLectures();  // 상태 변경 후 강의 목록을 다시 불러옴

        },
        goToLectureDetail(id) {
            this.$router.push(`/lecture/${id}`);
        },
        // 강의 목록을 서버에서 불러오는 메서드
        async fetchLectures() {
            this.isLoading = true;
            try {
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/list-of-lecture`, {
                    params: {
                        status: 'STANDBY',
                        size: this.size,
                        page: this.page,
                    },
                });

                this.allLectures = response.data.result.content;
                this.totalPages = response.data.result.totalPages; // 전체 페이지 수 업데이트
            } catch (error) {
                console.error("Failed to fetch lectures:", error);
            } finally {
                this.isLoading = false;
            }
        },
        // 페이지네이션 - 이전 페이지로 이동
        goToPreviousPage() {
            if (this.page > 0) {
                this.page--;
                this.fetchLectures();
            }
        },
        // 페이지네이션 - 다음 페이지로 이동
        goToNextPage() {
            if (this.page + 1 < this.totalPages) {
                this.page++;
                this.fetchLectures();
            }
        },
        // 날짜 포맷팅
        formatDate(date) {
            if (!date) return 'N/A';
            const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
            return new Date(date).toLocaleDateString(undefined, options);
        },
        // 상태에 따른 클래스 지정
        getStatusClass(status) {
            switch (status) {
                case 'STANDBY':
                    return 'status-standby';
                case 'REJECT':
                    return 'status-reject';
                default:
                    return '';
            }
        },
        // 상태 텍스트 반환
        getStatusText(status) {
            switch (status) {
                case 'STANDBY':
                    return '수락 대기';
                case 'REJECT':
                    return '거절';
                default:
                    return '상태 불명';
            }
        },
    },
    created() {
        this.fetchLectures();  // 컴포넌트가 생성될 때 첫 번째 데이터를 불러옴
    }
};
</script>

<style scoped>
.v-container {
    margin-top: 60px;
    width: 70vw;
}

.header {
    font-weight: bold;
    border-bottom: 2px solid #ccc;
}

.item {
    padding: 10px 0;
}

.lecture-title:hover {
    background-color: #ececec;
    border-radius: 15px;
    cursor: pointer;
}

.status-standby {
    color: orange;
}

.status-admit {
    color: green;
}

.status-rejected {
    color: red;
}
</style>
