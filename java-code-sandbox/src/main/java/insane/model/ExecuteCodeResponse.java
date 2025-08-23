package insane.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeResponse {

    /**
     判题失败码
     */
    private String executeErrorMessageEnum;

    /**
     判题失败信息
     */
    private String message;

    /**
     每个测试用例对应的输出、运行时间、消耗内存和错误输出
     */
    private List<ExecuteCaseInfo> executeCaseInfoList;

}
