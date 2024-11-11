
import FirstPage from '@/views/FirstPage.vue';
import TuteeAppliedList from '@/pendingLectures/TuteeAppliedList.vue';
import TutorAppliedList from '@/pendingLectures/TutorAppliedList.vue';
import AdminAppliedList from '@/pendingLectures/AdminAppliedList.vue';
import CreateLectureView from '@/views/lecture/detail/CreateLectureView.vue';
import EditLectureInfoView from '@/views/lecture/detail/EditLectureInfoView.vue';
import LectureDetailView from '@/views/lecture/detail/LectureDetailView.vue';

export const FirstRouter = [
    // 메인 화면
    {
        path: "/home",
        name: "Home",
        component: FirstPage,
    },

    // 튜티가 신청한 모든 강의 리스트(진행중X, 신청, 거절, 결제 요청)
    {
        path: "/tutee-applied-list", 
        name: "TuteeAppliedList",
        component: TuteeAppliedList,
    },

    // 튜터가 신청한 모든 강의 리스트(진행중X)
    {
        path: "/tutor-applied-list",
        name: "TutorAppliedList",
        component: TutorAppliedList,
    },

    // 튜터가 신청한 강의 리스트 - 어드민용
    {
        path: "/admin-applied-list",
        name: "AdminAppliedList",
        component: AdminAppliedList,
    },
    
    // 강의/과외 개설 페이지
    {
        path: "/create-lecture",
        name: "CreateLecture",
        component: CreateLectureView,
    },

    // 강의 내용 수정 페이지
    {
        path: "/edit-lecture/:id",
        name: "EditLecture",
        component: EditLectureInfoView
    },

    // 강의 상세 정보 페이지
    {
        path: "/lecture/:id",
        name: "LectureDetail",
        component: LectureDetailView,
    }

]