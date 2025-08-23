package insane.questionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import insane.model.dto.questionSubmit.QuestionSubmitAddRequest;
import insane.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import insane.model.entity.QuestionSubmit;
import insane.model.entity.User;
import insane.model.vo.QuestionSubmitVO;

/**
 @author 32461
 @description 针对表【question_submit(题目提交)】的数据库操作Service
 @createDate 2025-03-29 15:18:50 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     点赞

     @param questionSubmitAddRequest 题目提交信息
     @param loginUser
     @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     获取查询条件
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);


    /**
     获取题目封装
     */
    QuestionSubmitVO getQuestionSubmitVO(Long questionSubmitId);


    /**
     分页获取题目封装
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage );

}
