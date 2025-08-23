package insane.fileservice.controller.inner;

import insane.client.service.FileFeignClient;
import insane.fileservice.service.FileRecordService;
import insane.model.dto.question.JudgeCase;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/inner")
public class FileRecorderInnerController implements FileFeignClient {

    @Resource
    private FileRecordService fileRecordService;

    /**
     * 上传用户头像
     *
     * @param file   头像文件
     * @param userId 用户ID
     * @return 上传结果
     */
    @PostMapping("/avatar/upload")
    public boolean uploadAvatar(@RequestPart("file") MultipartFile file, @RequestParam("userId") Long userId) {
        return fileRecordService.uploadAvatar(file, userId);
    }

    /**
     * 获取用户头像
     *
     * @param userId 用户ID
     * @return 头像字节数据
     */
    @GetMapping("/avatar/{userId}")
    public byte[] getAvatar(@PathVariable("userId") Long userId) {
        return fileRecordService.getAvatar(userId);
    }

    /**
     * 上传判题用例文件
     *
     * @param questionId  题目ID
     * @param inputFiles  输入文件数组
     * @param outputFiles 输出文件数组
     * @return 上传结果
     */
    @PostMapping("/judgecase/upload")
    public boolean uploadJudgecase(@RequestParam("questionId") Long questionId,
                                   @RequestPart("inputFiles") MultipartFile[] inputFiles,
                                   @RequestPart("outputFiles") MultipartFile[] outputFiles) {
        return fileRecordService.uploadJudgecase(questionId, inputFiles, outputFiles);
    }

    /**
     * 获取判题用例
     *
     * @param questionId 题目ID
     * @return 判题用例列表
     */
    @GetMapping("/judgecase/{questionId}")
    public List<JudgeCase> getJudgecase(@PathVariable("questionId") Long questionId) {
        return getJudgecase(questionId);
    }

}
