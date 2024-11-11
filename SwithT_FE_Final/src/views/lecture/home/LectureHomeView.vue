<template>
    <!-- Lecture Info Card 컴포넌트 -->
    <LectureInfoCard :isTutor="istutor" :infoData="infoData" :lectureSchedules="lectureSchedules"
        :isTuteeExist="isTuteeExist" :isShowMap="isShowMap" />

    <div class="notice-slider">
        <div v-for="(notice, index) in topNotice" :key="index" v-show="currentNotice === index">
            <v-icon style="font-size: 16px;">mdi-bullhorn-variant-outline</v-icon> <strong> [공지사항] </strong> {{
                notice.title }}
        </div>
    </div>
    <v-container>
        <!-- Lecture Tabs -->
        <v-tabs v-model="tab" align-tabs="center" class="mt-5">
            <v-tab value="dashboard">대시보드</v-tab>
            <v-tab value="assignment">과제</v-tab>
            <v-tab value="notice">게시판</v-tab>
            <v-tab value="tuteeList" v-if="isLecture && istutor">튜티 리스트</v-tab>
        </v-tabs>

        <v-tabs-window v-model="tab">
            <!-- 대시보드 탭 -->
            <v-tabs-window-item value="dashboard">
                <v-container style="width: 70vw; min-height: 500px;">
                    <v-row>
                        <!-- 과제 리스트 -->
                        <v-col cols="12" md="6">
                            <div class="box-style" outlined height="400px">
                                <v-card-title><strong>과제 리스트</strong> </v-card-title>
                                <v-spacer></v-spacer>
                                <v-card-text v-if="urgentAssignment.length">
                                    <v-row>
                                        <v-col cols="6"><strong>과제 제목</strong></v-col>
                                        <v-col cols="6"><strong>기간</strong></v-col>
                                    </v-row>
                                    <hr>
                                    <v-row v-for="assignment in urgentAssignment" :key="assignment.id"
                                        @click="viewAssignmentOpen(assignment.id)" class="mb-2 list-item">
                                        <v-col cols="6">{{ assignment.title }}</v-col>
                                        <v-col cols="6">{{ assignment.startDate }}~{{ assignment.endDate }}</v-col>
                                    </v-row>
                                </v-card-text>
                                <v-card-text v-else>
                                    <v-row>
                                        <v-col cols="6"><strong>과제 제목</strong></v-col>
                                        <v-col cols="6"><strong>시작일</strong></v-col>
                                    </v-row>
                                    <hr>
                                    <v-row class="mb-2">
                                        <v-col cols="12" class="text-center"
                                            style="height: 100px; display: flex; align-items: center; justify-content: center; font-size:large;">
                                            등록된 과제가 없습니다
                                        </v-col>
                                    </v-row>
                                </v-card-text>
                            </div>
                        </v-col>

                        <!-- 공지사항 -->

                        <v-col cols="12" md="6">
                            <div class="box-style" outlined height="400px">
                                <v-card-item>
                                    <v-card-title><strong>학습 진행률</strong></v-card-title>
                                </v-card-item>
                                <v-card-text>
                                    <div v-if="infoData?.totalDayCount">
                                        <div class="chartDoughnut" v-if="isDataLoaded">
                                            <DoughnutChart :totalDayCount="infoData?.totalDayCount"
                                                :pastDayCount="infoData?.pastDayCount" />
                                        </div>
                                        <div v-else>
                                            <!-- 데이터가 로드되기 전까지는 로딩 메시지 또는 스피너를 보여줌 -->
                                            <v-progress-circular color="primary" indeterminate></v-progress-circular>
                                        </div>
                                    </div>
                                    <div v-else class="text-center"
                                        style="height: 170px; display: flex; align-items: center; justify-content: center; font-size:large;">
                                        아직 시작되지 않았습니다.
                                    </div>
                                </v-card-text>
                            </div>
                        </v-col>
                    </v-row>


                </v-container>
            </v-tabs-window-item>

            <!-- 과제 탭 -->
            <v-tabs-window-item value="assignment">
                <v-container style="width: 60vw; min-height: 500px;">
                    <v-card-text>
                        <v-row justify="end">
                            <v-btn rounded color="#6C97FD" style="font-weight: bold;"
                                @click="renewAssignment(); assignmentCreateModal = true;" v-if="this.istutor">
                                과제 생성
                            </v-btn>
                        </v-row>

                        <!-- 과제 목록 -->
                        <div justify="center" v-if="assignments.length">
                            <div cols="5" md="7" v-for="assignment in assignments" :key="assignment.id"
                                class="text-left">
                                <div class="box-style assignment-item">
                                    <v-row @click="viewAssignmentOpen(assignment.id)">
                                        <v-col>
                                            <span
                                                v-if="daysRemaining(assignment.endDate) > 0 && daysRemaining(assignment.endDate) <= 3"
                                                style="display: inline-block; background-color: #FD6C6C; color: #fff; font-weight: bold; padding: 5px 10px; border-radius: 15px;">
                                                마감 {{ daysRemaining(assignment.endDate) }}일 전
                                            </span>
                                            <span style="font-size: 20px; font-weight: 700; margin-left: 10px;">{{
                                                assignment.title }}</span>
                                        </v-col>
                                        <v-col cols="3">
                                            <span style="font-size: 17px; right: 0;">
                                                기간: {{ assignment.startDate }}~{{ assignment.endDate }}
                                            </span>
                                        </v-col>
                                    </v-row>
                                </div>
                            </div>
                        </div>

                        <!-- 과제가 없을 경우 표시할 내용 -->
                        <v-row justify="center" v-else>
                            <v-col cols="12" class="text-center">
                                <div class="box-style" min-height="200px" width="80%">
                                    <span style="font-size: 20pxl">등록된 과제가 없습니다</span>
                                </div>
                            </v-col>
                        </v-row>
                        <v-pagination v-model="assignmentPage" :length="assignmentPages"
                            @click="handleAssignmentPageChange()" style="margin-top:100px"></v-pagination>

                    </v-card-text>
                </v-container>

            </v-tabs-window-item>
            <!-- 게시글 리스트 -->
            <v-tabs-window-item value="notice">
                <v-container style="width: 70vw; min-height: 500px;">
                    <v-row>
                        <v-col>
                            <v-btn variant="outlined" elevation="0" rounded @click="onlyNoticeClick()">
                                {{ onlyNotice ? "전체" : "공지사항만" }}
                            </v-btn>
                        </v-col>

                        <v-col>
                            <v-btn rounded color="#6C97FD" style="font-weight: bold" @click="noticeCreateModal = true">
                                글 작성
                                <!-- <v-icon>mdi-plus</v-icon> -->
                            </v-btn>
                        </v-col>


                    </v-row>
                    <v-row justify="center">
                        <v-col cols="8">
                            <v-row class="header">
                                <v-col cols="2">작성자</v-col>
                                <v-col cols="2">분류</v-col>
                                <v-col cols="5">제목</v-col>
                                <v-col cols="3">작성 일자</v-col>
                            </v-row>
                        </v-col>
                    </v-row>

                    <!-- 공지사항 리스트 -->
                    <v-row justify="center">
                        <v-col cols="8" v-if="notices.length">
                            <v-row v-for="(notice) in notices" :key="notice.id" @click="noticeView(notice)"
                                class="list-item" style="margin: 2px 0">
                                <v-col cols="2">{{ notice.memberName }}</v-col>
                                <v-col cols="2">{{ changePostType(notice.type) }}</v-col>
                                <v-col cols="5">{{ notice.title }}</v-col>
                                <v-col cols="3">{{ notice.postDate }}</v-col>
                            </v-row>
                        </v-col>

                        <!-- 공지사항이 없을 때 표시 -->
                        <v-col cols="8" v-else class="text-center">
                            <span style="font-size: 20px; color: #666; padding: 50px;">등록된 공지사항이 없습니다</span>
                        </v-col>
                    </v-row>

                    <v-pagination v-model="noticePage" :length="noticePages" @click="handleNoticePageChange()"
                        style="margin-top:100px"></v-pagination>
                </v-container>
            </v-tabs-window-item>
            <!-- 튜티 리스트 탭 -->
            <v-tabs-window-item value="tuteeList" v-if="this.isLecture && this.istutor">
                <v-container style="width: 50vw; min-height: 500px;">
                    <v-card-text>
                        <!-- 튜티 리스트 -->
                        <v-list class="mx-auto" style="overflow: hidden;">
                            <!-- 튜티가 있을 경우 리스트 출력 -->
                            <template v-if="tutees.length">
                                <v-list-item v-for="tutee in tutees" :key="tutee.id" justify="center"
                                    class="box-style pa-1 mx-auto d-flex " rounded="lg"
                                    style="align-items: center; justify-content: flex-start; padding: 12px; border-radius: 20px; margin-bottom: 10px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); display: flex;"
                                    variant="outlined">

                                    <v-row>
                                        <v-col>
                                            <v-list-item-avatar>
                                                <v-avatar size="50">
                                                    <v-img :src="tutee.tuteeProfile" :alt="tutee.name" />
                                                </v-avatar>
                                            </v-list-item-avatar>
                                        </v-col>
                                        <v-col class="d-flex justify-center" style="align-items: center;">
                                            <v-list-item-content>
                                                <v-list-item-title class="tutee-name"
                                                    style="font-weight:400; text-align: center;">
                                                    <h5>{{ tutee.tuteeName }}</h5>
                                                </v-list-item-title>
                                            </v-list-item-content>
                                        </v-col>
                                    </v-row>
                                </v-list-item>
                            </template>

                            <!-- 튜티가 없을 경우 메시지 출력 -->
                            <v-row v-else justify="center">
                                <v-col cols="12" class="text-center">
                                    <h3>등록된 튜티가 없습니다</h3>
                                </v-col>
                            </v-row>
                        </v-list>
                    </v-card-text>
                </v-container>
            </v-tabs-window-item>
        </v-tabs-window>

        <!-- 과제 모달 -->
        <v-dialog v-model="assignmentCreateModal" max-width="800px">
            <v-card style="border-radius: 20px;">
                <!-- <v-card-title class="text-h4 pa-4 d-flex justify-center">
                    과제 생성
                </v-card-title> -->
                <v-divider class="mt-10 mb-2"></v-divider>
                <v-card-text class="pa-4 pt-0 mt-5">
                    <h4 class="mb-1 ml-6 mr-2"> <strong>과제 생성</strong> </h4>
                    <h6 class="mt-6 mb-1 ml-6 mr-2"> <strong>제출 일자</strong> </h6>
                    <input class="mb-2 ml-6 mr-6 mt-3" v-model="assignmentDate" type="datetime-local" outlined>
                    <v-text-field v-model="assignmentTitle" type="text" rounded="xs" variant="outlined"
                        class="mb-2 ml-6 mr-6 mt-6" placeholder="제목을 입력해주세요" label="제목"></v-text-field>
                    <v-textarea v-model="assignmentContent" variant="outlined" rows="5" class="mb-2 ml-6 mr-6 mt-3"
                        placeholder="제목을 입력해주세요" label="내용"></v-textarea>
                </v-card-text>
                <v-card-actions class="pa-4">
                    <v-row justify="center">
                        <v-btn variant="outlined" rounded @click="renewAssignment()"
                            class="cancel-btn mr-3">취소하기</v-btn>
                        <v-btn variant="outlined" rounded @click="submitAssignmentCreate()"
                            class="submit-btn">등록하기</v-btn>
                    </v-row>
                </v-card-actions>
                <v-divider class="mt-2 mb-10"></v-divider>

            </v-card>
        </v-dialog>
        <v-dialog v-model="assignmentViewModal" max-width="800px">
            <v-card style="border-radius: 20px; padding: 20px 30px;" min-height="600px">
                <v-card-text style="padding: 30px 40px;">
                    <v-row class="justify-space-between mt-5 mb-5">
                        <div class="mb-1 ml-8 mr-2" style="font-size: 24px; font-weight: 700;">{{ this.assignmentTitle
                            }}</div>
                        <v-icon v-if="this.istutor" class="mr-5" style="font-size: 30px;"
                            @click="drawer = !drawer">mdi-dots-horizontal-circle-outline
                        </v-icon>
                        <v-navigation-drawer location="right" v-if="drawer" v-model="drawer" temporary>
                            <v-list nav>
                                <v-list-item prepend-icon="mdi-pencil" title="수정"
                                    @click="assignmentUpdateModal = true; drawer = !drawer; assignmentViewModal = false;">
                                </v-list-item>
                                <v-list-item prepend-icon="mdi-delete" title="삭제"
                                    @click="deleteAssignments(); drawer = !drawer;">
                                </v-list-item>
                            </v-list>
                        </v-navigation-drawer>
                    </v-row>

                    <v-divider></v-divider>

                    <div class="mb-2 ml-8 mr-2" style="font-size: 20px;">{{ this.assignmentContent }}</div>

                    <div class="mb-1 ml-8 mr-2"
                        style="font-size: 20px; font-weight: 700; color: #6C97FD; margin-top: 30px;">
                        ~
                        {{ this.assignmentDate?.split('T')[0] }}
                        {{ this.assignmentDate?.split('T')[1] }} 까지
                    </div>

                </v-card-text>
                <v-card-actions class="pa-4">
                    <v-row justify="center">
                        <v-btn variant="outlined" @click="renewAssignment()" class="mr-3">확인</v-btn>
                    </v-row>
                </v-card-actions>

            </v-card>
        </v-dialog>
        <v-dialog v-model="assignmentUpdateModal" max-width="800px">
            <v-card style="border-radius: 20px;">
                <v-divider class="mt-10 mb-2"></v-divider>

                <v-card-text class="pa-4 pt-0 mt-5">
                    <h4 class="mb-1 ml-6 mr-2"> <strong>과제 수정</strong> </h4>
                    <!-- 제목  -->
                    <h6 class="mt-6 mb-1 ml-6 mr-2"> <strong>제출 일자</strong> </h6>
                    <input class="mb-2 ml-6 mr-6 mt-3" v-model="assignmentDate" type="datetime-local" outlined>
                    <v-text-field v-model="assignmentTitle" type="text" rounded="xs" variant="outlined"
                        class="mb-2 ml-6 mr-6 mt-6" placeholder="제목을 입력해주세요" label="제목"></v-text-field>
                    <v-textarea v-model="assignmentContent" variant="outlined" rows="5" class="mb-2 ml-6 mr-6 mt-3"
                        placeholder="내용을 입력해주세요" label="내용"></v-textarea>
                </v-card-text>
                <v-card-actions class="pa-4">
                    <v-row justify="center" class="flex">
                        <v-btn variant="outlined" rounded class="cancel-btn mr-3"
                            @click="renewAssignment()">취소하기</v-btn>
                        <v-btn variant="outlined" rounded class="submit-btn" @click="updateAssignment()">등록하기</v-btn>
                    </v-row>
                </v-card-actions>
                <v-divider class="mt-2 mb-10"></v-divider>

            </v-card>
        </v-dialog>


        <!-- 공지사항 모달-->
        <v-dialog v-model="noticeCreateModal" max-width="800px">
            <v-card style="border-radius: 20px;">
                <v-divider class="mt-10 mb-2"></v-divider>

                <v-card-text class="pa-4 pt-0 mt-5">
                    <h5 class="mt-6 mb-1 ml-6 mr-2"> <strong>게시판 작성</strong> </h5>
                    <h6 class="mt-6 mb-1 ml-8 mr-2"> <strong>종류</strong> </h6>
                    <v-radio-group v-model="isNotice" class="mb-2 ml-8" hide-details>
                        <v-radio label="공지사항으로 등록" :value="true" v-if="istutor"></v-radio>
                        <v-radio label="게시글로 등록" :value="false"></v-radio>
                    </v-radio-group>
                    <v-text-field v-model="noticeTitle" placeholder="제목을 입력해주세요" label="제목" type="text" rounded="xs"
                        variant="outlined" class="mb-2 ml-8 mr-2"></v-text-field>
                    <v-textarea v-model="noticeContent" placeholder="내용을 입력해주세요" label="내용" variant="outlined" rows="5"
                        class="mb-2 ml-8 mr-2"></v-textarea>
                </v-card-text>
                <v-card-actions class="pa-4">
                    <v-row justify="center">
                        <v-btn variant="outlined" @click="noticeCreateModal = false" rounded
                            class="cancel-btn mr-3">취소하기</v-btn>
                        <v-btn rounded variant="outlined" class="submit-btn" @click="noticeCreate()">등록하기</v-btn>
                    </v-row>
                </v-card-actions>
                <v-divider class="mt-2 mb-10"></v-divider>

            </v-card>
        </v-dialog>
        <v-dialog v-model="noticeUpdateModal" max-width="800px">
            <v-card style="border-radius: 20px;">
                <v-divider class="mt-10 mb-2"></v-divider>
                <v-card-text class="pa-4 pt-0">
                    <h5 class="mt-6 mb-1 ml-6 mr-2"> <strong>게시판 수정</strong> </h5>

                    <h6 class="mt-6 mb-1 ml-8 mr-2"> <strong>종류</strong> </h6>
                    <v-radio-group v-model="isNotice" class="mb-2 ml-8" hide-details>
                        <v-radio label="공지사항으로 등록" :value="true" v-if="istutor"></v-radio>
                        <v-radio label="게시글로 등록" :value="false"></v-radio>
                    </v-radio-group>
                    <!-- 제목  -->
                    <!-- <h6 class="mt-6 mb-4 ml-8 mr-2"> <strong>내용</strong> </h6> -->
                    <v-text-field v-model="noticeTitle" placeholder="제목을 입력해주세요" label="제목" type="text" rounded="xs"
                        variant="outlined" class="mb-2 ml-8 mr-2"></v-text-field>
                    <v-textarea v-model="noticeContent" placeholder="내용을 입력해주세요" label="내용" variant="outlined" rows="5"
                        class="mb-2 ml-8 mr-2"></v-textarea>
                </v-card-text>
                <v-card-actions class="pa-4">
                    <v-row justify="center">
                        <v-btn variant="outlined" rounded @click="renewNotice()"
                            class="cancel-btn mr-3"><strong>취소하기</strong></v-btn>
                        <v-btn variant="outlined" rounded @click="submitEditNotice()"
                            class="submit-btn"><strong>수정하기</strong></v-btn>
                    </v-row>
                </v-card-actions>
                <v-divider class="mt-2 mb-10"></v-divider>

            </v-card>
        </v-dialog>
        <v-dialog v-model="noticeViewModal" max-width="70vw">
            <v-card style="border-radius: 20px;" min-height="800px">
                <!-- <v-card-text class="pa-4 pt-0"> -->
                <v-row>
                    <v-col cols="7">
                        <v-row class="justify-space-between">
                            <v-row style="font-size: 24px; font-weight: 700; margin: 40px 50px 0">
                                {{ this.noticeTitle }}
                            </v-row>
                            <v-icon v-if="isAuthor" style="margin: 40px 10px 0"
                                @click="noticeDrawer = !noticeDrawer">mdi-dots-horizontal-circle-outline
                            </v-icon>
                            <v-navigation-drawer location="right" v-if="noticeDrawer" v-model="noticeDrawer" temporary>
                                <v-list density="compact" nav>
                                    <v-list-item prepend-icon="mdi-pencil" title="수정"
                                        @click="noticeUpdateModal = true; noticeViewModal = false; noticeDrawer = !noticeDrawer;">
                                    </v-list-item>
                                    <v-list-item prepend-icon="mdi-delete" title="삭제"
                                        @click="deleteItem()"></v-list-item>
                                </v-list>
                            </v-navigation-drawer>
                        </v-row>
                        <v-divider class="mt-10 mb-2"></v-divider>
                        <v-row style="margin: 20px 40px; font-size: 20px;">
                            {{ this.noticeContent }}
                        </v-row>
                    </v-col>
                    <v-col>

                        <!-- 댓글 리스트 -->
                        <v-row>
                            <h4 style="font-weight: 700; margin: 40px 0 30px; padding: 0 20px">댓글</h4>
                        </v-row>
                        <!-- 댓글 입력 폼 -->
                        <v-row style="margin: 20px 10px 50px 0">
                            <v-textarea v-model="newComment" placeholder="댓글을 입력하세요" dense rows="1" auto-grow
                                hide-details style="max-height: 36px; margin-right: 8px;"></v-textarea>
                            <v-btn @click="submitComment" color="primary" height="40px">
                                등록
                            </v-btn>
                        </v-row>
                        <hr>
                        <v-row v-if="comments.length === 0" justify="center">
                            <div style="font-size: 18px; color: #999; margin: 30px 0">
                                댓글이 없습니다
                            </div>

                        </v-row>
                        <div v-else>
                            <v-row v-for="comment in comments" :key="comment.id">
                                <div
                                    style="background-color: #F2F2F2; margin: 20px 20px 10px 10px; width: 75%; padding: 10px 20px; font-size: 18px; border-radius: 10px;">
                                    <strong>{{ comment.memberName }}</strong><br>
                                    <span>{{ comment.contents }}</span>
                                </div>
                                <div style="margin: 20px 20px 10px 10px; display: flex; align-items: center;">
                                    <v-icon @click="editComment(comment)" class="mr-1">mdi-pencil</v-icon>
                                    <v-icon @click="deleteComment(comment)">mdi-delete</v-icon>
                                </div>
                            </v-row>
                            <v-pagination v-model="commentPage" :length="commentPages"
                                @click="handleCommentPageChange()"></v-pagination>
                        </div>
                    </v-col>
                </v-row>

                <!-- </v-card-text> -->
                <v-card-actions class="pa-4">
                    <v-row justify="center">
                        <v-btn variant="outlined" @click="renewNotice()" class="mr-3">닫기</v-btn>
                    </v-row>
                </v-card-actions>

            </v-card>
        </v-dialog>
        <AlertModal v-model="alertModal" @update:dialog="alertModal = $event" icon=mdi-alert-circle-outline
            :title=this.alertModalTitle :contents=this.alertModalContents />
    </v-container>
</template>

<script>
/* global kakao */
import LectureInfoCard from './LectureInfoCard.vue';
// import LectureTabs from './LectureTabs.vue';
import axios from 'axios';
import { useRoute } from 'vue-router';
import DoughnutChart from './DoughnutChart.vue';
import AlertModal from "@/components/AlertModal.vue";

export default {
    components: {
        LectureInfoCard,
        AlertModal,
        DoughnutChart
    },
    data() {
        return {
            drawer: false,
            topNotice: [],
            currentNotice: 0,
            isShowMap: false,
            istutor: false,
            page: 0,
            size: 5,
            noticePageSize: 10,
            noticePages: 0,
            noticePage: 1,
            assignmentPages: 0,
            assignmentPage: 1,
            commentPage: 0,
            commentPages: 1,
            infoData: {
                category: "",
                chatRoomId: null,
                contents: "",
                image: "",
                limitPeople: null,
                address: "",
                detailAddress: "",
                price: 0,
                memberName: "",
                startDate: "",
                endData: "",
                title: "",
                lectureGroupTimes: [],
                totalDayCount: 0,
                pastDayCount: 0,
                tuteeName: "",
            },
            isTuteeExist: false,
            notice: [],
            noticeTitle: "",
            noticeContent: "",
            noticeId: null,
            comments: [], // 댓글 리스트
            newComment: '', // 새 댓글
            commentId: null, // 수정 중인 댓글 ID

            tab: 0,
            lectureSchedules: "",
            lectureGroupId: 0,
            assignmentCreateModal: false,
            assignmentUpdateModal: false,
            assignmentViewModal: false,
            noticeCreateModal: false,
            noticeUpdateModal: false,
            noticeViewModal: false,
            isNotice: false,
            breadItems: [
                {
                    title: '',
                    disabled: false,
                    href: 'breadcrumbs_dashboard',
                },

                {
                    title: '',
                    disabled: true,
                    href: 'breadcrumbs_link_2',
                },
            ],

            assignments: [],
            assignmentTitle: "",
            assignmentDate: null,
            assignmentContent: "",
            assignmentId: null,
            notices: [],
            tutees: [],
            isLecture: false,
            isKakaoScriptLoaded: false,
            mapModal: false,
            urgentAssignment: [],
            isDataLoaded: false,
            isCommentEdit: false,
            noticeDrawer: false,
            isAuthor: false,
            onlyNotice: false,
            menu: false,
            selectOptions: ["전체 보기", "공지사항만 보기"],
            alertModal: false,
            alertModalTitle: "",
            alertModalContents: "",
        };
    },
    mounted() {
        this.startSlider();
    },
    async created() {
        const route = useRoute();
        this.lectureGroupId = route.params.lectureGroupId;
        try {
            const infoGetResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture-group-home/${this.lectureGroupId}`)
            const data = infoGetResponse?.data?.result;
            this.infoData.category = this.changeCategory(data.category);
            this.isLecture = data.lectureType === "LECTURE" ? true : false;
            this.breadItems[0].title = this.isLecture ? "강의" : "과외";
            this.infoData.contents = data.contents;
            this.infoData.image = data.image;
            this.infoData.limitPeople = data.limitPeople;
            this.infoData.price = data.price;
            this.infoData.memberName = data.memberName;
            this.infoData.startDate = data.startDate;
            this.infoData.endDate = data.endDate;
            this.infoData.title = data.title;
            this.infoData.address = data.address;
            this.infoData.detailAddress = data.detailAddress;
            this.infoData.totalDayCount = data.totalDayCount;
            this.infoData.pastDayCount = data.pastDayCount;
            this.isDataLoaded = true;
            this.breadItems[1].title = this.infoData.title;
            this.infoData.lectureGroupTimes = data.lectureGroupTimes;
            this.lectureSchedules = this.infoData.lectureGroupTimes.reduce((acc, cur) => {
                return acc + `<div>• ${this.changeDay(cur.lectureDay)} ${cur.startTime} ~ ${cur.endTime}</div>`;
            }, '');

            const tuteesResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture-tutee-list/${this.lectureGroupId}`);
            this.tutees = tuteesResponse?.data?.result?.content;
            if (this.tutees.length > 0) this.isTuteeExist = true;

            let params = { size: this.noticePageSize, page: this.page };
            if (this.onlyNotice) params.type = 'notice';

            const noticeResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/board/list`, { params });
            this.notices = noticeResponse?.data?.result?.content;
            this.noticePages = noticeResponse?.data?.result?.totalPages;

            const topNoticeResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/board/list?page=0&type=notice`);
            this.topNotice = topNoticeResponse?.data?.result?.content;

            const urgentAssignmentResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/assignment?page=0&isDashBoard=Y&size=5`);
            this.urgentAssignment = urgentAssignmentResponse?.data?.result?.content;

            const assignmentResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/assignment`, { params });
            this.assignments = assignmentResponse?.data?.result?.content;
            this.assignmentPages = assignmentResponse?.data?.result?.totalPages;

            const memberInfo = localStorage.getItem('memberInfo');
            if (memberInfo && JSON.parse(memberInfo).name === this.infoData.memberName) this.istutor = true;
            if (this.infoData.address !== "") this.isShowMap = true;

            if (!this.isLecture && this.istutor && this.tutees.length) {
                const body = { lectureGroupId: this.lectureGroupId, tuteeId: this.tutees[0].memberId };
                const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/tutor-lesson-chat-check-or-create`, body);
                this.infoData.chatRoomId = response.data.result.roomId;
            } else if (!this.isLecture && !this.istutor) {
                const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/tutee-room-check-or-create?lectureGroupId=${this.lectureGroupId}`);
                this.infoData.chatRoomId = response.data.result.roomId;
            }
            else {
                this.infoData.chatRoomId = data.chatRoomId;
            }
            
            if (!this.isLecture && this.tutees.length > 0) this.infoData.tuteeName = this.tutees[0].tuteeName;
        } catch (e) {
            this.alertModalContents = e?.response?.data?.error_message || '오류가 발생했습니다.';
            this.alertModal = true;
        }

    },

    methods: {
        // 마감일까지 남은 일수를 계산하는 메소드
        daysRemaining(endDate) {
            const today = new Date();
            const end = new Date(endDate);
            const diffTime = end - today;
            return Math.ceil(diffTime / (1000 * 60 * 60 * 24)); // 밀리초를 일수로 변환
        },
        startSlider() {
            setInterval(() => {
                this.currentNotice = (this.currentNotice + 1) % this.topNotice.length;
            }, 5000);
        },
        showMap() {
            this.execDaumPostcode();
            this.mapModal = true;
        },
        setNoticeView(isNoticeOnly) {
            this.onlyNotice = isNoticeOnly;  // 공지사항만 보기 상태 설정
            this.menu = false;  // 메뉴 닫기
            this.onlyNoticeClick();  // 공지사항 또는 전체 데이터를 불러오는 로직
        },
        async onlyNoticeClick() {
            try {
                let params = { size: this.noticePageSize, page: this.page };
                if (this.onlyNotice) params.type = 'notice';

                const noticeResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/board/list`, { params });
                this.notices = noticeResponse?.data?.result?.content;
                this.noticePages = noticeResponse?.data?.result?.totalPages;
            } catch (e) {
                this.alertModalContents = e?.response?.data?.error_message || '오류가 발생했습니다.';
                this.alertModal = true;
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
        async handleCommentPageChange() {
            this.page = this.commentPage - 1;
            let params = {
                size: this.size,
                page: this.page,
            };
            const commentResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/board/${this.noticeId}/comment/list`, { params })
            this.comments = commentResponse?.data?.result?.content;
            this.commentPages = commentResponse?.data?.result?.totalPages;
        },
        async handleNoticePageChange() {
            try {
                this.page = this.noticePage - 1;
                let params = { size: this.noticePageSize, page: this.page };
                if (this.onlyNotice) params.type = 'notice';
                const noticeResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/board/list`, { params });
                this.notices = noticeResponse?.data?.result?.content;
                this.commentPages = noticeResponse?.data?.result?.totalPages;
            } catch (e) {
                this.alertModalContents = e?.response?.data?.error_message || '오류가 발생했습니다.';
                this.alertModal = true;
            }
        },
        async handleAssignmentPageChange() {
            this.page = this.assignmentPage - 1;
            let params = {
                size: this.size,
                page: this.page,
            };
            const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/assignment`, { params })
            this.assignments = response?.data?.result?.content;
            this.assignmentPages = response?.data?.result?.totalPages;
        },
        async noticeView(item) {
            this.renewNotice();
            const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/board/${item.id}`);
            this.noticeTitle = response?.data?.result?.title;
            this.noticeContent = response?.data?.result?.contents;
            this.noticeId = item.id;
            this.isAuthor = response?.data?.result?.author
            if (response?.data?.result?.type === "POST") this.isNotice = false;
            else this.isNotice = true;
            // 댓글 목록 가져오기
            await this.fetchComments(item.id);
            this.noticeViewModal = true;

        },
        async fetchComments(noticeId) {
            try {
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/board/${noticeId}/comment/list?page=0`);
                this.commentPages = response?.data?.result?.totalPages;
                this.comments = response?.data?.result?.content || [];
            } catch (e) {
                this.alertModalContents = e?.response?.data?.error_message || '오류가 발생했습니다.';
                this.alertModal = true;
            }
        },
        async submitComment() {
            try {
                const body = { contents: this.newComment };
                if (this.commentId) {
                    await axios.put(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/board/comment/${this.commentId}`, body);
                } else {
                    await axios.post(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/board/${this.noticeId}/comment/create`, body);
                }
                this.newComment = '';
                this.isCommentEdit = false;
                await this.fetchComments(this.noticeId);
            } catch (e) {
                this.alertModalContents = e?.response?.data?.error_message || '오류가 발생했습니다.';
                this.alertModal = true;
            }
        },
        async editComment(comment) {
            this.isCommentEdit = true;
            this.commentId = comment.id;
            this.newComment = comment.contents;
        },
        async deleteComment(comment) {
            try {
                this.commentId = comment.id;
                await axios.patch(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/board/comment/${this.commentId}/delete`);
                await this.fetchComments(this.noticeId);
            } catch (e) {
                this.alertModalContents = e?.response?.data?.error_message || '오류가 발생했습니다.';
                this.alertModal = true;
            }
        },
        async renewNotice() {
            this.isNotice = false;
            this.noticeTitle = "";
            this.noticeContent = "";
            this.noticeCreateModal = false;
            this.noticeUpdateModal = false;
            this.noticeViewModal = false;
            this.isAuthor = false;
            this.noticeId = null;
            const topNoticeResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/board/list?page=0&type=notice`)
            this.topNotice = topNoticeResponse?.data?.result?.content;

        },
        async noticeCreate() {
            try {
                const type = this.isNotice ? "NOTICE" : "POST";
                const body = { title: this.noticeTitle, contents: this.noticeContent, type };
                const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/board/create`, body);
                if (response) {
                    let params = { size: this.noticePageSize, page: this.page };
                    if (this.onlyNotice) params.type = 'notice';
                    const noticeResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/board/list`, { params });
                    this.notices = noticeResponse?.data?.result?.content;
                    this.noticeCreateModal = false;
                    this.renewNotice();
                }
            } catch (e) {
                this.alertModalContents = e?.response?.data?.error_message || '오류가 발생했습니다.';
                this.alertModal = true;
            }
        },
        // 과제 CRUD 메서드
        async renewAssignment() {
            this.assignmentCreateModal = false;
            this.assignmentUpdateModal = false;
            this.assignmentViewModal = false;
            this.assignmentTitle = "";
            this.assignmentDate = null;
            this.assignmentContent = "";
            this.assignmentId = null;
            const urgentAssignmentResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/assignment?page=0&isDashBoard=Y&size=5`);
            this.urgentAssignment = urgentAssignmentResponse?.data?.result?.content;
            const assignmentResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/assignment`)
            this.assignments = assignmentResponse?.data?.result?.content;
            this.assignmentPages = assignmentResponse?.data?.result?.totalPages;

        },
        async submitAssignmentCreate() {
            try {
                const [endDate, endTime] = this.assignmentDate.split('T');
                const body = { title: this.assignmentTitle, contents: this.assignmentContent, endDate, endTime };
                await axios.post(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/assignment/create`, body);
                this.assignmentCreateModal = false;
                this.renewAssignment();
                this.handleAssignmentPageChange();
            } catch (e) {
                this.alertModalContents = e?.response?.data?.error_message || '오류가 발생했습니다.';
                this.alertModal = true;
            }
        },
        async viewAssignmentOpen(id) {
            try {
                const getResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/assignment/${id}`);
                this.assignmentViewModal = true;
                this.assignmentTitle = getResponse?.data?.result?.title;
                this.assignmentContent = getResponse?.data?.result?.contents;
                this.assignmentDate = getResponse?.data?.result?.endDate + 'T' + getResponse?.data?.result?.endTime;
                this.assignmentId = getResponse?.data?.result?.id;
            } catch (e) {
                this.alertModalContents = e?.response?.data?.error_message || '오류가 발생했습니다.';
                this.alertModal = true;
            }
        },
        async updateAssignmentOpen(id) {
            const getResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/assignment/${id}`);
            this.assignmentUpdateModal = true;

            this.assignmentTitle = getResponse?.data?.result?.title;
            this.assignmentContent = getResponse?.data?.result?.contents;
            this.assignmentDate = getResponse?.data?.result?.endDate + 'T' + getResponse?.data?.result?.endTime;
            this.assignmentId = getResponse?.data?.result?.id;
        },
        async updateAssignment() {
            try {
                const [endDate, endTime] = this.assignmentDate.split('T');
                const body = { title: this.assignmentTitle, contents: this.assignmentContent, endDate, endTime };
                const response = await axios.put(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/assignment/${this.assignmentId}/update`, body);
                if (response) {
                    await this.fetchAssignments();
                    this.renewAssignment();
                    this.assignmentUpdateModal = false;
                }
            } catch (e) {
                this.alertModalContents = e?.response?.data?.error_message || '오류가 발생했습니다.';
                this.alertModal = true;
            }
        },
        // todo : update reload 잘 해보기
        async fetchAssignments() {
            const assignmentResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/assignment`)
            this.assignments = assignmentResponse?.data?.result?.content;
        },
        async deleteAssignments() {
            try {
                const response = await axios.patch(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/assignment/${this.assignmentId}/delete`);
                if (response) {
                    await this.fetchAssignments();
                    this.renewAssignment();
                    this.assignmentUpdateModal = false;
                }
            } catch (e) {
                this.alertModalContents = e?.response?.data?.error_message || '오류가 발생했습니다.';
                this.alertModal = true;
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
        changeCategory(category) {
            switch (category) {
                case 'CAREER':
                    return '취업';
                case 'ADMISSION':
                    return '입시';
                case 'HOBBY':
                    return '취미';
                case 'DEVELOPMENT':
                    return '자기계발';
            }
        },
        clickChatRoom() {
            console.log("채팅방 입장" + this.infoData.chatRoomId);
        },
        getitemcontrols() {
            return `item.actions`;
        },
        async editItem(item) {
            const getResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/board/${item.id}`);
            console.log(getResponse)
            this.noticeTitle = getResponse?.data?.result?.title;
            this.noticeContent = getResponse?.data?.result?.contents;
            if (getResponse?.data?.result?.type === "POST") this.isNotice = false;
            else this.isNotice = true;
            this.noticeUpdateModal = true;
            this.noticeId = item.id;

        },
        async submitEditNotice() {
            let type = null;
            if (this.isNotice) type = "NOTICE";
            else type = "POST"
            const body = {
                title: this.noticeTitle,
                contents: this.noticeContent,
                type
            }
            const response = await axios.put(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/board/${this.noticeId}`, body)
            if (response) {
                let params = {
                    size: this.noticePageSize,
                    page: this.page,
                };
                if (this.onlyNotice) {
                    params.type = 'notice';
                }
                const noticeResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/board/list`, { params })
                this.notices = noticeResponse?.data?.result?.content;
                this.noticeUpdateModal = false;
            }
            this.renewNotice();

        },
        async deleteItem() {
            const response = await axios.patch(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/board/${this.noticeId}/delete`)
            if (response) {
                let params = {
                    size: this.noticePageSize,
                    page: this.page,
                };
                if (this.onlyNotice) {
                    params.type = 'notice';
                }
                const noticeResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/${this.lectureGroupId}/board/list`, { params })
                this.notices = noticeResponse?.data?.result?.content;
            }
            this.renewNotice();
        },
        changePostType(type) {
            switch (type) {
                case 'POST':
                    return '게시글';
                case 'NOTICE':
                    return '화요일';
            }

        },
    }

};
</script>
<style scoped>
#map {
    width: 100%;
    height: 150px;
}

.header {
    font-weight: bold;
    border-bottom: 2px solid #ccc;
}

.text-left {
    text-align: left;
}

.tutee-list-item {
    border-radius: 20px;
    margin-bottom: 10px;
    padding: 10px;
    display: flex;
    align-items: center;
    /* 이미지와 텍스트를 같은 선상에 배치 */
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
    /* 그림자 추가 */
}

.v-list-item-avatar {
    margin-right: 10px;
}

.v-container {
    padding: 0 !important;
}

.noticeBanner {
    background-color: #D9D9D9;
    color: white;
    text-align: left;
}

.chartDoughnut {
    max-width: 300px;
    min-height: 300px;
    /* 차트 크기 조정 */
    margin: 0 auto;
}

.notice-slider {
    height: 70px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #222;
    font-size: 17px;
    color: #fff;
}

.cancel-btn {
    background-color: rgb(255, 94, 94);
    color: white;
    border-color: rgb(255, 94, 94);
    width: 40%;
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
    width: 40%;
    height: 40px;
}

.active {
    background-color: #90CDFF !important;
}

.inactive {
    background-color: #CCCCCC !important;
    /* 회색 */
}

.box-style {
    border: 2px solid #f0efef;
    border-radius: 10px;
    padding: 30px 5px;
    margin-top: 20px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.assignment-item {
    padding: 30px;
    transition: all 0.3s ease;
}

.assignment-item:hover {
    transform: scale(1.03);
    cursor: pointer;
}

.list-item:hover {
    background-color: #e9e9e9;
    border-radius: 5px;
    cursor: pointer;
}
</style>
