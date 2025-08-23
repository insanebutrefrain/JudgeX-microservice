<template>
  <div class="background-container">
    <a-card id="userRegisterView">
      <a-button @click="toLogin"
                class="back-button" shape="round"
      >
        返回
        <template #icon>
          <icon-arrow-right rotate="180"/>
        </template>

      </a-button>
      <div class="register-header">
        <h2>新用户注册</h2>
      </div>
      <a-form class="form" :model="form" label-align="left" auto-label-width>
        <a-form-item field="userAccount" label="账号">
          <a-input
              class="form-input"
              v-model="form.userAccount"
              placeholder="请输入账号..."
              size="large"
          >
            <template #prefix>
              <icon-user/>
            </template>
          </a-input>
        </a-form-item>
        <a-form-item
            field="userPassword" tooltip="密码不少于8位"
            label="密码"
            :tooltip-props="{
                        contentStyle: { fontSize: '16px' },
                        iconStyle: { fontSize: '16px' }
                      }">
          <a-input-password class="form-input"
                            v-model="form.userPassword"
                            placeholder="请输入密码..."
                            size="large"
          >
            <template #prefix>
              <icon-lock/>
            </template>
          </a-input-password>
        </a-form-item>
        <a-form-item
            field="checkPassword" tooltip="两次密码须一致"
            label="重复密码"
            :tooltip-props="{
                        contentStyle: { fontSize: '16px' },
                        iconStyle: { fontSize: '16px' }
                      }">
          <a-input-password class="form-input"
                            v-model="form.checkPassword"
                            placeholder="请再次输入密码..."
                            size="large"
          >
            <template #prefix>
              <icon-lock/>
            </template>
          </a-input-password>
        </a-form-item>
      </a-form>
      <div class="button-group">
        <a-button @click="handleSubmit" type="primary"
                  class="register-button" shape="round">
          提交
          <template #icon>
            <icon-arrow-right/>
          </template>
        </a-button>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import {reactive} from 'vue';
import {IconArrowRight, IconLock, IconUser} from '@arco-design/web-vue/es/icon';
import {UserControllerService, UserRegisterRequest} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import {useRouter} from "vue-router";

const form = reactive({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
} as UserRegisterRequest);

const router = useRouter();

const handleSubmit = async () => {
  const res = await UserControllerService.userRegisterUsingPost(form);
  if (res.code == 0 && res.data) {
    message.success("注册成功, 请进行登录");
    await router.push({
      path: "/user/login",
      replace: true
    });
  } else {
    message.error(res.message);
  }
}
const toLogin = () => {
  router.push({path: "/user/login"});
}
</script>


<style scoped>
.background-container {
  position: relative;
  width: 100%;
  height: 100vh;
  background-image: url('@/assets/background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  justify-content: center;
  align-items: center;
}

#userRegisterView {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 450px;
  padding: 40px 50px;
  background-color: rgba(255, 255, 255, 0.33);
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: fadeInUp 0.6s ease-out;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header h2 {
  font-size: 28px;
  color: #1d2129;
  margin-bottom: 8px;
}


:deep( .arco-row) {
  margin: 16px 0;
}


:deep(.arco-form-item-label-col > .arco-form-item-label) {
  font-size: 20px !important; /* label */
  margin-top: 8px;
}

:deep(.arco-input-wrapper .arco-input-prefix > svg, .arco-input-wrapper .arco-input-suffix > svg) {
  font-size: 18px !important;
}

:deep(.arco-icon-hover) {
  font-size: 18px !important;
}

:deep(.arco-input-wrapper .arco-input.arco-input-size-large ) {
  font-size: 20px !important; /* 输入框内 */
  line-height: 2.2 !important;
  padding-left: 12px;
}

.button-group {
  display: flex;
  justify-content: center;
  width: 100%;
  margin-top: 50px;
  margin-bottom: 20px;
}

.register-button {
  width: 80%;
  height: 48px !important;
  font-size: 16px !important;
  transition: all 0.2s ease;
  letter-spacing: 1px;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--primary-6), 0.2);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px) translateY(-50%);
  }
  to {
    opacity: 1;
    transform: translateY(0) translateY(-50%);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  #userRegisterView {
    right: auto;
    width: 90%;
    max-width: 400px;
    padding: 30px;
  }
}
</style>
