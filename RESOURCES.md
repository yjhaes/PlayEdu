# PlayEdu 后端学习资源

## Knowledge
- [当前课程接口源码](playedu-api/playedu-api/src/main/java/xyz/playedu/api/controller/frontend/CourseController.java)
  项目的一手事实；第一课核对第 37～60 行的路由声明与首个业务调用。源码更新后重新核对。
- [Spring 官方：Mapping Requests](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html)
  解释类与方法的请求映射、路径变量和 HTTP 方法。当前网页版本可能高于项目，第一课只采用项目中已存在的基础注解语义，不用于决定依赖版本。
- [Spring 官方：Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
  入门阅读：聚焦 Controller 示例和 GetMapping 说明，不要求照抄其当前构建依赖。
- [后端文件职责指南](docs/backend-file-guide.md)
  定位六个模块及具体类；实现行为仍以当前源码为准。
- [积分首发清单](docs/points-launch-checklist.md)
  后续准备测试数据与执行只读对账。
- [双实例验收](docs/acceptance/multi-instance-learning.md)
  后续验证共享状态，保存实际运行结果和未验证内容。

- [课程查询实现](playedu-api/playedu-course/src/main/java/xyz/playedu/course/service/impl/CourseServiceImpl.java)
  第二课的一手事实：findOrFail 第 174～179 行，按 id 查询并在空结果时抛异常。
- [MyBatis-Plus 官方：持久层接口](https://baomidou.com/guides/data-interface/)
  第二课聚焦 get 小节，核对 getOne 的查询结果和使用条件。
- [MyBatis-Plus 官方：条件构造器](https://baomidou.com/guides/wrapper/)
  核对 eq 的列名、参数值与等值条件含义。

## Wisdom (Communities)
当前课程以项目实验和反馈为主，尚未选择社区；用户没有表达社区参与偏好。

## Gaps
- Java、SQL、Spring 的基础能力待通过短练习判断。
- 当前尚无学习掌握证据，不把已展示的材料当作已学会。
