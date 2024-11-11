import MemberMainView from "@/views/member/MemberMainView.vue";
import MemberSocialExplainView from "@/views/member/MemberSocialExplainView.vue";
import TutorEmailRegisterView from "@/views/member/TutorEmailRegisterView.vue";
import MemberTutorSocialRegisterView from "@/views/member/MemberTutorSocialRegisterView.vue";
import MemberTuteeSocialRegisterView from "@/views/member/MemberTuteeSocialRegisterView.vue";
import MemberEmailLoginView from "@/views/member/MemberEmailLoginView.vue";
import TuteeEmailRegisterView from "@/views/member/TuteeEmailRegisterView.vue";
import MemberWelcome from "@/views/member/MemberWelcome.vue";
import MemberMyPage from "@/views/member/MemberMyPage.vue";
import ReviewList from "@/views/review/ReviewList.vue";
import ReviewCreateTest from "@/views/review/ReviewCreateTest.vue";
import AdminLogin from "@/views/member/AdminLogin.vue"; 
import BalanceTable from "@/views/member/BalanceTable.vue";

import { jwtDecode } from 'jwt-decode'
export const memberRouter = [
    {
        path: '/balance/table',
        name: 'BalanceTable',
        component: BalanceTable
    },
    // ReviewCreateTest
    {
        path: '/review/test',
        name: 'ReviewCreateTest',
        component: ReviewCreateTest
    },
    {
        path: '/member/main',
        name: 'MemberMainView',
        component: MemberMainView
    },
    {
        path: '/member/explain',
        name: 'MemberSocialExplainView',
        component: MemberSocialExplainView
    },
    {
        path: '/member/email/register/tutor',
        name: 'TutorEmailRegisterView',
        component: TutorEmailRegisterView
    },
    {
        path: '/member/social/register/tutor',
        name: 'MemberTutorSocialRegisterView',
        component: MemberTutorSocialRegisterView
    },
    {
        path: '/member/social/register/tutee',
        name: 'MemberTuteeSocialRegisterView',
        component: MemberTuteeSocialRegisterView
    },
    {
        path: '/member/email/login',
        name: 'MemberEmailLoginView',
        component: MemberEmailLoginView
    },
    {
        path: '/member/email/register/tutee',
        name: 'TuteeEmailRegisterView',
        component: TuteeEmailRegisterView
    },
    {
        path: '/member/welcome',
        name: 'MemberWelcome',
        component: MemberWelcome
    },
    {
        path: '/member/mypage',
        name: 'MemberMyPage',
        component: MemberMyPage
    },
    {
        path: '/review/list',
        name: 'ReviewList',
        component: ReviewList
    },
    {
        path: '/admin',
        name: 'AdminLogin',
        component: AdminLogin
    },
    {
        path: '/loginSuccess',
        component: MemberMainView,
        beforeEnter: (to, from, next) => {
            
            function getCookie(name) {
                const value = `; ${document.cookie}`;
                const parts = value.split(`; ${name}=`);
                if (parts.length === 2) return parts.pop().split(';').shift();
            }
    
            // 쿠키에서 토큰과 추가 정보를 가져옴
            const token = getCookie('accessToken');
            const refreshToken = getCookie('refreshToken');
            const id = getCookie('memberId');
            
            console.log(token+" 토큰 가져옴");
            console.log(refreshToken+" 리프레쉬토큰 가져옴");
            console.log(id+" id 가져옴");
            
    
            if (token) {
                try {
                    console.log("여기옴? if문 내부");
                    // JWT에서 role 정보 추출
                    const role = jwtDecode(token).role;
    
                    // LocalStorage에 저장
                    localStorage.setItem('token', token);
                    if (refreshToken) {
                        localStorage.setItem('refreshToken', refreshToken);
                    }
                    localStorage.setItem('role', role);
                    localStorage.setItem('id', id);
    
                    // 홈 페이지로 리다이렉트
                    window.location.href = '/home';
                    // next('/home');
                } catch (error) {
                    console.log("여기냐?");

                    console.error('Invalid token', error);
                    
                    next('/member/main');  // 토큰이 유효하지 않을 경우 로그인 페이지로 리다이렉트
                }
            } else {
                console.log("여기냐?2");
                next('/member/home');  // 쿠키에 토큰이 없을 경우 로그인 페이지로 리다이렉트
            }
        }
    },
    

]