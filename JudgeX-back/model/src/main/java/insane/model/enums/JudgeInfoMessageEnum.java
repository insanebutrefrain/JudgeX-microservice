package insane.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 题目提交状态枚举
 */
public enum JudgeInfoMessageEnum {

    ACCEPTED("答案正确", "Accepted"),
    WRONG_ANSWER("答案错误", "Wrong Answer"),
    TIME_LIMIT_EXCEEDED("运行超时", "Time Limit Exceeded"),
    MEMORY_LIMIT_EXCEEDED("内存超限", "Memory Limit Exceeded"),
    OUTPUT_LIMIT_EXCEEDED("输出超限", "Output Limit Exceeded"),
    COMPILE_ERROR("编译错误", "Compile Error"),
    RUNTIME_ERROR("运行错误", "Runtime Error"),
    PRESENTATION_ERROR("输出格式错误", "Presentation Error"),
    SYSTEM_ERROR("系统错误", "System Error"),
    DANGEROUS_CODE("危险代码", "Dangerous Code"),
    WAITING("等待中", "Waiting");


    private final String text;

    private final String value;

    JudgeInfoMessageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     获取值列表

     @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     根据 value 获取枚举

     @param value
     @return
     */
    public static JudgeInfoMessageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoMessageEnum anEnum : JudgeInfoMessageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
