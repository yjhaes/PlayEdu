import { useEffect, useState } from "react";
import { Button, NavBar, Skeleton } from "antd-mobile";
import { useNavigate } from "react-router-dom";
import { points } from "../../api";
import type { PointsRules } from "../../api/points";
import styles from "./rules.module.scss";

const FALLBACK_DESCRIPTIONS = [
  "首次完成任一可学习课程获得10积分，同一学员同一课程只奖励一次，重学或重置不会重复获得。",
  "积分不会仅因时间经过而失效；商品售罄或下架也不影响课程完成奖励。",
  "每次兑换只交付一枚兑换码，兑换成功后不可取消或退回积分。",
  "兑换码失效后不提供系统内补码、售后或退回积分。",
  "仅积分超级管理员的人工扣分可以产生负积分余额，后续课程完成奖励会自然抵扣。",
  "积分不能购买、转让、提现或兑换现金等价物。",
];

const RulesPage = () => {
  const navigate = useNavigate();
  const [rules, setRules] = useState<PointsRules | null>(null);
  const [error, setError] = useState("");

  const loadRules = async () => {
    setError("");
    try {
      const response = await points.rules();
      setRules(response.data);
    } catch {
      setError("积分规则加载失败，请重试");
    }
  };

  useEffect(() => {
    document.title = "积分规则";
    void loadRules();
  }, []);

  const descriptions =
    rules?.descriptions?.length ? rules.descriptions : FALLBACK_DESCRIPTIONS;

  return (
    <div className={styles["main-body"]}>
      <NavBar onBack={() => navigate(-1)}>积分规则</NavBar>
      <div className={styles["content"]}>
        {error && (
          <div className={styles["error-box"]} role="alert">
            <span>{error}</span>
            <Button size="small" onClick={() => void loadRules()}>
              重试
            </Button>
          </div>
        )}
        <div className={styles["highlight"]}>
          <div>首次课程完成奖励</div>
          {rules ? (
            <strong>{rules.course_completion_reward_points} 积分</strong>
          ) : (
            <Skeleton animated style={{ width: 100, height: 32 }} />
          )}
          <p>同一学员同一课程只奖励一次，重学或重置不会重复获得。</p>
        </div>
        <div className={styles["rule-card"]}>
          <div className={styles["rule-title"]}>长期有效的规则说明</div>
          <ul>
            {descriptions.map((description) => (
              <li key={description}>{description}</li>
            ))}
          </ul>
        </div>
        <div className={styles["facts"]}>
          <div className={styles["fact"]}>
            <strong>永久积分</strong>
            <span>
              {rules?.points_never_expire === false
                ? "以页面实际规则为准"
                : "不会仅因时间经过而失效"}
            </span>
          </div>
          <div className={styles["fact"]}>
            <strong>兑换确认</strong>
            <span>
              {rules?.redemption_cancelable
                ? "请以兑换页面提示为准"
                : "兑换成功后不可取消或退回积分"}
            </span>
          </div>
          <div className={styles["fact"]}>
            <strong>失效兑换码</strong>
            <span>
              {rules?.invalid_code_after_delivery_support
                ? "请联系平台处理"
                : "不提供系统内补码、售后或退回积分"}
            </span>
          </div>
          <div className={styles["fact"]}>
            <strong>负积分余额</strong>
            <span>
              {rules?.manual_deduction_can_create_negative_balance === false
                ? "不支持人工扣分产生负数"
                : "仅人工扣分可产生，后续课程奖励会自然抵扣"}
            </span>
          </div>
        </div>
        <div className={styles["footer-note"]}>
          积分是平台内封闭式权益，只能按规则获得并兑换指定兑换码，不能购买、转让、提现或兑换现金等价物。
        </div>
      </div>
    </div>
  );
};

export default RulesPage;
