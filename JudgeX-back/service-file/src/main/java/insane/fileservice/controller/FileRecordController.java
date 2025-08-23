package insane.fileservice.controller;

import insane.client.service.UserFeignClient;
import insane.common.common.BaseResponse;
import insane.common.common.ResultUtils;
import insane.fileservice.service.FileRecordService;
import insane.model.dto.question.JudgeCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 文件管理接口
 */
@RestController
@Slf4j
public class FileRecordController {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private FileRecordService fileRecordService;

    /**
     上传头像
     */
    @PostMapping("/upload/avatar")
    public BaseResponse<Boolean> uploadAvatar(@RequestPart MultipartFile file,
                                              HttpServletRequest request) {
        Long userId = userFeignClient.getLoginUser(request).getId();
        boolean result = fileRecordService.uploadAvatar(file, userId);
        return ResultUtils.success(result);
    }

    /**
     获取头像
     */
    @GetMapping("/get/avatar")
    public BaseResponse<byte[]> getAvatar(HttpServletRequest request) {
        Long userId = userFeignClient.getLoginUser(request).getId();

        byte[] avatarBytes = fileRecordService.getAvatar(userId);
        return ResultUtils.success(avatarBytes);
    }

    /**
     上传题目测试用例
     */
    @PostMapping("/upload/judgecase")
    public BaseResponse<Boolean> uploadJudgecase(
            @RequestParam Long questionId,
            @RequestPart MultipartFile[] inputFiles,
            @RequestPart MultipartFile[] outputFiles,
            HttpServletRequest request) {
        boolean result = fileRecordService.uploadJudgecase(questionId, inputFiles, outputFiles);
        return ResultUtils.success(result);
    }

    /**
     获取题目测试用例
     */
    @PostMapping("/get/judgecase")
    public BaseResponse<List<JudgeCase>> getJudgecase(
            @RequestParam Long questionId,
            HttpServletRequest request) {
        List<JudgeCase> judgeCaseList = fileRecordService.getJudgecase(questionId);
        return ResultUtils.success(judgeCaseList);
    }
}
