package insane.questionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import insane.model.dto.question.QuestionQueryRequest;
import insane.model.entity.Question;
import insane.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 @author 32461
 @description 针对表【question(题目)】的数据库操作Service
 @createDate 2025-08-02 15:17:00 */
public interface QuestionService extends IService<Question> {
    /**
     校验

     @param question
     @param add       */
    void validQuestion(Question question, boolean add);



    /**
     获取查询条件

     @param questionQueryRequest
     @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);


    /**
     获取题目封装

     @param question
     @param request
     @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     分页获取题目封装

     @param questionPage
     @param request
     @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     获取测试用例版本号
     */
    String getJudgeCaseVersion(Long questionId);
    String getJudgeCaseVersionNullable(Long questionId);


    List<Question> listQuestionsAfterUpdateTime(Date updateTime);


}
