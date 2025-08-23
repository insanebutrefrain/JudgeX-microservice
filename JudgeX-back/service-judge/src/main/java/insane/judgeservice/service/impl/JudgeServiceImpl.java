package insane.judgeservice.service.impl;

import cn.hutool.json.JSONUtil;
import insane.client.service.FileFeignClient;
import insane.client.service.QuestionFeignClient;
import insane.common.common.ErrorCode;
import insane.common.exception.BusinessException;
import insane.judgeservice.judge.CodeSandbox;
import insane.judgeservice.judge.CodeSandboxFactory;
import insane.judgeservice.judge.CodeSandboxProxy;
import insane.judgeservice.service.JudgeService;
import insane.model.codeSandBox.*;
import insane.model.dto.question.JudgeCase;
import insane.model.dto.question.JudgeConfig;
import insane.model.entity.Question;
import insane.model.entity.QuestionSubmit;
import insane.model.enums.JudgeInfoMessageEnum;
import insane.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionFeignClient questionService;

    @Resource
    private FileFeignClient fileFeignClient;

    @Value("${code-sand-box.type}")
    String codeSandBoxType;

    /**
     * 判题
     *
     * @param questionSubmitId 题目id
     * @return 提交记录
     */
    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 获取并验证提交信息和题目信息
        QuestionSubmit questionSubmit = validateAndGetQuestionSubmit(questionSubmitId);
        Question question = validateAndGetQuestion(questionSubmit.getQuestionId());
        log.info("[判题模块]检验提交信息和题目信息完成");
        // 更新判题状态为运行中
        updateQuestionSubmitStatusToRunning(questionSubmitId);
        log.info("[判题模块]已更改为'判题中',准备判题");
        try {
            // 从文件系统获取测试用例
            List<JudgeCase> judgeCaseList = fileFeignClient.getJudgecase(question.getId());
            // 执行代码沙箱
            ExecuteCodeResponse executeCodeResponse = executeCodeSandbox(questionSubmit, judgeCaseList);
            log.info("[判题模块]代码沙箱执行完成,准备处理判题结果");
            // 处理判题结果
            return processJudgeResult(questionSubmitId, question, judgeCaseList, executeCodeResponse);
        } catch (Exception e) {
            log.error("[判题模块]判题系统异常{}", e.getMessage());
            handleJudgeException(questionSubmitId, e.getMessage());  // 处理系统异常
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "判题系统异常 ");
        }
    }

    /**
     * 校验提交id是否存在
     */
    private QuestionSubmit validateAndGetQuestionSubmit(long questionSubmitId) {
        QuestionSubmit questionSubmit = questionService.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Integer status = questionSubmit.getStatus();
        if (Objects.equals(status, QuestionSubmitStatusEnum.RUNNING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "提交正在判题中");
        }
        if (Objects.equals(status, QuestionSubmitStatusEnum.SUCCEED.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "提交已判题完毕");
        }
        return questionSubmit;
    }

    /**
     * 校验题目是否存在
     */
    private Question validateAndGetQuestion(long questionId) {
        Question question = questionService.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        return question;
    }

    /**
     * 更新判题状态, 幂等处理
     */
    private void updateQuestionSubmitStatusToRunning(long questionSubmitId) {
        boolean updated = questionService.update()
                .eq("id", questionSubmitId)
                .eq("status", QuestionSubmitStatusEnum.WAITING.getValue()) // 只有当前状态是原始状态时才更新
                .set("status", QuestionSubmitStatusEnum.RUNNING.getValue())
                .update();
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "重复提交");
        }
    }

    /**
     * 调用代码沙箱
     */
    private ExecuteCodeResponse executeCodeSandbox(QuestionSubmit questionSubmit, List<JudgeCase> judgeCaseList) {
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();

        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(codeSandBoxType);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code).language(language).inputList(inputList).build();

        return codeSandbox.executeCode(executeCodeRequest);
    }

    /**
     * 处理判题结果
     */
    private QuestionSubmit processJudgeResult(long questionSubmitId, Question question, List<JudgeCase> judgeCaseList, ExecuteCodeResponse executeCodeResponse) {
        List<String> outputList = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
        // 处理每个用例的结果
        List<JudgeInfo> judgeInfoList = handleExecuteResponse(executeCodeResponse, outputList, judgeConfig, judgeCaseList.size());

        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setJudgeInfoList(JSONUtil.toJsonStr(judgeInfoList));
        // 判断总体结果
        JudgeInfoMessageEnum judgeResult;
        String executeErrorMessageEnum = executeCodeResponse.getExecuteErrorMessageEnum();
        if (executeErrorMessageEnum != null) { // 如果有全局错误, 直接使用该错误作为最终结果
            ExecuteErrorMessageEnum errorEnum = ExecuteErrorMessageEnum.getEnumByValue(executeErrorMessageEnum);
            judgeResult = convertExecuteErrorToJudgeResult(errorEnum);
        } else {  // 否则按测试用例结果判断
            judgeResult = determineFinalResult(judgeInfoList);
        }
        questionSubmitUpdate.setJudgeResult(judgeResult);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED);

        // 更新判题结果
        boolean update = questionService.updateQuestionSubmit(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        return questionService.getQuestionSubmitById(questionSubmitId);
    }

    /**
     * 处理每个用例的结果
     * 含有错误 -> handleGlobalErrorCases
     * 不含错误 -> handleNormalCases
     */
    private List<JudgeInfo> handleExecuteResponse(ExecuteCodeResponse executeCodeResponse, List<String> outputList,
                                                  JudgeConfig judgeConfig, int caseCount) {
        List<JudgeInfo> judgeInfoList = new ArrayList<>();
        List<ExecuteCaseInfo> executeCaseInfoList = executeCodeResponse.getExecuteCaseInfoList();
        String executeError = executeCodeResponse.getExecuteErrorMessageEnum();
        String errorMessage = executeCodeResponse.getErrorMessage();

        if (executeError != null || StringUtils.isNotBlank(errorMessage)) {
            ExecuteErrorMessageEnum errorEnum = ExecuteErrorMessageEnum.getEnumByValue(executeError);
            handleGlobalErrorCases(judgeInfoList, errorEnum, errorMessage, executeCaseInfoList,
                    outputList, judgeConfig, caseCount);
        } else {
            handleNormalCases(judgeInfoList, executeCaseInfoList, outputList, judgeConfig, caseCount);
        }

        return judgeInfoList;
    }

    /**
     * 处理含有错误的用例输出情况
     */
    private void handleGlobalErrorCases(List<JudgeInfo> judgeInfoList, ExecuteErrorMessageEnum errorEnum,
                                        String errorMessage, List<ExecuteCaseInfo> executeCaseInfoList,
                                        List<String> outputList, JudgeConfig judgeConfig, int caseCount) {
        if (errorEnum == ExecuteErrorMessageEnum.DANGEROUS_CODE || errorEnum == ExecuteErrorMessageEnum.COMPILE_ERROR) {
            // 对于危险代码和编译错误,所有用例都未执行
            for (int i = 0; i < caseCount; i++) {
                JudgeInfo info = createJudgeInfoWithError(errorEnum.getValue(), errorMessage);
                judgeInfoList.add(info);
            }
        } else if (errorEnum == ExecuteErrorMessageEnum.SYSTEM_ERROR) {
            // 对于系统错误,部分用例可能已执行
            for (int i = 0; i < caseCount; i++) {
                JudgeInfo info = new JudgeInfo();
                if (i < executeCaseInfoList.size() && executeCaseInfoList.get(i) != null) {
                    ExecuteCaseInfo caseInfo = executeCaseInfoList.get(i);
                    info.setOutput(caseInfo.getOutput());
                    info.setTime(caseInfo.getTime());
                    info.setMemory(caseInfo.getMemory());
                    info.setErrorMessage(caseInfo.getErrorMessage());

                    JudgeInfoMessageEnum judgeCaseResult = determineCaseResult(
                            outputList.get(i), caseInfo.getErrorMessage(),
                            caseInfo.getOutput(), caseInfo.getTime(), judgeConfig, caseInfo.getMemory()
                    );
                    info.setJudgeCaseResult(judgeCaseResult);
                } else {
                    info.setJudgeCaseResult(JudgeInfoMessageEnum.SYSTEM_ERROR);
                    info.setErrorMessage("该用例未执行");
                }
                judgeInfoList.add(info);
            }
        }
    }

    private void handleNormalCases(List<JudgeInfo> judgeInfoList, List<ExecuteCaseInfo> executeCaseInfoList,
                                   List<String> outputList, JudgeConfig judgeConfig, int caseCount) {
        for (int i = 0; i < caseCount; i++) {
            JudgeInfo info = new JudgeInfo();

            if (i >= executeCaseInfoList.size()) {
                info.setJudgeCaseResult(JudgeInfoMessageEnum.SYSTEM_ERROR);
                info.setErrorMessage("该用例未执行");
                judgeInfoList.add(info);
                continue;
            }

            ExecuteCaseInfo caseInfo = executeCaseInfoList.get(i);
            info.setOutput(caseInfo.getOutput());
            info.setTime(caseInfo.getTime());
            info.setMemory(caseInfo.getMemory());
            info.setErrorMessage(caseInfo.getErrorMessage());

            JudgeInfoMessageEnum judgeCaseResult = determineCaseResult(
                    outputList.get(i), caseInfo.getErrorMessage(),
                    caseInfo.getOutput(), caseInfo.getTime(), judgeConfig, caseInfo.getMemory()
            );
            info.setJudgeCaseResult(judgeCaseResult.getValue());
            judgeInfoList.add(info);
        }
    }

    /**
     * 将 ExecuteErrorMessageEnum 映射为 JudgeInfoMessageEnum
     * null -> SYSTEM_ERROR
     */
    private static JudgeInfoMessageEnum convertExecuteErrorToJudgeResult(ExecuteErrorMessageEnum errorEnum) {
        if (errorEnum == null) {
            return JudgeInfoMessageEnum.SYSTEM_ERROR;
        }
        switch (errorEnum) {
            case COMPILE_ERROR:
                return JudgeInfoMessageEnum.COMPILE_ERROR;
            case DANGEROUS_CODE:
                return JudgeInfoMessageEnum.DANGEROUS_CODE;
            default:
                return JudgeInfoMessageEnum.SYSTEM_ERROR;
        }
    }


    private static JudgeInfo createJudgeInfoWithError(String errorType, String errorMessage) {
        JudgeInfo info = new JudgeInfo();
        info.setJudgeCaseResult(errorType);
        info.setErrorMessage(errorMessage);
        return info;
    }

    /**
     * 处理判题服务出现错误
     */
    private void handleJudgeException(long questionSubmitId, String errorMessage) {
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED);
        questionSubmitUpdate.setJudgeResult(JudgeInfoMessageEnum.SYSTEM_ERROR);
        List<JudgeInfo> judgeInfoList = new ArrayList<>();
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setJudgeCaseResult(JudgeInfoMessageEnum.SYSTEM_ERROR);
        judgeInfo.setErrorMessage(errorMessage);
        judgeInfoList.add(judgeInfo);
        questionSubmitUpdate.setJudgeInfoList(JSONUtil.toJsonStr(judgeInfoList));
        questionService.updateQuestionSubmit(questionSubmitUpdate);
    }

    /**
     * 判题用例结果
     */
    private static JudgeInfoMessageEnum determineCaseResult(String stdOutput, String errorMessage,
                                                            String caseOutput, Long time, JudgeConfig judgeConfig, Long memory) {

        if (errorMessage != null && !errorMessage.isEmpty()) {
            return JudgeInfoMessageEnum.RUNTIME_ERROR;
        } else if (!stdOutput.equals(caseOutput)) {
            return JudgeInfoMessageEnum.WRONG_ANSWER;
        } else if (time > judgeConfig.getTimeLimit()) {
            return JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
        } else if (memory > judgeConfig.getMemoryLimit()) {
            return JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
        } else {
            return JudgeInfoMessageEnum.ACCEPTED;
        }
    }

    /**
     * 判断总体结果
     */
    private static JudgeInfoMessageEnum determineFinalResult(List<JudgeInfo> judgeInfoList) {
        if (judgeInfoList == null || judgeInfoList.isEmpty()) {
            return JudgeInfoMessageEnum.SYSTEM_ERROR;
        }

        // 优先级从高到低排序
        for (JudgeInfo info : judgeInfoList) {
            String result = info.getJudgeCaseResult();
            if (JudgeInfoMessageEnum.SYSTEM_ERROR.getValue().equals(result)) {
                return JudgeInfoMessageEnum.SYSTEM_ERROR;
            }
            if (JudgeInfoMessageEnum.RUNTIME_ERROR.getValue().equals(result)) {
                return JudgeInfoMessageEnum.RUNTIME_ERROR;
            }
            if (JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue().equals(result)) {
                return JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            }
            if (JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getValue().equals(result)) {
                return JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            }
            if (JudgeInfoMessageEnum.WRONG_ANSWER.getValue().equals(result)) {
                return JudgeInfoMessageEnum.WRONG_ANSWER;
            }
        }
        // 所有用例都通过
        return JudgeInfoMessageEnum.ACCEPTED;
    }
}
