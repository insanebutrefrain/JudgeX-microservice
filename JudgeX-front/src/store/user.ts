import {StoreOptions} from "vuex";
import AccessEnum from "@/access/accessEnum";
import {UserControllerService} from "../../generated";


export default {
    namespaced: true,
    state: () => ({
        loginUser: {
            userName: "未登录",
            userAvatarUrl: "",
            userRole: AccessEnum.NOT_LOGIN
        }
    }),
    getters: {},
    // action#commit → mutation → state → 组件
    // user.ts
    actions: {
        async getLoginUser({commit}) {
            try {
                const res = await UserControllerService.getLoginUserUsingGet();
                if (res.code === 0 && res.data) {
                    commit('updateUser', res.data);
                    return res.data;
                } else {
                    commit('updateUser', {
                        userName: "未登录",
                        userAvatarUrl: "",
                        userRole: AccessEnum.NOT_LOGIN
                    });
                    return null;
                }
            } catch (error) {
                console.error('获取登录用户信息失败:', error);
                commit('updateUser', {userName: "未登录", userAvatarUrl: "", userRole: AccessEnum.NOT_LOGIN});
                return null;
            }
        }

    },
    mutations: {
        updateUser(state, User) {
            state.loginUser = User
        },
        setUserAvatarUrl(state, avatarUrl) {
            state.loginUser.userAvatarUrl = avatarUrl;
        }
    },
} as StoreOptions<any>;
