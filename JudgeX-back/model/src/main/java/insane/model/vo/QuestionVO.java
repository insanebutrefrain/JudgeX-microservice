package insane.model.vo;

import com.google.gson.Gson;
import insane.model.dto.question.JudgeConfig;
import insane.model.entity.Question;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 题目视图
 */
@Slf4j
@Data
public class QuestionVO implements Serializable {

    private final static Gson GSON = new Gson();
    /**
     id
     */
    private Long id;

    /**
     标题
     */
    private String title;

    /**
     内容
     */
    private String content;

    /**
     标签(json数组)
     */
    private String tagList;

    /**
     提交数
     */
    private Integer submitNum;

    /**
     通过数
     */
    private Integer acceptNum;

    /**
     判题配置
     */
    private JudgeConfig judgeConfig;

    /**
     点赞数
     */
    private Integer thumbNum;

    /**
     收藏数
     */
    private Integer favourNum;

    /**
     创建用户id
     */
    private Long userId;
    /**
     创建题目人的信息
     */
    private UserVO userVO;

    /**
     包装类转对象

     @param questionVO
     @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);

        JudgeConfig judgeConfig = questionVO.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        return question;
    }

    /**
     对象转包装类

     @param question
     @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);

        JudgeConfig judgeConfig = GSON.fromJson(question.getJudgeConfig(), JudgeConfig.class);
        questionVO.setJudgeConfig(judgeConfig);

        return questionVO;
    }

    private static final long serialVersionUID = 1L;
}
