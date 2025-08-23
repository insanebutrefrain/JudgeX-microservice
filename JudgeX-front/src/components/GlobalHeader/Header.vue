<template>
  <a-row class="Header" align="center" :wrap="false">
    <!-- 菜单栏 -->
    <a-col class="headerLeft" flex="auto">
      <a-menu
          mode="horizontal"
          :selectedKeys="selectedKeys"
          :default-selected-keys="['1']"
          @menu-item-click="doMenuClick"
          class="custom-menu"
      >
        <!-- 图标 -->
        <a-menu-item
            key="0"
            :style="{ padding: 0, marginRight: '38px' }"
            disabled
        >
          <div class="title-bar">
            <img class="logo" src="../../assets/X.svg" alt="logo"/>
            <div class="title">JudgeX</div>
          </div>
        </a-menu-item>
        <!-- 菜单项 -->
        <a-menu-item
            class="menu-item"
            v-for="item in visibleRoutes"
            :key="item.path"
        >
          <template #icon v-if="item.meta?.icon">
            <component :is="item.meta.icon"/>
          </template>
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <!-- 用户信息 -->
    <a-col class="headerRight">
      <!-- 未登录状态 -->
      <div
          v-if="store.state.user.loginUser.userRole === AccessEnum.NOT_LOGIN"
          class="auth-buttons"
      >
        <a-button @click="toLogin" class="login-btn"> 登录</a-button>
        或
        <a-button @click="toRegister" class="register-btn"> 注册</a-button>
      </div>
      <!-- 已登录状态 -->
      <div v-else class="user-profile">
        <UserCardPopover
            :user-avatar="avatarUrl"
            :user-name="store.state.user.loginUser.userName"
            @to-user-profile="toUserProfile"
            @login-out="loginOut"
        >
          <a-avatar
              :size="40"
              :image-url="avatarUrl"
              class="avatar-hover"
              :style="{ backgroundColor: '#14a9f8', cursor: 'pointer' }"
          >
            {{ store.state.user.loginUser.userName?.charAt(0) }}
          </a-avatar>
        </UserCardPopover>
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import {routes} from "@/router/routes";
import {useRouter} from "vue-router";
import {computed, ref} from "vue";
import {useStore} from "vuex";
import checkAccess from "@/access/checkAccess";
import AccessEnum from "@/access/accessEnum";
import UserCardPopover from "@/components/GlobalHeader/UserCardPopover.vue";

import {FileRecordControllerService} from "../../../generated";
import message from "@arco-design/web-vue/es/message";

const selectedKeys = ref(["/"]);
const router = useRouter();
const store = useStore();
const avatarUrl = ref("");

// 路由变化时更新菜单高亮
router.afterEach((to) => {
  console.log("跳转:", to.path);
  selectedKeys.value = [to.path];
});

const doMenuClick = (key: string) => {
  router.push({path: key});
};

const toUserProfile = () => {
  router.push({path: "/user/profile"});
};

// 计算可见路由
const visibleRoutes = computed(() => {
  return routes
      .filter((item) => {
        if (item?.meta?.hideInMenu) return false;
        return checkAccess(store.state.user.loginUser, item?.meta?.access);
      })
      .sort((a, b) => {
        const orderA = a.meta?.order || 0;
        const orderB = b.meta?.order || 0;
        return orderA - orderB;
      });
});

const fetchAvatar = async (avatarUrl: ref<string>) => {
  const res = await FileRecordControllerService.getAvatarUsingGet();
  if (res.code !== 0) {
    message.error("头像获取失败: " + res.message);
    return;
  }
  avatarUrl.value = `data:image/png;base64,${res.data}`;
};

function loginOut() {
  console.log("退出登录");
  localStorage.removeItem("token");
  store.state.user.loginUser = {
    userName: "",
    userRole: AccessEnum.NOT_LOGIN,
  };
  router.push("/");
}

function toLogin() {
  router.push({path: "/user/login"});
}

function toRegister() {
  router.push({path: "/user/register"});
}

fetchAvatar(avatarUrl);
</script>

<style scoped lang="less">
.Header {
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;

  .headerLeft {
    margin-top: 16px;

    :deep(.arco-menu-horizontal .arco-menu-inner) {
      padding: 6px 20px !important;
    }
  }

  .headerRight {
    flex: 0;
    margin-right: 24px;
  }
}

.title-bar {
  display: flex;
  align-items: center;

  .logo {
    width: 32px;
    height: 32px;
    transition: all 0.3s;
  }

  .title {
    color: var(--color-text-1);
    margin-left: 8px;
    font-size: 20px;
    font-weight: 600;
    background: linear-gradient(90deg, #3498db, #2ecc71);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

.custom-menu {
  :deep(.arco-menu-overflow-wrap) {
    height: 64px;
  }

  :deep(.arco-menu-item) {
    font-size: 16px;
    font-weight: 500;
    margin: 0 8px;
    padding: 0 16px;
    border-radius: 6px;
    transition: all 0.2s;

    &:hover {
      background-color: var(--color-primary-light-1);
      color: var(--color-primary);
    }

    &.arco-menu-selected {
      color: var(--color-primary);
      font-weight: 600;
    }
  }
}

.auth-buttons {
  display: flex;
  gap: 12px;
  align-items: center;

  .login-button {
    border: none;
  }

  .register-button {
    border: none;
  }
}

.user-profile {
  display: flex;
  justify-content: flex-end;
}

.avatar-hover {
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}
</style>
