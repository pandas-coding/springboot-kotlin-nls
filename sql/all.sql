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


#
短信验证码
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

#
会员表
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

#
file_transfer 语音识别表
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

-- order_info 订单信息表
-- 保存订单 I； 调用支付宝；回调接口更新成S；补偿查询
drop table if exists `order_info`;
create table `order_info`
(
    `id`         bigint        not null comment 'id',
    `order_no`   char(20)      not null comment '订单号',
    `order_at`   datetime(3)   not null comment '下单时间',
    `order_type` char(1)       not null comment '订单类型|枚举[OrderInfoOrderTypeEnum]',
    `info`       varchar(100)  not null comment '订单信息|根据订单类型，存放不同的信息',
    `member_id`  bigint        not null comment '会员|id',
    `amount`     decimal(6, 2) not null comment '订单金额(元)',
    `pay_at`     datetime comment '支付时间|本地时间',
    `channel`    char(1) comment '支付通道|枚举[OrderInfoChannelEnum]',
    `channel_at` datetime comment '通道时间|支付通道返回的时间',
    `status`     char(1) comment '交易状态|枚举[OrderInfoStatusEnum]',
    `desc`       varchar(200) comment '订单描述',
    `created_at` datetime(3)   not null comment '创建时间',
    `updated_at` datetime(3)   not null comment '修改时间',
    primary key (`id`),
    unique key `order_no` (`order_no`)
) engine = innodb
  default charset = utf8 comment ='订单信息表';

-- file_transfer_subtitle 语音识别字幕
drop table if exists `file_transfer_subtitle`;
create table `file_transfer_subtitle`
(
    `id`             bigint not null comment 'id',
    file_transfer_id bigint not null comment '录音转换ID',
    `index`          int    not null comment '索引号',
    `begin`          int    not null comment '开始时间，毫秒',
    `end`            int    not null comment '结束时间，毫秒',
    `text`           varchar(2000) comment '字幕',
    `created_at`     datetime(3) not null comment '创建时间',
    `updated_at`     datetime(3) not null comment '修改时间',
    primary key (`id`),
    index            file_transfer_subtitle_file_transfer_id (file_transfer_id)
) engine = innodb
  default charset = utf8mb4 comment ='语音识别字幕';

-- 用户表
drop table if exists `user`;
create table `user`
(
    `id`         bigint      not null comment 'ID',
    `login_name` varchar(50) not null comment '登录名',
    `password`   char(32)    not null comment '密码',
    primary key (`id`),
    unique key `login_name_unique` (`login_name`)
) engine=innodb default charset=utf8mb4 comment='用户';

-- 密码：7039ad9f5fe2a41b76d49a3de9f227c3 对应 a111111
-- 算法：a111111拼接上盐值!@#$*&^%nls（写在md5.js），得到：a111111!@#$*&^%nls，对该值做两次md5（可以找一些在线md5工具）
-- 如果想设置自己的密码，比如:123456，password=md5(md5(123456!@#$*&^%nls))
insert into `user` (id, login_name, password)
values (1, 'test', '68d8210d7c3e5949e0d192aff93afe56');
