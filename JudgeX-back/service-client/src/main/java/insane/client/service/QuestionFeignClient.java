package insane.client.service;

import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import insane.model.entity.Question;
import insane.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 32461
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2025-08-05 03:19:18
 */
@FeignClient(name = "service-question", path = "/api/question/inner")
public interface QuestionFeignClient {
    /**
     * 更新题目
     *
     * @return
     */
    @PostMapping("/update")
    UpdateChainWrapper<Question> update();

    /**
     * 获取测试用例版本号
     *
     * @param questionId
     * @return
     */
    @GetMapping("/get/judge_case_version")
    String getJudgeCaseVersion(Long questionId);

    /**
     * 获取所有题目
     */
    @GetMapping("/list")
    List<Question> list();

    /**
     * 根据 id 获取
     *
     * @param questionId
     * @return
     */
    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    /**
     * 获取题目封装
     *
     * @param questionSubmitId
     * @return
     */
    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId);


    /**
     * 更新题目
     *
     * @param questionSubmit
     * @return
     */
    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmit(@RequestBody QuestionSubmit questionSubmit);
}
