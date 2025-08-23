<template>
  <a-popover
      position="bottom"
      trigger="click"
      :content-style="{ padding: '16px', width: '240px', borderRadius: '12px' }"
  >
    <slot></slot>

    <template #content>
      <div class="user-card">
        <a-avatar
            :size="64"
            :image-url="userAvatar"
            class="card-avatar"
            :style="{ backgroundColor: '#14a9f8' }"
        >
          {{ userName?.charAt(0) }}
        </a-avatar>
        <div class="user-name">{{ userName }}</div>

        <a-button
            type="outline"
            long
            @click="toUserProfile"
            class="profile-btn"
        >
          <template #icon>
            <icon-user/>
          </template>
          个人中心
        </a-button>

        <a-button
            type="outline"
            long
            @click="loginOut"
            class="loginOut-btn"
            status="danger"
        >
          <template #icon>
            <icon-export/>
          </template>
          退出登录
        </a-button>
      </div>
    </template>
  </a-popover>
</template>

<script setup lang="ts">
import {IconExport, IconUser} from '@arco-design/web-vue/es/icon';

const props = defineProps({
  userAvatar: {
    type: String,
    required: false,
    default: null
  },
  userName: {
    type: String,
    required: false,
    default: "未登录"
  }
});

const emit = defineEmits(['toUserProfile', 'loginOut']);

const toUserProfile = () => {
  emit('toUserProfile');
};

const loginOut = () => {
  emit('loginOut');
};
</script>

<style scoped lang="less">
.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;

  .card-avatar {
    margin-bottom: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .user-name {
    font-size: 16px;
    font-weight: 600;
    color: var(--color-text-1);
  }

  .profile-btn, .loginOut-btn {
    width: 100%;
    border-radius: 6px;
    padding: 8px 16px;
    font-size: 14px;

    &:hover {
      transform: none;
    }
  }

  .profile-btn {
    border-color: var(--color-primary);
    color: var(--color-primary);

    &:hover {
      background-color: var(--color-primary-light-1);
    }
  }

  .loginOut-btn {
    margin-top: 8px;
  }
}
</style>
