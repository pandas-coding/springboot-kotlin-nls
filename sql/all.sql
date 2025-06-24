drop table if exists `demo`;
create table `demo`
(
    `id`       bigint not null comment 'id',
    `mobile`   varchar(11) comment '手机号',
    `password` varchar(50) comment '密码',
    primary key (`id`),
    unique key `mobile_unique` (`mobile`)
) engine = innodb
  default charset = utf8mb4 comment ='示例';


# 短信验证码
drop table if exists `sms_code`;
create table `sms_code`
(
    `id`         bigint      not null comment 'id',
    `mobile`     varchar(50) not null comment '手机号',
    `code`       char(6)     not null comment '验证码',
    `use`        varchar(20) not null comment '用途|枚举[SmsCodeUseEnum]：REGISTER("0", "注册"), FORGET_PASSWORD("1", "忘记密码")',
    `status`     char(1)     not null comment '状态|枚举[SmsCodeStatusEnum]：USED("1", "已使用"), NOT_USED("0", "未使用")',
    `created_at` datetime(3) comment '创建时间',
    `updated_at` datetime(3) comment '修改时间',
    primary key (`id`)
) engine = innodb
  default charset = utf8 comment ='短信验证码表';

# 会员表
drop table if exists `member`;
create table `member`
(
    `id`         bigint      not null comment 'id',
    `mobile`     varchar(50) not null comment '手机号',
    `password`   char(32)    not null comment '密码',
    `name`       varchar(50) comment '昵称',
    `created_at` datetime(3) comment '创建时间',
    `updated_at` datetime(3) comment '修改时间',
    primary key (`id`),
    unique key `mobile_unique` (`mobile`)
) engine = innodb
  default charset = utf8 comment ='会员表';

# file_transfer 语音识别表
drop table if exists `file_transfer`;
create table `file_transfer`
(
    `id`                bigint       not null comment 'id',
    `member_id`         bigint       not null comment '会员ID',
    `name`              varchar(200) not null comment '名称',
    `second`            int comment '音频文件时长|秒',
    `amount`            decimal(6, 2) comment '金额|元，second*单价',
    `audio`             varchar(500) comment '文件链接',
    `file_sign`         char(32) comment '文件签名md5',
    `pay_status`        char(2) comment '支付状态|枚举[FileTransferPayStatusEnum];',
    `status`            char(2) comment '识别状态|枚举[FileTransferStatusEnum];',
    `lang`              char(16)     not null comment '音频语言|枚举[FileTransferLangEnum]',
    `vod`               char(32) comment 'VOD|videoId',
    `task_id`           char(32) comment '任务ID',
    `trans_status_code` int comment '转换状态码',
    `trans_status_text` varchar(200) comment '转换状态说明',
    `trans_time`        datetime(3) comment '转换时间|开始转换的时间',
    `solve_time`        datetime(3) comment '完成时间|录音文件识别完成的时间',
    `created_at`        datetime(3) comment '创建时间',
    `updated_at`        datetime(3) comment '修改时间',
    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment ='语音识别表';
