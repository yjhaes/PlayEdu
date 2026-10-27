import { useEffect, useState } from "react";
import { Button, NavBar, Skeleton } from "antd-mobile";
import { useNavigate } from "react-router-dom";
import { points } from "../../api";
import type { PointsRules } from "../../api/points";
import styles from "./rules.module.scss";

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
        {rules && (
          <>
            <div className={styles["highlight"]}>
              <div>首次课程完成奖励</div>
              <strong>{rules.course_completion_reward_points} 积分</strong>
            </div>
            <div className={styles["rule-card"]}>
              <div className={styles["rule-title"]}>长期有效的规则说明</div>
              <ul>
                {rules.descriptions.map((description) => (
                  <li key={description}>{description}</li>
                ))}
              </ul>
            </div>
          </>
        )}
        {!rules && !error && (
          <div className={styles["loading-card"]}>
            <Skeleton animated style={{ width: "45%", height: 24 }} />
            <Skeleton animated style={{ width: "100%", height: 80 }} />
          </div>
        )}
        {rules && (
          <div className={styles["facts"]}>
            <div className={styles["fact"]}>
              <strong>永久积分</strong>
              <span>
                {rules.points_never_expire
                  ? "不会仅因时间经过而失效"
                  : "以服务端规则说明为准"}
              </span>
            </div>
            <div className={styles["fact"]}>
              <strong>兑换确认</strong>
              <span>
                {rules.redemption_cancelable
                  ? "以服务端规则说明为准"
                  : "兑换成功后不可取消或退回积分"}
              </span>
            </div>
            <div className={styles["fact"]}>
              <strong>失效兑换码</strong>
              <span>
                {rules.invalid_code_after_delivery_support
                  ? "以服务端规则说明为准"
                  : "不提供系统内补码、售后或退回积分"}
              </span>
            </div>
            <div className={styles["fact"]}>
              <strong>负积分余额</strong>
              <span>
                {rules.manual_deduction_can_create_negative_balance
                  ? "仅人工扣分可产生，后续课程奖励会自然抵扣"
                  : "以服务端规则说明为准"}
              </span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default RulesPage;
