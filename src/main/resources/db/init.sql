CREATE DATABASE IF NOT EXISTS paytogether;

USE paytogether;

CREATE TABLE IF NOT EXISTS exchange_rate
(
    exchange_rate_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date             DATE           NOT NULL,
    base_currency    VARCHAR(10)    NOT NULL,
    quote_currency   VARCHAR(10)    NOT NULL,
    rate             DECIMAL(32, 8) NOT NULL,
    provider         VARCHAR(255)   NOT NULL,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


CREATE TABLE IF NOT EXISTS locale
(
    id                   INT AUTO_INCREMENT PRIMARY KEY COMMENT 'pk',
    image_url            VARCHAR(255)                        NOT NULL COMMENT '이미지 URL',
    continent            VARCHAR(50)                         NOT NULL COMMENT '대륙',
    currency             VARCHAR(10)                         NOT NULL COMMENT '통화',
    country_korean_name  VARCHAR(100)                        NOT NULL COMMENT '국가 한글명',
    country_english_name VARCHAR(255)                        NOT NULL COMMENT '국가 영문명',
    locale_code          VARCHAR(10)                         NOT NULL COMMENT '로케일 코드',
    sort                 INT                                 NOT NULL COMMENT '정렬',
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    UNIQUE KEY unique_locale_code_currency (locale_code, currency)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO locale (continent, currency, country_korean_name, country_english_name, locale_code, sort, image_url)
VALUES ('아시아', 'KHR', '캄보디아', 'Cambodia', 'KH', 1, ''),
       ('아시아', 'CNH', '중국', 'China', 'CN', 2, ''),
       ('아시아', 'HKD', '홍콩', 'Hong Kong', 'HK', 3, ''),
       ('아시아', 'IDR', '인도네시아', 'Indonesia', 'ID', 4, ''),
       ('아시아', 'ILS', '이스라엘', 'Israel', 'IL', 5, ''),
       ('아시아', 'JPY', '일본', 'Japan', 'JP', 6, ''),
       ('아시아', 'MOP', '마카오', 'Macao / Macau', 'MO', 7, ''),
       ('아시아', 'MYR', '말레이시아', 'Malaysia', 'MY', 8, ''),
       ('아시아', 'MVR', '몰디브', 'Maldives', 'MV', 9, ''),
       ('아시아', 'MNT', '몽골', 'Mongolia', 'MN', 10, ''),
       ('아시아', 'PHP', '필리핀', 'Philippines', 'PH', 11, ''),
       ('아시아', 'SGD', '싱가포르', 'Singapore', 'SG', 12, ''),
       ('아시아', 'KRW', '대한민국', 'South Korea', 'KR', 13, ''),
       ('아시아', 'TWD', '대만', 'Taiwan', 'TW', 14, ''),
       ('아시아', 'THB', '태국', 'Thailand', 'TH', 15, ''),
       ('아시아', 'TRY', '튀르키예', 'Türkiye (Turkey)', 'TR', 16, ''),
       ('아시아', 'VND', '베트남', 'Vietnam', 'VN', 17, ''),
       ('아메리카', 'CAD', '캐나다', 'Canada', 'CA', 18, ''),
       ('아메리카', 'DOP', '도미니카공화국', 'Dominican Republic', 'DO', 19, ''),
       ('아메리카', 'MXN', '멕시코', 'Mexico', 'MX', 20, ''),
       ('아메리카', 'USD', '미국', 'United States (USA)', 'US', 21, ''),
       ('오세아니아', 'AUD', '호주', 'Australia', 'AU', 22, ''),
       ('오세아니아', 'USD', '괌(미국령)', 'Guam (USA)', 'GU', 23, ''),
       ('오세아니아', 'NZD', '뉴질랜드', 'New Zealand', 'NZ', 24, ''),
       ('오세아니아', 'USD', '북마리아나(사이판)', 'Northern Mariana Islands (USA)', 'MP', 25, ''),
       ('유럽', 'EUR', '오스트리아', 'Austria', 'AT', 26, ''),
       ('유럽', 'EUR', '벨기에', 'Belgium', 'BE', 27, ''),
       ('유럽', 'BAM', '보스니아', 'Bosnia and Herzegovina', 'BA', 28, ''),
       ('유럽', 'DKK', '덴마크', 'Denmark', 'DK', 29, ''),
       ('유럽', 'EUR', '유로존', 'Eurozone', 'EU', 30, ''),
       ('유럽', 'EUR', '핀란드', 'Finland', 'FI', 31, ''),
       ('유럽', 'EUR', '프랑스', 'France', 'FR', 32, ''),
       ('유럽', 'EUR', '독일', 'Germany', 'DE', 33, ''),
       ('유럽', 'EUR', '이탈리아', 'Italy', 'IT', 34, ''),
       ('유럽', 'EUR', '네덜란드', 'Netherlands', 'NL', 35, ''),
       ('유럽', 'EUR', '슬로베니아', 'Slovenia', 'SI', 36, ''),
       ('유럽', 'EUR', '스페인', 'Spain', 'ES', 37, ''),
       ('유럽', 'CHF', '스위스', 'Switzerland', 'CH', 38, ''),
       ('유럽', 'GBP', '영국', 'United Kingdom (UK)', 'GB', 39, ''),
       ('아프리카', 'XOF', '서아프리카', 'West Africa', 'WA', 40, '');


CREATE TABLE IF NOT EXISTS code
(
    code_id     INT AUTO_INCREMENT PRIMARY KEY COMMENT 'pk',
    group_code  VARCHAR(255) NOT NULL,
    parent_code VARCHAR(255) NOT NULL,
    code        VARCHAR(255) NOT NULL,
    value       VARCHAR(255) NOT NULL,
    description VARCHAR(255) NULL,
    sort        INT          NOT NULL,
    deleted_at  TIMESTAMP    NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS code_log
(
    log_id        INT AUTO_INCREMENT PRIMARY KEY,
    code_id       INT,
    group_code    VARCHAR(255)             NOT NULL,
    parent_code   VARCHAR(255)             NOT NULL,
    code          VARCHAR(255)             NOT NULL,
    value         VARCHAR(255)             NOT NULL,
    description   VARCHAR(255)             NULL,
    sort          INT                      NOT NULL,
    deleted_at    TIMESTAMP                NULL,
    updated_at    TIMESTAMP                NOT NULL,
    created_at    TIMESTAMP                NOT NULL,
    action        ENUM ('INSERT','UPDATE') NOT NULL,
    log_timestamp TIMESTAMP                NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DELIMITER $$
CREATE TRIGGER IF NOT EXISTS code_after_insert
    AFTER INSERT
    ON code
    FOR EACH ROW
BEGIN
    INSERT INTO code_log (code_id, group_code, parent_code, code, value, description, sort, deleted_at, updated_at, created_at, action)
    VALUES (NEW.code_id, NEW.group_code, NEW.parent_code, NEW.code, NEW.value, NEW.description, NEW.sort, NEW.deleted_at, NEW.updated_at,
            NEW.created_at, 'INSERT');
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER IF NOT EXISTS code_after_update
    AFTER UPDATE
    ON code
    FOR EACH ROW
BEGIN
    INSERT INTO code_log (code_id, group_code, parent_code, code, value, description, sort, deleted_at, updated_at, created_at, action)
    VALUES (NEW.code_id, NEW.group_code, NEW.parent_code, NEW.code, NEW.value, NEW.description, NEW.sort, NEW.deleted_at, NEW.updated_at,
            NEW.created_at, 'UPDATE');
END$$
DELIMITER ;

CREATE TABLE IF NOT EXISTS journey
(
    journey_id     VARCHAR(255)   NOT NULL PRIMARY KEY,
    base_currency  VARCHAR(10)    NOT NULL DEFAULT 'USD',
    quote_currency VARCHAR(10)    NOT NULL DEFAULT 'KRW',
    exchange_rate  DECIMAL(32, 8) NOT NULL,
    title          VARCHAR(255)   NOT NULL,
    start_date     DATE           NOT NULL,
    end_date       DATE           NOT NULL,
    locale_code    VARCHAR(10)    NOT NULL,
    closed_at      TIMESTAMP      NULL,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS journey_member
(
    journey_member_id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'pk',
    journey_id        VARCHAR(255)          NOT NULL,
    name              VARCHAR(255) NOT NULL COMMENT '이름',
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY journey_id_name (journey_id, name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS journey_expense
(
    journey_expense_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    journey_id         VARCHAR(255)   NOT NULL,
    expense_payer_id   BIGINT         NOT NULL,
    category           VARCHAR(100)   NOT NULL,
    expense_date       DATE           NOT NULL,
    currency           VARCHAR(10)    NOT NULL,
    amount             DECIMAL(32, 8) NOT NULL,
    memo               VARCHAR(255),
    deleted_at         TIMESTAMP      NULL,
    updated_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS journey_member_ledger
(
    journey_member_ledger_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    journey_id               VARCHAR(255)   NOT NULL,
    journey_member_id        BIGINT         NOT NULL,
    journey_expense_id       BIGINT         NOT NULL,
    amount                   DECIMAL(32, 8) NOT NULL,
    note                     VARCHAR(255),
    deleted_at               TIMESTAMP      NULL,
    created_at               TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS journey_settlement
(
    journey_settlement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    journey_id            VARCHAR(255)   NOT NULL,
    from_member_id        BIGINT         NOT NULL,
    to_member_id          BIGINT         NOT NULL,
    amount                DECIMAL(15, 2) NOT NULL,
    created_at            TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;