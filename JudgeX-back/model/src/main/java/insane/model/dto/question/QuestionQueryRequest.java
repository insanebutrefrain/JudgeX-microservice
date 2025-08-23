package insane.model.dto.question;

import insane.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

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
     创建用户id
     */
    private Long userId;


    private static final long serialVersionUID = 1L;
}
