package insane.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public enum ExecuteErrorMessageEnum {

    SUCCEED("成功运行", "Success"),

    COMPILE_ERROR("编译错误", "Compile Error"),

    DANGEROUS_OPERATION("危险操作", "Dangerous Operation"),

    RUNTIME_ERROR("运行错误", "Runtime Error"),

    CompileError("编译错误", "Compile Error"),


    TIME_EXCEEDED("运行超时", "Time Exceeded"),

    MEMORY_EXCEEDED("内存超限", "Memory Exceeded"),

    SYSTEM_ERROR("系统错误", "System Error");

    private final String text;

    private final String value;

    ExecuteErrorMessageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
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
