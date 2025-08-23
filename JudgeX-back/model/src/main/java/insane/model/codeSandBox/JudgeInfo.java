package insane.model.codeSandBox;

import insane.model.enums.JudgeInfoMessageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 判题信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeInfo {
    /**
     用例结果, JudgeInfoMessageEnum(Accepted/Wrong Answer/...)
     */
    private String judgeCaseResult;
    /**
     用例的错误信息
     */
    private String errorMessage;
    /**
     输出
     */
    private String output;
    /**
     消耗时间(ms)
     */
    private Long time;
    /**
     消耗内存(B)
     */
    private Long memory;

    public void setJudgeCaseResult(JudgeInfoMessageEnum judgeCaseResult) {
        if (judgeCaseResult == null) {
            this.judgeCaseResult = null;
        } else {
            this.judgeCaseResult = judgeCaseResult.getValue();
        }
    }

    public void setJudgeCaseResult(String judgeCaseResult) {
        JudgeInfoMessageEnum enumByValue = JudgeInfoMessageEnum.getEnumByValue(judgeCaseResult);
        setJudgeCaseResult(enumByValue);
    }
}
