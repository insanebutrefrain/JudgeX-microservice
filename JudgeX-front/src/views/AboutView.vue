<template>
  <div class="about-container">
    <a-space direction="vertical" size="large" fill>
      <a-typography-title :heading="3" class="title">🌟 关于我们</a-typography-title>

      <a-card hoverable class="fun-card">
        <template #cover>
          <div class="cover-image">
            <img src="https://img.freepik.com/free-vector/hand-drawn-web-developers_23-2148819604.jpg"
                 alt="Team illustration">
          </div>
        </template>
        <!--        <a-typography-text class="fun-text">-->
        <!--          Java是世界上最好的语言!-->
        <!--        </a-typography-text>-->
      </a-card>

      <a-divider orientation="left">
        <a-typography-text type="success">✨ 技术栈</a-typography-text>
      </a-divider>

      <a-space wrap size="large">
        <a-tag v-for="tech in techStack" :key="tech" color="arcoblue" size="large">
          {{ tech }}
        </a-tag>
      </a-space>

      <a-divider orientation="left">
        <a-typography-text type="primary">🚀 趣味数据</a-typography-text>
      </a-divider>

      <a-row :gutter="16">
        <a-col :span="8" v-for="stat in funStats" :key="stat.title">
          <a-statistic :title="stat.title" :value="stat.value" show-group-separator>
            <template #suffix>
              <span v-if="stat.unit">{{ stat.unit }}</span>
            </template>
          </a-statistic>
        </a-col>
      </a-row>

      <a-divider orientation="left">
        <a-typography-text type="warning">🎮 算法小游戏 - 鸡蛋掉落问题</a-typography-text>
      </a-divider>

      <div class="game-container">
        <a-typography-paragraph>
          你有 <span class="egg-count">{{ eggsLeft }}</span> 个鸡蛋和一座100层的楼。
          你的目标是找到鸡蛋从哪层楼扔下不会碎的最高楼层。
          每次扔鸡蛋后，系统会告诉你鸡蛋是否碎了。
          请用最少的尝试次数找到这个临界楼层。
        </a-typography-paragraph>

        <a-space direction="vertical" size="large" fill>
          <a-space align="center">
            <span>当前尝试楼层:</span>
            <a-input-number
                v-model="currentFloor"
                :min="1"
                :max="100"
                :disabled="gameOver"
            />
            <a-button
                type="primary"
                @click="dropEgg"
                :disabled="gameOver || eggsLeft === 0"
            >
              扔鸡蛋 (剩余: {{ eggsLeft }})
            </a-button>
          </a-space>

          <a-alert v-if="lastResult" :type="lastResult.type">
            {{ lastResult.message }}
          </a-alert>


          <a-alert
              v-if="gameOver"
              :type="gameWon ? 'success' : 'error'"
              :title="gameWon ? `游戏胜利! 临界楼层是 ${criticalFloor}` : '游戏失败! 鸡蛋用完了'"
          >
            {{ gameWon ? `你用了 ${attempts.length} 次尝试找到了答案。` : `你在找到临界楼层前用完了所有鸡蛋。` }}
            <a-button type="text" @click="resetGame" style="margin-left: 10px">
              再玩一次
            </a-button>
          </a-alert>
          <a-space>
            尝试次数: {{ attempts.length }}次 , 最优解: 14次
          </a-space>
        </a-space>
      </div>

      <a-divider orientation="left">
        <a-typography-text type="danger">📬 联系我们</a-typography-text>
      </a-divider>

      <a-space direction="vertical">
        <a-link href="mailto:3246126564@qq.com" icon>
          <icon-email/>
          3246126564@qq.com
        </a-link>
        <a-link href="https://github.com/insanebutrefrain" icon>
          <icon-github/>
          GitHub
        </a-link>
        <!--        <a-link href="https://leetcode.cn/u/juejidexing/" icon>-->
        <!--          <icon-twitter/>-->
        <!--          LeetCode-->
        <!--        </a-link>-->
      </a-space>
    </a-space>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';

const techStack = ref([
  'Spring Boot', "Spring Cloud Alibaba", 'Mybatis Plus', 'MySQL',
  'Vue 3', 'Arco Design',
  'Redis', 'Docker', 'JWT', 'Rabbit MQ'
]);

const funStats = ref([
  {title: '已解决问题', value: 999, unit: '道'},
  {title: '用户提交', value: 666666, unit: '次'},
  {title: '咖啡消耗', value: 1000000000, unit: '杯'}
]);
/**
 * 鸡蛋掉落游戏状态
 */
const criticalFloor = ref(Math.floor(Math.random() * 100) + 1); // 随机生成1-100的临界楼层
const currentFloor = ref(1);
const eggsLeft = ref(2); // 初始鸡蛋数量
const attempts = ref<Array<{ attempt: number, floor: number, result: string, eggsLeft: number }>>([]);
const lastResult = ref<{ type: 'success' | 'warning' | 'error', message: string } | null>(null);
const gameOver = ref(false);
const gameWon = ref(false); // 是否成功找到临界楼层
const dropEgg = () => {
  const attemptNumber = attempts.value.length + 1;

  if (currentFloor.value <= criticalFloor.value) {
    // 鸡蛋没碎
    attempts.value.push({
      attempt: attemptNumber,
      floor: currentFloor.value,
      result: '鸡蛋没碎',
      eggsLeft: eggsLeft.value
    });
    lastResult.value = {
      type: 'success',
      message: `在第 ${currentFloor.value} 层扔鸡蛋 - 鸡蛋没碎!`
    };

    // 如果刚好是临界楼层
    if (currentFloor.value === criticalFloor.value) {
      gameOver.value = true;
      gameWon.value = true;
      lastResult.value = {
        type: 'success',
        message: `恭喜! 你找到了临界楼层 ${criticalFloor.value}`
      };
    }
  } else {
    // 鸡蛋碎了
    eggsLeft.value -= 1;
    attempts.value.push({
      attempt: attemptNumber,
      floor: currentFloor.value,
      result: '鸡蛋碎了',
      eggsLeft: eggsLeft.value
    });
    lastResult.value = {
      type: 'error',
      message: `在第 ${currentFloor.value} 层扔鸡蛋 - 鸡蛋碎了! 剩余鸡蛋: ${eggsLeft.value}`
    };
    // 检查是否还有鸡蛋
    if (eggsLeft.value === 0) {
      gameOver.value = true;
      gameWon.value = false;
      lastResult.value = {
        type: 'error',
        message: `鸡蛋用完了! 游戏结束`
      };
    }

    // 如果上一层没碎，这就是临界楼层
    else if (currentFloor.value - 1 === criticalFloor.value) {
      gameOver.value = true;
      gameWon.value = true;
    }
  }
};

const resetGame = () => {
  criticalFloor.value = Math.floor(Math.random() * 100) + 1;
  currentFloor.value = 1;
  eggsLeft.value = 2;
  attempts.value = [];
  lastResult.value = null;
  gameOver.value = false;
  gameWon.value = false;
};
</script>

<style scoped>
.about-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
}

.fun-card {
  max-width: 600px;
  margin: 0 auto;
  border-radius: 8px;
}

.fun-text {
  font-size: 16px;
  line-height: 1.6;
}

.cover-image img {
  width: 100%;
  height: auto;
  border-radius: 8px 8px 0 0;
}


.egg-count {
  font-weight: bold;
  color: var(--color-primary-light-4);
}

.game-container {
  background-color: var(--color-fill-2);
  padding: 20px;
  border-radius: 8px;
  border: 1px solid var(--color-border-2);
}

:deep(.arco-alert-success) {
  background-color: var(--color-success-light-1);
}

:deep(.arco-alert-error) {
  background-color: var(--color-danger-light-1);
}

:deep(.arco-statistic) {
  text-align: center;
}

:deep(.arco-statistic-title) {
  font-size: 16px;
}

:deep(.arco-statistic-value) {
  font-size: 28px;
}
</style>
