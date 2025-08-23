package insane.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import insane.model.enums.JudgeInfoMessageEnum;
import insane.model.enums.QuestionSubmitStatusEnum;
import lombok.Data;

import java.util.Date;

/**
 题目提交

 @TableName question_submit */
@TableName(value = "question_submit")
@Data
public class QuestionSubmit {
    /**
     id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     判题状态(0待判题,1判题中,2成功,3失败)
     */
    private Integer status;

    /**
     判题信息(json数组, [{用例结果,信息,耗时,内存}])
     */
    private String judgeInfoList;

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
     是否删除
     */
    private Integer isDelete;

    public void setStatus(QuestionSubmitStatusEnum status) {
        if (status == null) {
            this.status = null;
        } else {
            this.status = status.getValue();
        }
    }

    public void setStatus(Integer status) {
        QuestionSubmitStatusEnum enumByValue = QuestionSubmitStatusEnum.getEnumByValue(status);
        setStatus(enumByValue);
    }

    public void setJudgeResult(JudgeInfoMessageEnum judgeResult) {
        if (judgeResult == null) {
            this.judgeResult = null;
        } else {
            this.judgeResult = judgeResult.getValue();
        }
    }

    public void setJudgeResult(String judgeResult) {
        JudgeInfoMessageEnum enumByValue = JudgeInfoMessageEnum.getEnumByValue(judgeResult);
        setJudgeResult(enumByValue);
    }
}
