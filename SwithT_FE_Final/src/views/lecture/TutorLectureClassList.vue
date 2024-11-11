<template>
    <LectureDetailInfoComponent 
        :lectureId=this.lectureId
        />
    <v-container width="70vw"  style="margin-top: 60px;">

        <v-row>
            <v-col cols="6">
                <div>
                    <div v-for="(group, index) in lectureGroupList" :key="group.lectureGroupId">
                        <div class="lecture-group-info">
                            <v-row>
                                <v-col>
                                    <div align="left" justify="center">
                                        <span class="tag" v-if="getLectureGroupStatus(group)=='종료'" style="text-align:center; background-color:grey;" >
                                            {{ getLectureGroupStatus(group) }}
                                        </span>
                                        <span class="tag" v-else style="text-align:center;" >
                                            {{ getLectureGroupStatus(group) }}
                                        </span>
                                        <span style="font-size: 20px; font-weight: 700;">그룹 {{index + 1}}</span>
                                    </div>
                                </v-col>
                                <v-col>
                                    <div align="right" justify="center">
                                        <span v-if="this.lectureType === 'LESSON' && group.isAvailable === 'Y'" class="material-icons icon-btn" style="font-size: 40px;" @click="clickLessonApplyList(group.lectureGroupId, this.lectureType)">
                                            groups
                                        </span>
                                        <span class="material-icons icon-btn"  style="font-size: 40px;" @click="clickLectureHome(group.lectureGroupId)">home</span>
                                    </div>
                                </v-col>
                            </v-row>
                            
                            <div class="group-time">
                                <div v-for="time in group.groupTimes" :key="time.groupTimeId">
                                    <strong>{{ convertDayToKorean(time.lectureDay) }}</strong> {{ formatTime(time.startTime) }}~{{ formatTime(time.endTime) }}
                                </div>
                            </div>
                                <!-- 조건에 따른 정보 표시 -->
                            <template v-if="showAdditionalInfo(group)">  
                                <div style="margin: 5px; text-align: right; font-size: 15px; color: #999;">
                                    기간: {{ group.startDate }} ~ {{ group.endDate || '미정' }}
                                </div>
                            </template>
                        </div>
                        

                        
                    </div>
                </div>
            </v-col>
            <v-col cols="6">
                <v-row>
                    <div class="list-title">최신 게시글</div>
                    <div class="list-style">
                        
                        <v-row>
                            <v-col cols="2"></v-col>
                            <v-col cols="5"><strong>제목</strong></v-col>
                            <v-col cols="2"><strong>작성자</strong></v-col>
                            <v-col cols="3"><strong>작성시간</strong></v-col>
                        </v-row>
                        <hr>
                        <span v-if="posts.length === 0" class="list-item" style="text-align: center; padding: 10px 0;">
                            게시글이 없습니다
                        </span>
                        <span v-for="(item, index) in posts" :key="item.id" class="list-item">
                            <v-row class="click-item" @click="viewNotice(item.id)">
                                <v-col cols="2" style="padding: 10px 0">{{ index + 1 }}</v-col>
                                <v-col cols="5" style="padding: 10px 0">{{ item.title }}</v-col>
                                <v-col cols="2" style="padding: 10px 0">{{ item.memberName || '알 수 없음' }}</v-col>
                                <v-col cols="3" style="padding: 10px 0">{{ formatRelativeTime(item.createdTime) }}</v-col>
                            </v-row>
                        </span>
                    </div>
                </v-row>
                <v-row>
                    <div class="list-title">곧 마감되는 과제</div>
                    <div class="list-style">
                        <v-row>
                            <v-col cols="2"></v-col>
                            <v-col cols="4"><strong>과제</strong></v-col>
                            <v-col cols="3"><strong>마감일</strong></v-col>
                            <v-col cols="3"><strong>마감시간</strong></v-col>
                        </v-row>
                        <hr>
                        <span v-if="assignments.length === 0" class="list-item" style="text-align: center; padding: 10px 0;">
                            과제가 없습니다
                        </span>
                        <span v-for="(item, index) in assignments" :key="item.id" class="list-item">
                            <v-row class="click-item" @click="viewAssignment(item.id)">
                                <v-col cols="2" style="padding: 10px 0">{{ index + 1 }}</v-col>
                                <v-col cols="4" style="padding: 10px 0">{{ item.title }}</v-col>
                                <v-col cols="3" style="padding: 10px 0">{{ item.endDate }}</v-col>
                                <v-col cols="3" style="padding: 10px 0">{{ item.endTime }}</v-col>
                            </v-row>
                          </span>
                    </div>
                </v-row>
            </v-col>
        </v-row>

        <!-- <v-card-text>
            <v-tabs-window v-model="tab">
                <v-tabs-window-item value="NOT_AVAILABLE_CLASS_LIST">
                    <v-row v-for="lesson in notAvailableClassList" :key="lesson.id">
                        <v-col>
                            <v-card class="custom-border">
                                <v-card-text class="d-flex justify-space-between  align-center">
                                    <v-col cols="10" class="text-left card-text-class">
                                        <div class="lecture-group-title">
                                            {{lesson.title}}
                                        </div>
                                        <br>
                                        <div class="lecture-group-info">
                                            <v-row >
                                                <v-col cols="1" class="lecture-group-info-col">튜티</v-col>
                                                <v-col class="lecture-group-info-col">{{lesson.memberName}}</v-col>
                                            </v-row>
                                            <v-row>
                                                <v-col cols="1" class="lecture-group-info-col">기간</v-col>
                                                <v-col class="lecture-group-info-col">{{lesson.startDate}} ~ {{lesson.endDate}}</v-col>
                                            </v-row>
                                            <v-row>
                                                <v-col cols="auto" class="lecture-group-info-col">강의료</v-col>
                                                <v-col class="lecture-group-info-col">{{formatPrice(lesson.price)}}</v-col>
                                            </v-row>
                                              
                                        </div>
                                    </v-col>
                                    <v-col cols="1" >
                                        <v-btn color="#6C97FD" style="font-size:18px;"  @click="clickLectureHome(lesson.lectureGroupId)" >강의 홈</v-btn>
                                    </v-col>
                                </v-card-text>
                            </v-card>
                        </v-col>
                    </v-row>
                </v-tabs-window-item>
                <v-tabs-window-item value="AVAILABLE_CLASS_LIST">
                    <v-row v-for="lesson in availableClassList" :key="lesson.id">
                        <v-col>
                            <v-card class="custom-border" style="height: 104px;">
                                <v-card-text class="d-flex justify-space-between  align-center">
                                    <v-col cols="10" class="text-left card-text-class">
                                        <div class="lecture-group-title">
                                            {{lesson.title}}
                                        </div>
                                        <br>
                                        <div class="lecture-group-info">
                                            <v-row>
                                                <v-col cols="auto" class="lecture-group-info-col">강의료</v-col>
                                                <v-col class="lecture-group-info-col">{{formatPrice(lesson.price)}}</v-col>
                                            </v-row>
                                        </div>
                                    </v-col>
                                    <v-col cols="1" >
                                        <v-btn color="#82D691" style="font-size:18px;" @click="clickLessonApplyList(lesson.lectureGroupId, lesson.title)"
                                        class="white-text" small>모집중</v-btn>
                                    </v-col>
                                </v-card-text>
                                
                            </v-card>
                        </v-col>
                    </v-row>
                </v-tabs-window-item>

                <v-tabs-window-item value="ALL_CLASS_LIST">
                    <v-row v-for="lesson in allClassList" :key="lesson.id">
                        <v-col>
                            <v-card class="custom-border">
                                <v-card-text class="d-flex justify-space-between  align-center">
                                    <v-col cols="10" class="text-left card-text-class">
                                        <div class="lecture-group-title">
                                            {{lesson.title}}
                                        </div>
                                        <br>
                                        
                                        <div class="lecture-group-info">
                                            <v-row >
                                                <v-col cols="2" class="lecture-group-info-col">모집인원</v-col>
                                                <v-col class="lecture-group-info-col">{{lesson.limitPeople}}</v-col>
                                            </v-row>
                                            <v-row>
                                                <v-col cols="2" class="lecture-group-info-col">기간</v-col>
                                                <v-col class="lecture-group-info-col">{{lesson.startDate}} ~ {{lesson.endDate}}</v-col>
                                            </v-row>
                                            
                                            
                                            <v-row>
                                                <v-col cols="2" class="lecture-group-info-col">강의료</v-col>
                                                <v-col class="lecture-group-info-col">{{formatPrice(lesson.price)}}</v-col>
                                            </v-row>
                                              
                                        </div>
                                    </v-col>
                                    <v-col cols="1" >
                                        <v-btn color="#6C97FD" style="font-size:18px;"  @click="clickLectureHome(lesson.lectureGroupId)" >강의 홈</v-btn>
                                    </v-col>
                                </v-card-text>
                            </v-card>
                        </v-col>
                    </v-row>
                </v-tabs-window-item>
            </v-tabs-window>
        </v-card-text> -->
    </v-container>
    <!-- 과제 조회 -->
    <v-dialog v-model="assignmentViewModal" max-width="800px">
        <v-card style="border-radius: 20px; padding: 20px 30px;" min-height="600px">
            <v-card-text style="padding: 30px 40px;">
                <v-row class="justify-space-between mt-5 mb-5">
                    <div class="mb-1 ml-8 mr-2" style="font-size: 24px; font-weight: 700;">{{ this.assignmentTitle }}</div>
                </v-row>
                <v-divider></v-divider>
               
                <div class="mb-2 ml-8 mr-2" style="font-size: 20px;">{{ this.assignmentContent }}</div>

                <div class="mb-1 ml-8 mr-2" style="font-size: 20px; font-weight: 700; color: #6C97FD; margin-top: 30px;">
                    ~
                    {{ this.assignmentDate }}
                    {{ this.assignmentTime }} 까지
                </div>
               
            </v-card-text>
            <v-card-actions class="pa-4">
                <v-row justify="center">
                    <v-btn variant="outlined" @click="assignmentViewModal=false" class="mr-3">확인</v-btn>
                </v-row>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <!-- 게시글 조회 -->
    <v-dialog v-model="noticeViewModal" max-width="75vw">
        <v-card style="border-radius: 20px;" min-height="800px">
            <!-- <v-card-text class="pa-4 pt-0"> -->
                <v-row>
                    <v-col cols="8">
                        <v-row class="justify-space-between mt-5 mb-5">
                            <v-row style="font-size: 24px; font-weight: 700; margin-left: 40px;">
                                {{ this.noticeTitle }}
                            </v-row>
                        </v-row>
                        <v-divider class="mt-10 mb-2"></v-divider>
                        <v-row class="mb-4 ml-7 mr-2 mt-2">
                            {{ this.noticeContent }}
                        </v-row>                        
                    </v-col>
                    <v-col>
                        <!-- 댓글 리스트 -->
                        <h4 class="mt-6 mb-1 ml-6 mr-2">댓글</h4>
                        <v-row v-for="comment in comments" :key="comment.id" class="mt-2 mr-2">
                            <div class="mb-4 ml-7 mr-2 mt-2 px-3">
                                <strong>{{ comment.memberName }}</strong>
                                <span class="ml-2"  style="white-space: pre-wrap; word-break: break-word;">{{ comment.contents }}</span>
                            </div>
                            <!-- <div class="align-center mt-1">
                                <v-icon @click="editComment(comment)" class="mr-1">mdi-pencil</v-icon>
                                <v-icon @click="deleteComment(comment)">mdi-delete</v-icon>
                            </div> -->
                        </v-row>
                        <v-pagination v-model="commentPage" :length="commentPages"
                            @click="handleCommentPageChange()" style="margin-top:100px"></v-pagination>
                        <!-- 댓글 입력 폼 -->
                        <!-- <h4 v-if="isCommentEdit" class="mt-6 mb-1 ml-6 mr-2">댓글 수정</h4>
                        <h4 class="mt-6 mb-1 ml-6 mr-2" v-else>댓글 작성 </h4>
                        <v-textarea class="mb-1 ml-6 mr-2" v-model="newComment" placeholder="댓글을 입력하세요"></v-textarea>
                        <v-btn class="ml-6" @click="submitComment">댓글 등록</v-btn> -->
                    </v-col>
                </v-row>

            <!-- </v-card-text> -->
            <v-card-actions class="pa-4">
                <v-row justify="center">
                    <v-btn variant="outlined" @click="noticeViewModal=false" class="mr-3">확인</v-btn>
                </v-row>
            </v-card-actions>

        </v-card>
    </v-dialog>
</template>

<script>
import axios from 'axios';
import LectureDetailInfoComponent from '@/components/LectureDetailInfoComponent.vue';
export default {
    components:{
        LectureDetailInfoComponent,
    },
    data() {
        return {
            lectureId: null,
            lectureGroupList: [],
            posts: [], // 게시글 리스트
            assignments: [], // 과제 리스트
            // tab: 'POSTS', // 탭 상태
            lectureType: '',
            lectureStatue: '',
            tuteeList: [],

            assignmentViewModal:false,
            assignmentTitle:"",
            assignmentContent:"",
            assignmentDate:"",
            assignmentTime:"",

            noticeViewModal:false,
            noticeTitle:"",
            noticeContent:"",
            noticeId:0,
            comments: [], // 댓글 리스트
            commentPage: 0,
            commentPages: 1,
            page:0,

        }
    },
    async created() {
            this.lectureId = this.$route.query.lectureId;
            this.lectureType = this.$route.query.lectureType;
            this.lectureStatus = this.$route.query.lectureStatus;

            console.log(this.lectureId, this.lectureType, this.lectureStatus);

            this.fetchLectureGroups();
            this.fetchPostsByLectureId();
            this.fetchAssignmentsByLectureId();   

    },
    mounted() {
        this.fetchLectureGroups();
    },
    methods: {
        async viewNotice(id){
            const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/board/${id}`);
            this.noticeTitle = response?.data?.result?.title;
            this.noticeContent = response?.data?.result?.contents;
            this.noticeId = id;
            this.fetchComments(id);
            this.noticeViewModal = true;
        },
        async fetchComments(noticeId) {
            const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/board/${noticeId}/comment/list?page=0`)
            this.commentPages = response?.data?.result?.totalPages;
            this.comments = response?.data?.result?.content || [];
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
        async viewAssignment(id){
            const getResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/assignment/${id}`);
            this.assignmentTitle = getResponse?.data?.result?.title;
            this.assignmentContent = getResponse?.data?.result?.contents;
            this.assignmentDate = getResponse?.data?.result?.endDate;
            this.assignmentTime= getResponse?.data?.result?.endTime;
            this.assignmentViewModal = true;
        },

        async fetchLectureGroups(){
            try {
                console.log(this.lectureId);
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture-group-info/${this.lectureId}`);
                console.log(response)
                if (response.data.status_code === 200 ) {
                    this.lectureGroupList = response.data.result;
                } else {
                    console.error("강의 그룹 정보를 불러오는 중 오류 발생:", response.data.message);
                }
            } catch (error) {
                console.error("강의 그룹 정보를 불러오는 중 오류 발생:", error);
            }
        },
        async fetchPostsByLectureId() {
            try {
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/board-list/${this.lectureId}`);
                console.log("post",response)
                if (response.status === 200) {
                    this.posts = response.data.result;
                } else {
                    console.error("게시글 정보를 불러오는 중 오류 발생:", response.data.message);
                }
            } catch (error) {
                console.error("게시글 정보를 불러오는 중 오류 발생:", error);
            }
        },
        async fetchAssignmentsByLectureId() {
            try {
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/assignment-list/${this.lectureId}`);

                console.log("과제",response)
                if (response.status === 200) {
                    this.assignments = response.data.result;
                } else {
                    console.error("과제 정보를 불러오는 중 오류 발생:", response.data.message);
                }
            } catch (error) {
                console.error("과제 정보를 불러오는 중 오류 발생:", error);
            }
        },
        formatTime(time) {
            // 시간에서 앞의 5자리만 잘라서 반환 (HH:MM)
            return time.substring(0, 5);
        },
        formatPrice(value) {
            if (!value)  return '재능기부';
            return new Intl.NumberFormat('ko-KR').format(value)+'원';
        },
        convertDayToKorean(day) {
            const daysInKorean = {
                MONDAY: '월요일',
                TUESDAY: '화요일',
                WEDNESDAY: '수요일',
                THURSDAY: '목요일',
                FRIDAY: '금요일',
                SATURDAY: '토요일',
                SUNDAY: '일요일'
            };
            return daysInKorean[day] || day;
        },  
        getLectureGroupStatus(group) {
            const now = new Date();
            const start = new Date(group.startDate);
            const end = group.endDate ? new Date(group.endDate) : null;
            

            if (this.lectureType === 'LESSON') {
                if (group.isAvailable === 'N') {
                    if(end<now){
                        return "종료";
                    }else{
                        return "진행중";
                    }
                } if (group.isAvailable === 'Y') {
                    return '모집중';
                } 
            } else {
                if (now < start) {
                    return '모집중';
                } else if (now >= start && (!end || now < end)) {
                    return '진행중';
                } else {
                    return '종료';
                }
            }
            return '상태 알 수 없음';
        },
        showAdditionalInfo(group) {
            const status = this.getLectureGroupStatus(group);
            if (this.lectureType === 'LESSON') {
                return status !== '모집중';
            }
            return true; // LECTURE인 경우 항상 추가 정보를 보여줌
        },
        formatRelativeTime(createdTime) {
            const now = new Date();
            const createdDate = new Date(createdTime);
            const diffInSeconds = Math.floor((now - createdDate) / 1000);

            if (diffInSeconds < 60) {
                return `${diffInSeconds}초 전`;
            } else if (diffInSeconds < 3600) { // 1시간 이내
                const minutes = Math.floor(diffInSeconds / 60);
                return `${minutes}분 전`;
            } else if (diffInSeconds < 86400) { // 24시간 이내
                const hours = Math.floor(diffInSeconds / 3600);
                return `${hours}시간 전`;
            } else { // 24시간 이후
                return createdDate.toLocaleDateString(); // 날짜 형식으로 반환
            }
        },
        clickLessonApplyList(lectureGroupId, lectureType) {
            console.log(lectureGroupId, "lectureGroup Id");
            this.$router.push(`/lesson-apply-list?lectureGroupId=${lectureGroupId}&lectureType=${lectureType}&lectureId=${this.lectureId}`);

        },
        clickLectureHome(lectureGroupId){
            this.$router.push(`/lecture-home/${lectureGroupId}`);
        },

    },
}

//     data() {
//         return {
//             tab: 'ALL_CLASS_LIST',
//             allClassList: [],  //전체 수업리스트
//             availableClassList: [], //모집중인 수업리스트
//             notAvailableClassList: [],  //진행중인 수업리스트
//             isAvailable: '',
//             isLastPage: false,
//             isLoading: false,
//             page: 0,
//             size: 5,

//         };

//     },
//     watch: {
//         tab(newTab) {

//             this.page = 0;
//             this.isLastPage = false;
//             this.isLoading = false;
//             if (newTab === 'ALL_CLASS_LIST') {
//                 this.tab = 'ALL_CLASS_LIST';
//                 this.isAvailable = '';
//                 this.allClassList = [];
//             } else if (newTab === 'NOT_AVAILABLE_CLASS_LIST') {
//                 this.tab = 'NOT_AVAILABLE_CLASS_LIST';
//                 this.isAvailable = 'N';
//                 this.notAvailableClassList = [];

//             } else if (newTab === 'AVAILABLE_CLASS_LIST') {
//                 this.tab = 'AVAILABLE_CLASS_LIST';
//                 this.isAvailable = 'Y';
//                 this.availableClassList = [];
//             }

//             this.showLessonClassList();
            
//         }
//     },

    
//     beforeUnmount(){
//         window.removeEventListener('scroll', this.scrollPagination);
//     },
//     mounted(){
//         window.addEventListener('scroll', this.scrollPagination);
//     },
//     created() {
//         this.lectureId = this.$route.query.lectureId;
//         this.lectureType = this.$route.query.lectureType;
//         this.lectureStatus = this.$route.query.lectureStatus;

//         console.log(this.lectureId, this.lectureType, this.lectureStatus);

//         if (this.lectureType === 'LESSON' && this.lectureStatus === 'ADMIT') {
//             this.tab = 'NOT_AVAILABLE_CLASS_LIST';
//             this.isAvailable = 'N';
//         }
//         if(this.lectureType === 'LECTURE' && this.lectureStatus === 'ADMIT'){
//             this.tab = 'ALL_CLASS_LIST';
//             this.isAvailable = '';
//             this.showLessonClassList();

//         }

        
    

        
//     },

//     methods: {
//         async fetchLectureGroups(){
//             try {
//                 const response = await axios.get(`http://localhost:8081/lecture-group-info/${this.lectureId}`);
//                 if (response.data.status === 'OK') {
//                     this.lectureGroups = response.data.data;
//                     console.log(this.lectureGroups)
//                 } else {
//                     console.error("강의 그룹 정보를 불러오는 중 오류 발생:", response.data.message);
//                 }
//             } catch (error) {
//                 console.error("강의 그룹 정보를 불러오는 중 오류 발생:", error);
//             }
//         },
//         async showLessonClassList() {
//             console.log(this.page);
//             try {
//                 if(this.isLoading || this.isLastPage){
//                     return;
//                 }
//                 this.isLoading = true;
//                 let params = {
//                     size: this.size,
//                     page: this.page,
//                     isAvailable: this.isAvailable,
//                 };
//                 const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service//lecture-group-info/${this.lectureId}`, {params});

//                 const additionalData = response.data.result.content;
                
//                 if(additionalData.length == 0){
//                     this.isLastPage = true;
//                     return;
//                 }
//                 if (this.tab === 'NOT_AVAILABLE_CLASS_LIST') {
//                     this.notAvailableClassList = [...this.notAvailableClassList, ...additionalData];
//                 }
//                 if (this.tab === 'AVAILABLE_CLASS_LIST') {
//                     console.log("모집 증");
//                     console.log(this.page);
//                     this.availableClassList = [...this.availableClassList, ...additionalData];
//                 }
//                 if (this.tab === 'ALL_CLASS_LIST') {
//                     this.allClassList = [...this.allClassList, ...additionalData];
//                 }
//                 this.page++;
//                 this.isLoading = false;

//             } catch (e) {
//                 console.log(e.response.data.error_message);
//             }
//         },

//         c

//        formatPrice(value) {
//             if (!value)  return '재능기부';
            
//             return new Intl.NumberFormat('ko-KR').format(value)+'원';
//         }


//     }
// }
</script>

<style scoped>
.tag {
    display: inline-block;
    background-color: #6C97FD;
    width: 65px;
    margin: 10px;
    border-radius: 15px;
    color: #fff;
    font-weight: 700;
}
.group-time {
    width: 100%;
    background-color: #f9f9f9;
    border: 1px solid #cdcdcd;
    border-radius: 2px;
    font-weight: 700;
    padding: 10px;
    margin: 10px 0;

}
.lecture-group-info {
    border: 1px solid #cdcdcd;
    padding: 20px 30px;
    margin: 20px;
    border-radius: 5px;
}
.list-title {
    font-size: 18px;
    font-weight: 700;
    margin: 0 30px;
}
.list-style {
    width: 100%;
    padding: 20px 30px;
    margin: 10px 10px 40px;
    border-radius: 5px;
    border: 1px solid #cdcdcd;
}
.click-item {
    /* display: inline-block;
    width: 100%;
    border-radius: 10px; */
    margin: 3px;
}
.click-item:hover {
    cursor: pointer;
    background-color: #f3f2f2;
    border-radius: 10px;
   

}
.icon-btn {
    color: #666;
    transition: all 0.3s ease;
    margin: 0 5px;

}
.icon-btn:hover {
    cursor: pointer;
    color: #0d6efd;
    transform: scale(1.05);

}
</style>