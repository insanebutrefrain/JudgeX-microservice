package insane.model.codeSandBox;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 代码沙箱响应状态
 需要是{@link insane.model.enums.JudgeInfoMessageEnum}的子集
 */
public enum ExecuteErrorMessageEnum {

    COMPILE_ERROR("编译错误", "Compile Error"),
    SYSTEM_ERROR("系统错误", "Runtime Error"),
    DANGEROUS_CODE("危险代码", "Dangerous Code")    ;
    private final String text;

    private final String value;

    ExecuteErrorMessageEnum(String text, String value) {
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
    public static ExecuteErrorMessageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ExecuteErrorMessageEnum anEnum : ExecuteErrorMessageEnum.values()) {
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
