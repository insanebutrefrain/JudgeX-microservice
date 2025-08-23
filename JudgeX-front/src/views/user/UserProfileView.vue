<template>
  <div class="user-profile-container">
    <a-card class="profile-card" :bordered="false" style=" overflow-y: auto;">
      <div class="profile-header">
        <h2>个人中心</h2>
      </div>

      <!--用户头像编辑-->
      <a-card class="info-card" title="头像设置">
        <div class="avatar-uploader">
          <div class="avatar-wrapper" @click="triggerFileInput">
            <img v-if="previewUrl || avatarUrl" :src="previewUrl ||avatarUrl" class="avatar" alt=""/>
            <div v-else class="avatar-placeholder">
              <icon-user/>
            </div>
            <div class="avatar-mask">
              <icon-upload/>
              <span>点击上传</span>
            </div>
            <input
                ref="fileInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleAvatarUpload"
            />
          </div>
        </div>
        <a-space>
          <a-button
              type="primary"
              :loading="avatarLoading"
              :disabled="!avatarFile"
              @click="submitAvatar"
          >
            保存头像
          </a-button>
          <a-button
              v-if="previewUrl"
              @click="cancelPreview"
          >
            取消
          </a-button>
        </a-space>
      </a-card>

      <!-- 用户信息编辑 -->
      <a-card class="info-card" title="个人信息">
        <a-form :model="form" layout="vertical">
          <a-form-item label="昵称" field="userName">
            <a-input v-model="form.userName" placeholder="请输入昵称"/>
          </a-form-item>
          <a-form-item label="个人简介" field="userProfile">
            <a-textarea
                v-model="form.userProfile"
                placeholder="介绍一下自己吧"
                :auto-size="{ minRows: 3, maxRows: 5 }"
            />
          </a-form-item>
          <a-form-item>
            <a-space>
              <a-button
                  type="primary"
                  :loading="infoLoading"
                  :disabled="!isFormChanged"
                  @click="submitUserInfo"
              >
                保存信息
              </a-button>
              <a-button @click="resetForm">重置</a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>

      <!--做题记录-->
      <a-card class="submissions-card" title="我的提交记录" :bordered="false">
        <a-table
            :columns="submissionColumns"
            :data="submissionData"
            :pagination="submissionPagination"
            @page-change="handleSubmissionPageChange"
            :loading="submissionLoading"
            row-key="id"
            @row-click="handleRowClick"
        >
          <template #judgeResult="{ record }">
            <a-tag :color="getStatusColor(record.judgeResult)">
              {{ getStatusText(record.judgeResult) }}
            </a-tag>
          </template>
          <template #createTime="{ record }">
            {{ moment(record.createTime).format('YYYY-MM-DD') }}
          </template>
        </a-table>
      </a-card>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import {FileRecordControllerService, QuestionControllerService, UserControllerService} from '../../../generated';
import getStatusText from '../../commonTs/getStatusText.ts';
import getStatusColor from '../../commonTs/getStatusColor.ts';
import moment from 'moment';
import message from '@arco-design/web-vue/es/message';
import {IconUpload, IconUser} from '@arco-design/web-vue/es/icon';

const router = useRouter();

// 用户相关
const form = ref({
  userName: '',
  userProfile: '',
});

const originalForm = ref({...form.value});
const infoLoading = ref(false);
const avatarLoading = ref(false);
const avatarFile = ref<File | null>(null);
const fileInput = ref<HTMLInputElement | null>(null);

const avatarUrl = ref<string>("");
const previewUrl = ref<string>(""); // 新增预览URL
// 计算表单是否修改过
const isFormChanged = computed(() => {
  return (
      form.value.userName !== originalForm.value.userName ||
      form.value.userProfile !== originalForm.value.userProfile
  );
});

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value?.click();
};


// 处理头像上传
const handleAvatarUpload = (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    const file = target.files[0];
    if (!file.type.startsWith('image/')) {
      message.error('请选择图片文件');
      return;
    }

    // 预览图片
    const reader = new FileReader();
    reader.onload = (e) => {
      previewUrl.value = e.target?.result as string; // 设置预览URL
    };
    reader.readAsDataURL(file);

    avatarFile.value = file;
  }
};

// 取消预览
const cancelPreview = () => {
  previewUrl.value = "";
  avatarFile.value = null;
  if (fileInput.value) {
    fileInput.value.value = ""; // 清空文件输入
  }
};

const fetchAvatar = async (avatarUrl: ref<string>) => {
  const res = await FileRecordControllerService.getAvatarUsingGet();
  if (res.code !== 0) {
    message.error('头像获取失败: ' + res.message)
    return;
  }
  avatarUrl.value =`data:image/png;base64,${res.data}`
}
// 上传头像文件到服务端
const submitAvatar = async () => {
  if (!avatarFile.value) return;

  try {
    avatarLoading.value = true;
    // 上传头像文件
    const resUpload = await FileRecordControllerService.uploadAvatarUsingPost(avatarFile.value);
    if (resUpload.code !== 0) {
      message.error('头像上传失败: ' + resUpload.message)
      return;
    }
    message.success('头像上传成功');
    // 上传成功后，更新正式头像并清除预览
    await fetchAvatar(avatarUrl);
    avatarFile.value = null;
  } catch (err) {
    message.error('头像上传失败');
    console.error(err);
  } finally {
    avatarLoading.value = false;
  }
};

// 提交用户信息
const submitUserInfo = async () => {
  try {
    infoLoading.value = true;
    const res = await UserControllerService.updateMyUserUsingPost({
      userName: form.value.userName,
      userProfile: form.value.userProfile
    });

    if (res.code === 0) {
      message.success('个人信息更新成功');
      originalForm.value = {...form.value};
    } else {
      message.error('个人信息更新失败: ' + res.message);
    }
  } catch (err) {
    message.error('个人信息更新失败');
    console.error(err);
  } finally {
    infoLoading.value = false;
  }
};

// 重置表单
const resetForm = () => {
  form.value = {...originalForm.value};
};

// 获取用户信息
const fetchUserInfo = async (form) => {
  try {
    const res = await UserControllerService.getLoginUserUsingGet();
    if (res.code === 0) {
      form.value = {
        userName: res.data.userName || '',
        userProfile: res.data.userProfile || '',
      };
      originalForm.value = {...form.value};
      console.log("form:", form.value);
    } else {
      message.error('获取用户信息失败: ' + res.message);
    }
  } catch (err) {
    message.error('获取用户信息失败');
    console.error(err);
  }
};

// -------------------------------------提交记录相关
const submissionData = ref([]);
const submissionLoading = ref(false);
const submissionPagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
});

const submissionColumns = [
  {
    title: '题目ID',
    dataIndex: 'questionId',
  },
  {
    title: '题目名称',
    dataIndex: 'questionTitle',
  },
  {
    title: '判题结果',
    slotName: 'judgeResult',
  },
  {
    title: '提交时间',
    slotName: 'createTime',
  },
];

// 获取提交记录
const fetchSubmissions = async () => {
  try {
    submissionLoading.value = true;
    const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost({
      current: submissionPagination.value.current,
      pageSize: submissionPagination.value.pageSize,
    });
    if (res.code === 0) {
      const records = res.data.records || [];
      submissionData.value = await Promise.all(
          records.map(async (record) => {
            const questionRes = await QuestionControllerService.getQuestionByIdUsingGet(record.questionId);
            if (questionRes.code === 0) {
              record.questionTitle = questionRes.data.title;
            } else {
              record.questionTitle = '未知';
            }
            return record;
          })
      );
      submissionPagination.value.total = res.data.total || 0;
    } else {
      message.error('获取提交记录失败: ' + res.message);
    }
  } catch (err) {
    message.error('获取提交记录失败');
    console.error(err);
  } finally {
    submissionLoading.value = false;
  }
};

// 处理分页变化
const handleSubmissionPageChange = (page: number) => {
  submissionPagination.value.current = page;
  fetchSubmissions();
};

// 处理行点击
const handleRowClick = (record: any) => {
  router.push({
    path: `/view/question/${record.questionId}`,
  });
};

onMounted(async () => {
  await fetchUserInfo(form);
  await fetchAvatar(avatarUrl);
  fetchSubmissions();
});
</script>

<style scoped lang="less">
.user-profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .profile-card {

    margin-bottom: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    .profile-header {
      margin-bottom: 24px;
      text-align: center;
    }
  }

  .info-card {
    margin-bottom: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }

  .submissions-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    :deep(.arco-table-tr) {
      cursor: pointer;

      &:hover {
        background-color: var(--color-fill-2);
      }
    }
  }

  .avatar-uploader {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 20px;

    .avatar-wrapper {
      position: relative;
      width: 120px;
      height: 120px;
      border-radius: 50%;
      overflow: hidden;
      cursor: pointer;
      border: 1px dashed var(--color-border-3);
      transition: all 0.2s;
      margin-bottom: 16px;

      &:hover {
        border-color: var(--color-primary-light-3);

        .avatar-mask {
          opacity: 1;
        }
      }

      .avatar {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .avatar-placeholder {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        color: var(--color-text-3);
        background-color: var(--color-fill-2);
        font-size: 24px;
      }

      .avatar-mask {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        background-color: rgba(0, 0, 0, 0.5);
        color: white;
        opacity: 0;
        transition: opacity 0.2s;
        font-size: 14px;
        gap: 4px;
      }
    }
  }
}
</style>
