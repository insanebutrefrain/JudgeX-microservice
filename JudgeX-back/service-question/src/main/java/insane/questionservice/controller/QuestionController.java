package insane.questionservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import insane.client.service.UserFeignClient;
import insane.common.common.BaseResponse;
import insane.common.common.DeleteRequest;
import insane.common.common.ErrorCode;
import insane.common.common.ResultUtils;
import insane.common.constant.CommonConstant;
import insane.common.constant.UserConstant;
import insane.common.exception.BusinessException;
import insane.common.exception.ThrowUtils;
import insane.common.annotation.AuthCheck;
import insane.model.dto.question.*;
import insane.model.dto.questionSubmit.QuestionSubmitAddRequest;
import insane.model.dto.questionSubmit.QuestionSubmitGetRequest;
import insane.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import insane.model.entity.Question;
import insane.model.entity.QuestionSubmit;
import insane.model.entity.User;
import insane.model.enums.UserRoleEnum;
import insane.model.vo.QuestionSubmitVO;
import insane.model.vo.QuestionVO;
import insane.questionservice.service.QuestionService;
import insane.questionservice.service.QuestionSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 题目接口
 */
@RestController
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionSubmitService questionSubmitService;


    private final static Gson GSON = new Gson();

    /**
     创建

     @param questionAddRequest
     @param request
     @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tagList = questionAddRequest.getTagList();
        if (tagList != null) {
            question.setTagList(GSON.toJson(tagList));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, true);

        User loginUser = userFeignClient.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     删除

     @param deleteRequest
     @param request
     @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        if (oldQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userFeignClient.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     更新（仅管理员）

     @param questionUpdateRequest
     @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tagList = questionUpdateRequest.getTagList();
        if (tagList != null) {
            question.setTagList(GSON.toJson(tagList));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     根据 id 获取题目(脱敏)
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        if (!Objects.equals(loginUser.getId(), question.getUserId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     根据 id 获取
     */
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(question);
    }

    /**
     分页获取列表（封装类）

     @param questionQueryRequest
     @param request
     @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        Page<Question> questionPage;
        //if (StringUtils.isNotBlank(questionQueryRequest.getTitle()) ||
        //        StringUtils.isNotBlank(questionQueryRequest.getContent())) {// 题目名称和内容查询
        //    questionPage = questionEsService.searchFromEs(questionQueryRequest);
        //} else { // 常规查询
            questionPage = questionService.page(new Page<>(current, size),
                    questionService.getQueryWrapper(questionQueryRequest));
        //}
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     分页获取题目列表 (仅管理员可见)
     */
    @PostMapping("/list/page/vo_admin")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionVOByPage_Admin(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                   HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage;
        //if (StringUtils.isNotBlank(questionQueryRequest.getTitle()) ||
        //        StringUtils.isNotBlank(questionQueryRequest.getContent())) {// 题目名称和内容查询
        //    questionPage = questionEsService.searchFromEs(questionQueryRequest);
        //} else { // 常规查询
            questionPage = questionService.page(new Page<>(current, size),
                    questionService.getQueryWrapper(questionQueryRequest));
        //}
        return ResultUtils.success(questionPage);
    }


    // endregion


    /**
     编辑（用户）

     @param questionEditRequest
     @param request
     @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tagList = questionEditRequest.getTagList();
        if (tagList != null) {
            question.setTagList(GSON.toJson(tagList));
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userFeignClient.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }


    /**
     提交代码

     @param questionSubmitAddRequest
     @param request
     @return submitId 提交id
     */
    @PostMapping("/question_submit")
    public BaseResponse<Long> doSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final User loginUser = userFeignClient.getLoginUser(request);
        long submitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(submitId);
    }

    /**
     获取代码评测结果
     */
    @PostMapping("/question_submit/get")
    public BaseResponse<QuestionSubmitVO> getQuestionSubmit(@RequestBody QuestionSubmitGetRequest questionSubmitGetRequest, HttpServletRequest request) {
        if (questionSubmitGetRequest == null || questionSubmitGetRequest.getQuestionSubmitId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long questionSubmitId = questionSubmitGetRequest.getQuestionSubmitId();
        QuestionSubmitVO questionSubmitVO = questionSubmitService.getQuestionSubmitVO(questionSubmitId);
        return ResultUtils.success(questionSubmitVO);
    }

    /**
     分页获取题目提交列表

     @return submitId 提交id
     */
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        if (questionSubmitQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        String userRole = loginUser.getUserRole();
        if (!UserRoleEnum.ADMIN.getValue().equals(userRole)) {// 非管理员, 只能查询自己的提交
            questionSubmitQueryRequest.setUserId(loginUser.getId());
        }
        questionSubmitQueryRequest.setSortField("createTime");
        questionSubmitQueryRequest.setSortOrder(CommonConstant.SORT_ORDER_DESC);
        long current = questionSubmitQueryRequest.getCurrent();
        long pageSize = questionSubmitQueryRequest.getPageSize();
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(
                new Page<>(current, pageSize),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest)
        );

        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage));
    }

}
