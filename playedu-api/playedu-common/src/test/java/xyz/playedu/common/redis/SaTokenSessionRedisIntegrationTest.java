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
package xyz.playedu.common.redis;

import static org.assertj.core.api.Assertions.assertThat;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import xyz.playedu.common.config.SaTokenConfig;

class SaTokenSessionRedisIntegrationTest {
    @Test
    void sharesLoginAndLogoutAcrossTwoIndependentJvms() throws Exception {
        try (GenericContainer<?> redis =
                new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
                        .withExposedPorts(6379)) {
            redis.start();
            try (Instance first = new Instance(redis);
                    Instance second = new Instance(redis)) {
                String token = first.request("login");
                assertThat(second.request("check " + token)).isEqualTo("42");
                assertThat(second.request("logout " + token)).isEqualTo("logged-out");
                assertThat(first.request("check " + token)).isEqualTo("null");
            }
        }
    }

    private static class Instance implements AutoCloseable {
        private final Process process;
        private final BufferedReader output;
        private final PrintWriter input;

        Instance(GenericContainer<?> redis) throws Exception {
            process =
                    new ProcessBuilder(
                                    Path.of(System.getProperty("java.home"), "bin", "java")
                                            .toString(),
                                    "-cp",
                                    System.getProperty("surefire.test.class.path"),
                                    Worker.class.getName(),
                                    redis.getHost(),
                                    redis.getMappedPort(6379).toString())
                            .redirectErrorStream(true)
                            .start();
            output = new BufferedReader(new InputStreamReader(process.getInputStream()));
            input = new PrintWriter(process.getOutputStream(), true);
            try {
                assertThat(response()).isEqualTo("ready");
            } catch (Exception | AssertionError failure) {
                close();
                throw failure;
            }
        }

        String request(String command) throws Exception {
            input.println(command);
            return response();
        }

        private String response() throws Exception {
            return CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    String line;
                                    while ((line = output.readLine()) != null) {
                                        if (line.startsWith("RESULT:")) {
                                            return line.substring(7);
                                        }
                                    }
                                    throw new IllegalStateException(
                                            "Session test JVM exited without a result");
                                } catch (java.io.IOException exception) {
                                    throw new IllegalStateException(exception);
                                }
                            })
                    .get(30, TimeUnit.SECONDS);
        }

        @Override
        public void close() {
            process.destroyForcibly();
        }
    }

    public static class Worker {
        public static void main(String[] args) {
            new ApplicationContextRunner()
                    .withUserConfiguration(TestApplication.class)
                    .withPropertyValues(
                            "spring.data.redis.host=" + args[0],
                            "spring.data.redis.port=" + args[1],
                            "sa-token.is-print=false",
                            "sa-token.jwt-secret-key=multi-instance-test-only-key",
                            "sa-token.is-concurrent=false",
                            "sa-token.is-share=false",
                            "sa-token.timeout=60")
                    .run(
                            context -> {
                                assertThat(context).hasSingleBean(SaTokenDao.class);
                                assertThat(
                                                context.getBean(SaTokenDao.class)
                                                        .getClass()
                                                        .getSimpleName())
                                        .isEqualTo("SaTokenDaoRedisJackson");
                                System.out.println("RESULT:ready");
                                BufferedReader commands =
                                        new BufferedReader(new InputStreamReader(System.in));
                                String command;
                                while ((command = commands.readLine()) != null) {
                                    String result;
                                    if (command.equals("login")) {
                                        result = StpUtil.getStpLogic().createLoginSession(42);
                                    } else if (command.startsWith("check ")) {
                                        result =
                                                String.valueOf(
                                                        StpUtil.getLoginIdByToken(
                                                                command.substring(6)));
                                    } else if (command.startsWith("logout ")) {
                                        StpUtil.logoutByTokenValue(command.substring(7));
                                        result = "logged-out";
                                    } else {
                                        throw new IllegalArgumentException("Unknown test command");
                                    }
                                    System.out.println("RESULT:" + result);
                                }
                            });
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
    @Import(SaTokenConfig.class)
    static class TestApplication {}
}
