package insane.fileservice.service.impl;

import insane.client.service.QuestionFeignClient;
import insane.client.service.UserFeignClient;
import insane.common.common.ErrorCode;
import insane.common.exception.BusinessException;
import insane.fileservice.service.FileRecordService;
import insane.model.dto.question.JudgeCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileRecordServiceImpl implements FileRecordService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionFeignClient questionFeignClient;

    private final String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

    @Value("${file.prefix.user-dir:user}")
    private String userDirPrefix;

    @Value("${file.prefix.judge-case-dir:judge-case}")
    private String judgeCaseDirPrefix;

    @Override
    public boolean uploadAvatar(MultipartFile file, Long userId) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        try {
            // 生成新版本号
            String newVersion = UUID.randomUUID().toString();

            // 创建版本目录
            String versionDir = userDirPrefix + File.separator + userId + File.separator + newVersion;
            Path versionPath = Paths.get(uploadDir, versionDir);
            Files.createDirectories(versionPath);

            // 获取文件扩展名
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            String fileName = "avatar." + fileExtension;

            // 保存文件
            Path filePath = versionPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 更新用户头像版本
            boolean update = userFeignClient.update()
                    .eq("id", userId)
                    .set("userAvatarVersion", newVersion)
                    .update();

            if (!update) {
                // 更新失败，删除新上传的文件
                FileUtils.deleteDirectory(versionPath.toFile());
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "头像版本更新失败");
            }

            return true;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "头像上传失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] getAvatar(Long userId) {
        try {
            // 获取当前版本号
            String currentVersion = userFeignClient.getUserAvatarVersion(userId);

            // 构建文件路径
            String versionDir = userDirPrefix + File.separator + userId + File.separator + currentVersion;
            Path versionPath = Paths.get(uploadDir, versionDir);

            // 查找目录中的头像文件（因为扩展名可能不同）
            File[] files = versionPath.toFile().listFiles((dir, name) -> name.startsWith("avatar."));
            if (files == null || files.length == 0) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "头像文件未找到");
            }
            return Files.readAllBytes(files[0].toPath());// 逻辑上只会有一个
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取头像失败: " + e.getMessage());
        }
    }

    @Override
    public boolean uploadJudgecase(Long questionId, MultipartFile[] inputFiles, MultipartFile[] outputFiles) {
        int n = inputFiles.length;
        if (outputFiles.length != n) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入输出文件数量不匹配");
        }

        try {
            // 生成新版本号
            String newVersion = UUID.randomUUID().toString();

            // 创建版本目录
            String versionDir = judgeCaseDirPrefix + File.separator + questionId + File.separator + newVersion;
            Path versionPath = Paths.get(uploadDir, versionDir);
            Files.createDirectories(versionPath);

            // 保存所有测试用例文件
            for (int i = 0; i < n; i++) {
                String inputFileName = "in" + (i + 1) + ".txt";
                String outputFileName = "out" + (i + 1) + ".txt";

                inputFiles[i].transferTo(versionPath.resolve(inputFileName).toFile());
                outputFiles[i].transferTo(versionPath.resolve(outputFileName).toFile());
            }

            // 更新题目测试用例版本
            boolean updateSuccess = questionFeignClient.update()
                    .eq("id", questionId)
                    .set("judgeCaseVersion", newVersion)
                    .update();

            if (!updateSuccess) {
                // 更新失败, 删除新上传的文件
                FileUtils.deleteDirectory(versionPath.toFile());
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "测试用例版本更新失败");
            }

            return true;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "测试用例上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<JudgeCase> getJudgecase(Long questionId) {
        try {
            // 获取当前测试用例版本号
            String currentVersion = questionFeignClient.getJudgeCaseVersion(questionId);

            // 构建测试用例目录路径
            String versionDir = judgeCaseDirPrefix + File.separator + questionId + File.separator + currentVersion;
            Path versionPath = Paths.get(uploadDir, versionDir);

            // 读取所有测试用例文件
            File[] inputFiles = versionPath.toFile().listFiles((dir, name) -> name.startsWith("in") && name.endsWith(".txt"));
            if (inputFiles == null || inputFiles.length == 0) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "测试用例未找到");
            }

            List<JudgeCase> judgeCases = new ArrayList<>();
            for (File inputFile : inputFiles) {
                String inputFileName = inputFile.getName();
                int caseNumber = Integer.parseInt(inputFileName.substring(2, inputFileName.indexOf('.')));
                String outputFileName = "out" + caseNumber + ".txt";
                Path outputPath = versionPath.resolve(outputFileName);
                File outputFile = outputPath.toFile();
                if (!outputFile.exists()) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "测试用例不完整: " + outputFileName + " 不存在");
                }

                JudgeCase judgeCase = new JudgeCase();
                judgeCase.setInput(com.alibaba.cloud.commons.io.FileUtils.readFileToString(inputFile, "UTF-8"));
                judgeCase.setOutput(com.alibaba.cloud.commons.io.FileUtils.readFileToString(outputFile, "UTF-8"));
                judgeCases.add(judgeCase);
            }

            return judgeCases;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取测试用例失败: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "测试用例文件名格式错误");
        }
    }

}
