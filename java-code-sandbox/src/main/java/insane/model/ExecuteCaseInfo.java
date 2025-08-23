package insane.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCaseInfo {
    /**
     * 运行出现错误时的输出结果
     */
    private String errorMessage;
    /**
     测试用例输出
     */
    private String output;
    /**
     该用例消耗的时间
     */
    private Long time;
    /**
     该用例消耗的内存
     */
    private Long memory;

}
