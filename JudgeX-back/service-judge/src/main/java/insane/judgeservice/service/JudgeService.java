package insane.judgeservice.service;


import insane.model.entity.QuestionSubmit;

public interface JudgeService {
    /**
     判题服务
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
