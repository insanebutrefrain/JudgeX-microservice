import {RouteRecordRaw} from 'vue-router'
import AboutView from '@/views/AboutView.vue'
import UserLoginView from '@/views/user/UserLoginView.vue'
import UserRegisterView from '@/views/user/UserRegisterView.vue'
import AdminView from '@/views/AdminView.vue'
import NoAuth from '@/views/NoAuthView.vue'
import AccessEnum from "@/access/accessEnum";
import AddQuestionView from "@/views/question/AddQuestionView.vue";
import ManageQuestionView from "@/views/question/ManageQuestionView.vue";
import QuestionView from "@/views/question/QuestionView.vue";
import DoQuestionView from "@/views/question/DoQuestionView.vue";
import UserProfileView from "@/views/user/UserProfileView.vue";
import UpdateJudgeCaseView from "@/views/question/UpdateJudgeCaseView.vue";


const userRouters: Array<RouteRecordRaw> = [
    {
        path: "/user/login",
        name: "用户登录",
        component: UserLoginView,
        meta: {
            access: AccessEnum.NOT_LOGIN,
            hideInMenu: true,
            layout: ""
        }
    },
    {
        path: "/user/register",
        name: "用户注册",
        component: UserRegisterView,
        meta: {
            access: AccessEnum.NOT_LOGIN,
            hideInMenu: true,
            layout: ""
        }
    },
    {
        path: "/user/profile",
        name: "个人中心",
        component: UserProfileView,
        meta: {
            access: AccessEnum.USER,
            hideInMenu: true,
            layout: "BasicLayout"
        }
    }
];
const questionRouters: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: '主页',
        component: QuestionView,
        meta: {
            access: AccessEnum.NOT_LOGIN,
            hideInMenu: false,
            layout: "BasicLayout",
            order: 1
        },
    },
    {
        path: '/add/question',
        name: '创建题目',
        component: AddQuestionView,
        meta: {
            access: AccessEnum.ADMIN,
            hideInMenu: false,
            layout: "BasicLayout",
            order: 4
        },
    },
    {
        path: '/update/question',
        name: '更新题目',
        component: AddQuestionView,
        meta: {
            access: AccessEnum.ADMIN,
            hideInMenu: true,
            layout: "BasicLayout",
        },
    },
    {
        path: '/manage/question',
        name: '管理题目',
        component: ManageQuestionView,
        meta: {
            access: AccessEnum.ADMIN,
            hideInMenu: false,
            layout: "BasicLayout",
            order: 3
        },
    },
    {
        path: '/view/question/:id',
        name: '在线做题',
        component: DoQuestionView,
        props: true,
        meta: {
            access: AccessEnum.USER,
            hideInMenu: true,
            layout: "BasicLayout",
        },
    }, {
        path: '/update/judgecase',
        name: '更新测试用例',
        component: UpdateJudgeCaseView,
        props: true,
        meta: {
            access: AccessEnum.ADMIN,
            hideInMenu: true,
            layout: "BasicLayout",
        },
    },

];
export const routes: Array<RouteRecordRaw> = [
    ...userRouters,
    ...questionRouters,
    {
        path: '/admin',
        name: '管理员',
        component: AdminView,
        meta: {
            access: AccessEnum.ADMIN,
            hideInMenu: true,
            layout: "BasicLayout",
        },
    },
    {
        path: '/about',
        name: '关于',
        component: AboutView,
        meta: {
            access: AccessEnum.NOT_LOGIN,
            hideInMenu: false,
            layout: "BasicLayout",
            order: 100
        },
    },
    {
        path: '/noAuth',
        name: '无权限',
        component: NoAuth,
        meta: {
            access: AccessEnum.NOT_LOGIN,
            hideInMenu: true,
        },
    },
]

