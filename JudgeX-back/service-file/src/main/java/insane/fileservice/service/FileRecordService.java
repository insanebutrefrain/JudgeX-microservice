package insane.fileservice.service;

import insane.model.dto.question.JudgeCase;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件记录服务
 */
public interface FileRecordService {

    boolean uploadAvatar(MultipartFile file, Long userId);

    byte[] getAvatar(Long userId);

    boolean uploadJudgecase(Long questionId, MultipartFile[] inputFiles, MultipartFile[] outputFiles);

    List<JudgeCase> getJudgecase(Long questionId);
}
