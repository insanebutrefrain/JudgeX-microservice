package insane.questionservice.controller.inner;

import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import insane.client.service.QuestionFeignClient;
import insane.model.entity.Question;
import insane.model.entity.QuestionSubmit;
import insane.questionservice.service.QuestionService;
import insane.questionservice.service.QuestionSubmitService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    /**
     * 更新题目
     *
     * @return
     */
    @Override
    @PostMapping("/update")
    public UpdateChainWrapper<Question> update(){
        return questionService.update();
    };

    /**
     * 获取测试用例版本号
     *
     * @param questionId
     * @return
     */
    @GetMapping("/get/judge_case_version")
    public String getJudgeCaseVersion(Long questionId){
        return questionService.getJudgeCaseVersion(questionId);
    };

    /**
     * 获取所有题目
     */
    @GetMapping("/list")
    public List<Question> list(){
        return questionService.list();
    };


    /**
     * 根据 id 获取
     *
     * @param questionId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }

    /**
     * 获取题目封装
     *
     * @param questionSubmitId
     * @return
     */
    @Override
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }


    /**
     * 更新题目
     *
     * @param questionSubmit
     * @return
     */
    @Override
    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmit(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

}
