package insane.utils;

/*
禁止库:
sun.misc.Unsafe
java.nio.*
java.net.*
java.lang.reflect.*
java.lang.Thread
java.lang.ProcessBuilder
javax.script.*
javax.xml.*
java.sql.*
java.rmi.*
java.awt.*
javax.swing.*
允许库:
java.util.*
java.io.*
java.math.*
 */

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 代码校验 (文件交互、命令执行、网络、反射、进程等操作禁止)
 */
@Slf4j
public class JavaCodeValidator {
    private JavaCodeValidator() {}

    private static final List<Pattern> FORBIDDEN_PATTERNS = new ArrayList<>();

    static {
        // 禁止的import语句
        FORBIDDEN_PATTERNS.add(Pattern.compile(
                "^\\s*import\\s+(static\\s+)?" +
                        "(sun\\.misc\\.Unsafe|" +
                        "java\\.nio\\..*|" +
                        "java\\.net\\..*|" +
                        "java\\.lang\\.reflect\\..*|" +
                        "javax\\.script\\..*|" +
                        "javax\\.xml\\..*|" +
                        "java\\.sql\\..*|" +
                        "java\\.rmi\\..*|" +
                        "java\\.awt\\..*|" +
                        "javax\\.swing\\..*)\\s*;",
                Pattern.MULTILINE
        ));
        List<String> regexes = Arrays.asList(
                // 禁止的完全限定类名使用
                "\\b(java\\.nio\\..*|" +
                        "java\\.net\\..*|" +
                        "java\\.lang\\.reflect\\..*|" +
                        "java\\.lang\\.ProcessBuilder\\b|" +
                        "javax\\.script\\..*|" +
                        "javax\\.xml\\..*|" +
                        "java\\.sql\\..*|" +
                        "java\\.rmi\\..*|" +
                        "java\\.awt\\..*|" +
                        "javax\\.swing\\..*)\\b",
                // 危险类实例化
                "\\bnew\\s+ProcessBuilder\\s*\\(",
                "\\bnew\\s+Thread\\s*\\(",
                "\\bnew\\s+ScriptEngineManager\\s*\\(",
                // 文件操作相关检测
                "\\bnew\\s+FileOutputStream\\s*\\(",// 禁止创建文件输出流
                "\\bnew\\s+FileWriter\\s*\\(", // 禁止创建文件写入器
                "\\bFile\\s*\\.\\s*createNewFile\\s*\\(", // 禁止静态文件创建方法
                "\\.\\s*createNewFile\\s*\\(",       // 禁止实例文件创建方法
                "\\.\\s*mkdirs?\\s*\\(",             // 禁止创建目录
                "\\.\\s*renameTo\\s*\\(",            // 禁止文件重命名
                // 危险方法调用
                "\\bRuntime\\s*\\.\\s*getRuntime\\s*\\(\\s*\\)\\s*\\.\\s*exec\\s*\\(",
                "\\bClass\\s*\\.\\s*forName\\s*\\(",
                "\\bMethod\\s*\\.\\s*invoke\\s*\\(",
                "\\bConstructor\\s*\\.\\s*newInstance\\s*\\(",
                "\\bDriverManager\\s*\\.\\s*getConnection\\s*\\("
        );
        regexes.forEach(regex -> FORBIDDEN_PATTERNS.add(Pattern.compile(regex, Pattern.MULTILINE)));


    }

    /**
     校验代码合法性

     @param code
     @return
     */
    public static boolean validateCode(String code) {
        String processedCode = preprocess(code);
        for (Pattern pattern : FORBIDDEN_PATTERNS) {
            if (pattern.matcher(processedCode).find()) {
                log.warn("危险匹配: {}", pattern);
                return false;
            }
        }
        return true;
    }

    /**
     预加工代码: 去除注释、引号
     */
    private static String preprocess(String code) {
        String processed = removeComments(code);
        // 移除双引号字符串内容
        processed = processed.replaceAll("\"(?:\\\\\"|[^\"\\\\])*\"", "");
        // 移除单引号字符内容
        processed = processed.replaceAll("'(?:\\\\'|[^'\\\\])'", "");
        return processed;
    }

    /**
     去除代码中的注释
     */
    private static String removeComments(String code) {
        code = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL).matcher(code).replaceAll("");
        code = Pattern.compile("//.*").matcher(code).replaceAll("");
        return code;
    }

}
