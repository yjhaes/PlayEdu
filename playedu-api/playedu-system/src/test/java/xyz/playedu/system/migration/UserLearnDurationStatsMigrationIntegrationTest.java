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
package xyz.playedu.system.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
class UserLearnDurationStatsMigrationIntegrationTest {

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(MYSQL.getDriverClassName());
        dataSource.setUrl(MYSQL.getJdbcUrl());
        dataSource.setUsername(MYSQL.getUsername());
        dataSource.setPassword(MYSQL.getPassword());
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DROP TABLE IF EXISTS user_learn_duration_stats");
        jdbcTemplate.execute(
                """
                CREATE TABLE user_learn_duration_stats (
                    id int unsigned NOT NULL AUTO_INCREMENT,
                    user_id int NOT NULL,
                    duration bigint NOT NULL,
                    created_date date NOT NULL,
                    PRIMARY KEY (id),
                    KEY u_d (user_id, created_date)
                ) ENGINE=InnoDB
                """);
    }

    @Test
    void consolidatesHistoricalDuplicatesAndEnforcesOneRowPerLearnerAndDate() {
        jdbcTemplate.update(
                "INSERT INTO user_learn_duration_stats (user_id, duration, created_date) VALUES (7,"
                        + " 10, '2026-09-08')");
        jdbcTemplate.update(
                "INSERT INTO user_learn_duration_stats (user_id, duration, created_date) VALUES (7,"
                        + " 15, '2026-09-08')");
        jdbcTemplate.update(
                "INSERT INTO user_learn_duration_stats (user_id, duration, created_date) VALUES (8,"
                        + " 20, '2026-09-08')");

        UserLearnDurationStatsMigration migration = new UserLearnDurationStatsMigration();
        migration.migrate(jdbcTemplate);
        migration.migrate(jdbcTemplate);

        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM user_learn_duration_stats WHERE user_id = 7"
                                        + " AND created_date = '2026-09-08'",
                                Integer.class))
                .isEqualTo(1);
        assertThat(
                        jdbcTemplate.queryForObject(
                                "SELECT duration FROM user_learn_duration_stats WHERE user_id = 7"
                                        + " AND created_date = '2026-09-08'",
                                Long.class))
                .isEqualTo(25L);
        assertThatThrownBy(
                        () ->
                                jdbcTemplate.update(
                                        "INSERT INTO user_learn_duration_stats (user_id, duration,"
                                                + " created_date) VALUES (7, 1, '2026-09-08')"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
