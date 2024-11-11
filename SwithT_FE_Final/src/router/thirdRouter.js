import LectureHomeView from '@/views/lecture/home/LectureHomeView.vue'
import MapTest from '@/views/lecture/home/MapTest.vue'
import UpdateLectureGroupView from '@/views/lecture/home/UpdateLectureGroupView.vue'
export const thirdRouter=[
    // 강의홈 (튜터/튜티별 강의/과외별 강의그룹화면 
    // - 튜티의 경우 해당 화면으로 강의 정보&강의 과제& 강의 게시판 및 댓글&튜티리스트 확인, 강의 게시판/댓글 작성 및 수정
    // - 튜터의 경우 해당 화면으로 강의 정보&튜티리스트 확인, 강의 과제/강의 게시판 및 댓글 CRUD 가능

    {
        path: '/lecture-home/:lectureGroupId',
        name: 'LectureHomeView',
        component: LectureHomeView
    },
    // 아래의 페이지는 도로명 주소 api 사용 및 위도 경도 찾는 api 입니다.
    {
        path: '/lecture/test',
        name: 'MapTest',
        component: MapTest
    },
    // 강의 그룹 수정
    {
        path: '/lecture-group/:lectureGroupId',
        name: 'UpdateLectureGroupView',
        component: UpdateLectureGroupView
    },

]