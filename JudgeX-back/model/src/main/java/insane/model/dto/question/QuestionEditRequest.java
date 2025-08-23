package insane.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 编辑请求
 */
@Data
public class QuestionEditRequest implements Serializable {

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
     标签
     */
    private List<String> tagList;

    /**
     标准答案
     */
    private String answer;


    /**
     判题配置
     */
    private JudgeConfig judgeConfig;


    private static final long serialVersionUID = 1L;
}
