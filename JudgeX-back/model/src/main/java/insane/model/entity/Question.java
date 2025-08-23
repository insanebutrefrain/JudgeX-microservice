package insane.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 题目
 */
@TableName(value = "question")
@Data
public class Question {
    /**
     id
     */
    @TableId(type = IdType.AUTO)
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
     标准答案
     */
    private String answer;

    /**
     提交数
     */
    private Integer submitNum;

    /**
     通过数
     */
    private Integer acceptNum;

    /**
     测试用例版本号(时间戳)
     */
    private String judgeCaseVersion;

    /**
     判题配置(json对象)
     */
    private String judgeConfig;

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
}
