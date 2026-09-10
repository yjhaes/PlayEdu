/*
 * Copyright (C) 2023 杭州白书科技有限公司
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.playedu.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.playedu.api.service.impl.UserDeletionServiceImpl;
import xyz.playedu.common.exception.NotFoundException;
import xyz.playedu.common.service.impl.UserDepartmentServiceImpl;
import xyz.playedu.common.service.impl.UserLoginRecordServiceImpl;
import xyz.playedu.common.service.impl.UserServiceImpl;
import xyz.playedu.course.service.impl.DailyLearningRankingService;
import xyz.playedu.course.service.impl.UserCourseHourRecordServiceImpl;
import xyz.playedu.course.service.impl.UserCourseRecordServiceImpl;
import xyz.playedu.course.service.impl.UserLearnDurationRecordServiceImpl;
import xyz.playedu.course.service.impl.UserLearnDurationStatsServiceImpl;
import xyz.playedu.points.migration.PointsFeatureGate;
import xyz.playedu.points.service.impl.PointBalanceServiceImpl;
import xyz.playedu.points.service.impl.PointLedgerServiceImpl;
import xyz.playedu.points.service.impl.PointRedemptionServiceImpl;

@SpringBootTest(classes = UserDeletionServiceIntegrationTest.TestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class UserDeletionServiceIntegrationTest {

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private UserDeletionService userDeletionService;

    @Autowired private DailyLearningRankingService dailyLearningRankingService;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
        registry.add("mybatis.mapper-locations", () -> "classpath*:mapper/*.xml");
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_redemptions");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_codes");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_products");
        jdbcTemplate.execute("DROP TABLE IF EXISTS point_ledgers");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_login_records");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_learn_duration_stats");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_learn_duration_records");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_course_hour_records");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_course_records");
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_department");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users");

        jdbcTemplate.execute(
                """
                CREATE TABLE users (
                    id int unsigned NOT NULL,
                    email varchar(32) NOT NULL DEFAULT '',
                    name varchar(24) NOT NULL DEFAULT '',
                    avatar int NOT NULL DEFAULT 0,
                    password varchar(128) NOT NULL DEFAULT '',
                    salt varchar(12) NOT NULL DEFAULT '',
                    id_card varchar(64) NOT NULL DEFAULT '',
                    credit1 int NOT NULL DEFAULT 0,
                    create_ip varchar(15) NOT NULL DEFAULT '',
                    create_city varchar(32) NOT NULL DEFAULT '',
                    is_active tinyint NOT NULL DEFAULT 0,
                    is_lock tinyint NOT NULL DEFAULT 0,
                    is_verify tinyint NOT NULL DEFAULT 0,
                    verify_at timestamp NULL,
                    is_set_password tinyint NOT NULL DEFAULT 0,
                    login_at timestamp NULL,
                    created_at timestamp NULL,
                    updated_at timestamp NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_users_email (email)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.update(
                """
                INSERT INTO users (id, email, name, credit1, is_lock, created_at, updated_at)
                VALUES (7, 'deleted@example.test', 'Deleted learner', 10, 0, NOW(), NOW()),
                       (8, 'kept@example.test', 'Kept learner', 30, 0, NOW(), NOW())
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE user_department (
                    user_id int unsigned NOT NULL,
                    dep_id int unsigned NOT NULL,
                    KEY user_id (user_id),
                    KEY dep_id (dep_id)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE user_course_records (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int NOT NULL,
                    course_id int NOT NULL,
                    hour_count int NOT NULL DEFAULT 0,
                    finished_count int NOT NULL DEFAULT 0,
                    progress int NOT NULL DEFAULT 0,
                    is_finished tinyint NOT NULL DEFAULT 0,
                    finished_at timestamp NULL,
                    created_at timestamp NULL,
                    updated_at timestamp NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE user_course_hour_records (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int NOT NULL,
                    course_id int NOT NULL,
                    hour_id int NOT NULL,
                    total_duration int NOT NULL DEFAULT 0,
                    finished_duration int NOT NULL DEFAULT 0,
                    real_duration int NOT NULL DEFAULT 0,
                    is_finished tinyint NOT NULL DEFAULT 0,
                    finished_at timestamp NULL,
                    created_at timestamp NULL,
                    updated_at timestamp NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE user_learn_duration_records (
                    id bigint unsigned NOT NULL AUTO_INCREMENT,
                    user_id int NOT NULL,
                    created_date date NOT NULL,
                    duration int unsigned NOT NULL DEFAULT 0,
                    start_at timestamp NULL,
                    end_at timestamp NULL,
                    from_id varchar(64) NOT NULL DEFAULT '',
                    from_scene varchar(20) NOT NULL DEFAULT '',
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE user_learn_duration_stats (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int NOT NULL,
                    duration bigint NOT NULL DEFAULT 0,
                    created_date date NOT NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE user_login_records (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int NOT NULL,
                    jti varchar(64) NOT NULL DEFAULT '',
                    ip varchar(15) NOT NULL DEFAULT '',
                    ip_area varchar(64) NOT NULL DEFAULT '',
                    browser varchar(64) NOT NULL DEFAULT '',
                    browser_version varchar(64) NOT NULL DEFAULT '',
                    os varchar(128) NOT NULL DEFAULT '',
                    expired bigint NOT NULL DEFAULT 0,
                    is_logout tinyint NOT NULL DEFAULT 0,
                    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_user_login_records_jti (jti)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE point_ledgers (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int unsigned NOT NULL,
                    delta int NOT NULL,
                    balance_after int NOT NULL,
                    type varchar(32) NOT NULL,
                    source_key varchar(191) NOT NULL,
                    reason text NULL,
                    operator_admin_id int unsigned NULL,
                    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_point_ledgers_source_key (source_key)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE point_products (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    name varchar(191) NOT NULL,
                    points_price int unsigned NOT NULL,
                    status varchar(20) NOT NULL,
                    created_at datetime NOT NULL,
                    updated_at datetime NOT NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE point_codes (
                    id int unsigned NOT NULL,
                    product_id int unsigned NOT NULL,
                    code_ciphertext text NOT NULL,
                    code_digest varchar(191) NOT NULL,
                    status varchar(20) NOT NULL,
                    delivered_at datetime NULL,
                    created_at datetime NOT NULL,
                    updated_at datetime NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_point_codes_code_digest (code_digest)
                ) ENGINE=InnoDB
                """);
        jdbcTemplate.execute(
                """
                CREATE TABLE point_redemptions (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int unsigned NOT NULL,
                    product_id int unsigned NOT NULL,
                    code_id int unsigned NOT NULL,
                    request_key varchar(160) NULL,
                    points_cost int unsigned NOT NULL,
                    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_point_redemptions_code_id (code_id)
                ) ENGINE=InnoDB
                """);

        jdbcTemplate.update(
                "INSERT INTO user_department (user_id, dep_id) VALUES (7, 11), (8, 12)");
        jdbcTemplate.update(
                """
                INSERT INTO user_course_records
                    (user_id, course_id, hour_count, finished_count, progress, is_finished)
                VALUES (7, 21, 2, 1, 50, 0), (8, 22, 1, 1, 100, 1)
                """);
        jdbcTemplate.update(
                """
                INSERT INTO user_course_hour_records
                    (user_id, course_id, hour_id, total_duration, finished_duration, is_finished)
                VALUES (7, 21, 31, 100, 50, 0), (8, 22, 32, 100, 100, 1)
                """);
        jdbcTemplate.update(
                """
                INSERT INTO user_learn_duration_records
                    (user_id, created_date, duration, from_id, from_scene)
                VALUES (7, CURRENT_DATE, 1000, '21_31', 'hour'),
                       (8, CURRENT_DATE, 2000, '22_32', 'hour')
                """);
        jdbcTemplate.update(
                """
                INSERT INTO user_learn_duration_stats (user_id, duration, created_date)
                VALUES (7, 1000, CURRENT_DATE), (8, 2000, CURRENT_DATE)
                """);
        jdbcTemplate.update(
                "INSERT INTO user_login_records (user_id, jti) VALUES (7, 'jti-7'), (8, 'jti-8')");
        jdbcTemplate.update(
                """
                INSERT INTO point_ledgers
                    (user_id, delta, balance_after, type, source_key)
                VALUES (7, 20, 20, 'COURSE_COMPLETION', 'course-completion:7:21'),
                       (8, 30, 30, 'COURSE_COMPLETION', 'course-completion:8:22')
                """);
        jdbcTemplate.update(
                """
                INSERT INTO point_products (id, name, points_price, status, created_at, updated_at)
                VALUES (1, 'Test product', 20, 'ON_SALE', NOW(), NOW())
                """);
        jdbcTemplate.update(
                """
                INSERT INTO point_codes
                    (id, product_id, code_ciphertext, code_digest, status, delivered_at,
                     created_at, updated_at)
                VALUES (100, 1, 'delivered-ciphertext', 'delivered-digest', 'DELIVERED', NOW(), NOW(), NOW()),
                       (101, 1, 'available-ciphertext', 'available-digest', 'AVAILABLE', NULL, NOW(), NOW())
                """);
        jdbcTemplate.update(
                """
                INSERT INTO point_redemptions
                    (user_id, product_id, code_id, request_key, points_cost)
                VALUES (7, 1, 100, 'request-7', 20)
                """);
    }

    @Test
    void physicallyDeletesLearnerPointsAndAssociationsButPreservesVoucherState()
            throws NotFoundException {
        userDeletionService.destroy(7);

        assertThat(count("users", 7)).isZero();
        assertThat(count("user_department", 7)).isZero();
        assertThat(count("user_course_records", 7)).isZero();
        assertThat(count("user_course_hour_records", 7)).isZero();
        assertThat(count("user_learn_duration_records", 7)).isZero();
        assertThat(count("user_learn_duration_stats", 7)).isZero();
        assertThat(count("user_login_records", 7)).isZero();
        assertThat(count("point_ledgers", 7)).isZero();
        assertThat(count("point_redemptions", 7)).isZero();
        assertThat(count("users", 8)).isEqualTo(1);
        assertThat(count("point_ledgers", 8)).isEqualTo(1);
        assertThat(statusOfCode(100)).isEqualTo("DELIVERED");
        assertThat(statusOfCode(101)).isEqualTo("AVAILABLE");
        verify(dailyLearningRankingService).removeUser(7);
    }

    @Test
    void rollsBackAllDeletionWritesWhenARequiredCleanupFails() {
        jdbcTemplate.execute("DROP TABLE user_learn_duration_stats");

        assertThatThrownBy(() -> userDeletionService.destroy(7)).isInstanceOf(Exception.class);

        assertThat(count("users", 7)).isEqualTo(1);
        assertThat(count("user_department", 7)).isEqualTo(1);
        assertThat(count("user_course_records", 7)).isEqualTo(1);
        assertThat(count("user_course_hour_records", 7)).isEqualTo(1);
        assertThat(count("user_learn_duration_records", 7)).isEqualTo(1);
        assertThat(count("point_ledgers", 7)).isEqualTo(1);
        assertThat(count("point_redemptions", 7)).isEqualTo(1);
        assertThat(statusOfCode(100)).isEqualTo("DELIVERED");
    }

    @Test
    void deletesLearnersWithNegativeOrEmptyPointDataWithoutTouchingOtherLearners()
            throws NotFoundException {
        jdbcTemplate.update("DELETE FROM point_redemptions WHERE user_id = 7");
        jdbcTemplate.update("DELETE FROM point_ledgers WHERE user_id = 7");
        jdbcTemplate.update("UPDATE users SET credit1 = -30 WHERE id = 7");
        jdbcTemplate.update(
                """
                INSERT INTO point_ledgers
                    (user_id, delta, balance_after, type, source_key, reason, operator_admin_id)
                VALUES (7, -30, -30, 'MANUAL_ADJUSTMENT', 'manual:7:1', '纠正重复发放', 99)
                """);

        userDeletionService.destroy(7);
        assertThat(count("users", 7)).isZero();
        assertThat(count("point_ledgers", 7)).isZero();
        assertThat(count("point_redemptions", 7)).isZero();
        assertThat(count("users", 8)).isEqualTo(1);

        jdbcTemplate.update("DELETE FROM point_ledgers WHERE user_id = 8");
        jdbcTemplate.update("DELETE FROM point_redemptions WHERE user_id = 8");
        userDeletionService.destroy(8);
        assertThat(count("users", 8)).isZero();
    }

    private int count(String table, int userId) {
        String column = "users".equals(table) ? "id" : "user_id";
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?",
                Integer.class,
                userId);
    }

    private String statusOfCode(int codeId) {
        return jdbcTemplate.queryForObject(
                "SELECT status FROM point_codes WHERE id = ?", String.class, codeId);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableTransactionManagement
    @MapperScan({"xyz.playedu.common.mapper", "xyz.playedu.course.mapper", "xyz.playedu.points.mapper"})
    @Import({
        UserDeletionServiceImpl.class,
        UserServiceImpl.class,
        UserDepartmentServiceImpl.class,
        UserLoginRecordServiceImpl.class,
        UserCourseHourRecordServiceImpl.class,
        UserCourseRecordServiceImpl.class,
        UserLearnDurationRecordServiceImpl.class,
        UserLearnDurationStatsServiceImpl.class,
        PointBalanceServiceImpl.class,
        PointLedgerServiceImpl.class,
        PointRedemptionServiceImpl.class,
        TestDependencies.class
    })
    static class TestApplication {}

    @TestConfiguration
    static class TestDependencies {
        @Bean
        DailyLearningRankingService dailyLearningRankingService() {
            return mock(DailyLearningRankingService.class);
        }

        @Bean
        PointsFeatureGate pointsFeatureGate() {
            return new PointsFeatureGate() {
                @Override
                public boolean isOpen() {
                    return true;
                }

                @Override
                public void requireOpen() {}
            };
        }
    }
}
