# 三元表达式收集清单

收集日期：2026-09-16。范围：playedu-api 下 rg 默认可见的后端 Java 文件，包含测试；排除依赖和构建产物。

屏蔽 Java 注释和字符串后识别问号，并排除泛型通配符及 SQL 占位符。每个三元运算符计一处，嵌套表达式分别计数；行号指向 `?`。代码片段为所在语句上下文。

扫描 **380** 个文件，发现 **534** 处三元表达式，涉及 **59** 个文件。

Java 实体 equals/hashCode 中的三元表达式也保留在清单中。

| 模块 | 表达式数 | 文件数 |
| --- | ---: | ---: |
| playedu-api/playedu-api | 7 | 5 |
| playedu-api/playedu-common | 284 | 26 |
| playedu-api/playedu-course | 188 | 17 |
| playedu-api/playedu-points | 15 | 7 |
| playedu-api/playedu-resource | 36 | 3 |
| playedu-api/playedu-system | 4 | 1 |

## 文件索引

| 文件 | 数量 | 问号行号（重复表示同一行多处） |
| --- | ---: | --- |
| [playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/acceptance/AcceptanceProbeController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/acceptance/AcceptanceProbeController.java:76) | 1 | 76 |
| [playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/HourController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/HourController.java:126) | 1 | 126 |
| [playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/LoginController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/LoginController.java:96) | 2 | 96, 97 |
| [playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/PointsController.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/PointsController.java:150) | 2 | 150, 234 |
| [playedu-api/playedu-api/src/test/java/xyz/playedu/api/service/UserDeletionServiceIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/test/java/xyz/playedu/api/service/UserDeletionServiceIntegrationTest.java:381) | 1 | 381 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/bus/LDAPBus.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/bus/LDAPBus.java:358) | 2 | 358, 621 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:99) | 30 | 99, 101, 104, 107, 110, 113, 116, 119, 122, 125, 128, 131, 134, 137, 140, 148, 149, 150, 151, 152, 153, 154, 156, 157, 158, 159, 160, 161, 162, 163 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:71) | 14 | 71, 73, 76, 79, 82, 85, 88, 96, 97, 98, 99, 100, 101, 102 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java:64) | 10 | 64, 66, 69, 72, 75, 83, 84, 85, 86, 87 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRolePermission.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRolePermission.java:55) | 4 | 55, 58, 66, 67 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:101) | 22 | 101, 103, 106, 109, 112, 115, 118, 121, 124, 127, 130, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUserRole.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUserRole.java:55) | 4 | 55, 58, 66, 67 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:80) | 25 | 80, 98, 100, 103, 106, 109, 112, 115, 118, 121, 124, 127, 130, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:73) | 14 | 73, 75, 78, 81, 84, 87, 90, 98, 99, 100, 101, 102, 103, 104 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:72) | 14 | 72, 74, 77, 80, 83, 86, 89, 97, 98, 99, 100, 101, 102, 103 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:125) | 12 | 125, 127, 130, 133, 136, 139, 147, 148, 149, 150, 151, 152 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:176) | 20 | 176, 178, 181, 184, 187, 190, 193, 196, 199, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:133) | 36 | 133, 135, 138, 141, 144, 147, 150, 153, 156, 159, 162, 165, 168, 171, 174, 177, 180, 183, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 206, 207, 208, 209 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserDepartment.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserDepartment.java:55) | 4 | 55, 58, 66, 67 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:81) | 22 | 81, 83, 86, 89, 92, 95, 98, 101, 104, 107, 110, 118, 119, 120, 121, 122, 123, 126, 127, 128, 129, 130 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:81) | 20 | 81, 83, 86, 89, 92, 95, 98, 101, 104, 107, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AppConfigServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AppConfigServiceImpl.java:110) | 1 | 110 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/CategoryServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/CategoryServiceImpl.java:120) | 2 | 120, 174 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/DepartmentServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/DepartmentServiceImpl.java:127) | 3 | 127, 154, 277 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/LdapUserServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/LdapUserServiceImpl.java:74) | 3 | 74, 82, 90 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/Base64Util.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/Base64Util.java:85) | 7 | 85, 102, 103, 104, 115, 125, 126 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/IpUtil.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/IpUtil.java:87) | 4 | 87, 120, 121, 122 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/ldap/LdapUtil.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/ldap/LdapUtil.java:208) | 2 | 208, 341 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/RequestUtil.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/RequestUtil.java:31) | 6 | 31, 56, 62, 69, 74, 79 |
| [playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/StringUtil.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/StringUtil.java:37) | 2 | 37, 152 |
| [playedu-api/playedu-common/src/test/java/xyz/playedu/common/redis/RedisRuntimeIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/test/java/xyz/playedu/common/redis/RedisRuntimeIntegrationTest.java:184) | 1 | 184 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:95) | 30 | 95, 97, 100, 103, 106, 109, 112, 115, 118, 121, 124, 127, 130, 133, 136, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:80) | 18 | 80, 82, 85, 88, 91, 94, 97, 100, 103, 111, 112, 113, 114, 115, 116, 117, 118, 119 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:76) | 16 | 76, 78, 81, 84, 87, 90, 93, 96, 104, 105, 106, 107, 111, 113, 114, 115 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseCategory.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseCategory.java:57) | 4 | 57, 60, 68, 69 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:68) | 12 | 68, 70, 73, 76, 79, 82, 90, 91, 92, 93, 94, 95 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseDepartmentUser.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseDepartmentUser.java:60) | 6 | 60, 63, 66, 74, 75, 76 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:82) | 20 | 82, 84, 87, 90, 93, 96, 99, 102, 105, 108, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:92) | 22 | 92, 94, 97, 100, 103, 106, 109, 112, 115, 118, 121, 129, 130, 131, 132, 134, 137, 138, 139, 140, 141, 142 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:87) | 20 | 87, 89, 92, 95, 98, 101, 104, 107, 110, 113, 121, 122, 123, 124, 126, 127, 128, 129, 130, 131 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:79) | 16 | 79, 81, 84, 87, 90, 93, 96, 99, 107, 108, 109, 110, 111, 112, 113, 114 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java:63) | 8 | 63, 65, 68, 71, 79, 80, 81, 82 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/ActiveLearningLeaseService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/ActiveLearningLeaseService.java:150) | 1 | 150 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/DailyLearningRankingService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/DailyLearningRankingService.java:284) | 1 | 284 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseHourRecordServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseHourRecordServiceImpl.java:67) | 3 | 67, 76, 87 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseRecordServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseRecordServiceImpl.java:61) | 4 | 61, 71, 83, 89 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserLearnDurationStatsServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserLearnDurationStatsServiceImpl.java:68) | 3 | 68, 76, 105 |
| [playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/LearningFactPersistenceService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/LearningFactPersistenceService.java:106) | 4 | 106, 126, 141, 169 |
| [playedu-api/playedu-points/src/main/java/xyz/playedu/points/crypto/PointCodeCryptoService.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/crypto/PointCodeCryptoService.java:64) | 2 | 64, 65 |
| [playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointBalanceServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointBalanceServiceImpl.java:85) | 1 | 85 |
| [playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointCodeServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointCodeServiceImpl.java:63) | 2 | 63, 207 |
| [playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointLedgerServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointLedgerServiceImpl.java:65) | 2 | 65, 84 |
| [playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointProductServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointProductServiceImpl.java:86) | 4 | 86, 122, 142, 209 |
| [playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointRedemptionServiceImpl.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointRedemptionServiceImpl.java:103) | 2 | 103, 222 |
| [playedu-api/playedu-points/src/test/java/xyz/playedu/points/service/PointBalanceServiceIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/test/java/xyz/playedu/points/service/PointBalanceServiceIntegrationTest.java:348) | 2 | 348, 350 |
| [playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:86) | 22 | 86, 88, 91, 94, 97, 100, 103, 106, 109, 112, 115, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133 |
| [playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceCategory.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceCategory.java:54) | 4 | 54, 57, 65, 66 |
| [playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java:65) | 10 | 65, 67, 70, 73, 76, 84, 85, 86, 87, 88 |
| [playedu-api/playedu-system/src/main/java/xyz/playedu/system/domain/Migration.java](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/domain/Migration.java:71) | 4 | 71, 73, 81, 82 |

## 代码明细

### playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/acceptance/AcceptanceProbeController.java

[第 76 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/acceptance/AcceptanceProbeController.java:76)

```java
long holdMillis = request.holdMillis() == null ? 0 : request.holdMillis();
```

### playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/HourController.java

[第 126 行，第 44 列](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/HourController.java:126)

```java
String sessionId = request == null ? null : request.getSessionId();
```

### playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/LoginController.java

[第 96 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/LoginController.java:96)

```java
String mail = StringUtil.contains(username, "@") ? username : null;
```

[第 97 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/LoginController.java:97)

```java
String uid = StringUtil.contains(username, "@") ? null : username;
```

### playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/PointsController.java

[第 150 行，第 45 列](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/PointsController.java:150)

```java
String requestKey = request == null ? null : request.getRequestKey();
```

[第 234 行，第 62 列](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/PointsController.java:234)

```java
List<PointLedger> ledgers = result.getData() == null ? List.of() : result.getData();
```

### playedu-api/playedu-api/src/test/java/xyz/playedu/api/service/UserDeletionServiceIntegrationTest.java

[第 381 行，第 47 列](C:/Users/86198/Desktop/play/playedu-api/playedu-api/src/test/java/xyz/playedu/api/service/UserDeletionServiceIntegrationTest.java:381)

```java
String column = "users".equals(table) ? "id" : "user_id";
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/bus/LDAPBus.java

[第 358 行，第 65 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/bus/LDAPBus.java:358)

```java
deletedDepartmentCount = ldapDepartmentList != null ? ldapDepartmentList.size() : 0;
```

[第 621 行，第 39 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/bus/LDAPBus.java:621)

```java
Integer[] depIds = depId == 0 ? null : new Integer[] {depId};
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java

[第 99 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:99)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()))
                && (this.getAdminName() == null
                        ? other.getAdminName() == null
                        : this.getAdminName().equals(other.getAdminName()))
                && (this.getModule() == null
                        ? other.getModule() == null
                        : this.getModule().equals(other.getModule()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getOpt() == null
                        ? other.getOpt() == null
                        : this.getOpt().equals(other.getOpt()))
                && (this.getMethod() == null
                        ? other.getMethod() == null
                        : this.getMethod().equals(other.getMethod()))
                && (this.getRequestMethod() == null
                        ? other.getRequestMethod() == null
                        : this.getRequestMethod().equals(other.getRequestMethod()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getParam() == null
                        ? other.getParam() == null
                        : this.getParam().equals(other.getParam()))
                && (this.getResult() == null
                        ? other.getResult() == null
                        : this.getResult().equals(other.getResult()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 101 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:101)

```java
&& (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()))
                && (this.getAdminName() == null
                        ? other.getAdminName() == null
                        : this.getAdminName().equals(other.getAdminName()))
                && (this.getModule() == null
                        ? other.getModule() == null
                        : this.getModule().equals(other.getModule()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getOpt() == null
                        ? other.getOpt() == null
                        : this.getOpt().equals(other.getOpt()))
                && (this.getMethod() == null
                        ? other.getMethod() == null
                        : this.getMethod().equals(other.getMethod()))
                && (this.getRequestMethod() == null
                        ? other.getRequestMethod() == null
                        : this.getRequestMethod().equals(other.getRequestMethod()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getParam() == null
                        ? other.getParam() == null
                        : this.getParam().equals(other.getParam()))
                && (this.getResult() == null
                        ? other.getResult() == null
                        : this.getResult().equals(other.getResult()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 104 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:104)

```java
&& (this.getAdminName() == null
                        ? other.getAdminName() == null
                        : this.getAdminName().equals(other.getAdminName()))
                && (this.getModule() == null
                        ? other.getModule() == null
                        : this.getModule().equals(other.getModule()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getOpt() == null
                        ? other.getOpt() == null
                        : this.getOpt().equals(other.getOpt()))
                && (this.getMethod() == null
                        ? other.getMethod() == null
                        : this.getMethod().equals(other.getMethod()))
                && (this.getRequestMethod() == null
                        ? other.getRequestMethod() == null
                        : this.getRequestMethod().equals(other.getRequestMethod()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getParam() == null
                        ? other.getParam() == null
                        : this.getParam().equals(other.getParam()))
                && (this.getResult() == null
                        ? other.getResult() == null
                        : this.getResult().equals(other.getResult()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 107 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:107)

```java
&& (this.getModule() == null
                        ? other.getModule() == null
                        : this.getModule().equals(other.getModule()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getOpt() == null
                        ? other.getOpt() == null
                        : this.getOpt().equals(other.getOpt()))
                && (this.getMethod() == null
                        ? other.getMethod() == null
                        : this.getMethod().equals(other.getMethod()))
                && (this.getRequestMethod() == null
                        ? other.getRequestMethod() == null
                        : this.getRequestMethod().equals(other.getRequestMethod()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getParam() == null
                        ? other.getParam() == null
                        : this.getParam().equals(other.getParam()))
                && (this.getResult() == null
                        ? other.getResult() == null
                        : this.getResult().equals(other.getResult()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 110 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:110)

```java
&& (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getOpt() == null
                        ? other.getOpt() == null
                        : this.getOpt().equals(other.getOpt()))
                && (this.getMethod() == null
                        ? other.getMethod() == null
                        : this.getMethod().equals(other.getMethod()))
                && (this.getRequestMethod() == null
                        ? other.getRequestMethod() == null
                        : this.getRequestMethod().equals(other.getRequestMethod()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getParam() == null
                        ? other.getParam() == null
                        : this.getParam().equals(other.getParam()))
                && (this.getResult() == null
                        ? other.getResult() == null
                        : this.getResult().equals(other.getResult()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 113 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:113)

```java
&& (this.getOpt() == null
                        ? other.getOpt() == null
                        : this.getOpt().equals(other.getOpt()))
                && (this.getMethod() == null
                        ? other.getMethod() == null
                        : this.getMethod().equals(other.getMethod()))
                && (this.getRequestMethod() == null
                        ? other.getRequestMethod() == null
                        : this.getRequestMethod().equals(other.getRequestMethod()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getParam() == null
                        ? other.getParam() == null
                        : this.getParam().equals(other.getParam()))
                && (this.getResult() == null
                        ? other.getResult() == null
                        : this.getResult().equals(other.getResult()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 116 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:116)

```java
&& (this.getMethod() == null
                        ? other.getMethod() == null
                        : this.getMethod().equals(other.getMethod()))
                && (this.getRequestMethod() == null
                        ? other.getRequestMethod() == null
                        : this.getRequestMethod().equals(other.getRequestMethod()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getParam() == null
                        ? other.getParam() == null
                        : this.getParam().equals(other.getParam()))
                && (this.getResult() == null
                        ? other.getResult() == null
                        : this.getResult().equals(other.getResult()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 119 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:119)

```java
&& (this.getRequestMethod() == null
                        ? other.getRequestMethod() == null
                        : this.getRequestMethod().equals(other.getRequestMethod()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getParam() == null
                        ? other.getParam() == null
                        : this.getParam().equals(other.getParam()))
                && (this.getResult() == null
                        ? other.getResult() == null
                        : this.getResult().equals(other.getResult()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 122 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:122)

```java
&& (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getParam() == null
                        ? other.getParam() == null
                        : this.getParam().equals(other.getParam()))
                && (this.getResult() == null
                        ? other.getResult() == null
                        : this.getResult().equals(other.getResult()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 125 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:125)

```java
&& (this.getParam() == null
                        ? other.getParam() == null
                        : this.getParam().equals(other.getParam()))
                && (this.getResult() == null
                        ? other.getResult() == null
                        : this.getResult().equals(other.getResult()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 128 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:128)

```java
&& (this.getResult() == null
                        ? other.getResult() == null
                        : this.getResult().equals(other.getResult()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 131 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:131)

```java
&& (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 134 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:134)

```java
&& (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 137 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:137)

```java
&& (this.getErrorMsg() == null
                        ? other.getErrorMsg() == null
                        : this.getErrorMsg().equals(other.getErrorMsg()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 140 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:140)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 148 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:148)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 149 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:149)

```java
result = prime * result + ((getAdminId() == null) ? 0 : getAdminId().hashCode());
```

[第 150 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:150)

```java
result = prime * result + ((getAdminName() == null) ? 0 : getAdminName().hashCode());
```

[第 151 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:151)

```java
result = prime * result + ((getModule() == null) ? 0 : getModule().hashCode());
```

[第 152 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:152)

```java
result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
```

[第 153 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:153)

```java
result = prime * result + ((getOpt() == null) ? 0 : getOpt().hashCode());
```

[第 154 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:154)

```java
result = prime * result + ((getMethod() == null) ? 0 : getMethod().hashCode());
```

[第 156 行，第 64 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:156)

```java
prime * result + ((getRequestMethod() == null) ? 0 : getRequestMethod().hashCode());
```

[第 157 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:157)

```java
result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
```

[第 158 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:158)

```java
result = prime * result + ((getParam() == null) ? 0 : getParam().hashCode());
```

[第 159 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:159)

```java
result = prime * result + ((getResult() == null) ? 0 : getResult().hashCode());
```

[第 160 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:160)

```java
result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
```

[第 161 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:161)

```java
result = prime * result + ((getIpArea() == null) ? 0 : getIpArea().hashCode());
```

[第 162 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:162)

```java
result = prime * result + ((getErrorMsg() == null) ? 0 : getErrorMsg().hashCode());
```

[第 163 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminLog.java:163)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java

[第 71 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:71)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getGroupName() == null
                        ? other.getGroupName() == null
                        : this.getGroupName().equals(other.getGroupName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSlug() == null
                        ? other.getSlug() == null
                        : this.getSlug().equals(other.getSlug()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 73 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:73)

```java
&& (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getGroupName() == null
                        ? other.getGroupName() == null
                        : this.getGroupName().equals(other.getGroupName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSlug() == null
                        ? other.getSlug() == null
                        : this.getSlug().equals(other.getSlug()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 76 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:76)

```java
&& (this.getGroupName() == null
                        ? other.getGroupName() == null
                        : this.getGroupName().equals(other.getGroupName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSlug() == null
                        ? other.getSlug() == null
                        : this.getSlug().equals(other.getSlug()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 79 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:79)

```java
&& (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSlug() == null
                        ? other.getSlug() == null
                        : this.getSlug().equals(other.getSlug()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 82 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:82)

```java
&& (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSlug() == null
                        ? other.getSlug() == null
                        : this.getSlug().equals(other.getSlug()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 85 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:85)

```java
&& (this.getSlug() == null
                        ? other.getSlug() == null
                        : this.getSlug().equals(other.getSlug()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 88 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:88)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 96 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:96)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 97 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:97)

```java
result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
```

[第 98 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:98)

```java
result = prime * result + ((getGroupName() == null) ? 0 : getGroupName().hashCode());
```

[第 99 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:99)

```java
result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
```

[第 100 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:100)

```java
result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
```

[第 101 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:101)

```java
result = prime * result + ((getSlug() == null) ? 0 : getSlug().hashCode());
```

[第 102 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminPermission.java:102)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java

[第 64 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java:64)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSlug() == null
                        ? other.getSlug() == null
                        : this.getSlug().equals(other.getSlug()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 66 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java:66)

```java
&& (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSlug() == null
                        ? other.getSlug() == null
                        : this.getSlug().equals(other.getSlug()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 69 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java:69)

```java
&& (this.getSlug() == null
                        ? other.getSlug() == null
                        : this.getSlug().equals(other.getSlug()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 72 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java:72)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 75 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java:75)

```java
&& (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 83 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java:83)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 84 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java:84)

```java
result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
```

[第 85 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java:85)

```java
result = prime * result + ((getSlug() == null) ? 0 : getSlug().hashCode());
```

[第 86 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java:86)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 87 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRole.java:87)

```java
result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRolePermission.java

[第 55 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRolePermission.java:55)

```java
return (this.getRoleId() == null
                        ? other.getRoleId() == null
                        : this.getRoleId().equals(other.getRoleId()))
                && (this.getPermId() == null
                        ? other.getPermId() == null
                        : this.getPermId().equals(other.getPermId()));
```

[第 58 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRolePermission.java:58)

```java
&& (this.getPermId() == null
                        ? other.getPermId() == null
                        : this.getPermId().equals(other.getPermId()));
```

[第 66 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRolePermission.java:66)

```java
result = prime * result + ((getRoleId() == null) ? 0 : getRoleId().hashCode());
```

[第 67 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminRolePermission.java:67)

```java
result = prime * result + ((getPermId() == null) ? 0 : getPermId().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java

[第 101 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:101)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getPassword() == null
                        ? other.getPassword() == null
                        : this.getPassword().equals(other.getPassword()))
                && (this.getSalt() == null
                        ? other.getSalt() == null
                        : this.getSalt().equals(other.getSalt()))
                && (this.getLoginIp() == null
                        ? other.getLoginIp() == null
                        : this.getLoginIp().equals(other.getLoginIp()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getIsBanLogin() == null
                        ? other.getIsBanLogin() == null
                        : this.getIsBanLogin().equals(other.getIsBanLogin()))
                && (this.getLoginTimes() == null
                        ? other.getLoginTimes() == null
                        : this.getLoginTimes().equals(other.getLoginTimes()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 103 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:103)

```java
&& (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getPassword() == null
                        ? other.getPassword() == null
                        : this.getPassword().equals(other.getPassword()))
                && (this.getSalt() == null
                        ? other.getSalt() == null
                        : this.getSalt().equals(other.getSalt()))
                && (this.getLoginIp() == null
                        ? other.getLoginIp() == null
                        : this.getLoginIp().equals(other.getLoginIp()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getIsBanLogin() == null
                        ? other.getIsBanLogin() == null
                        : this.getIsBanLogin().equals(other.getIsBanLogin()))
                && (this.getLoginTimes() == null
                        ? other.getLoginTimes() == null
                        : this.getLoginTimes().equals(other.getLoginTimes()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 106 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:106)

```java
&& (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getPassword() == null
                        ? other.getPassword() == null
                        : this.getPassword().equals(other.getPassword()))
                && (this.getSalt() == null
                        ? other.getSalt() == null
                        : this.getSalt().equals(other.getSalt()))
                && (this.getLoginIp() == null
                        ? other.getLoginIp() == null
                        : this.getLoginIp().equals(other.getLoginIp()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getIsBanLogin() == null
                        ? other.getIsBanLogin() == null
                        : this.getIsBanLogin().equals(other.getIsBanLogin()))
                && (this.getLoginTimes() == null
                        ? other.getLoginTimes() == null
                        : this.getLoginTimes().equals(other.getLoginTimes()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 109 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:109)

```java
&& (this.getPassword() == null
                        ? other.getPassword() == null
                        : this.getPassword().equals(other.getPassword()))
                && (this.getSalt() == null
                        ? other.getSalt() == null
                        : this.getSalt().equals(other.getSalt()))
                && (this.getLoginIp() == null
                        ? other.getLoginIp() == null
                        : this.getLoginIp().equals(other.getLoginIp()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getIsBanLogin() == null
                        ? other.getIsBanLogin() == null
                        : this.getIsBanLogin().equals(other.getIsBanLogin()))
                && (this.getLoginTimes() == null
                        ? other.getLoginTimes() == null
                        : this.getLoginTimes().equals(other.getLoginTimes()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 112 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:112)

```java
&& (this.getSalt() == null
                        ? other.getSalt() == null
                        : this.getSalt().equals(other.getSalt()))
                && (this.getLoginIp() == null
                        ? other.getLoginIp() == null
                        : this.getLoginIp().equals(other.getLoginIp()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getIsBanLogin() == null
                        ? other.getIsBanLogin() == null
                        : this.getIsBanLogin().equals(other.getIsBanLogin()))
                && (this.getLoginTimes() == null
                        ? other.getLoginTimes() == null
                        : this.getLoginTimes().equals(other.getLoginTimes()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 115 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:115)

```java
&& (this.getLoginIp() == null
                        ? other.getLoginIp() == null
                        : this.getLoginIp().equals(other.getLoginIp()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getIsBanLogin() == null
                        ? other.getIsBanLogin() == null
                        : this.getIsBanLogin().equals(other.getIsBanLogin()))
                && (this.getLoginTimes() == null
                        ? other.getLoginTimes() == null
                        : this.getLoginTimes().equals(other.getLoginTimes()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 118 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:118)

```java
&& (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getIsBanLogin() == null
                        ? other.getIsBanLogin() == null
                        : this.getIsBanLogin().equals(other.getIsBanLogin()))
                && (this.getLoginTimes() == null
                        ? other.getLoginTimes() == null
                        : this.getLoginTimes().equals(other.getLoginTimes()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 121 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:121)

```java
&& (this.getIsBanLogin() == null
                        ? other.getIsBanLogin() == null
                        : this.getIsBanLogin().equals(other.getIsBanLogin()))
                && (this.getLoginTimes() == null
                        ? other.getLoginTimes() == null
                        : this.getLoginTimes().equals(other.getLoginTimes()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 124 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:124)

```java
&& (this.getLoginTimes() == null
                        ? other.getLoginTimes() == null
                        : this.getLoginTimes().equals(other.getLoginTimes()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 127 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:127)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 130 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:130)

```java
&& (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 138 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:138)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 139 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:139)

```java
result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
```

[第 140 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:140)

```java
result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
```

[第 141 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:141)

```java
result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
```

[第 142 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:142)

```java
result = prime * result + ((getSalt() == null) ? 0 : getSalt().hashCode());
```

[第 143 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:143)

```java
result = prime * result + ((getLoginIp() == null) ? 0 : getLoginIp().hashCode());
```

[第 144 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:144)

```java
result = prime * result + ((getLoginAt() == null) ? 0 : getLoginAt().hashCode());
```

[第 145 行，第 62 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:145)

```java
result = prime * result + ((getIsBanLogin() == null) ? 0 : getIsBanLogin().hashCode());
```

[第 146 行，第 62 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:146)

```java
result = prime * result + ((getLoginTimes() == null) ? 0 : getLoginTimes().hashCode());
```

[第 147 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:147)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 148 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUser.java:148)

```java
result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUserRole.java

[第 55 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUserRole.java:55)

```java
return (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()))
                && (this.getRoleId() == null
                        ? other.getRoleId() == null
                        : this.getRoleId().equals(other.getRoleId()));
```

[第 58 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUserRole.java:58)

```java
&& (this.getRoleId() == null
                        ? other.getRoleId() == null
                        : this.getRoleId().equals(other.getRoleId()));
```

[第 66 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUserRole.java:66)

```java
result = prime * result + ((getAdminId() == null) ? 0 : getAdminId().hashCode());
```

[第 67 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AdminUserRole.java:67)

```java
result = prime * result + ((getRoleId() == null) ? 0 : getRoleId().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java

[第 80 行，第 30 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:80)

```java
return isHidden == 1 ? "******" : keyValue;
```

[第 98 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:98)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getGroupName() == null
                        ? other.getGroupName() == null
                        : this.getGroupName().equals(other.getGroupName()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getFieldType() == null
                        ? other.getFieldType() == null
                        : this.getFieldType().equals(other.getFieldType()))
                && (this.getKeyName() == null
                        ? other.getKeyName() == null
                        : this.getKeyName().equals(other.getKeyName()))
                && (this.getKeyValue() == null
                        ? other.getKeyValue() == null
                        : this.getKeyValue().equals(other.getKeyValue()))
                && (this.getOptionValue() == null
                        ? other.getOptionValue() == null
                        : this.getOptionValue().equals(other.getOptionValue()))
                && (this.getIsPrivate() == null
                        ? other.getIsPrivate() == null
                        : this.getIsPrivate().equals(other.getIsPrivate()))
                && (this.getHelp() == null
                        ? other.getHelp() == null
                        : this.getHelp().equals(other.getHelp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 100 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:100)

```java
&& (this.getGroupName() == null
                        ? other.getGroupName() == null
                        : this.getGroupName().equals(other.getGroupName()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getFieldType() == null
                        ? other.getFieldType() == null
                        : this.getFieldType().equals(other.getFieldType()))
                && (this.getKeyName() == null
                        ? other.getKeyName() == null
                        : this.getKeyName().equals(other.getKeyName()))
                && (this.getKeyValue() == null
                        ? other.getKeyValue() == null
                        : this.getKeyValue().equals(other.getKeyValue()))
                && (this.getOptionValue() == null
                        ? other.getOptionValue() == null
                        : this.getOptionValue().equals(other.getOptionValue()))
                && (this.getIsPrivate() == null
                        ? other.getIsPrivate() == null
                        : this.getIsPrivate().equals(other.getIsPrivate()))
                && (this.getHelp() == null
                        ? other.getHelp() == null
                        : this.getHelp().equals(other.getHelp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 103 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:103)

```java
&& (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getFieldType() == null
                        ? other.getFieldType() == null
                        : this.getFieldType().equals(other.getFieldType()))
                && (this.getKeyName() == null
                        ? other.getKeyName() == null
                        : this.getKeyName().equals(other.getKeyName()))
                && (this.getKeyValue() == null
                        ? other.getKeyValue() == null
                        : this.getKeyValue().equals(other.getKeyValue()))
                && (this.getOptionValue() == null
                        ? other.getOptionValue() == null
                        : this.getOptionValue().equals(other.getOptionValue()))
                && (this.getIsPrivate() == null
                        ? other.getIsPrivate() == null
                        : this.getIsPrivate().equals(other.getIsPrivate()))
                && (this.getHelp() == null
                        ? other.getHelp() == null
                        : this.getHelp().equals(other.getHelp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 106 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:106)

```java
&& (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getFieldType() == null
                        ? other.getFieldType() == null
                        : this.getFieldType().equals(other.getFieldType()))
                && (this.getKeyName() == null
                        ? other.getKeyName() == null
                        : this.getKeyName().equals(other.getKeyName()))
                && (this.getKeyValue() == null
                        ? other.getKeyValue() == null
                        : this.getKeyValue().equals(other.getKeyValue()))
                && (this.getOptionValue() == null
                        ? other.getOptionValue() == null
                        : this.getOptionValue().equals(other.getOptionValue()))
                && (this.getIsPrivate() == null
                        ? other.getIsPrivate() == null
                        : this.getIsPrivate().equals(other.getIsPrivate()))
                && (this.getHelp() == null
                        ? other.getHelp() == null
                        : this.getHelp().equals(other.getHelp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 109 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:109)

```java
&& (this.getFieldType() == null
                        ? other.getFieldType() == null
                        : this.getFieldType().equals(other.getFieldType()))
                && (this.getKeyName() == null
                        ? other.getKeyName() == null
                        : this.getKeyName().equals(other.getKeyName()))
                && (this.getKeyValue() == null
                        ? other.getKeyValue() == null
                        : this.getKeyValue().equals(other.getKeyValue()))
                && (this.getOptionValue() == null
                        ? other.getOptionValue() == null
                        : this.getOptionValue().equals(other.getOptionValue()))
                && (this.getIsPrivate() == null
                        ? other.getIsPrivate() == null
                        : this.getIsPrivate().equals(other.getIsPrivate()))
                && (this.getHelp() == null
                        ? other.getHelp() == null
                        : this.getHelp().equals(other.getHelp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 112 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:112)

```java
&& (this.getKeyName() == null
                        ? other.getKeyName() == null
                        : this.getKeyName().equals(other.getKeyName()))
                && (this.getKeyValue() == null
                        ? other.getKeyValue() == null
                        : this.getKeyValue().equals(other.getKeyValue()))
                && (this.getOptionValue() == null
                        ? other.getOptionValue() == null
                        : this.getOptionValue().equals(other.getOptionValue()))
                && (this.getIsPrivate() == null
                        ? other.getIsPrivate() == null
                        : this.getIsPrivate().equals(other.getIsPrivate()))
                && (this.getHelp() == null
                        ? other.getHelp() == null
                        : this.getHelp().equals(other.getHelp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 115 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:115)

```java
&& (this.getKeyValue() == null
                        ? other.getKeyValue() == null
                        : this.getKeyValue().equals(other.getKeyValue()))
                && (this.getOptionValue() == null
                        ? other.getOptionValue() == null
                        : this.getOptionValue().equals(other.getOptionValue()))
                && (this.getIsPrivate() == null
                        ? other.getIsPrivate() == null
                        : this.getIsPrivate().equals(other.getIsPrivate()))
                && (this.getHelp() == null
                        ? other.getHelp() == null
                        : this.getHelp().equals(other.getHelp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 118 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:118)

```java
&& (this.getOptionValue() == null
                        ? other.getOptionValue() == null
                        : this.getOptionValue().equals(other.getOptionValue()))
                && (this.getIsPrivate() == null
                        ? other.getIsPrivate() == null
                        : this.getIsPrivate().equals(other.getIsPrivate()))
                && (this.getHelp() == null
                        ? other.getHelp() == null
                        : this.getHelp().equals(other.getHelp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 121 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:121)

```java
&& (this.getIsPrivate() == null
                        ? other.getIsPrivate() == null
                        : this.getIsPrivate().equals(other.getIsPrivate()))
                && (this.getHelp() == null
                        ? other.getHelp() == null
                        : this.getHelp().equals(other.getHelp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 124 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:124)

```java
&& (this.getHelp() == null
                        ? other.getHelp() == null
                        : this.getHelp().equals(other.getHelp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 127 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:127)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 130 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:130)

```java
&& (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 138 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:138)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 139 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:139)

```java
result = prime * result + ((getGroupName() == null) ? 0 : getGroupName().hashCode());
```

[第 140 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:140)

```java
result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
```

[第 141 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:141)

```java
result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
```

[第 142 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:142)

```java
result = prime * result + ((getFieldType() == null) ? 0 : getFieldType().hashCode());
```

[第 143 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:143)

```java
result = prime * result + ((getKeyName() == null) ? 0 : getKeyName().hashCode());
```

[第 144 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:144)

```java
result = prime * result + ((getKeyValue() == null) ? 0 : getKeyValue().hashCode());
```

[第 145 行，第 63 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:145)

```java
result = prime * result + ((getOptionValue() == null) ? 0 : getOptionValue().hashCode());
```

[第 146 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:146)

```java
result = prime * result + ((getIsPrivate() == null) ? 0 : getIsPrivate().hashCode());
```

[第 147 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:147)

```java
result = prime * result + ((getHelp() == null) ? 0 : getHelp().hashCode());
```

[第 148 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:148)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 149 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/AppConfig.java:149)

```java
result = prime * result + ((getIsHidden() == null) ? 0 : getIsHidden().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java

[第 73 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:73)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getParentChain() == null
                        ? other.getParentChain() == null
                        : this.getParentChain().equals(other.getParentChain()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 75 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:75)

```java
&& (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getParentChain() == null
                        ? other.getParentChain() == null
                        : this.getParentChain().equals(other.getParentChain()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 78 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:78)

```java
&& (this.getParentChain() == null
                        ? other.getParentChain() == null
                        : this.getParentChain().equals(other.getParentChain()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 81 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:81)

```java
&& (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 84 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:84)

```java
&& (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 87 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:87)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 90 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:90)

```java
&& (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 98 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:98)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 99 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:99)

```java
result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
```

[第 100 行，第 63 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:100)

```java
result = prime * result + ((getParentChain() == null) ? 0 : getParentChain().hashCode());
```

[第 101 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:101)

```java
result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
```

[第 102 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:102)

```java
result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
```

[第 103 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:103)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 104 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Category.java:104)

```java
result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java

[第 72 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:72)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getParentChain() == null
                        ? other.getParentChain() == null
                        : this.getParentChain().equals(other.getParentChain()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 74 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:74)

```java
&& (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getParentChain() == null
                        ? other.getParentChain() == null
                        : this.getParentChain().equals(other.getParentChain()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 77 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:77)

```java
&& (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getParentChain() == null
                        ? other.getParentChain() == null
                        : this.getParentChain().equals(other.getParentChain()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 80 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:80)

```java
&& (this.getParentChain() == null
                        ? other.getParentChain() == null
                        : this.getParentChain().equals(other.getParentChain()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 83 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:83)

```java
&& (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 86 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:86)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 89 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:89)

```java
&& (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 97 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:97)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 98 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:98)

```java
result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
```

[第 99 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:99)

```java
result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
```

[第 100 行，第 63 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:100)

```java
result = prime * result + ((getParentChain() == null) ? 0 : getParentChain().hashCode());
```

[第 101 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:101)

```java
result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
```

[第 102 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:102)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 103 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/Department.java:103)

```java
result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java

[第 125 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:125)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUuid() == null
                        ? other.getUuid() == null
                        : this.getUuid().equals(other.getUuid()))
                && (this.getDepartmentId() == null
                        ? other.getDepartmentId() == null
                        : this.getDepartmentId().equals(other.getDepartmentId()))
                && (this.getDn() == null
                        ? other.getDn() == null
                        : this.getDn().equals(other.getDn()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 127 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:127)

```java
&& (this.getUuid() == null
                        ? other.getUuid() == null
                        : this.getUuid().equals(other.getUuid()))
                && (this.getDepartmentId() == null
                        ? other.getDepartmentId() == null
                        : this.getDepartmentId().equals(other.getDepartmentId()))
                && (this.getDn() == null
                        ? other.getDn() == null
                        : this.getDn().equals(other.getDn()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 130 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:130)

```java
&& (this.getDepartmentId() == null
                        ? other.getDepartmentId() == null
                        : this.getDepartmentId().equals(other.getDepartmentId()))
                && (this.getDn() == null
                        ? other.getDn() == null
                        : this.getDn().equals(other.getDn()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 133 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:133)

```java
&& (this.getDn() == null
                        ? other.getDn() == null
                        : this.getDn().equals(other.getDn()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 136 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:136)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 139 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:139)

```java
&& (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 147 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:147)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 148 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:148)

```java
result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
```

[第 149 行，第 64 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:149)

```java
result = prime * result + ((getDepartmentId() == null) ? 0 : getDepartmentId().hashCode());
```

[第 150 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:150)

```java
result = prime * result + ((getDn() == null) ? 0 : getDn().hashCode());
```

[第 151 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:151)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 152 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapDepartment.java:152)

```java
result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java

[第 176 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:176)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUuid() == null
                        ? other.getUuid() == null
                        : this.getUuid().equals(other.getUuid()))
                && (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getCn() == null
                        ? other.getCn() == null
                        : this.getCn().equals(other.getCn()))
                && (this.getDn() == null
                        ? other.getDn() == null
                        : this.getDn().equals(other.getDn()))
                && (this.getOu() == null
                        ? other.getOu() == null
                        : this.getOu().equals(other.getOu()))
                && (this.getUid() == null
                        ? other.getUid() == null
                        : this.getUid().equals(other.getUid()))
                && (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 178 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:178)

```java
&& (this.getUuid() == null
                        ? other.getUuid() == null
                        : this.getUuid().equals(other.getUuid()))
                && (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getCn() == null
                        ? other.getCn() == null
                        : this.getCn().equals(other.getCn()))
                && (this.getDn() == null
                        ? other.getDn() == null
                        : this.getDn().equals(other.getDn()))
                && (this.getOu() == null
                        ? other.getOu() == null
                        : this.getOu().equals(other.getOu()))
                && (this.getUid() == null
                        ? other.getUid() == null
                        : this.getUid().equals(other.getUid()))
                && (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 181 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:181)

```java
&& (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getCn() == null
                        ? other.getCn() == null
                        : this.getCn().equals(other.getCn()))
                && (this.getDn() == null
                        ? other.getDn() == null
                        : this.getDn().equals(other.getDn()))
                && (this.getOu() == null
                        ? other.getOu() == null
                        : this.getOu().equals(other.getOu()))
                && (this.getUid() == null
                        ? other.getUid() == null
                        : this.getUid().equals(other.getUid()))
                && (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 184 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:184)

```java
&& (this.getCn() == null
                        ? other.getCn() == null
                        : this.getCn().equals(other.getCn()))
                && (this.getDn() == null
                        ? other.getDn() == null
                        : this.getDn().equals(other.getDn()))
                && (this.getOu() == null
                        ? other.getOu() == null
                        : this.getOu().equals(other.getOu()))
                && (this.getUid() == null
                        ? other.getUid() == null
                        : this.getUid().equals(other.getUid()))
                && (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 187 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:187)

```java
&& (this.getDn() == null
                        ? other.getDn() == null
                        : this.getDn().equals(other.getDn()))
                && (this.getOu() == null
                        ? other.getOu() == null
                        : this.getOu().equals(other.getOu()))
                && (this.getUid() == null
                        ? other.getUid() == null
                        : this.getUid().equals(other.getUid()))
                && (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 190 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:190)

```java
&& (this.getOu() == null
                        ? other.getOu() == null
                        : this.getOu().equals(other.getOu()))
                && (this.getUid() == null
                        ? other.getUid() == null
                        : this.getUid().equals(other.getUid()))
                && (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 193 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:193)

```java
&& (this.getUid() == null
                        ? other.getUid() == null
                        : this.getUid().equals(other.getUid()))
                && (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 196 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:196)

```java
&& (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 199 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:199)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 202 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:202)

```java
&& (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 210 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:210)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 211 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:211)

```java
result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
```

[第 212 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:212)

```java
result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
```

[第 213 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:213)

```java
result = prime * result + ((getCn() == null) ? 0 : getCn().hashCode());
```

[第 214 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:214)

```java
result = prime * result + ((getDn() == null) ? 0 : getDn().hashCode());
```

[第 215 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:215)

```java
result = prime * result + ((getOu() == null) ? 0 : getOu().hashCode());
```

[第 216 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:216)

```java
result = prime * result + ((getUid() == null) ? 0 : getUid().hashCode());
```

[第 217 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:217)

```java
result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
```

[第 218 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:218)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 219 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/LdapUser.java:219)

```java
result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java

[第 133 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:133)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getAvatar() == null
                        ? other.getAvatar() == null
                        : this.getAvatar().equals(other.getAvatar()))
                && (this.getPassword() == null
                        ? other.getPassword() == null
                        : this.getPassword().equals(other.getPassword()))
                && (this.getSalt() == null
                        ? other.getSalt() == null
                        : this.getSalt().equals(other.getSalt()))
                && (this.getIdCard() == null
                        ? other.getIdCard() == null
                        : this.getIdCard().equals(other.getIdCard()))
                && (this.getCredit1() == null
                        ? other.getCredit1() == null
                        : this.getCredit1().equals(other.getCredit1()))
                && (this.getCreateIp() == null
                        ? other.getCreateIp() == null
                        : this.getCreateIp().equals(other.getCreateIp()))
                && (this.getCreateCity() == null
                        ? other.getCreateCity() == null
                        : this.getCreateCity().equals(other.getCreateCity()))
                && (this.getIsActive() == null
                        ? other.getIsActive() == null
                        : this.getIsActive().equals(other.getIsActive()))
                && (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 135 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:135)

```java
&& (this.getEmail() == null
                        ? other.getEmail() == null
                        : this.getEmail().equals(other.getEmail()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getAvatar() == null
                        ? other.getAvatar() == null
                        : this.getAvatar().equals(other.getAvatar()))
                && (this.getPassword() == null
                        ? other.getPassword() == null
                        : this.getPassword().equals(other.getPassword()))
                && (this.getSalt() == null
                        ? other.getSalt() == null
                        : this.getSalt().equals(other.getSalt()))
                && (this.getIdCard() == null
                        ? other.getIdCard() == null
                        : this.getIdCard().equals(other.getIdCard()))
                && (this.getCredit1() == null
                        ? other.getCredit1() == null
                        : this.getCredit1().equals(other.getCredit1()))
                && (this.getCreateIp() == null
                        ? other.getCreateIp() == null
                        : this.getCreateIp().equals(other.getCreateIp()))
                && (this.getCreateCity() == null
                        ? other.getCreateCity() == null
                        : this.getCreateCity().equals(other.getCreateCity()))
                && (this.getIsActive() == null
                        ? other.getIsActive() == null
                        : this.getIsActive().equals(other.getIsActive()))
                && (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 138 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:138)

```java
&& (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getAvatar() == null
                        ? other.getAvatar() == null
                        : this.getAvatar().equals(other.getAvatar()))
                && (this.getPassword() == null
                        ? other.getPassword() == null
                        : this.getPassword().equals(other.getPassword()))
                && (this.getSalt() == null
                        ? other.getSalt() == null
                        : this.getSalt().equals(other.getSalt()))
                && (this.getIdCard() == null
                        ? other.getIdCard() == null
                        : this.getIdCard().equals(other.getIdCard()))
                && (this.getCredit1() == null
                        ? other.getCredit1() == null
                        : this.getCredit1().equals(other.getCredit1()))
                && (this.getCreateIp() == null
                        ? other.getCreateIp() == null
                        : this.getCreateIp().equals(other.getCreateIp()))
                && (this.getCreateCity() == null
                        ? other.getCreateCity() == null
                        : this.getCreateCity().equals(other.getCreateCity()))
                && (this.getIsActive() == null
                        ? other.getIsActive() == null
                        : this.getIsActive().equals(other.getIsActive()))
                && (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 141 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:141)

```java
&& (this.getAvatar() == null
                        ? other.getAvatar() == null
                        : this.getAvatar().equals(other.getAvatar()))
                && (this.getPassword() == null
                        ? other.getPassword() == null
                        : this.getPassword().equals(other.getPassword()))
                && (this.getSalt() == null
                        ? other.getSalt() == null
                        : this.getSalt().equals(other.getSalt()))
                && (this.getIdCard() == null
                        ? other.getIdCard() == null
                        : this.getIdCard().equals(other.getIdCard()))
                && (this.getCredit1() == null
                        ? other.getCredit1() == null
                        : this.getCredit1().equals(other.getCredit1()))
                && (this.getCreateIp() == null
                        ? other.getCreateIp() == null
                        : this.getCreateIp().equals(other.getCreateIp()))
                && (this.getCreateCity() == null
                        ? other.getCreateCity() == null
                        : this.getCreateCity().equals(other.getCreateCity()))
                && (this.getIsActive() == null
                        ? other.getIsActive() == null
                        : this.getIsActive().equals(other.getIsActive()))
                && (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 144 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:144)

```java
&& (this.getPassword() == null
                        ? other.getPassword() == null
                        : this.getPassword().equals(other.getPassword()))
                && (this.getSalt() == null
                        ? other.getSalt() == null
                        : this.getSalt().equals(other.getSalt()))
                && (this.getIdCard() == null
                        ? other.getIdCard() == null
                        : this.getIdCard().equals(other.getIdCard()))
                && (this.getCredit1() == null
                        ? other.getCredit1() == null
                        : this.getCredit1().equals(other.getCredit1()))
                && (this.getCreateIp() == null
                        ? other.getCreateIp() == null
                        : this.getCreateIp().equals(other.getCreateIp()))
                && (this.getCreateCity() == null
                        ? other.getCreateCity() == null
                        : this.getCreateCity().equals(other.getCreateCity()))
                && (this.getIsActive() == null
                        ? other.getIsActive() == null
                        : this.getIsActive().equals(other.getIsActive()))
                && (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 147 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:147)

```java
&& (this.getSalt() == null
                        ? other.getSalt() == null
                        : this.getSalt().equals(other.getSalt()))
                && (this.getIdCard() == null
                        ? other.getIdCard() == null
                        : this.getIdCard().equals(other.getIdCard()))
                && (this.getCredit1() == null
                        ? other.getCredit1() == null
                        : this.getCredit1().equals(other.getCredit1()))
                && (this.getCreateIp() == null
                        ? other.getCreateIp() == null
                        : this.getCreateIp().equals(other.getCreateIp()))
                && (this.getCreateCity() == null
                        ? other.getCreateCity() == null
                        : this.getCreateCity().equals(other.getCreateCity()))
                && (this.getIsActive() == null
                        ? other.getIsActive() == null
                        : this.getIsActive().equals(other.getIsActive()))
                && (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 150 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:150)

```java
&& (this.getIdCard() == null
                        ? other.getIdCard() == null
                        : this.getIdCard().equals(other.getIdCard()))
                && (this.getCredit1() == null
                        ? other.getCredit1() == null
                        : this.getCredit1().equals(other.getCredit1()))
                && (this.getCreateIp() == null
                        ? other.getCreateIp() == null
                        : this.getCreateIp().equals(other.getCreateIp()))
                && (this.getCreateCity() == null
                        ? other.getCreateCity() == null
                        : this.getCreateCity().equals(other.getCreateCity()))
                && (this.getIsActive() == null
                        ? other.getIsActive() == null
                        : this.getIsActive().equals(other.getIsActive()))
                && (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 153 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:153)

```java
&& (this.getCredit1() == null
                        ? other.getCredit1() == null
                        : this.getCredit1().equals(other.getCredit1()))
                && (this.getCreateIp() == null
                        ? other.getCreateIp() == null
                        : this.getCreateIp().equals(other.getCreateIp()))
                && (this.getCreateCity() == null
                        ? other.getCreateCity() == null
                        : this.getCreateCity().equals(other.getCreateCity()))
                && (this.getIsActive() == null
                        ? other.getIsActive() == null
                        : this.getIsActive().equals(other.getIsActive()))
                && (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 156 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:156)

```java
&& (this.getCreateIp() == null
                        ? other.getCreateIp() == null
                        : this.getCreateIp().equals(other.getCreateIp()))
                && (this.getCreateCity() == null
                        ? other.getCreateCity() == null
                        : this.getCreateCity().equals(other.getCreateCity()))
                && (this.getIsActive() == null
                        ? other.getIsActive() == null
                        : this.getIsActive().equals(other.getIsActive()))
                && (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 159 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:159)

```java
&& (this.getCreateCity() == null
                        ? other.getCreateCity() == null
                        : this.getCreateCity().equals(other.getCreateCity()))
                && (this.getIsActive() == null
                        ? other.getIsActive() == null
                        : this.getIsActive().equals(other.getIsActive()))
                && (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 162 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:162)

```java
&& (this.getIsActive() == null
                        ? other.getIsActive() == null
                        : this.getIsActive().equals(other.getIsActive()))
                && (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 165 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:165)

```java
&& (this.getIsLock() == null
                        ? other.getIsLock() == null
                        : this.getIsLock().equals(other.getIsLock()))
                && (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 168 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:168)

```java
&& (this.getIsVerify() == null
                        ? other.getIsVerify() == null
                        : this.getIsVerify().equals(other.getIsVerify()))
                && (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 171 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:171)

```java
&& (this.getVerifyAt() == null
                        ? other.getVerifyAt() == null
                        : this.getVerifyAt().equals(other.getVerifyAt()))
                && (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 174 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:174)

```java
&& (this.getIsSetPassword() == null
                        ? other.getIsSetPassword() == null
                        : this.getIsSetPassword().equals(other.getIsSetPassword()))
                && (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 177 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:177)

```java
&& (this.getLoginAt() == null
                        ? other.getLoginAt() == null
                        : this.getLoginAt().equals(other.getLoginAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 180 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:180)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 183 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:183)

```java
&& (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 191 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:191)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 192 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:192)

```java
result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
```

[第 193 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:193)

```java
result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
```

[第 194 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:194)

```java
result = prime * result + ((getAvatar() == null) ? 0 : getAvatar().hashCode());
```

[第 195 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:195)

```java
result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
```

[第 196 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:196)

```java
result = prime * result + ((getSalt() == null) ? 0 : getSalt().hashCode());
```

[第 197 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:197)

```java
result = prime * result + ((getIdCard() == null) ? 0 : getIdCard().hashCode());
```

[第 198 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:198)

```java
result = prime * result + ((getCredit1() == null) ? 0 : getCredit1().hashCode());
```

[第 199 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:199)

```java
result = prime * result + ((getCreateIp() == null) ? 0 : getCreateIp().hashCode());
```

[第 200 行，第 62 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:200)

```java
result = prime * result + ((getCreateCity() == null) ? 0 : getCreateCity().hashCode());
```

[第 201 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:201)

```java
result = prime * result + ((getIsActive() == null) ? 0 : getIsActive().hashCode());
```

[第 202 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:202)

```java
result = prime * result + ((getIsLock() == null) ? 0 : getIsLock().hashCode());
```

[第 203 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:203)

```java
result = prime * result + ((getIsVerify() == null) ? 0 : getIsVerify().hashCode());
```

[第 204 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:204)

```java
result = prime * result + ((getVerifyAt() == null) ? 0 : getVerifyAt().hashCode());
```

[第 206 行，第 64 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:206)

```java
prime * result + ((getIsSetPassword() == null) ? 0 : getIsSetPassword().hashCode());
```

[第 207 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:207)

```java
result = prime * result + ((getLoginAt() == null) ? 0 : getLoginAt().hashCode());
```

[第 208 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:208)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 209 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/User.java:209)

```java
result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserDepartment.java

[第 55 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserDepartment.java:55)

```java
return (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getDepId() == null
                        ? other.getDepId() == null
                        : this.getDepId().equals(other.getDepId()));
```

[第 58 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserDepartment.java:58)

```java
&& (this.getDepId() == null
                        ? other.getDepId() == null
                        : this.getDepId().equals(other.getDepId()));
```

[第 66 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserDepartment.java:66)

```java
result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
```

[第 67 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserDepartment.java:67)

```java
result = prime * result + ((getDepId() == null) ? 0 : getDepId().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java

[第 81 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:81)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getJti() == null
                        ? other.getJti() == null
                        : this.getJti().equals(other.getJti()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getBrowser() == null
                        ? other.getBrowser() == null
                        : this.getBrowser().equals(other.getBrowser()))
                && (this.getBrowserVersion() == null
                        ? other.getBrowserVersion() == null
                        : this.getBrowserVersion().equals(other.getBrowserVersion()))
                && (this.getOs() == null
                        ? other.getOs() == null
                        : this.getOs().equals(other.getOs()))
                && (this.getExpired() == null
                        ? other.getExpired() == null
                        : this.getExpired().equals(other.getExpired()))
                && (this.getIsLogout() == null
                        ? other.getIsLogout() == null
                        : this.getIsLogout().equals(other.getIsLogout()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 83 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:83)

```java
&& (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getJti() == null
                        ? other.getJti() == null
                        : this.getJti().equals(other.getJti()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getBrowser() == null
                        ? other.getBrowser() == null
                        : this.getBrowser().equals(other.getBrowser()))
                && (this.getBrowserVersion() == null
                        ? other.getBrowserVersion() == null
                        : this.getBrowserVersion().equals(other.getBrowserVersion()))
                && (this.getOs() == null
                        ? other.getOs() == null
                        : this.getOs().equals(other.getOs()))
                && (this.getExpired() == null
                        ? other.getExpired() == null
                        : this.getExpired().equals(other.getExpired()))
                && (this.getIsLogout() == null
                        ? other.getIsLogout() == null
                        : this.getIsLogout().equals(other.getIsLogout()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 86 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:86)

```java
&& (this.getJti() == null
                        ? other.getJti() == null
                        : this.getJti().equals(other.getJti()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getBrowser() == null
                        ? other.getBrowser() == null
                        : this.getBrowser().equals(other.getBrowser()))
                && (this.getBrowserVersion() == null
                        ? other.getBrowserVersion() == null
                        : this.getBrowserVersion().equals(other.getBrowserVersion()))
                && (this.getOs() == null
                        ? other.getOs() == null
                        : this.getOs().equals(other.getOs()))
                && (this.getExpired() == null
                        ? other.getExpired() == null
                        : this.getExpired().equals(other.getExpired()))
                && (this.getIsLogout() == null
                        ? other.getIsLogout() == null
                        : this.getIsLogout().equals(other.getIsLogout()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 89 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:89)

```java
&& (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getBrowser() == null
                        ? other.getBrowser() == null
                        : this.getBrowser().equals(other.getBrowser()))
                && (this.getBrowserVersion() == null
                        ? other.getBrowserVersion() == null
                        : this.getBrowserVersion().equals(other.getBrowserVersion()))
                && (this.getOs() == null
                        ? other.getOs() == null
                        : this.getOs().equals(other.getOs()))
                && (this.getExpired() == null
                        ? other.getExpired() == null
                        : this.getExpired().equals(other.getExpired()))
                && (this.getIsLogout() == null
                        ? other.getIsLogout() == null
                        : this.getIsLogout().equals(other.getIsLogout()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 92 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:92)

```java
&& (this.getIpArea() == null
                        ? other.getIpArea() == null
                        : this.getIpArea().equals(other.getIpArea()))
                && (this.getBrowser() == null
                        ? other.getBrowser() == null
                        : this.getBrowser().equals(other.getBrowser()))
                && (this.getBrowserVersion() == null
                        ? other.getBrowserVersion() == null
                        : this.getBrowserVersion().equals(other.getBrowserVersion()))
                && (this.getOs() == null
                        ? other.getOs() == null
                        : this.getOs().equals(other.getOs()))
                && (this.getExpired() == null
                        ? other.getExpired() == null
                        : this.getExpired().equals(other.getExpired()))
                && (this.getIsLogout() == null
                        ? other.getIsLogout() == null
                        : this.getIsLogout().equals(other.getIsLogout()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 95 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:95)

```java
&& (this.getBrowser() == null
                        ? other.getBrowser() == null
                        : this.getBrowser().equals(other.getBrowser()))
                && (this.getBrowserVersion() == null
                        ? other.getBrowserVersion() == null
                        : this.getBrowserVersion().equals(other.getBrowserVersion()))
                && (this.getOs() == null
                        ? other.getOs() == null
                        : this.getOs().equals(other.getOs()))
                && (this.getExpired() == null
                        ? other.getExpired() == null
                        : this.getExpired().equals(other.getExpired()))
                && (this.getIsLogout() == null
                        ? other.getIsLogout() == null
                        : this.getIsLogout().equals(other.getIsLogout()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 98 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:98)

```java
&& (this.getBrowserVersion() == null
                        ? other.getBrowserVersion() == null
                        : this.getBrowserVersion().equals(other.getBrowserVersion()))
                && (this.getOs() == null
                        ? other.getOs() == null
                        : this.getOs().equals(other.getOs()))
                && (this.getExpired() == null
                        ? other.getExpired() == null
                        : this.getExpired().equals(other.getExpired()))
                && (this.getIsLogout() == null
                        ? other.getIsLogout() == null
                        : this.getIsLogout().equals(other.getIsLogout()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 101 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:101)

```java
&& (this.getOs() == null
                        ? other.getOs() == null
                        : this.getOs().equals(other.getOs()))
                && (this.getExpired() == null
                        ? other.getExpired() == null
                        : this.getExpired().equals(other.getExpired()))
                && (this.getIsLogout() == null
                        ? other.getIsLogout() == null
                        : this.getIsLogout().equals(other.getIsLogout()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 104 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:104)

```java
&& (this.getExpired() == null
                        ? other.getExpired() == null
                        : this.getExpired().equals(other.getExpired()))
                && (this.getIsLogout() == null
                        ? other.getIsLogout() == null
                        : this.getIsLogout().equals(other.getIsLogout()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 107 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:107)

```java
&& (this.getIsLogout() == null
                        ? other.getIsLogout() == null
                        : this.getIsLogout().equals(other.getIsLogout()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 110 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:110)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 118 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:118)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 119 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:119)

```java
result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
```

[第 120 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:120)

```java
result = prime * result + ((getJti() == null) ? 0 : getJti().hashCode());
```

[第 121 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:121)

```java
result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
```

[第 122 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:122)

```java
result = prime * result + ((getIpArea() == null) ? 0 : getIpArea().hashCode());
```

[第 123 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:123)

```java
result = prime * result + ((getBrowser() == null) ? 0 : getBrowser().hashCode());
```

[第 126 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:126)

```java
+ ((getBrowserVersion() == null) ? 0 : getBrowserVersion().hashCode());
```

[第 127 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:127)

```java
result = prime * result + ((getOs() == null) ? 0 : getOs().hashCode());
```

[第 128 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:128)

```java
result = prime * result + ((getExpired() == null) ? 0 : getExpired().hashCode());
```

[第 129 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:129)

```java
result = prime * result + ((getIsLogout() == null) ? 0 : getIsLogout().hashCode());
```

[第 130 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserLoginRecord.java:130)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java

[第 81 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:81)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getTyped() == null
                        ? other.getTyped() == null
                        : this.getTyped().equals(other.getTyped()))
                && (this.getScene() == null
                        ? other.getScene() == null
                        : this.getScene().equals(other.getScene()))
                && (this.getDriver() == null
                        ? other.getDriver() == null
                        : this.getDriver().equals(other.getDriver()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 83 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:83)

```java
&& (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getTyped() == null
                        ? other.getTyped() == null
                        : this.getTyped().equals(other.getTyped()))
                && (this.getScene() == null
                        ? other.getScene() == null
                        : this.getScene().equals(other.getScene()))
                && (this.getDriver() == null
                        ? other.getDriver() == null
                        : this.getDriver().equals(other.getDriver()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 86 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:86)

```java
&& (this.getTyped() == null
                        ? other.getTyped() == null
                        : this.getTyped().equals(other.getTyped()))
                && (this.getScene() == null
                        ? other.getScene() == null
                        : this.getScene().equals(other.getScene()))
                && (this.getDriver() == null
                        ? other.getDriver() == null
                        : this.getDriver().equals(other.getDriver()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 89 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:89)

```java
&& (this.getScene() == null
                        ? other.getScene() == null
                        : this.getScene().equals(other.getScene()))
                && (this.getDriver() == null
                        ? other.getDriver() == null
                        : this.getDriver().equals(other.getDriver()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 92 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:92)

```java
&& (this.getDriver() == null
                        ? other.getDriver() == null
                        : this.getDriver().equals(other.getDriver()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 95 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:95)

```java
&& (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 98 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:98)

```java
&& (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 101 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:101)

```java
&& (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 104 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:104)

```java
&& (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 107 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:107)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 115 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:115)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 116 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:116)

```java
result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
```

[第 117 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:117)

```java
result = prime * result + ((getTyped() == null) ? 0 : getTyped().hashCode());
```

[第 118 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:118)

```java
result = prime * result + ((getScene() == null) ? 0 : getScene().hashCode());
```

[第 119 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:119)

```java
result = prime * result + ((getDriver() == null) ? 0 : getDriver().hashCode());
```

[第 120 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:120)

```java
result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
```

[第 121 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:121)

```java
result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
```

[第 122 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:122)

```java
result = prime * result + ((getSize() == null) ? 0 : getSize().hashCode());
```

[第 123 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:123)

```java
result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
```

[第 124 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/domain/UserUploadImageLog.java:124)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AppConfigServiceImpl.java

[第 110 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/AppConfigServiceImpl.java:110)

```java
s3Config.setRegion(StringUtil.isEmpty(region) ? null : region);
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/CategoryServiceImpl.java

[第 120 行，第 49 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/CategoryServiceImpl.java:120)

```java
newChildrenPC.length() == 0
                                                ? newChildrenPC
                                                : newChildrenPC + ',');
```

[第 174 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/CategoryServiceImpl.java:174)

```java
parentChain = pc == null || pc.length() == 0 ? parentId + "" : pc + "," + parentId;
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/DepartmentServiceImpl.java

[第 127 行，第 49 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/DepartmentServiceImpl.java:127)

```java
newChildrenPC.isEmpty()
                                                ? newChildrenPC
                                                : newChildrenPC + ',');
```

[第 154 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/DepartmentServiceImpl.java:154)

```java
parentChain = pc == null || pc.isEmpty() ? parentId + "" : pc + "," + parentId;
```

[第 277 行，第 51 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/DepartmentServiceImpl.java:277)

```java
Integer parentId = department == null ? 0 : department.getId();
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/LdapUserServiceImpl.java

[第 74 行，第 31 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/LdapUserServiceImpl.java:74)

```java
user.setCn(cn == null ? "" : cn);
```

[第 82 行，第 34 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/LdapUserServiceImpl.java:82)

```java
user.setOu(newOU == null ? "" : newOU);
```

[第 90 行，第 37 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/service/impl/LdapUserServiceImpl.java:90)

```java
user.setEmail(email == null ? "" : email);
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/Base64Util.java

[第 85 行，第 50 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/Base64Util.java:85)

```java
int numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1 : numberTriplets;
```

[第 102 行，第 44 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/Base64Util.java:102)

```java
byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
```

[第 103 行，第 44 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/Base64Util.java:103)

```java
byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
```

[第 104 行，第 44 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/Base64Util.java:104)

```java
byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6) : (byte) ((b3) >> 6 ^ 0xfc);
```

[第 115 行，第 44 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/Base64Util.java:115)

```java
byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
```

[第 125 行，第 44 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/Base64Util.java:125)

```java
byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
```

[第 126 行，第 44 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/Base64Util.java:126)

```java
byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/IpUtil.java

[第 87 行，第 45 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/IpUtil.java:87)

```java
return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
```

[第 120 行，第 47 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/IpUtil.java:120)

```java
String country = parts.length > 0 ? cleanField(parts[0]) : "";
```

[第 121 行，第 43 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/IpUtil.java:121)

```java
String pro = parts.length > 1 ? cleanField(parts[1]) : "";
```

[第 122 行，第 44 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/IpUtil.java:122)

```java
String city = parts.length > 2 ? cleanField(parts[2]) : "";
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/ldap/LdapUtil.java

[第 208 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/ldap/LdapUtil.java:208)

```java
name = name + (ouScopesStr.isEmpty() ? "" : "," + ouScopesStr);
```

[第 341 行，第 33 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/ldap/LdapUtil.java:341)

```java
(baseDNOuScope.isEmpty()
                                ? ldapUser.getDn().toLowerCase()
                                : ldapUser.getDn().toLowerCase() + "," + baseDNOuScope)
                        .split(",");
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/RequestUtil.java

[第 31 行，第 49 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/RequestUtil.java:31)

```java
return servletRequestAttributes == null ? null : servletRequestAttributes.getRequest();
```

[第 56 行，第 32 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/RequestUtil.java:56)

```java
return request == null ? "" : request.getRequestURL().toString();
```

[第 62 行，第 67 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/RequestUtil.java:62)

```java
+ (Arrays.asList(443, 80, 0).contains(portNumber) ? "" : ":" + portNumber);
```

[第 69 行，第 67 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/RequestUtil.java:69)

```java
+ (Arrays.asList(443, 80, 0).contains(portNumber) ? "" : ":" + portNumber);
```

[第 74 行，第 32 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/RequestUtil.java:74)

```java
return request == null ? "" : request.getRequestURI();
```

[第 79 行，第 32 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/RequestUtil.java:79)

```java
return request == null ? 0 : request.getServerPort();
```

### playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/StringUtil.java

[第 37 行，第 30 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/StringUtil.java:37)

```java
return value != null ? value : defaultValue;
```

[第 152 行，第 29 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/main/java/xyz/playedu/common/util/StringUtil.java:152)

```java
return (str == null ? "" : str.trim());
```

### playedu-api/playedu-common/src/test/java/xyz/playedu/common/redis/RedisRuntimeIntegrationTest.java

[第 184 行，第 77 列](C:/Users/86198/Desktop/play/playedu-api/playedu-common/src/test/java/xyz/playedu/common/redis/RedisRuntimeIntegrationTest.java:184)

```java
(attempt % 2 == 0
                                                                            ? loginFailureTracker
                                                                            : secondInstance)
                                                                    .recordFailure(
                                                                            LoginFailureTracker
                                                                                    .LoginType
                                                                                    .LEARNER,
                                                                            learner)))
                            .toList();
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java

[第 95 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:95)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getThumb() == null
                        ? other.getThumb() == null
                        : this.getThumb().equals(other.getThumb()))
                && (this.getCharge() == null
                        ? other.getCharge() == null
                        : this.getCharge().equals(other.getCharge()))
                && (this.getClassHour() == null
                        ? other.getClassHour() == null
                        : this.getClassHour().equals(other.getClassHour()))
                && (this.getIsShow() == null
                        ? other.getIsShow() == null
                        : this.getIsShow().equals(other.getIsShow()))
                && (this.getIsRequired() == null
                        ? other.getIsRequired() == null
                        : this.getIsRequired().equals(other.getIsRequired()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()))
                && (this.getDeletedAt() == null
                        ? other.getDeletedAt() == null
                        : this.getDeletedAt().equals(other.getDeletedAt()))
                && (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 97 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:97)

```java
&& (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getThumb() == null
                        ? other.getThumb() == null
                        : this.getThumb().equals(other.getThumb()))
                && (this.getCharge() == null
                        ? other.getCharge() == null
                        : this.getCharge().equals(other.getCharge()))
                && (this.getClassHour() == null
                        ? other.getClassHour() == null
                        : this.getClassHour().equals(other.getClassHour()))
                && (this.getIsShow() == null
                        ? other.getIsShow() == null
                        : this.getIsShow().equals(other.getIsShow()))
                && (this.getIsRequired() == null
                        ? other.getIsRequired() == null
                        : this.getIsRequired().equals(other.getIsRequired()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()))
                && (this.getDeletedAt() == null
                        ? other.getDeletedAt() == null
                        : this.getDeletedAt().equals(other.getDeletedAt()))
                && (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 100 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:100)

```java
&& (this.getThumb() == null
                        ? other.getThumb() == null
                        : this.getThumb().equals(other.getThumb()))
                && (this.getCharge() == null
                        ? other.getCharge() == null
                        : this.getCharge().equals(other.getCharge()))
                && (this.getClassHour() == null
                        ? other.getClassHour() == null
                        : this.getClassHour().equals(other.getClassHour()))
                && (this.getIsShow() == null
                        ? other.getIsShow() == null
                        : this.getIsShow().equals(other.getIsShow()))
                && (this.getIsRequired() == null
                        ? other.getIsRequired() == null
                        : this.getIsRequired().equals(other.getIsRequired()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()))
                && (this.getDeletedAt() == null
                        ? other.getDeletedAt() == null
                        : this.getDeletedAt().equals(other.getDeletedAt()))
                && (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 103 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:103)

```java
&& (this.getCharge() == null
                        ? other.getCharge() == null
                        : this.getCharge().equals(other.getCharge()))
                && (this.getClassHour() == null
                        ? other.getClassHour() == null
                        : this.getClassHour().equals(other.getClassHour()))
                && (this.getIsShow() == null
                        ? other.getIsShow() == null
                        : this.getIsShow().equals(other.getIsShow()))
                && (this.getIsRequired() == null
                        ? other.getIsRequired() == null
                        : this.getIsRequired().equals(other.getIsRequired()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()))
                && (this.getDeletedAt() == null
                        ? other.getDeletedAt() == null
                        : this.getDeletedAt().equals(other.getDeletedAt()))
                && (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 106 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:106)

```java
&& (this.getClassHour() == null
                        ? other.getClassHour() == null
                        : this.getClassHour().equals(other.getClassHour()))
                && (this.getIsShow() == null
                        ? other.getIsShow() == null
                        : this.getIsShow().equals(other.getIsShow()))
                && (this.getIsRequired() == null
                        ? other.getIsRequired() == null
                        : this.getIsRequired().equals(other.getIsRequired()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()))
                && (this.getDeletedAt() == null
                        ? other.getDeletedAt() == null
                        : this.getDeletedAt().equals(other.getDeletedAt()))
                && (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 109 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:109)

```java
&& (this.getIsShow() == null
                        ? other.getIsShow() == null
                        : this.getIsShow().equals(other.getIsShow()))
                && (this.getIsRequired() == null
                        ? other.getIsRequired() == null
                        : this.getIsRequired().equals(other.getIsRequired()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()))
                && (this.getDeletedAt() == null
                        ? other.getDeletedAt() == null
                        : this.getDeletedAt().equals(other.getDeletedAt()))
                && (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 112 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:112)

```java
&& (this.getIsRequired() == null
                        ? other.getIsRequired() == null
                        : this.getIsRequired().equals(other.getIsRequired()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()))
                && (this.getDeletedAt() == null
                        ? other.getDeletedAt() == null
                        : this.getDeletedAt().equals(other.getDeletedAt()))
                && (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 115 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:115)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()))
                && (this.getDeletedAt() == null
                        ? other.getDeletedAt() == null
                        : this.getDeletedAt().equals(other.getDeletedAt()))
                && (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 118 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:118)

```java
&& (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()))
                && (this.getDeletedAt() == null
                        ? other.getDeletedAt() == null
                        : this.getDeletedAt().equals(other.getDeletedAt()))
                && (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 121 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:121)

```java
&& (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()))
                && (this.getDeletedAt() == null
                        ? other.getDeletedAt() == null
                        : this.getDeletedAt().equals(other.getDeletedAt()))
                && (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 124 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:124)

```java
&& (this.getDeletedAt() == null
                        ? other.getDeletedAt() == null
                        : this.getDeletedAt().equals(other.getDeletedAt()))
                && (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 127 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:127)

```java
&& (this.getShortDesc() == null
                        ? other.getShortDesc() == null
                        : this.getShortDesc().equals(other.getShortDesc()))
                && (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 130 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:130)

```java
&& (this.getSortAt() == null
                        ? other.getSortAt() == null
                        : this.getSortAt().equals(other.getSortAt()))
                && (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 133 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:133)

```java
&& (this.getExtra() == null
                        ? other.getExtra() == null
                        : this.getExtra().equals(other.getExtra()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 136 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:136)

```java
&& (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()));
```

[第 144 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:144)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 145 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:145)

```java
result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
```

[第 146 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:146)

```java
result = prime * result + ((getThumb() == null) ? 0 : getThumb().hashCode());
```

[第 147 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:147)

```java
result = prime * result + ((getCharge() == null) ? 0 : getCharge().hashCode());
```

[第 148 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:148)

```java
result = prime * result + ((getShortDesc() == null) ? 0 : getShortDesc().hashCode());
```

[第 149 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:149)

```java
result = prime * result + ((getClassHour() == null) ? 0 : getClassHour().hashCode());
```

[第 150 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:150)

```java
result = prime * result + ((getIsShow() == null) ? 0 : getIsShow().hashCode());
```

[第 151 行，第 62 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:151)

```java
result = prime * result + ((getIsRequired() == null) ? 0 : getIsRequired().hashCode());
```

[第 152 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:152)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 153 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:153)

```java
result = prime * result + ((getSortAt() == null) ? 0 : getSortAt().hashCode());
```

[第 154 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:154)

```java
result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
```

[第 155 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:155)

```java
result = prime * result + ((getDeletedAt() == null) ? 0 : getDeletedAt().hashCode());
```

[第 156 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:156)

```java
result = prime * result + ((getSortAt() == null) ? 0 : getSortAt().hashCode());
```

[第 157 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:157)

```java
result = prime * result + ((getExtra() == null) ? 0 : getExtra().hashCode());
```

[第 158 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/Course.java:158)

```java
result = prime * result + ((getAdminId() == null) ? 0 : getAdminId().hashCode());
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java

[第 80 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:80)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getExt() == null
                        ? other.getExt() == null
                        : this.getExt().equals(other.getExt()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 82 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:82)

```java
&& (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getExt() == null
                        ? other.getExt() == null
                        : this.getExt().equals(other.getExt()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 85 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:85)

```java
&& (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getExt() == null
                        ? other.getExt() == null
                        : this.getExt().equals(other.getExt()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 88 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:88)

```java
&& (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getExt() == null
                        ? other.getExt() == null
                        : this.getExt().equals(other.getExt()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 91 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:91)

```java
&& (this.getExt() == null
                        ? other.getExt() == null
                        : this.getExt().equals(other.getExt()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 94 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:94)

```java
&& (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 97 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:97)

```java
&& (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 100 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:100)

```java
&& (this.getUrl() == null
                        ? other.getUrl() == null
                        : this.getUrl().equals(other.getUrl()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 103 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:103)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 111 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:111)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 112 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:112)

```java
result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
```

[第 113 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:113)

```java
result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
```

[第 114 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:114)

```java
result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
```

[第 115 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:115)

```java
result = prime * result + ((getExt() == null) ? 0 : getExt().hashCode());
```

[第 116 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:116)

```java
result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
```

[第 117 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:117)

```java
result = prime * result + ((getRid() == null) ? 0 : getRid().hashCode());
```

[第 118 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:118)

```java
result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
```

[第 119 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachment.java:119)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java

[第 76 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:76)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getCourserAttachmentId() == null
                        ? other.getCourserAttachmentId() == null
                        : this.getCourserAttachmentId().equals(other.getCourserAttachmentId()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 78 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:78)

```java
&& (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getCourserAttachmentId() == null
                        ? other.getCourserAttachmentId() == null
                        : this.getCourserAttachmentId().equals(other.getCourserAttachmentId()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 81 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:81)

```java
&& (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getCourserAttachmentId() == null
                        ? other.getCourserAttachmentId() == null
                        : this.getCourserAttachmentId().equals(other.getCourserAttachmentId()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 84 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:84)

```java
&& (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getCourserAttachmentId() == null
                        ? other.getCourserAttachmentId() == null
                        : this.getCourserAttachmentId().equals(other.getCourserAttachmentId()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 87 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:87)

```java
&& (this.getCourserAttachmentId() == null
                        ? other.getCourserAttachmentId() == null
                        : this.getCourserAttachmentId().equals(other.getCourserAttachmentId()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 90 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:90)

```java
&& (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 93 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:93)

```java
&& (this.getIp() == null
                        ? other.getIp() == null
                        : this.getIp().equals(other.getIp()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 96 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:96)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 104 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:104)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 105 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:105)

```java
result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
```

[第 106 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:106)

```java
result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
```

[第 107 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:107)

```java
result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
```

[第 111 行，第 33 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:111)

```java
+ ((getCourserAttachmentId() == null)
                                ? 0
                                : getCourserAttachmentId().hashCode());
```

[第 113 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:113)

```java
result = prime * result + ((getRid() == null) ? 0 : getRid().hashCode());
```

[第 114 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:114)

```java
result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
```

[第 115 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseAttachmentDownloadLog.java:115)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseCategory.java

[第 57 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseCategory.java:57)

```java
return (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getCategoryId() == null
                        ? other.getCategoryId() == null
                        : this.getCategoryId().equals(other.getCategoryId()));
```

[第 60 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseCategory.java:60)

```java
&& (this.getCategoryId() == null
                        ? other.getCategoryId() == null
                        : this.getCategoryId().equals(other.getCategoryId()));
```

[第 68 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseCategory.java:68)

```java
result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
```

[第 69 行，第 62 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseCategory.java:69)

```java
result = prime * result + ((getCategoryId() == null) ? 0 : getCategoryId().hashCode());
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java

[第 68 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:68)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 70 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:70)

```java
&& (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 73 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:73)

```java
&& (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 76 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:76)

```java
&& (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 79 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:79)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 82 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:82)

```java
&& (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 90 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:90)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 91 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:91)

```java
result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
```

[第 92 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:92)

```java
result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
```

[第 93 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:93)

```java
result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
```

[第 94 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:94)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 95 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseChapter.java:95)

```java
result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseDepartmentUser.java

[第 60 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseDepartmentUser.java:60)

```java
return (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getRangeId() == null
                        ? other.getRangeId() == null
                        : this.getRangeId().equals(other.getRangeId()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()));
```

[第 63 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseDepartmentUser.java:63)

```java
&& (this.getRangeId() == null
                        ? other.getRangeId() == null
                        : this.getRangeId().equals(other.getRangeId()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()));
```

[第 66 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseDepartmentUser.java:66)

```java
&& (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()));
```

[第 74 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseDepartmentUser.java:74)

```java
result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
```

[第 75 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseDepartmentUser.java:75)

```java
result = prime * result + ((getRangeId() == null) ? 0 : getRangeId().hashCode());
```

[第 76 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseDepartmentUser.java:76)

```java
result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java

[第 82 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:82)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getChapterId() == null
                        ? other.getChapterId() == null
                        : this.getChapterId().equals(other.getChapterId()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getDeleted() == null
                        ? other.getDeleted() == null
                        : this.getDeleted().equals(other.getDeleted()));
```

[第 84 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:84)

```java
&& (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getChapterId() == null
                        ? other.getChapterId() == null
                        : this.getChapterId().equals(other.getChapterId()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getDeleted() == null
                        ? other.getDeleted() == null
                        : this.getDeleted().equals(other.getDeleted()));
```

[第 87 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:87)

```java
&& (this.getChapterId() == null
                        ? other.getChapterId() == null
                        : this.getChapterId().equals(other.getChapterId()))
                && (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getDeleted() == null
                        ? other.getDeleted() == null
                        : this.getDeleted().equals(other.getDeleted()));
```

[第 90 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:90)

```java
&& (this.getSort() == null
                        ? other.getSort() == null
                        : this.getSort().equals(other.getSort()))
                && (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getDeleted() == null
                        ? other.getDeleted() == null
                        : this.getDeleted().equals(other.getDeleted()));
```

[第 93 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:93)

```java
&& (this.getTitle() == null
                        ? other.getTitle() == null
                        : this.getTitle().equals(other.getTitle()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getDeleted() == null
                        ? other.getDeleted() == null
                        : this.getDeleted().equals(other.getDeleted()));
```

[第 96 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:96)

```java
&& (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getDeleted() == null
                        ? other.getDeleted() == null
                        : this.getDeleted().equals(other.getDeleted()));
```

[第 99 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:99)

```java
&& (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getDeleted() == null
                        ? other.getDeleted() == null
                        : this.getDeleted().equals(other.getDeleted()));
```

[第 102 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:102)

```java
&& (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getDeleted() == null
                        ? other.getDeleted() == null
                        : this.getDeleted().equals(other.getDeleted()));
```

[第 105 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:105)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getDeleted() == null
                        ? other.getDeleted() == null
                        : this.getDeleted().equals(other.getDeleted()));
```

[第 108 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:108)

```java
&& (this.getDeleted() == null
                        ? other.getDeleted() == null
                        : this.getDeleted().equals(other.getDeleted()));
```

[第 116 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:116)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 117 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:117)

```java
result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
```

[第 118 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:118)

```java
result = prime * result + ((getChapterId() == null) ? 0 : getChapterId().hashCode());
```

[第 119 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:119)

```java
result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
```

[第 120 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:120)

```java
result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
```

[第 121 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:121)

```java
result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
```

[第 122 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:122)

```java
result = prime * result + ((getRid() == null) ? 0 : getRid().hashCode());
```

[第 123 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:123)

```java
result = prime * result + ((getDuration() == null) ? 0 : getDuration().hashCode());
```

[第 124 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:124)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 125 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/CourseHour.java:125)

```java
result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java

[第 92 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:92)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getHourId() == null
                        ? other.getHourId() == null
                        : this.getHourId().equals(other.getHourId()))
                && (this.getTotalDuration() == null
                        ? other.getTotalDuration() == null
                        : this.getTotalDuration().equals(other.getTotalDuration()))
                && (this.getFinishedDuration() == null
                        ? other.getFinishedDuration() == null
                        : this.getFinishedDuration().equals(other.getFinishedDuration()))
                && (this.getRealDuration() == null
                        ? other.getRealDuration() == null
                        : this.getRealDuration().equals(other.getRealDuration()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 94 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:94)

```java
&& (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getHourId() == null
                        ? other.getHourId() == null
                        : this.getHourId().equals(other.getHourId()))
                && (this.getTotalDuration() == null
                        ? other.getTotalDuration() == null
                        : this.getTotalDuration().equals(other.getTotalDuration()))
                && (this.getFinishedDuration() == null
                        ? other.getFinishedDuration() == null
                        : this.getFinishedDuration().equals(other.getFinishedDuration()))
                && (this.getRealDuration() == null
                        ? other.getRealDuration() == null
                        : this.getRealDuration().equals(other.getRealDuration()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 97 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:97)

```java
&& (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getHourId() == null
                        ? other.getHourId() == null
                        : this.getHourId().equals(other.getHourId()))
                && (this.getTotalDuration() == null
                        ? other.getTotalDuration() == null
                        : this.getTotalDuration().equals(other.getTotalDuration()))
                && (this.getFinishedDuration() == null
                        ? other.getFinishedDuration() == null
                        : this.getFinishedDuration().equals(other.getFinishedDuration()))
                && (this.getRealDuration() == null
                        ? other.getRealDuration() == null
                        : this.getRealDuration().equals(other.getRealDuration()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 100 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:100)

```java
&& (this.getHourId() == null
                        ? other.getHourId() == null
                        : this.getHourId().equals(other.getHourId()))
                && (this.getTotalDuration() == null
                        ? other.getTotalDuration() == null
                        : this.getTotalDuration().equals(other.getTotalDuration()))
                && (this.getFinishedDuration() == null
                        ? other.getFinishedDuration() == null
                        : this.getFinishedDuration().equals(other.getFinishedDuration()))
                && (this.getRealDuration() == null
                        ? other.getRealDuration() == null
                        : this.getRealDuration().equals(other.getRealDuration()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 103 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:103)

```java
&& (this.getTotalDuration() == null
                        ? other.getTotalDuration() == null
                        : this.getTotalDuration().equals(other.getTotalDuration()))
                && (this.getFinishedDuration() == null
                        ? other.getFinishedDuration() == null
                        : this.getFinishedDuration().equals(other.getFinishedDuration()))
                && (this.getRealDuration() == null
                        ? other.getRealDuration() == null
                        : this.getRealDuration().equals(other.getRealDuration()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 106 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:106)

```java
&& (this.getFinishedDuration() == null
                        ? other.getFinishedDuration() == null
                        : this.getFinishedDuration().equals(other.getFinishedDuration()))
                && (this.getRealDuration() == null
                        ? other.getRealDuration() == null
                        : this.getRealDuration().equals(other.getRealDuration()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 109 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:109)

```java
&& (this.getRealDuration() == null
                        ? other.getRealDuration() == null
                        : this.getRealDuration().equals(other.getRealDuration()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 112 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:112)

```java
&& (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 115 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:115)

```java
&& (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 118 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:118)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 121 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:121)

```java
&& (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 129 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:129)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 130 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:130)

```java
result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
```

[第 131 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:131)

```java
result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
```

[第 132 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:132)

```java
result = prime * result + ((getHourId() == null) ? 0 : getHourId().hashCode());
```

[第 134 行，第 64 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:134)

```java
prime * result + ((getTotalDuration() == null) ? 0 : getTotalDuration().hashCode());
```

[第 137 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:137)

```java
+ ((getFinishedDuration() == null) ? 0 : getFinishedDuration().hashCode());
```

[第 138 行，第 64 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:138)

```java
result = prime * result + ((getRealDuration() == null) ? 0 : getRealDuration().hashCode());
```

[第 139 行，第 62 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:139)

```java
result = prime * result + ((getIsFinished() == null) ? 0 : getIsFinished().hashCode());
```

[第 140 行，第 62 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:140)

```java
result = prime * result + ((getFinishedAt() == null) ? 0 : getFinishedAt().hashCode());
```

[第 141 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:141)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 142 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseHourRecord.java:142)

```java
result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java

[第 87 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:87)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getHourCount() == null
                        ? other.getHourCount() == null
                        : this.getHourCount().equals(other.getHourCount()))
                && (this.getFinishedCount() == null
                        ? other.getFinishedCount() == null
                        : this.getFinishedCount().equals(other.getFinishedCount()))
                && (this.getProgress() == null
                        ? other.getProgress() == null
                        : this.getProgress().equals(other.getProgress()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 89 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:89)

```java
&& (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getHourCount() == null
                        ? other.getHourCount() == null
                        : this.getHourCount().equals(other.getHourCount()))
                && (this.getFinishedCount() == null
                        ? other.getFinishedCount() == null
                        : this.getFinishedCount().equals(other.getFinishedCount()))
                && (this.getProgress() == null
                        ? other.getProgress() == null
                        : this.getProgress().equals(other.getProgress()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 92 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:92)

```java
&& (this.getCourseId() == null
                        ? other.getCourseId() == null
                        : this.getCourseId().equals(other.getCourseId()))
                && (this.getHourCount() == null
                        ? other.getHourCount() == null
                        : this.getHourCount().equals(other.getHourCount()))
                && (this.getFinishedCount() == null
                        ? other.getFinishedCount() == null
                        : this.getFinishedCount().equals(other.getFinishedCount()))
                && (this.getProgress() == null
                        ? other.getProgress() == null
                        : this.getProgress().equals(other.getProgress()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 95 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:95)

```java
&& (this.getHourCount() == null
                        ? other.getHourCount() == null
                        : this.getHourCount().equals(other.getHourCount()))
                && (this.getFinishedCount() == null
                        ? other.getFinishedCount() == null
                        : this.getFinishedCount().equals(other.getFinishedCount()))
                && (this.getProgress() == null
                        ? other.getProgress() == null
                        : this.getProgress().equals(other.getProgress()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 98 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:98)

```java
&& (this.getFinishedCount() == null
                        ? other.getFinishedCount() == null
                        : this.getFinishedCount().equals(other.getFinishedCount()))
                && (this.getProgress() == null
                        ? other.getProgress() == null
                        : this.getProgress().equals(other.getProgress()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 101 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:101)

```java
&& (this.getProgress() == null
                        ? other.getProgress() == null
                        : this.getProgress().equals(other.getProgress()))
                && (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 104 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:104)

```java
&& (this.getIsFinished() == null
                        ? other.getIsFinished() == null
                        : this.getIsFinished().equals(other.getIsFinished()))
                && (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 107 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:107)

```java
&& (this.getFinishedAt() == null
                        ? other.getFinishedAt() == null
                        : this.getFinishedAt().equals(other.getFinishedAt()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 110 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:110)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 113 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:113)

```java
&& (this.getUpdatedAt() == null
                        ? other.getUpdatedAt() == null
                        : this.getUpdatedAt().equals(other.getUpdatedAt()));
```

[第 121 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:121)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 122 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:122)

```java
result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
```

[第 123 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:123)

```java
result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
```

[第 124 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:124)

```java
result = prime * result + ((getHourCount() == null) ? 0 : getHourCount().hashCode());
```

[第 126 行，第 64 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:126)

```java
prime * result + ((getFinishedCount() == null) ? 0 : getFinishedCount().hashCode());
```

[第 127 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:127)

```java
result = prime * result + ((getProgress() == null) ? 0 : getProgress().hashCode());
```

[第 128 行，第 62 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:128)

```java
result = prime * result + ((getIsFinished() == null) ? 0 : getIsFinished().hashCode());
```

[第 129 行，第 62 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:129)

```java
result = prime * result + ((getFinishedAt() == null) ? 0 : getFinishedAt().hashCode());
```

[第 130 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:130)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 131 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserCourseRecord.java:131)

```java
result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java

[第 79 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:79)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getCreatedDate() == null
                        ? other.getCreatedDate() == null
                        : this.getCreatedDate().equals(other.getCreatedDate()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getStartAt() == null
                        ? other.getStartAt() == null
                        : this.getStartAt().equals(other.getStartAt()))
                && (this.getEndAt() == null
                        ? other.getEndAt() == null
                        : this.getEndAt().equals(other.getEndAt()))
                && (this.getFromId() == null
                        ? other.getFromId() == null
                        : this.getFromId().equals(other.getFromId()))
                && (this.getFromScene() == null
                        ? other.getFromScene() == null
                        : this.getFromScene().equals(other.getFromScene()));
```

[第 81 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:81)

```java
&& (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getCreatedDate() == null
                        ? other.getCreatedDate() == null
                        : this.getCreatedDate().equals(other.getCreatedDate()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getStartAt() == null
                        ? other.getStartAt() == null
                        : this.getStartAt().equals(other.getStartAt()))
                && (this.getEndAt() == null
                        ? other.getEndAt() == null
                        : this.getEndAt().equals(other.getEndAt()))
                && (this.getFromId() == null
                        ? other.getFromId() == null
                        : this.getFromId().equals(other.getFromId()))
                && (this.getFromScene() == null
                        ? other.getFromScene() == null
                        : this.getFromScene().equals(other.getFromScene()));
```

[第 84 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:84)

```java
&& (this.getCreatedDate() == null
                        ? other.getCreatedDate() == null
                        : this.getCreatedDate().equals(other.getCreatedDate()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getStartAt() == null
                        ? other.getStartAt() == null
                        : this.getStartAt().equals(other.getStartAt()))
                && (this.getEndAt() == null
                        ? other.getEndAt() == null
                        : this.getEndAt().equals(other.getEndAt()))
                && (this.getFromId() == null
                        ? other.getFromId() == null
                        : this.getFromId().equals(other.getFromId()))
                && (this.getFromScene() == null
                        ? other.getFromScene() == null
                        : this.getFromScene().equals(other.getFromScene()));
```

[第 87 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:87)

```java
&& (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getStartAt() == null
                        ? other.getStartAt() == null
                        : this.getStartAt().equals(other.getStartAt()))
                && (this.getEndAt() == null
                        ? other.getEndAt() == null
                        : this.getEndAt().equals(other.getEndAt()))
                && (this.getFromId() == null
                        ? other.getFromId() == null
                        : this.getFromId().equals(other.getFromId()))
                && (this.getFromScene() == null
                        ? other.getFromScene() == null
                        : this.getFromScene().equals(other.getFromScene()));
```

[第 90 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:90)

```java
&& (this.getStartAt() == null
                        ? other.getStartAt() == null
                        : this.getStartAt().equals(other.getStartAt()))
                && (this.getEndAt() == null
                        ? other.getEndAt() == null
                        : this.getEndAt().equals(other.getEndAt()))
                && (this.getFromId() == null
                        ? other.getFromId() == null
                        : this.getFromId().equals(other.getFromId()))
                && (this.getFromScene() == null
                        ? other.getFromScene() == null
                        : this.getFromScene().equals(other.getFromScene()));
```

[第 93 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:93)

```java
&& (this.getEndAt() == null
                        ? other.getEndAt() == null
                        : this.getEndAt().equals(other.getEndAt()))
                && (this.getFromId() == null
                        ? other.getFromId() == null
                        : this.getFromId().equals(other.getFromId()))
                && (this.getFromScene() == null
                        ? other.getFromScene() == null
                        : this.getFromScene().equals(other.getFromScene()));
```

[第 96 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:96)

```java
&& (this.getFromId() == null
                        ? other.getFromId() == null
                        : this.getFromId().equals(other.getFromId()))
                && (this.getFromScene() == null
                        ? other.getFromScene() == null
                        : this.getFromScene().equals(other.getFromScene()));
```

[第 99 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:99)

```java
&& (this.getFromScene() == null
                        ? other.getFromScene() == null
                        : this.getFromScene().equals(other.getFromScene()));
```

[第 107 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:107)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 108 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:108)

```java
result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
```

[第 109 行，第 63 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:109)

```java
result = prime * result + ((getCreatedDate() == null) ? 0 : getCreatedDate().hashCode());
```

[第 110 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:110)

```java
result = prime * result + ((getDuration() == null) ? 0 : getDuration().hashCode());
```

[第 111 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:111)

```java
result = prime * result + ((getStartAt() == null) ? 0 : getStartAt().hashCode());
```

[第 112 行，第 57 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:112)

```java
result = prime * result + ((getEndAt() == null) ? 0 : getEndAt().hashCode());
```

[第 113 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:113)

```java
result = prime * result + ((getFromId() == null) ? 0 : getFromId().hashCode());
```

[第 114 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationRecord.java:114)

```java
result = prime * result + ((getFromScene() == null) ? 0 : getFromScene().hashCode());
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java

[第 63 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java:63)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedDate() == null
                        ? other.getCreatedDate() == null
                        : this.getCreatedDate().equals(other.getCreatedDate()));
```

[第 65 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java:65)

```java
&& (this.getUserId() == null
                        ? other.getUserId() == null
                        : this.getUserId().equals(other.getUserId()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedDate() == null
                        ? other.getCreatedDate() == null
                        : this.getCreatedDate().equals(other.getCreatedDate()));
```

[第 68 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java:68)

```java
&& (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedDate() == null
                        ? other.getCreatedDate() == null
                        : this.getCreatedDate().equals(other.getCreatedDate()));
```

[第 71 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java:71)

```java
&& (this.getCreatedDate() == null
                        ? other.getCreatedDate() == null
                        : this.getCreatedDate().equals(other.getCreatedDate()));
```

[第 79 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java:79)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 80 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java:80)

```java
result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
```

[第 81 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java:81)

```java
result = prime * result + ((getDuration() == null) ? 0 : getDuration().hashCode());
```

[第 82 行，第 63 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/domain/UserLearnDurationStats.java:82)

```java
result = prime * result + ((getCreatedDate() == null) ? 0 : getCreatedDate().hashCode());
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/ActiveLearningLeaseService.java

[第 150 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/ActiveLearningLeaseService.java:150)

```java
StringUtils.hasText(suppliedSessionId)
                        ? suppliedSessionId
                        : UUID.randomUUID().toString();
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/DailyLearningRankingService.java

[第 284 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/DailyLearningRankingService.java:284)

```java
long targetScore = authoritativeDuration == null ? 0L : authoritativeDuration;
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseHourRecordServiceImpl.java

[第 67 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseHourRecordServiceImpl.java:67)

```java
Date finishedAt = isFinished ? new Date() : null;
```

[第 76 行，第 51 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseHourRecordServiceImpl.java:76)

```java
insertRecord.setIsFinished(isFinished ? 1 : 0);
```

[第 87 行，第 51 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseHourRecordServiceImpl.java:87)

```java
updateRecord.setIsFinished(isFinished ? 1 : 0);
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseRecordServiceImpl.java

[第 61 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseRecordServiceImpl.java:61)

```java
Date finishedAt = isFinished ? new Date() : null;
```

[第 71 行，第 51 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseRecordServiceImpl.java:71)

```java
insertRecord.setIsFinished(isFinished ? 1 : 0);
```

[第 83 行，第 51 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseRecordServiceImpl.java:83)

```java
updateRecord.setIsFinished(isFinished ? 1 : 0);
```

[第 89 行，第 17 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserCourseRecordServiceImpl.java:89)

```java
return isFinished
                ? CourseCompletionTransition.UNFINISHED_TO_FINISHED
                : CourseCompletionTransition.NONE;
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserLearnDurationStatsServiceImpl.java

[第 68 行，第 30 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserLearnDurationStatsServiceImpl.java:68)

```java
return total == null ? 0L : total;
```

[第 76 行，第 30 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserLearnDurationStatsServiceImpl.java:76)

```java
return total == null ? 0L : total;
```

[第 105 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/UserLearnDurationStatsServiceImpl.java:105)

```java
return totalDuration == null ? 0L : totalDuration;
```

### playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/LearningFactPersistenceService.java

[第 106 行，第 50 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/LearningFactPersistenceService.java:106)

```java
previous == null ? 0 : previous.getFinishedDuration();
```

[第 126 行，第 49 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/LearningFactPersistenceService.java:126)

```java
int previousDuration = previous == null ? 0 : previous.getFinishedDuration();
```

[第 141 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/LearningFactPersistenceService.java:141)

```java
completionTransition.isFirstCompletion()
                        ? awardCourseCompletion(userId, courseId)
                        : 0;
```

[第 169 行，第 33 列](C:/Users/86198/Desktop/play/playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/LearningFactPersistenceService.java:169)

```java
return result.applied() ? COURSE_COMPLETION_REWARD_POINTS : 0;
```

### playedu-api/playedu-points/src/main/java/xyz/playedu/points/crypto/PointCodeCryptoService.java

[第 64 行，第 44 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/crypto/PointCodeCryptoService.java:64)

```java
configuredKeyBytes == null ? null : sha256(configuredKeyBytes, "加密密钥派生失败");
```

[第 65 行，第 48 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/crypto/PointCodeCryptoService.java:65)

```java
this.digestKey = encryptionKey == null ? null : deriveDigestKey(encryptionKey);
```

### playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointBalanceServiceImpl.java

[第 85 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointBalanceServiceImpl.java:85)

```java
change.type() == PointLedgerType.REDEMPTION
                        ? pointBalanceMapper.applyCredit1DeltaIfNonNegative(
                                change.userId(), change.delta())
                        : pointBalanceMapper.applyCredit1Delta(change.userId(), change.delta());
```

### playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointCodeServiceImpl.java

[第 63 行，第 48 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointCodeServiceImpl.java:63)

```java
code == null || code.isBlank() ? null : cryptoService.digest(code);
```

[第 207 行，第 43 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointCodeServiceImpl.java:207)

```java
return offset > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) offset;
```

### playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointLedgerServiceImpl.java

[第 65 行，第 40 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointLedgerServiceImpl.java:65)

```java
result.setData(ledgers == null ? List.of() : ledgers);
```

[第 84 行，第 43 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointLedgerServiceImpl.java:84)

```java
return offset > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) offset;
```

### playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointProductServiceImpl.java

[第 86 行，第 62 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointProductServiceImpl.java:86)

```java
PointProductStatus normalizedStatus = status == null ? PointProductStatus.ON_SALE : status;
```

[第 122 行，第 32 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointProductServiceImpl.java:122)

```java
return updated == null ? update : updated;
```

[第 142 行，第 32 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointProductServiceImpl.java:142)

```java
return updated == null ? update : updated;
```

[第 209 行，第 43 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointProductServiceImpl.java:209)

```java
return offset > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) offset;
```

### playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointRedemptionServiceImpl.java

[第 103 行，第 44 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointRedemptionServiceImpl.java:103)

```java
result.setData(redemptions == null ? List.of() : redemptions);
```

[第 222 行，第 43 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/java/xyz/playedu/points/service/impl/PointRedemptionServiceImpl.java:222)

```java
return offset > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) offset;
```

### playedu-api/playedu-points/src/test/java/xyz/playedu/points/service/PointBalanceServiceIntegrationTest.java

[第 348 行，第 53 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/test/java/xyz/playedu/points/service/PointBalanceServiceIntegrationTest.java:348)

```java
sourceNumber % 2 == 0
                                                    ? PointLedgerType.COURSE_COMPLETION
                                                    : PointLedgerType.REDEMPTION;
```

[第 350 行，第 71 列](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/test/java/xyz/playedu/points/service/PointBalanceServiceIntegrationTest.java:350)

```java
int delta = sourceNumber % 2 == 0 ? 10 : -10;
```

### playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java

[第 86 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:86)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getExtension() == null
                        ? other.getExtension() == null
                        : this.getExtension().equals(other.getExtension()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getDisk() == null
                        ? other.getDisk() == null
                        : this.getDisk().equals(other.getDisk()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 88 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:88)

```java
&& (this.getAdminId() == null
                        ? other.getAdminId() == null
                        : this.getAdminId().equals(other.getAdminId()))
                && (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getExtension() == null
                        ? other.getExtension() == null
                        : this.getExtension().equals(other.getExtension()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getDisk() == null
                        ? other.getDisk() == null
                        : this.getDisk().equals(other.getDisk()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 91 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:91)

```java
&& (this.getType() == null
                        ? other.getType() == null
                        : this.getType().equals(other.getType()))
                && (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getExtension() == null
                        ? other.getExtension() == null
                        : this.getExtension().equals(other.getExtension()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getDisk() == null
                        ? other.getDisk() == null
                        : this.getDisk().equals(other.getDisk()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 94 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:94)

```java
&& (this.getName() == null
                        ? other.getName() == null
                        : this.getName().equals(other.getName()))
                && (this.getExtension() == null
                        ? other.getExtension() == null
                        : this.getExtension().equals(other.getExtension()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getDisk() == null
                        ? other.getDisk() == null
                        : this.getDisk().equals(other.getDisk()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 97 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:97)

```java
&& (this.getExtension() == null
                        ? other.getExtension() == null
                        : this.getExtension().equals(other.getExtension()))
                && (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getDisk() == null
                        ? other.getDisk() == null
                        : this.getDisk().equals(other.getDisk()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 100 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:100)

```java
&& (this.getSize() == null
                        ? other.getSize() == null
                        : this.getSize().equals(other.getSize()))
                && (this.getDisk() == null
                        ? other.getDisk() == null
                        : this.getDisk().equals(other.getDisk()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 103 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:103)

```java
&& (this.getDisk() == null
                        ? other.getDisk() == null
                        : this.getDisk().equals(other.getDisk()))
                && (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 106 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:106)

```java
&& (this.getPath() == null
                        ? other.getPath() == null
                        : this.getPath().equals(other.getPath()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 109 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:109)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()))
                && (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 112 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:112)

```java
&& (this.getParentId() == null
                        ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 115 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:115)

```java
&& (this.getIsHidden() == null
                        ? other.getIsHidden() == null
                        : this.getIsHidden().equals(other.getIsHidden()));
```

[第 123 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:123)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 124 行，第 59 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:124)

```java
result = prime * result + ((getAdminId() == null) ? 0 : getAdminId().hashCode());
```

[第 125 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:125)

```java
result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
```

[第 126 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:126)

```java
result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
```

[第 127 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:127)

```java
result = prime * result + ((getExtension() == null) ? 0 : getExtension().hashCode());
```

[第 128 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:128)

```java
result = prime * result + ((getSize() == null) ? 0 : getSize().hashCode());
```

[第 129 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:129)

```java
result = prime * result + ((getDisk() == null) ? 0 : getDisk().hashCode());
```

[第 130 行，第 56 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:130)

```java
result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
```

[第 131 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:131)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

[第 132 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:132)

```java
result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
```

[第 133 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/Resource.java:133)

```java
result = prime * result + ((getIsHidden() == null) ? 0 : getIsHidden().hashCode());
```

### playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceCategory.java

[第 54 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceCategory.java:54)

```java
return (this.getCid() == null
                        ? other.getCid() == null
                        : this.getCid().equals(other.getCid()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()));
```

[第 57 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceCategory.java:57)

```java
&& (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()));
```

[第 65 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceCategory.java:65)

```java
result = prime * result + ((getCid() == null) ? 0 : getCid().hashCode());
```

[第 66 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceCategory.java:66)

```java
result = prime * result + ((getRid() == null) ? 0 : getRid().hashCode());
```

### playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java

[第 65 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java:65)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getPoster() == null
                        ? other.getPoster() == null
                        : this.getPoster().equals(other.getPoster()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 67 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java:67)

```java
&& (this.getRid() == null
                        ? other.getRid() == null
                        : this.getRid().equals(other.getRid()))
                && (this.getPoster() == null
                        ? other.getPoster() == null
                        : this.getPoster().equals(other.getPoster()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 70 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java:70)

```java
&& (this.getPoster() == null
                        ? other.getPoster() == null
                        : this.getPoster().equals(other.getPoster()))
                && (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 73 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java:73)

```java
&& (this.getDuration() == null
                        ? other.getDuration() == null
                        : this.getDuration().equals(other.getDuration()))
                && (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 76 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java:76)

```java
&& (this.getCreatedAt() == null
                        ? other.getCreatedAt() == null
                        : this.getCreatedAt().equals(other.getCreatedAt()));
```

[第 84 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java:84)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 85 行，第 55 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java:85)

```java
result = prime * result + ((getRid() == null) ? 0 : getRid().hashCode());
```

[第 86 行，第 58 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java:86)

```java
result = prime * result + ((getPoster() == null) ? 0 : getPoster().hashCode());
```

[第 87 行，第 60 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java:87)

```java
result = prime * result + ((getDuration() == null) ? 0 : getDuration().hashCode());
```

[第 88 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-resource/src/main/java/xyz/playedu/resource/domain/ResourceExtra.java:88)

```java
result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
```

### playedu-api/playedu-system/src/main/java/xyz/playedu/system/domain/Migration.java

[第 71 行，第 38 列](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/domain/Migration.java:71)

```java
return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getMigration() == null
                        ? other.getMigration() == null
                        : this.getMigration().equals(other.getMigration()));
```

[第 73 行，第 25 列](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/domain/Migration.java:73)

```java
&& (this.getMigration() == null
                        ? other.getMigration() == null
                        : this.getMigration().equals(other.getMigration()));
```

[第 81 行，第 54 列](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/domain/Migration.java:81)

```java
result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
```

[第 82 行，第 61 列](C:/Users/86198/Desktop/play/playedu-api/playedu-system/src/main/java/xyz/playedu/system/domain/Migration.java:82)

```java
result = prime * result + ((getMigration() == null) ? 0 : getMigration().hashCode());
```
