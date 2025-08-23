package insane.client.service;

import insane.model.dto.question.JudgeCase;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件记录服务
 */
@FeignClient(name = "service-file", path = "/api/file/inner")
public interface FileFeignClient {

    /**
     * 上传用户头像
     * @param file 头像文件
     * @param userId 用户ID
     * @return 上传结果
     */
    @PostMapping("/avatar/upload")
    boolean uploadAvatar(@RequestPart("file") MultipartFile file, @RequestParam("userId") Long userId);

    /**
     * 获取用户头像
     * @param userId 用户ID
     * @return 头像字节数据
     */
    @GetMapping("/avatar/{userId}")
    byte[] getAvatar(@PathVariable("userId") Long userId);

    /**
     * 上传判题用例文件
     * @param questionId 题目ID
     * @param inputFiles 输入文件数组
     * @param outputFiles 输出文件数组
     * @return 上传结果
     */
    @PostMapping("/judgecase/upload")
    boolean uploadJudgecase(@RequestParam("questionId") Long questionId,
                            @RequestPart("inputFiles") MultipartFile[] inputFiles,
                            @RequestPart("outputFiles") MultipartFile[] outputFiles);

    /**
     * 获取判题用例
     * @param questionId 题目ID
     * @return 判题用例列表
     */
    @GetMapping("/judgecase/{questionId}")
    List<JudgeCase> getJudgecase(@PathVariable("questionId") Long questionId);
}
