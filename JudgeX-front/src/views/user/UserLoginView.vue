<template>
  <div class="background-container">
    <a-card id="userLoginView">
      <div class="login-header">
        <h2>登录</h2>
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

      </a-form>
      <div class="button-group">
        <a-button type="primary" @click="handleSubmit"
                  class="login-button" shape="round"
        >
          登 录
          <template #icon>
            <icon-arrow-right/>
          </template>
        </a-button>

        <a-button @click="toRegister"
                  class="register-button" shape="round">
          去注册
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
import {UserControllerService, UserLoginRequest} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import {useRouter} from "vue-router";

const form = reactive({
  userAccount: '',
  userPassword: '',
} as UserLoginRequest);

const router = useRouter();

const handleSubmit = async () => {
  const res = await UserControllerService.userLoginUsingPost(form);
  if (res.code == 0 && res.data) {
    message.success("登录成功");
    localStorage.setItem('token', res.data);
    console.log("token:",res.data);
    await router.push({
      path: "/",
      replace: true
    });
  } else {
    message.error(res.message);
  }
}
const toRegister = () => {
  router.push({path: "/user/register"});
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

#userLoginView {
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

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
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
  justify-content: space-between;
  align-items: center;
  margin-top: 50px;
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 48px !important;
  font-size: 16px !important;
  transition: all 0.2s ease;
  letter-spacing: 1px;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--primary-6), 0.2);
}


.login-button {
  width: 200px;
  height: 48px !important;
  font-size: 18px !important;
}

.register-button {
  margin-top: 10px;
  width: 120px;
  height: 40px !important;
  font-size: 16px !important;
  background-color: transparent !important;
  color: var(--color-text-1) !important;
  border: none !important;
  transition: all 0.2s ease;
  margin-left: 50px;
}

.register-button:hover {
  color: #cc3636 !important;
  text-decoration: underline; /* 添加下划线 */
  text-underline-offset: 4px; /* 控制下划线距离文字的间距 */
  text-decoration-thickness: 2px; /* 控制下划线粗细 */
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
  #userLoginView {
    right: auto;
    width: 90%;
    max-width: 400px;
    padding: 30px;
  }
}
</style>
