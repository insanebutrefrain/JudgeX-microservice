package insane.model.dto.questionSubmit;

import insane.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 题目提交查询请求(一条)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitGetRequest extends PageRequest implements Serializable {
    /**
     提交 id
     */
    private Long questionSubmitId;

    private static final long serialVersionUID = 1L;
}
