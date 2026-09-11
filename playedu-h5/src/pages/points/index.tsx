import { useEffect, useRef, useState } from "react";
import {
  Button,
  Dialog,
  NavBar,
  PullToRefresh,
  Skeleton,
  Tabs,
  Tag,
  Toast,
} from "antd-mobile";
import { useNavigate } from "react-router-dom";
import { points } from "../../api";
import type {
  DeliveredRedemption,
  HistoricalRewardSummary,
  PointLedger,
  PointProduct,
  PointRedemption,
  PointsRules,
  PointsSummary,
} from "../../api/points";
import styles from "./index.module.scss";

type PointsTab = "products" | "ledgers" | "redemptions";

const PAGE_SIZE = 10;

const LEDGER_TYPE_LABELS: Record<string, string> = {
  COURSE_COMPLETION: "课程完成奖励",
  HISTORICAL_COURSE_COMPLETION: "历史课程补发",
  REDEMPTION: "兑换商品",
  MANUAL_ADJUSTMENT: "人工调整",
};

const isRecord = (value: unknown): value is Record<string, unknown> =>
  Boolean(value) && typeof value === "object";

const errorMessage = (error: unknown, fallback: string): string => {
  if (!isRecord(error)) {
    return fallback;
  }

  const response = error.response;
  if (isRecord(response) && isRecord(response.data)) {
    const responseMessage = response.data.msg;
    if (typeof responseMessage === "string" && responseMessage) {
      return responseMessage;
    }
  }

  if (typeof error.msg === "string" && error.msg) {
    return error.msg;
  }
  if (typeof error.message === "string" && error.message) {
    return error.message;
  }
  return fallback;
};

const redemptionErrorMessage = (error: unknown): string => {
  const message = errorMessage(error, "兑换失败，请检查网络后重试");
  if (message.includes("余额") || message.includes("积分不足")) {
    return "余额不足，当前积分余额不足以兑换此商品";
  }
  if (message.includes("兑完") || message.includes("库存")) {
    return "库存不足，商品当前可兑换库存为 0";
  }
  if (message.includes("下架") || message.includes("在售")) {
    return "商品已下架，暂不可兑换";
  }
  return message;
};

const createRequestKey = (): string => {
  if (typeof globalThis.crypto?.randomUUID === "function") {
    return globalThis.crypto.randomUUID();
  }
  return `h5-${Date.now()}-${Math.random().toString(36).slice(2)}`;
};

const formatDate = (value?: string | null): string => {
  if (!value) {
    return "-";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const PointsCenterPage = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<PointsTab>("products");
  const [summary, setSummary] = useState<PointsSummary | null>(null);
  const [products, setProducts] = useState<PointProduct[]>([]);
  const [ledgers, setLedgers] = useState<PointLedger[]>([]);
  const [redemptions, setRedemptions] = useState<PointRedemption[]>([]);
  const [productPage, setProductPage] = useState(0);
  const [ledgerPage, setLedgerPage] = useState(0);
  const [redemptionPage, setRedemptionPage] = useState(0);
  const [productTotal, setProductTotal] = useState(0);
  const [ledgerTotal, setLedgerTotal] = useState(0);
  const [redemptionTotal, setRedemptionTotal] = useState(0);
  const [summaryLoading, setSummaryLoading] = useState(true);
  const [productsLoading, setProductsLoading] = useState(false);
  const [ledgersLoading, setLedgersLoading] = useState(false);
  const [redemptionsLoading, setRedemptionsLoading] = useState(false);
  const [centerError, setCenterError] = useState("");
  const [operationError, setOperationError] = useState("");
  const [redeemingProductId, setRedeemingProductId] = useState<number | null>(
    null
  );
  const [retryRedemption, setRetryRedemption] = useState<{
    product: PointProduct;
    requestKey: string;
  } | null>(null);
  const [deliveredRedemption, setDeliveredRedemption] =
    useState<DeliveredRedemption | null>(null);
  const [detailLoadingId, setDetailLoadingId] = useState<number | null>(null);
  const historicalNoticeShown = useRef(false);
  const loadedTabs = useRef<Set<PointsTab>>(new Set());

  const acknowledgeHistoricalReward = async () => {
    try {
      await points.acknowledgeHistoricalReward();
      setSummary((current) =>
        current
          ? {
              ...current,
              historical_reward_summary_pending: false,
            }
          : current
      );
    } catch (error) {
      Toast.show({
        content: errorMessage(error, "历史补发提示确认失败，请稍后重试"),
      });
    }
  };

  const showHistoricalRewardNotice = (
    historicalSummary: HistoricalRewardSummary
  ) => {
    historicalNoticeShown.current = true;
    void Dialog.alert({
      title: "历史课程奖励到账",
      content: (
        <div className={styles["dialog-content"]}>
          <div>
            已根据积分上线前已完成的课程，为你补发历史课程完成奖励。
          </div>
          <strong>
            {historicalSummary.completion_count} 门课程，共获得{" "}
            {historicalSummary.points_awarded} 积分
          </strong>
          <div>历史补发只展示一次，积分不会因时间经过而失效。</div>
        </div>
      ),
      confirmText: "我知道了",
      onConfirm: acknowledgeHistoricalReward,
    });
  };

  const loadSummary = async (showHistoricalNotice: boolean) => {
    setSummaryLoading(true);
    try {
      const response = await points.summary();
      setSummary(response.data);
      if (
        showHistoricalNotice &&
        response.data.historical_reward_summary_pending &&
        response.data.historical_reward_summary &&
        !historicalNoticeShown.current
      ) {
        showHistoricalRewardNotice(response.data.historical_reward_summary);
      }
    } catch (error) {
      setCenterError(errorMessage(error, "积分中心加载失败，请稍后重试"));
    } finally {
      setSummaryLoading(false);
    }
  };

  const loadProducts = async (page: number) => {
    setProductsLoading(true);
    try {
      const response = await points.products(page, PAGE_SIZE);
      const pageData = response.data;
      setProducts((current) =>
        page === 1 ? pageData.data || [] : [...current, ...(pageData.data || [])]
      );
      setProductPage(page);
      setProductTotal(pageData.total || 0);
    } catch (error) {
      loadedTabs.current.delete("products");
      setCenterError(errorMessage(error, "兑换商品加载失败，请重试"));
    } finally {
      setProductsLoading(false);
    }
  };

  const loadLedgers = async (page: number) => {
    setLedgersLoading(true);
    try {
      const response = await points.ledgers(page, PAGE_SIZE);
      const pageData = response.data;
      setLedgers((current) =>
        page === 1 ? pageData.data || [] : [...current, ...(pageData.data || [])]
      );
      setLedgerPage(page);
      setLedgerTotal(pageData.total || 0);
    } catch (error) {
      loadedTabs.current.delete("ledgers");
      setCenterError(errorMessage(error, "积分流水加载失败，请重试"));
    } finally {
      setLedgersLoading(false);
    }
  };

  const loadRedemptions = async (page: number) => {
    setRedemptionsLoading(true);
    try {
      const response = await points.redemptions(page, PAGE_SIZE);
      const pageData = response.data;
      setRedemptions((current) =>
        page === 1 ? pageData.data || [] : [...current, ...(pageData.data || [])]
      );
      setRedemptionPage(page);
      setRedemptionTotal(pageData.total || 0);
    } catch (error) {
      loadedTabs.current.delete("redemptions");
      setCenterError(errorMessage(error, "我的兑换加载失败，请重试"));
    } finally {
      setRedemptionsLoading(false);
    }
  };

  const loadTab = async (tab: PointsTab, page: number) => {
    if (tab === "products") {
      await loadProducts(page);
    } else if (tab === "ledgers") {
      await loadLedgers(page);
    } else {
      await loadRedemptions(page);
    }
  };

  useEffect(() => {
    document.title = "积分中心";
    void loadSummary(true);
  }, []);

  useEffect(() => {
    if (loadedTabs.current.has(activeTab)) {
      return;
    }
    loadedTabs.current.add(activeTab);
    void loadTab(activeTab, 1);
  }, [activeTab]);

  const refresh = async () => {
    setCenterError("");
    await loadSummary(false);
    await loadTab(activeTab, 1);
  };

  const retryCurrentTab = () => {
    setCenterError("");
    void loadSummary(false);
    loadedTabs.current.add(activeTab);
    void loadTab(activeTab, 1);
  };

  const loadMore = () => {
    if (activeTab === "products" && products.length < productTotal) {
      void loadProducts(productPage + 1);
    } else if (activeTab === "ledgers" && ledgers.length < ledgerTotal) {
      void loadLedgers(ledgerPage + 1);
    } else if (
      activeTab === "redemptions" &&
      redemptions.length < redemptionTotal
    ) {
      void loadRedemptions(redemptionPage + 1);
    }
  };

  const openRedemptionDetail = async (id: number) => {
    setDetailLoadingId(id);
    setOperationError("");
    try {
      const response = await points.redemptionDetail(id);
      setDeliveredRedemption(response.data);
    } catch (error) {
      setOperationError(errorMessage(error, "兑换码加载失败，请重试"));
    } finally {
      setDetailLoadingId(null);
    }
  };

  const redeemProduct = async (product: PointProduct, requestKey: string) => {
    setOperationError("");
    setRetryRedemption({ product, requestKey });
    setRedeemingProductId(product.id);
    try {
      const response = await points.redeem(product.id, requestKey);
      if (!response.data?.code || !response.data.redemption) {
        throw new Error("兑换结果无效，请点击重试");
      }
      setDeliveredRedemption(response.data);
      setRetryRedemption(null);
      Toast.show({ content: "兑换成功，兑换码已交付" });
      const refreshRedemptions = loadedTabs.current.has("redemptions")
        ? loadRedemptions(1)
        : Promise.resolve();
      await Promise.all([
        loadSummary(false),
        loadProducts(1),
        refreshRedemptions,
      ]);
    } catch (error) {
      setOperationError(redemptionErrorMessage(error));
    } finally {
      setRedeemingProductId(null);
    }
  };

  const askRedeem = (product: PointProduct) => {
    const availableCount = product.available_count ?? 0;
    if (product.status !== "ON_SALE") {
      setOperationError("商品已下架，暂不可兑换");
      return;
    }
    if (availableCount <= 0) {
      setOperationError("库存不足，商品当前可兑换库存为 0");
      return;
    }
    if (summary && summary.credit1 < product.points_price) {
      setOperationError("余额不足，当前积分余额不足以兑换此商品");
      return;
    }

    const requestKey = createRequestKey();
    void Dialog.confirm({
      title: "确认兑换",
      content: (
        <div className={styles["dialog-content"]}>
          <div>商品：{product.name}</div>
          <div>
            价格：<strong>{product.points_price} 积分</strong>
          </div>
          <div>兑换成功后立即交付一枚兑换码，不可取消或退回积分。</div>
          <div>兑换码失效后不提供系统内补码、售后或退回积分。</div>
        </div>
      ),
      cancelText: "再想想",
      confirmText: "确认兑换",
      onConfirm: () => redeemProduct(product, requestKey),
    });
  };

  const copyCode = async (code: string) => {
    try {
      if (navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(code);
      } else {
        const textarea = document.createElement("textarea");
        textarea.value = code;
        textarea.style.position = "fixed";
        textarea.style.opacity = "0";
        document.body.appendChild(textarea);
        textarea.select();
        document.execCommand("copy");
        document.body.removeChild(textarea);
      }
      Toast.show({ content: "兑换码已复制" });
    } catch {
      Toast.show({ content: "复制失败，请长按兑换码复制" });
    }
  };

  const productNames = new Map(products.map((product) => [product.id, product.name]));
  const hasMore =
    activeTab === "products"
      ? products.length < productTotal
      : activeTab === "ledgers"
      ? ledgers.length < ledgerTotal
      : redemptions.length < redemptionTotal;
  const tabLoading =
    activeTab === "products"
      ? productsLoading
      : activeTab === "ledgers"
      ? ledgersLoading
      : redemptionsLoading;

  return (
    <div className={styles["main-body"]}>
      <NavBar onBack={() => navigate(-1)}>积分中心</NavBar>
      <PullToRefresh onRefresh={refresh}>
        <div className={styles["content"]}>
          {centerError && (
            <div className={styles["error-box"]} role="alert">
              <span>{centerError}</span>
              <Button size="small" onClick={retryCurrentTab}>
                重试
              </Button>
            </div>
          )}

          <div className={styles["balance-card"]}>
            <div className={styles["balance-label"]}>当前积分余额</div>
            {summaryLoading && !summary ? (
              <Skeleton animated style={{ width: 130, height: 42 }} />
            ) : (
              <div className={styles["balance-number"]}>{summary?.credit1 ?? 0}</div>
            )}
            <div className={styles["balance-footer"]}>
              <span>积分不会因时间经过而失效</span>
              <Button
                fill="none"
                size="small"
                onClick={() => navigate("/points/rules")}
              >
                积分规则
              </Button>
            </div>
          </div>

          {operationError && (
            <div className={styles["operation-error"]} role="alert">
              <span>{operationError}</span>
              {retryRedemption && (
                <Button
                  size="small"
                  loading={redeemingProductId !== null}
                  onClick={() =>
                    void redeemProduct(
                      retryRedemption.product,
                      retryRedemption.requestKey
                    )
                  }
                >
                  重试兑换
                </Button>
              )}
              <Button
                size="small"
                fill="none"
                onClick={() => setOperationError("")}
              >
                关闭
              </Button>
            </div>
          )}

          {deliveredRedemption && (
            <div className={styles["delivered-card"]}>
              <div className={styles["delivered-title"]}>兑换码已交付</div>
              <div className={styles["delivered-meta"]}>
                已扣除 {deliveredRedemption.redemption.points_cost} 积分 ·{" "}
                {formatDate(deliveredRedemption.redemption.created_at)}
              </div>
              <div className={styles["code"]}>{deliveredRedemption.code}</div>
              <div className={styles["delivered-actions"]}>
                <Button
                  color="primary"
                  size="small"
                  onClick={() => void copyCode(deliveredRedemption.code)}
                >
                  复制兑换码
                </Button>
                <Button
                  fill="none"
                  size="small"
                  onClick={() => setDeliveredRedemption(null)}
                >
                  关闭
                </Button>
              </div>
              <div className={styles["delivered-hint"]}>
                关闭后可在“我的兑换”中再次查看。
              </div>
            </div>
          )}

          <Tabs
            activeKey={activeTab}
            onChange={(key) => setActiveTab(key as PointsTab)}
          >
            <Tabs.Tab title="兑换商品" key="products" />
            <Tabs.Tab title="积分流水" key="ledgers" />
            <Tabs.Tab title="我的兑换" key="redemptions" />
          </Tabs>

          {activeTab === "products" && (
            <div className={styles["list"]}>
              {productsLoading && products.length === 0 &&
                Array.from({ length: 2 }).map((_, index) => (
                  <div className={styles["skeleton-card"]} key={index}>
                    <Skeleton animated style={{ width: "60%", height: 22 }} />
                    <Skeleton animated style={{ width: "35%", height: 18 }} />
                    <Skeleton animated style={{ width: "100%", height: 40 }} />
                  </div>
                ))}
              {!productsLoading && products.length === 0 && (
                <div className={styles["empty"]}>暂无兑换商品</div>
              )}
              {products.map((product) => {
                const availableCount = product.available_count ?? 0;
                const soldOut = availableCount <= 0;
                const offSale = product.status !== "ON_SALE";
                const insufficient =
                  summary !== null && summary.credit1 < product.points_price;
                return (
                  <div className={styles["product-card"]} key={product.id}>
                    <div className={styles["card-heading"]}>
                      <strong>{product.name}</strong>
                      <Tag color={offSale ? "default" : soldOut ? "warning" : "success"}>
                        {offSale ? "已下架" : soldOut ? "已兑完" : "可兑换"}
                      </Tag>
                    </div>
                    <div className={styles["product-price"]}>
                      {product.points_price} <span>积分</span>
                    </div>
                    <div className={styles["product-stock"]}>
                      可兑换库存：<strong>{availableCount}</strong>
                    </div>
                    <Button
                      block
                      color="primary"
                      disabled={offSale || soldOut || insufficient}
                      loading={redeemingProductId === product.id}
                      onClick={() => askRedeem(product)}
                    >
                      {offSale
                        ? "已下架"
                        : soldOut
                        ? "已兑完"
                        : insufficient
                        ? "积分不足"
                        : "立即兑换"}
                    </Button>
                  </div>
                );
              })}
            </div>
          )}

          {activeTab === "ledgers" && (
            <div className={styles["list"]}>
              {ledgersLoading && ledgers.length === 0 &&
                Array.from({ length: 3 }).map((_, index) => (
                  <div className={styles["ledger-card"]} key={index}>
                    <Skeleton animated style={{ width: "50%", height: 20 }} />
                    <Skeleton animated style={{ width: "80%", height: 16 }} />
                  </div>
                ))}
              {!ledgersLoading && ledgers.length === 0 && (
                <div className={styles["empty"]}>暂无积分流水</div>
              )}
              {ledgers.map((ledger) => {
                const delta = ledger.delta ?? 0;
                return (
                  <div className={styles["ledger-card"]} key={`${ledger.created_at}-${delta}`}>
                    <div className={styles["ledger-heading"]}>
                      <strong
                        className={
                          delta >= 0
                            ? styles["positive"]
                            : styles["negative"]
                        }
                      >
                        {delta > 0 ? "+" : ""}
                        {delta} 积分
                      </strong>
                      <span>{LEDGER_TYPE_LABELS[ledger.type] || "积分变动"}</span>
                    </div>
                    <div className={styles["ledger-detail"]}>
                      余额：{ledger.balance_after} 积分
                      {ledger.reason ? ` · ${ledger.reason}` : ""}
                    </div>
                    <div className={styles["date"]}>{formatDate(ledger.created_at)}</div>
                  </div>
                );
              })}
            </div>
          )}

          {activeTab === "redemptions" && (
            <div className={styles["list"]}>
              {redemptionsLoading && redemptions.length === 0 &&
                Array.from({ length: 3 }).map((_, index) => (
                  <div className={styles["redemption-card"]} key={index}>
                    <Skeleton animated style={{ width: "55%", height: 20 }} />
                    <Skeleton animated style={{ width: "75%", height: 16 }} />
                  </div>
                ))}
              {!redemptionsLoading && redemptions.length === 0 && (
                <div className={styles["empty"]}>还没有兑换记录</div>
              )}
              {redemptions.map((redemption) => (
                <div className={styles["redemption-card"]} key={redemption.id}>
                  <div className={styles["card-heading"]}>
                    <strong>
                      {productNames.get(redemption.product_id) ||
                        `兑换商品 #${redemption.product_id}`}
                    </strong>
                    <Tag color="success">已交付</Tag>
                  </div>
                  <div className={styles["ledger-detail"]}>
                    已扣除 {redemption.points_cost} 积分 ·{" "}
                    {formatDate(redemption.created_at)}
                  </div>
                  <Button
                    block
                    fill="outline"
                    size="small"
                    loading={detailLoadingId === redemption.id}
                    onClick={() => void openRedemptionDetail(redemption.id)}
                  >
                    查看兑换码
                  </Button>
                </div>
              ))}
            </div>
          )}

          {hasMore && (
            <Button
              className={styles["load-more"]}
              block
              fill="none"
              loading={tabLoading}
              onClick={loadMore}
            >
              加载更多
            </Button>
          )}
        </div>
      </PullToRefresh>
    </div>
  );
};

export default PointsCenterPage;
