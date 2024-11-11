import { createRouter, createWebHistory } from 'vue-router';
import TestView from '@/components/TestView.vue'
import { secondRouter } from './SecondRouter';
import LoginPage from '@/components/LoginPage.vue';
import ScheduleView from '@/views/scheduler/MonthlyScheduleView.vue';
import { memberRouter } from './memberRouter';
import { thirdRouter } from './thirdRouter';
import { FirstRouter } from './FirstRouter';
import { FourthRouter } from './FourthRouter';

const routes = [
    {
        path: '/',
        redirect: '/home'  // /로 접근하면 /home으로 리디렉션
    },
    {
        path: '/test',
        name: 'TEST',
        component: TestView
    },
    ...secondRouter,
    ...thirdRouter,
    {
        path: '/login',
        name: 'LOGIN',
        component: LoginPage
    },
    {
        path: '/schedule', //스케쥴러
        name: 'Calendar',
        component: ScheduleView
    },
    ...memberRouter,
    ...FirstRouter,
    ...FourthRouter
  
]

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router
