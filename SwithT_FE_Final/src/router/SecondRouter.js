import ChatRoom from "@/views/chat/ChatRoom.vue";
import TutorLectureClassList from "@/views/lecture/TutorLectureClassList.vue";
import TutorMyLectureList from "@/views/lecture/TutorMyLectureList.vue";
import LessonApplyList from "@/views/lectureApply/LessonApplyList.vue";
import TuteeMyLectureList from "@/views/lecture/TuteeMyLectureList.vue";

export const secondRouter = [
    // 튜터 - 내 강의/과외 리스트
    {
        path: '/tutor-lecture-list',
        name: 'TutorMyLectureList',
        component: TutorMyLectureList
    },

    
    //튜터 - 한 과외/강의의 강의 그룹 리스트 
    {
        path: '/tutor-lecture-class-list',
        name: 'TutorLectureClassList',
        component: TutorLectureClassList
    },

    //튜터 - 하나의 과외 그룹에 신청한 튜티 리스트
    {
        path: '/lesson-apply-list',
        name: 'LessonApplyList',
        component: LessonApplyList
    },


    //채팅방
    {
        path: '/chat-room',
        name: 'ChatRoom',
        component: ChatRoom
    },

    //튜티 - 내 강의 리스트(진행중, 종료)
    {
        path: '/tutee-my-lecture-list',
        name: 'TuteeMyLectureList',
        component: TuteeMyLectureList
    },

]