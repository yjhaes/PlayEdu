# 积分首发清单

项目尚未上线且没有业务历史数据，积分余额从零开始。无需执行旧积分清零、历史课程补发或迁移状态确认，数据库结构初始化后积分中心和兑换接口即可使用。

## 上线前与验收

1. 使用现有数据库结构初始化机制创建积分流水、商品、兑换码和兑换记录表及相关约束。
2. 配置兑换码专用加密密钥及其他上线配置。
3. 超级管理员创建试运行商品，价格设为 50 积分，并导入 20 个兑换码，验证库存和重复导入报告。
4. 验证新学员积分余额为零，首次完成课程获得 10 分，重学或重复请求不再次奖励。
5. 验证 PC/H5 积分余额一致、正常兑换交付兑换码、余额不足和售罄时不扣分。
6. 验证普通管理员无法执行积分运营操作，余额与流水、库存与兑换记录对账一致。

## 上线后监控与对账

上线后至少在首日持续观察以下指标，并在发布记录中保存结果：余额与流水差异、重复导入拒绝数、兑换失败数、兑换成功数、库存剩余数和已发放数。日志与报表只允许使用用户 ID、商品 ID、兑换请求键和结果状态等非敏感字段，不得输出兑换码原码、密文或密钥。

可用下面的只读 SQL 检查余额与流水是否一致；`mismatched_user_count` 必须为 0：

```sql
SELECT COUNT(*) AS mismatched_user_count
FROM users u
LEFT JOIN (
    SELECT user_id, COALESCE(SUM(delta), 0) AS ledger_balance
    FROM point_ledgers
    GROUP BY user_id
) l ON l.user_id = u.id
WHERE COALESCE(u.credit1, 0) <> COALESCE(l.ledger_balance, 0);
```

库存对账应同时查看商品的可用兑换码数量和兑换记录数量，避免只依赖缓存或前端显示：

```sql
SELECT
    p.id AS product_id,
    p.status,
    SUM(c.status = 'AVAILABLE') AS available_code_count,
    SUM(c.status = 'DELIVERED') AS delivered_code_count
FROM point_products p
LEFT JOIN point_codes c ON c.product_id = p.id
GROUP BY p.id, p.status
ORDER BY p.id;
```

发现差异、重复导入、兑换失败突增或库存异常时，暂停兑换相关服务，保留日志与数据库状态，定位原因并修复后再恢复服务。
