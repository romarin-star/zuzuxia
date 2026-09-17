-- ============================================================
--  租租侠租赁平台 —— 数据库建表脚本
--  数据库：MySQL 8.0
--  字符集：utf8mb4 / utf8mb4_general_ci
--  引擎：InnoDB
--
--  执行方式：
--    mysql -u root -p < docs/db/schema.sql
--
--  注意：本脚本会 DROP 同名数据库，请勿在有数据的库上直接执行。
-- ============================================================

DROP DATABASE IF EXISTS `zuzuxia`;
CREATE DATABASE `zuzuxia`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;
USE `zuzuxia`;


-- ------------------------------------------------------------
-- 1. user 用户表
--    普通用户与管理员同表，靠 role 区分
-- ------------------------------------------------------------
CREATE TABLE `user` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`       VARCHAR(50)   NOT NULL                COMMENT '登录名',
    `password`       VARCHAR(100)  NOT NULL                COMMENT '密码（BCrypt 加密）',
    `nickname`       VARCHAR(50)   DEFAULT NULL            COMMENT '昵称',
    `phone`          VARCHAR(20)   DEFAULT NULL            COMMENT '手机号',
    `avatar`         VARCHAR(255)  DEFAULT NULL            COMMENT '头像 URL',
    `role`           VARCHAR(20)   NOT NULL DEFAULT 'USER'   COMMENT '角色：USER/ADMIN',
    `status`         VARCHAR(20)   NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL/BANNED',
    `credit_score`   INT           NOT NULL DEFAULT 100    COMMENT '信用分，初始 100',
    `balance`        DECIMAL(10,2) NOT NULL DEFAULT 0.00   COMMENT '可用余额',
    `frozen_balance` DECIMAL(10,2) NOT NULL DEFAULT 0.00   COMMENT '冻结余额（押金）',
    `created_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户';


-- ------------------------------------------------------------
-- 2. category 物品分类表
-- ------------------------------------------------------------
CREATE TABLE `category` (
    `id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`   VARCHAR(50)  NOT NULL                COMMENT '分类名称',
    `sort`   INT          NOT NULL DEFAULT 0      COMMENT '排序，越小越靠前',
    `icon`   VARCHAR(255) DEFAULT NULL            COMMENT '图标',
    `status` VARCHAR(20)  NOT NULL DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '物品分类';


-- ------------------------------------------------------------
-- 3. item 物品表
--    注意：物品"当前是否租用中"不存本表，由 orders 动态推导，
--          因为同一物品在不同日期可分别出租给不同的人。
-- ------------------------------------------------------------
CREATE TABLE `item` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `owner_id`       BIGINT        NOT NULL                COMMENT '出租人 user.id',
    `category_id`    BIGINT        NOT NULL                COMMENT '分类 category.id',
    `title`          VARCHAR(100)  NOT NULL                COMMENT '标题',
    `description`    TEXT                                  COMMENT '详细描述',
    `location`       VARCHAR(100)  DEFAULT NULL            COMMENT '取件地点',
    `daily_price`    DECIMAL(10,2) NOT NULL                COMMENT '日租金（元/天）',
    `deposit`        DECIMAL(10,2) NOT NULL DEFAULT 0.00   COMMENT '押金（元）',
    `cover_image`    VARCHAR(255)  DEFAULT NULL            COMMENT '封面图 URL',
    `status`         VARCHAR(20)   NOT NULL DEFAULT 'ON_SHELF'
                     COMMENT '状态：ON_SHELF 上架 / OFF_SHELF 下架 / DELISTED 违规下架',
    `view_count`     INT           NOT NULL DEFAULT 0      COMMENT '浏览量（热度榜用）',
    `favorite_count` INT           NOT NULL DEFAULT 0      COMMENT '收藏数（热度榜用）',
    `created_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_owner`    (`owner_id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_status`   (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '物品';


-- ------------------------------------------------------------
-- 4. item_image 物品图片表
-- ------------------------------------------------------------
CREATE TABLE `item_image` (
    `id`      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `item_id` BIGINT       NOT NULL                COMMENT '物品 item.id',
    `url`     VARCHAR(255) NOT NULL                COMMENT '图片 URL',
    `sort`    INT          NOT NULL DEFAULT 0      COMMENT '排序',
    PRIMARY KEY (`id`),
    KEY `idx_item` (`item_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '物品图片';


-- ------------------------------------------------------------
-- 5. item_available_period 可租时间段表
--    出租人声明的可被租借的日期区间，可多段
-- ------------------------------------------------------------
CREATE TABLE `item_available_period` (
    `id`         BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `item_id`    BIGINT NOT NULL                COMMENT '物品 item.id',
    `start_date` DATE   NOT NULL                COMMENT '可租开始日期（含）',
    `end_date`   DATE   NOT NULL                COMMENT '可租结束日期（含）',
    PRIMARY KEY (`id`),
    KEY `idx_item_date` (`item_id`, `start_date`, `end_date`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '可租时间段';


-- ------------------------------------------------------------
-- 6. orders 订单表
--    命名为 orders 而非 order，因为 order 是 MySQL 保留字
-- ------------------------------------------------------------
CREATE TABLE `orders` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no`       VARCHAR(32)   NOT NULL                COMMENT '业务单号',
    `item_id`        BIGINT        NOT NULL                COMMENT '物品 item.id',
    `owner_id`       BIGINT        NOT NULL                COMMENT '出租人（冗余，避免联表 item）',
    `renter_id`      BIGINT        NOT NULL                COMMENT '租用人 user.id',
    `start_date`     DATE          NOT NULL                COMMENT '租期开始（含）',
    `end_date`       DATE          NOT NULL                COMMENT '租期结束（含）',
    `days`           INT           NOT NULL                COMMENT '租借天数',
    `daily_price`    DECIMAL(10,2) NOT NULL                COMMENT '日租金快照',
    `rent_amount`    DECIMAL(10,2) NOT NULL                COMMENT '租金小计 = daily_price * days',
    `deposit_amount` DECIMAL(10,2) NOT NULL                COMMENT '押金快照',
    `total_amount`   DECIMAL(10,2) NOT NULL                COMMENT '合计 = rent_amount + deposit_amount',
    `status`         VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                     COMMENT 'PENDING/REJECTED/CANCELLED/RESERVED/RENTING/RETURNING/FINISHED',
    `apply_remark`   VARCHAR(255)  DEFAULT NULL            COMMENT '申请留言',
    `reject_reason`  VARCHAR(255)  DEFAULT NULL            COMMENT '拒绝原因',
    `created_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交申请时间',
    `confirmed_at`   DATETIME      DEFAULT NULL            COMMENT '出租人同意时间',
    `picked_at`      DATETIME      DEFAULT NULL            COMMENT '确认取件时间',
    `returned_at`    DATETIME      DEFAULT NULL            COMMENT '发起归还时间',
    `finished_at`    DATETIME      DEFAULT NULL            COMMENT '完成时间',
    `cancelled_at`   DATETIME      DEFAULT NULL            COMMENT '取消/拒绝时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_item_period` (`item_id`, `start_date`, `end_date`, `status`),
    KEY `idx_renter` (`renter_id`),
    KEY `idx_owner`  (`owner_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单';


-- ------------------------------------------------------------
-- 7. wallet_transaction 钱包流水表
-- ------------------------------------------------------------
CREATE TABLE `wallet_transaction` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`       BIGINT        NOT NULL                COMMENT '用户 user.id',
    `type`          VARCHAR(30)   NOT NULL
                    COMMENT 'RECHARGE/DEPOSIT_FREEZE/DEPOSIT_UNFREEZE/DEPOSIT_DEDUCT/RENT_PAY/RENT_INCOME/DEPOSIT_REFUND',
    `amount`        DECIMAL(10,2) NOT NULL                COMMENT '变动金额，正数收入、负数支出',
    `balance_after` DECIMAL(10,2) NOT NULL                COMMENT '变动后可用余额',
    `order_id`      BIGINT        DEFAULT NULL            COMMENT '关联订单，充值等无单可空',
    `remark`        VARCHAR(255)  DEFAULT NULL            COMMENT '备注',
    `created_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_time` (`user_id`, `created_at`),
    KEY `idx_order` (`order_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '钱包流水';


-- ------------------------------------------------------------
-- 8. review 双向评价表
-- ------------------------------------------------------------
CREATE TABLE `review` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id`    BIGINT       NOT NULL                COMMENT '订单 orders.id',
    `reviewer_id` BIGINT       NOT NULL                COMMENT '评价人 user.id',
    `reviewee_id` BIGINT       NOT NULL                COMMENT '被评价人 user.id',
    `role`        VARCHAR(20)  NOT NULL                COMMENT '评价人身份：LENDER/RENTER',
    `score`       TINYINT      NOT NULL                COMMENT '评分 1-5',
    `content`     VARCHAR(500) DEFAULT NULL            COMMENT '评价内容',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_reviewer` (`order_id`, `reviewer_id`),
    KEY `idx_reviewee` (`reviewee_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '双向评价';


-- ------------------------------------------------------------
-- 9. favorite 收藏表
-- ------------------------------------------------------------
CREATE TABLE `favorite` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`    BIGINT   NOT NULL                COMMENT '用户 user.id',
    `item_id`    BIGINT   NOT NULL                COMMENT '物品 item.id',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_item` (`user_id`, `item_id`),
    KEY `idx_item` (`item_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '收藏';


-- ============================================================
--  建表完成，共 9 张表
-- ============================================================
