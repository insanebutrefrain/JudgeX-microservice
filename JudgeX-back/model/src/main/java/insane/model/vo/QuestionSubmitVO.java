package insane.model.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import insane.model.codeSandBox.JudgeInfo;
import insane.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 题目提交视图
 */
@Data
public class QuestionSubmitVO implements Serializable {

    private final static Gson GSON = new Gson();
    /**
     提交记录id
     */
    private Long id;

    /**
     题目 id
     */
    private Long questionId;

    /**
     提交用户 id
     */
    private Long userId;

    /**
     编程语言
     */
    private String language;

    /**
     用户代码
     */
    private String code;

    /**
     判题状态(0待判题,1判题中,2判题成功,3判题发生系统错误)
     */
    private Integer status;

    /**
     判题信息([{用例结果JudgeInfoMessageEnum,用例错误信息,用例输出,用例耗时,用例使用内存}])
     */
    private List<JudgeInfo> judgeInfoList;

    /**
     判题结果, JudgeInfoMessageEnum(Accepted/Wrong Answer/...)
     */
    private String judgeResult;
    /**
     创建时间
     */
    private Date createTime;

    /**
     更新时间
     */
    private Date updateTime;


    /**
     包装类转对象

     @param questionVO
     @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionVO, questionSubmit);

        List<JudgeInfo> judgeInfoList = questionVO.getJudgeInfoList();
        if (judgeInfoList != null) {
            questionSubmit.setJudgeInfoList(GSON.toJson(judgeInfoList));
        }
        return questionSubmit;
    }

    /**
     对象转包装类
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        String judgeInfoListString = questionSubmit.getJudgeInfoList();
        List<JudgeInfo> judgeInfoList = GSON.fromJson(judgeInfoListString, new TypeToken<List<JudgeInfo>>() {}.getType());
        questionSubmitVO.setJudgeInfoList(judgeInfoList);
        return questionSubmitVO;
    }

    private static final long serialVersionUID = 1L;
}
