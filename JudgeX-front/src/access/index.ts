import router from "@/router";
import store from "@/store";
import AccessEnum from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";

function isNotLogin(loginUser: any) {
    if (!loginUser) return true;
    if (!loginUser.userRole) return true;
    return loginUser.userRole === AccessEnum.NOT_LOGIN;
}

// 路由拦截 - 权限管理
router.beforeEach(async (to, from, next) => {
        let loginUser = store.state.user.loginUser;
        if (isNotLogin(loginUser)) {// 自动登录
            await store.dispatch("user/getLoginUser");
            loginUser = store.state.user.loginUser;
        }
        let needAccess = (to.meta?.access as string) ?? AccessEnum.NOT_LOGIN;
        if (needAccess == AccessEnum.NOT_LOGIN) {// 不需要登录
            return next();
        }
        if (isNotLogin(loginUser)) {// 未登录, 跳转登录页
            return next(`/user/login?redirect=${to.fullPath}`);
        }
        if (!checkAccess(loginUser, needAccess)) {// 权限不足
            return next("/noAuth");
        }
        next();
    },
);
