<template>
    <v-container>
        <v-row justify="center">
            <v-col class="main-title" cols="12" align="center" style="font-size: 28px; font-weight: 700">
                <p>신청 내역</p>
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <v-row class="header">
                    <v-col cols="1">순서</v-col>
                    <v-col cols="6">강의명</v-col>
                    <v-col cols="3">신청날짜</v-col>
                    <v-col cols="2">상태</v-col>
                </v-row>
            </v-col>
        </v-row>
        <v-row>
            <v-col v-if="allLectures.length">
                <v-row v-for="(lecture, index) in allLectures" :key="lecture.applyId" class="item">
                    <v-col cols="1">{{ index + 1 }}</v-col>
                    <v-col cols="6" class="lecture-title">{{ lecture.title }}</v-col>
                    <v-col cols="3">{{ formatDate(lecture.createdTime) }}</v-col>
                    <v-col cols="2">
                        <span :class="getStatusClass(lecture.status)" style="font-weight: bold;">{{ getStatusText(lecture.status) }}</span>
                    </v-col>
                </v-row>
            </v-col>
            <v-col v-else>
                <div style="margin: 20px 0">신청한 강의가 없습니다.</div>
            </v-col>
        </v-row>
        <v-row justify="center" class="mr-2">
            <v-col cols="auto">
                <!-- 이전 페이지로 이동하는 클릭 가능한 텍스트 -->
                <span 
                    @click="goToAllPreviousPage" 
                    :class="{ 'disabled-text': allPage === 0 }"
                    style="cursor: pointer; color: #000000;" 
                    v-if="allPage !== 0"
                >
                    이전
                </span>
                <span v-else style="color: #B0BEC5;">이전</span> <!-- 비활성화된 경우 -->
            </v-col>
            
            <v-col cols="auto">
                <span>{{ allPage + 1 }} / {{ allTotalPages }}</span> <!-- 현재 페이지 및 전체 페이지 표시 -->
            </v-col>
            
            <v-col cols="auto">
                <!-- 다음 페이지로 이동하는 클릭 가능한 텍스트 -->
                <span 
                    @click="goToAllNextPage" 
                    :class="{ 'disabled-text': allPage + 1 >= allTotalPages }" 
                    style="cursor: pointer; color: #000000;" 
                    v-if="allPage + 1 < allTotalPages"
                >
                    다음
                </span>
                <span v-else style="color: #B0BEC5;">다음</span> <!-- 비활성화된 경우 -->
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import axios from "axios";

export default {
    data() {
        return {
            allLectures: [],
            
            size: 10,          // 페이지 당 불러올 강의 수
            allPage: 0,           // 현재 페이지
            allTotalPages: 0, 
        };
    },
    methods: {
        goToAllPreviousPage() {
            if (this.allPage > 0) {
                this.allPage--;
                this.fetchAllLectures();
            }
        },
        // 페이지네이션 - 다음 페이지로 이동
        goToAllNextPage() {
            if (this.allPage + 1 < this.allTotalPages) {
                this.allPage++;
                this.fetchAllLectures();
            }
        },
        async fetchLectures() {
            try {
                // 상태 목록을 배열로 정의
                const statuses = ['STANDBY', 'REJECT'];
                // 각 상태에 대해 데이터를 요청하고, 결과를 병합
                const lecturePromises = statuses.map(status => 
                    // axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/my-lecture-list?status=${status}`,{
                    axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/my-lecture-list`,{
                        params:{
                            status:`${status}`,
                            size:this.size,
                            page:this.allPage
                        }
                    })
                );

                const responses = await Promise.all(lecturePromises);

                // 모든 강의 목록을 합치기
                this.allLectures = responses.flatMap(response => response.data.result.content);
                console.log(this.allLectures);
            } catch (error) {
                console.error("Failed to fetch all lectures:", error);
            }
        },
        formatDate(date) {
            if (!date) return 'N/A';
            const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
            return new Date(date).toLocaleDateString(undefined, options);
        },
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
        this.fetchLectures();
    },
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
.lecture-title:hover{
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
.status-waiting {
    color: blue;
}
.status-terminate {
    color: gray;
}
</style>
