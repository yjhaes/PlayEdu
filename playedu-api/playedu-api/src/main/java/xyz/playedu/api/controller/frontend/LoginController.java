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
package xyz.playedu.api.controller.frontend;

import java.util.HashMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.playedu.api.bus.LoginBus;
import xyz.playedu.api.cache.LoginLimitCache;
import xyz.playedu.api.event.UserLogoutEvent;
import xyz.playedu.api.request.frontend.LoginLdapRequest;
import xyz.playedu.api.request.frontend.LoginPasswordRequest;
import xyz.playedu.common.context.FCtx;
import xyz.playedu.common.domain.User;
import xyz.playedu.common.exception.LimitException;
import xyz.playedu.common.exception.ServiceException;
import xyz.playedu.common.redis.RedisDistributedLock;
import xyz.playedu.common.redis.RedisLockException;
import xyz.playedu.common.service.*;
import xyz.playedu.common.types.JsonResponse;
import xyz.playedu.common.types.LdapConfig;
import xyz.playedu.common.util.*;
import xyz.playedu.common.util.ldap.LdapTransformUser;
import xyz.playedu.common.util.ldap.LdapUtil;

@RestController
@RequestMapping("/api/v1/auth/login")
@Slf4j
public class LoginController {

    @Autowired private UserService userService;

    @Autowired private FrontendAuthService authService;

    @Autowired private ApplicationContext ctx;

    @Autowired private AppConfigService appConfigService;

    @Autowired private LoginBus loginBus;

    @Autowired private LoginLimitCache loginLimitCache;

    @Autowired private RedisDistributedLock distributedLock;

    @PostMapping("/password")
    @SneakyThrows
    public JsonResponse password(@RequestBody @Validated LoginPasswordRequest req)
            throws LimitException {
        if (appConfigService.enabledLdapLogin()) {
            return JsonResponse.error("请使用LDAP登录");
        }

        String email = req.getEmail();

        User user = userService.find(email);
        if (user == null) {
            return JsonResponse.error("邮箱或密码错误");
        }

        loginLimitCache.check(email);

        if (!HelperUtil.MD5(req.getPassword() + user.getSalt()).equals(user.getPassword())) {
            return JsonResponse.error("邮箱或密码错误");
        }

        if (user.getIsLock() == 1) {
            return JsonResponse.error("当前学员已锁定无法登录");
        }

        loginLimitCache.destroy(email);

        return JsonResponse.data(loginBus.tokenByUser(user));
    }

    @PostMapping("/ldap")
    @SneakyThrows
    public JsonResponse ldap(@RequestBody @Validated LoginLdapRequest req) {
        String username = req.getUsername();

        LdapConfig ldapConfig = appConfigService.ldapConfig();

        String mail = StringUtil.contains(username, "@") ? username : null;
        String uid = StringUtil.contains(username, "@") ? null : username;

        // 限流控制
        loginLimitCache.check(username);

        try {
            return distributedLock.execute(
                    "login",
                    username,
                    () -> {
                        LdapTransformUser ldapTransformUser =
                                ldapLogin(ldapConfig, mail, uid, req.getPassword());
                        if (ldapTransformUser == null) {
                            return JsonResponse.error("登录失败.请检查账号和密码");
                        }

                        HashMap<String, Object> data =
                                loginBus.tokenByLdapTransformUser(ldapTransformUser);
                        loginLimitCache.destroy(username);
                        return JsonResponse.data(data);
                    });
        } catch (RedisLockException e) {
            return JsonResponse.error("请稍候再试");
        } catch (ServiceException e) {
            return JsonResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("LDAP登录失败", e);
            return JsonResponse.error("系统错误");
        }
    }

    @PostMapping("/logout")
    public JsonResponse logout() {
        authService.logout();
        ctx.publishEvent(new UserLogoutEvent(this, FCtx.getId(), FCtx.getJwtJti()));
        return JsonResponse.success();
    }

    private LdapTransformUser ldapLogin(
            LdapConfig config, String mail, String uid, String password) {
        try {
            return LdapUtil.loginByMailOrUid(config, mail, uid, password);
        } catch (ServiceException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new LdapLoginException(exception);
        }
    }

    private static class LdapLoginException extends RuntimeException {
        private LdapLoginException(Exception cause) {
            super(cause);
        }
    }
}
