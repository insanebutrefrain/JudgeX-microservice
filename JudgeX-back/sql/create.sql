create table question
(
    id               bigint auto_increment comment 'id'
        primary key,
    title            varchar(512)                       null comment '标题',
    content          text                               null comment '内容',
    tagList          varchar(1024)                      null comment '标签(json数组)',
    answer           text                               null comment '标准答案',
    submitNum        int      default 0                 not null comment '提交数',
    acceptNum        int      default 0                 not null comment '通过数',
    judgeCaseVersion mediumtext                         null comment '版本号',
    judgeConfig      text                               null comment '判题配置(json对象)',
    thumbNum         int      default 0                 not null comment '点赞数',
    favourNum        int      default 0                 not null comment '收藏数',
    userId           bigint                             not null comment '创建用户id',
    createTime       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete         tinyint  default 0                 not null comment '是否删除'
)
    comment '题目' collate = utf8mb4_unicode_ci;

create index idx_userId
    on question (userId);

create index question_createTime_index
    on question (createTime);

create index question_updateTime_index
    on question (updateTime);

create table question_submit
(
    id            bigint auto_increment comment 'id'
        primary key,
    questionId    bigint                             not null comment '题目 id',
    userId        bigint                             not null comment '创建用户 id',
    language      varchar(128)                       not null comment '编程语言',
    code          text                               not null comment '用户代码',
    status        int      default 0                 not null comment '判题状态(0待判题,1判题中,2成功,3失败)',
    judgeInfoList text                               null comment '每个测试用例的输出,时间,内存信息(json数组)',
    judgeResult   varchar(128)                       null comment '总的判题结果',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除'
)
    comment '题目提交';

create index idx_postId
    on question_submit (questionId);

create index idx_userId
    on question_submit (userId);

create index question_submit_createTime_index
    on question_submit (createTime);

create index question_submit_language_index
    on question_submit (language);

create index question_submit_updateTime_index
    on question_submit (updateTime);

create table user
(
    id                bigint auto_increment comment 'id'
        primary key,
    userAccount       varchar(256)                           not null comment '账号',
    userPassword      varchar(512)                           not null comment '密码',
    unionId           varchar(256)                           null comment '微信开放平台id',
    mpOpenId          varchar(256)                           null comment '公众号openId',
    userName          varchar(256)                           null comment '用户昵称',
    userAvatarVersion mediumtext                             null comment '版本号',
    userProfile       varchar(512)                           null comment '用户简介',
    userRole          varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime        datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime        datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete          tinyint      default 0                 not null comment '是否删除'
)
    comment '用户' collate = utf8mb4_unicode_ci;

create index idx_unionId
    on user (unionId);

create index user_createTime_index
    on user (createTime);

create index user_updateTime_index
    on user (updateTime);

create index user_userAccount_index
    on user (userAccount);

