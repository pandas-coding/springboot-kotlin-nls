drop table if exists `demo`;
create table `demo`
(
    `id`       bigint not null comment 'id',
    `mobile`   varchar(11) comment '手机号',
    `password` varchar(50) comment '密码',
    primary key (`id`),
    unique key `mobile_unique` (`mobile`)
) engine=innodb default charset=utf8mb4 comment='示例';


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
) engine=innodb default charset=utf8 comment='短信验证码表';