package insane.model.dto.questionSubmit;

import insane.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 题目提交查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     题目 id
     */
    private Long questionId;

    /**
     用户 id
     */
    private Long userId;
    /**
     编程语言
     */
    private String language;

    /**
     判题状态(0待判题,1判题中,2成功,3失败)
     */

    private Integer status;

    /**
     判题结果, JudgeInfoMessageEnum(Accepted/Wrong Answer/...)
     */
    private String judgeResult;

    private static final long serialVersionUID = 1L;
}
