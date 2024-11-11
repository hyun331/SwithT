<template>
    <v-container width="60%" style="margin-top: 60px;">
        <v-row justify="center">
            <v-col cols="auto">
                <v-tabs v-model="tab" color="black" slider-color="#6C97FD">
                    <v-tab value="LESSON">나의 과외</v-tab>
                    <v-tab value="LECTURE">나의 강의</v-tab>
                </v-tabs>
            </v-col>
        </v-row>

        <br>
        <v-row justify="left">
            <v-col cols="1">
                <v-btn color="#6C97FD" @click="changeStatus('ADMIT')">진행중</v-btn>
            </v-col>
            <v-col cols="1">
                <v-btn color="#CFCFCF" @click="changeStatus('TERMINATE')">종료</v-btn>
            </v-col>


        </v-row>
        <v-row justify="center">
            <v-col>
                <v-btn width="100%" color="#6C97FD" @click="clickCreateLectureBtn">과외/강의 개설하기</v-btn>
            </v-col>
        </v-row>


        <v-card-text>
            <v-tabs-window v-model="tab">
                <v-tabs-window-item value="LESSON">
                    <v-row v-if="tab === 'LESSON'" class="lessons-container">
                        <v-col v-for="lesson in lessons" :key="lesson.id" cols="4" class="mb-4">
                            <div @click="clickLectureDetail(lesson.id)" class="lesson-card">
                                <img :src="lesson.image" alt="강의 썸네일" class="lecture-image" />
                                <br>
                                <v-chip v-if="status === 'ADMIT'" color="primary" class="mr-2">진행중</v-chip>
                                <v-chip v-if="status === 'TERMINATE'" color="#CFCFCF" class="mr-2">종료</v-chip>
                                {{ lesson.title }}
  
                            </div>
                        </v-col>
                    </v-row>            
                </v-tabs-window-item>

                <v-tabs-window-item value="LECTURE">
                    <v-row v-if="tab === 'LECTURE'" class="lessons-container">
                        <v-col v-for="lecture in lectures" :key="lecture.id" cols="4" class="mb-4">
                            <div @click="clickLectureDetail(lecture.id)" class="lesson-card">
                                <img :src="lecture.image" alt="강의 썸네일" class="lecture-image" />
                                <br>
                                <v-chip v-if="status === 'ADMIT'" color="primary" class="mr-2">진행중</v-chip>
                                <v-chip v-if="status === 'TERMINATE'" color="#CFCFCF" class="mr-2">종료</v-chip>
                                {{ lecture.title }}
                            </div>
                        </v-col>
                    </v-row>
                </v-tabs-window-item>
            </v-tabs-window>
        </v-card-text>
        <v-pagination v-model="frontendPage" :length="totalPages" @click="handlePageChange"></v-pagination>

    </v-container>
</template>

<script>
import axios from 'axios';
export default {
    data() {
        return {
            tab: 'LESSON',
            lessons: [],
            lectures: [],
            status: 'ADMIT',
            page: 0,
            size: 6,
            totalPages: 0,
            frontendPage: 1

        };

    },
    async created() {
        this.showLessonList();
    },
    watch: {
        tab(newTab) {
            if (newTab === 'LESSON') {
                this.tab = 'LESSON';
            } else if (newTab === 'LECTURE') {
                this.tab = 'LECTURE';
            }
            this.status = 'ADMIT';
            this.page = 0;
            this.frontendPage = 1;

            this.showLessonList();

        }
    },
    methods: {

        async showLessonList() {

            try {
                let params = {
                    searchTitle: '',
                    lectureType: this.tab,
                    category: '',
                    status: this.status,
                    size: this.size,
                    page: this.page,

                };
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/my-lecture-list`, { params });
                this.totalPages = response.data.result.totalPages;
                if (this.tab === 'LESSON') {
                    this.lessons = response.data.result.content;
                }
                else if (this.tab === 'LECTURE') {
                    this.lectures = response.data.result.content;
                }

                console.log(response.data.result);
            } catch (e) {
                console.log(e.response.data.error_message);
            }

        },
        changeStatus(changedStatus) {
            this.status = changedStatus;
            this.page = 0;
            this.frontendPage = 1;
            this.showLessonList();
        },
        handlePageChange() {
            this.page = this.frontendPage - 1;
            this.showLessonList();
        },

        clickLectureDetail(id) {
            if (this.status === 'ADMIT') {
                this.$router.push(`/tutor-lecture-class-list?lectureId=${id}&lectureType=${this.tab}&lectureStatus=${this.status}`);
            }
        },
        clickCreateLectureBtn(){
            this.$router.push(`/create-lecture`);
        }

    }
}
</script>

<style scoped>
.lecture-image {
    width: 250px;
    height: 200px;
    object-fit: cover;
    margin-bottom: 10px;
}

.custom-border {
    border: 2px solid #cccccc;
    border-radius: 8px;
    box-shadow: none !important;
}

.v-tab {
    margin-right: 50px;
    margin-left: 50px;
    font-size: 18px;
    font-weight: 900;
}


.admit-color {
    background-color: #90CDFF;
}

.lesson-card:hover {
    cursor: pointer;
}
</style>