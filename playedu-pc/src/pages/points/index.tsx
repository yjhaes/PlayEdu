import { useEffect, useRef, useState } from "react";
import { ArrowLeftOutlined } from "@ant-design/icons";
import { Button, Modal, Skeleton, Tag, Tabs, message } from "antd";
import { useNavigate } from "react-router-dom";
import { points } from "../../api";
import type {
  DeliveredRedemption,
  PointLedger,
  PointProduct,
  PointRedemption,
  PointsSummary,
} from "../../api/points";
import { notifyPointsBalanceChanged } from "../../api/points";
import styles from "./index.module.scss";

type PointsTab = "products" | "ledgers" | "redemptions";

const PAGE_SIZE = 10;

const LEDGER_TYPE_LABELS: Record<string, string> = {
  COURSE_COMPLETION: "课程完成奖励",
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
  const browserCrypto = typeof window !== "undefined" ? window.crypto : null;
  if (typeof browserCrypto?.randomUUID === "function") {
    return browserCrypto.randomUUID();
  }
  return `pc-${Date.now()}-${Math.random().toString(36).slice(2)}`;
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
  const [confirmProduct, setConfirmProduct] = useState<PointProduct | null>(
    null
  );
  const [confirmRequestKey, setConfirmRequestKey] = useState("");
  const loadedTabs = useRef<Set<PointsTab>>(new Set());

  const loadSummary = async () => {
    setSummaryLoading(true);
    try {
      const response = await points.summary();
      setSummary(response.data);
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
        page === 1 ? pageData.data : [...current, ...pageData.data]
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
        page === 1 ? pageData.data : [...current, ...pageData.data]
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
        page === 1 ? pageData.data : [...current, ...pageData.data]
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
    void loadSummary();
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
    await loadSummary();
    await loadTab(activeTab, 1);
  };

  const retryCurrentTab = () => {
    setCenterError("");
    void loadSummary();
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

  const redeemProduct = async (
    product: PointProduct,
    requestKey: string
  ): Promise<boolean> => {
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
      notifyPointsBalanceChanged();
      message.success("兑换成功，兑换码已交付");
    } catch (error) {
      setOperationError(redemptionErrorMessage(error));
      return false;
    } finally {
      setRedeemingProductId(null);
    }

    const refreshRedemptions = loadedTabs.current.has("redemptions")
      ? loadRedemptions(1)
      : Promise.resolve();
    await Promise.all([
      loadSummary(),
      loadProducts(1),
      refreshRedemptions,
    ]);
    return true;
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
    if (summary === null) {
      setOperationError("积分余额暂不可用，请刷新后重试");
      return;
    }
    if (summary.credit1 < product.points_price) {
      setOperationError("余额不足，当前积分余额不足以兑换此商品");
      return;
    }

    setOperationError("");
    setConfirmRequestKey(createRequestKey());
    setConfirmProduct(product);
  };

  const confirmRedemption = async () => {
    if (!confirmProduct || !confirmRequestKey) {
      return;
    }
    const product = confirmProduct;
    await redeemProduct(product, confirmRequestKey);
    setConfirmProduct(null);
    setConfirmRequestKey("");
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
      message.success("兑换码已复制");
    } catch {
      message.error("复制失败，请手动复制兑换码");
    }
  };

  const productNames = new Map(
    products.map((product) => [product.id, product.name])
  );
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
      <div className={styles["content"]}>
        <div className={styles["page-heading"]}>
          <div>
            <h1>积分中心</h1>
            <p>查看积分余额、流水和兑换记录，使用积分兑换已交付的兑换码。</p>
          </div>
          <Button
            icon={
              <ArrowLeftOutlined
                onPointerEnterCapture={undefined}
                onPointerLeaveCapture={undefined}
              />
            }
            onClick={() => navigate(-1)}
          >
            返回
          </Button>
        </div>

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
            <Skeleton active paragraph={false} title={{ width: 160 }} />
          ) : summary === null ? (
            <div className={styles["balance-unavailable"]}>暂不可用</div>
          ) : (
            <div className={styles["balance-number"]}>{summary.credit1}</div>
          )}
          <div className={styles["balance-footer"]}>
            <span>积分不会因时间经过而失效</span>
            <Button type="link" onClick={() => navigate("/points/rules")}>
              积分规则
            </Button>
            <Button type="link" onClick={() => void refresh()}>
              刷新
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
            <Button type="link" size="small" onClick={() => setOperationError("")}>
              关闭
            </Button>
          </div>
        )}

        {deliveredRedemption && (
          <div className={styles["delivered-card"]}>
            <div className={styles["delivered-title"]}>兑换码已交付</div>
            <div className={styles["delivered-meta"]}>
              已扣除 {deliveredRedemption.redemption.points_cost} 积分 · {" "}
              {formatDate(deliveredRedemption.redemption.created_at)}
            </div>
            <div className={styles["code"]}>{deliveredRedemption.code}</div>
            <div className={styles["delivered-actions"]}>
              <Button
                type="primary"
                onClick={() => void copyCode(deliveredRedemption.code)}
              >
                复制兑换码
              </Button>
              <Button onClick={() => setDeliveredRedemption(null)}>关闭</Button>
            </div>
            <div className={styles["delivered-hint"]}>
              关闭后可在“我的兑换”中再次查看。
            </div>
          </div>
        )}

        <Tabs
          activeKey={activeTab}
          onChange={(key) => setActiveTab(key as PointsTab)}
          items={[
            { key: "products", label: "兑换商品" },
            { key: "ledgers", label: "积分流水" },
            { key: "redemptions", label: "我的兑换" },
          ]}
        />

        {activeTab === "products" && (
          <div className={styles["list"]}>
            {productsLoading && products.length === 0 && (
              <div className={styles["skeleton-card"]}>
                <Skeleton active paragraph={{ rows: 3 }} />
              </div>
            )}
            {!productsLoading && products.length === 0 && (
              <div className={styles["empty"]}>暂无兑换商品</div>
            )}
            <div className={styles["product-grid"]}>
              {products.map((product) => {
                const availableCount = product.available_count ?? 0;
                const soldOut = availableCount <= 0;
                const offSale = product.status !== "ON_SALE";
                const balanceUnavailable = summary === null;
                const insufficient =
                  summary !== null && summary.credit1 < product.points_price;
                return (
                  <div className={styles["product-card"]} key={product.id}>
                    <div className={styles["card-heading"]}>
                      <strong>{product.name}</strong>
                      <Tag color={offSale ? "default" : soldOut ? "orange" : "green"}>
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
                      type="primary"
                      disabled={
                        offSale ||
                        soldOut ||
                        balanceUnavailable ||
                        insufficient
                      }
                      loading={redeemingProductId === product.id}
                      onClick={() => askRedeem(product)}
                    >
                      {offSale
                        ? "已下架"
                        : soldOut
                        ? "已兑完"
                        : balanceUnavailable
                        ? "积分余额不可用"
                        : insufficient
                        ? "积分不足"
                        : "立即兑换"}
                    </Button>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {activeTab === "ledgers" && (
          <div className={styles["list"]}>
            {ledgersLoading && ledgers.length === 0 && (
              <div className={styles["skeleton-card"]}>
                <Skeleton active paragraph={{ rows: 3 }} />
              </div>
            )}
            {!ledgersLoading && ledgers.length === 0 && (
              <div className={styles["empty"]}>暂无积分流水</div>
            )}
            {ledgers.map((ledger, index) => {
              const delta = ledger.delta ?? 0;
              return (
                <div
                  className={styles["ledger-card"]}
                  key={`${ledger.created_at}-${ledger.type}-${ledger.delta}-${ledger.balance_after}-${index}`}
                >
                  <div className={styles["ledger-heading"]}>
                    <strong
                      className={
                        delta >= 0 ? styles["positive"] : styles["negative"]
                      }
                    >
                      {delta > 0 ? "+" : ""}
                      {delta} 积分
                    </strong>
                    <span>{LEDGER_TYPE_LABELS[ledger.type] || "积分变动"}</span>
                    <time>{formatDate(ledger.created_at)}</time>
                  </div>
                  <div className={styles["ledger-detail"]}>
                    余额：{ledger.balance_after} 积分
                    {ledger.reason ? ` · ${ledger.reason}` : ""}
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {activeTab === "redemptions" && (
          <div className={styles["list"]}>
            {redemptionsLoading && redemptions.length === 0 && (
              <div className={styles["skeleton-card"]}>
                <Skeleton active paragraph={{ rows: 3 }} />
              </div>
            )}
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
                  <Tag color="green">已交付</Tag>
                </div>
                <div className={styles["ledger-detail"]}>
                  已扣除 {redemption.points_cost} 积分 · {" "}
                  {formatDate(redemption.created_at)}
                </div>
                <Button
                  type="link"
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
          <div className={styles["load-more"]}>
            <Button loading={tabLoading} onClick={loadMore}>
              加载更多
            </Button>
          </div>
        )}
      </div>


      <Modal
        open={confirmProduct !== null}
        title="确认兑换"
        okText="确认兑换"
        cancelText="再想想"
        confirmLoading={redeemingProductId !== null}
        onCancel={() => {
          if (redeemingProductId === null) {
            setConfirmProduct(null);
            setConfirmRequestKey("");
          }
        }}
        onOk={confirmRedemption}
      >
        {confirmProduct && (
          <div className={styles["dialog-content"]}>
            <div>商品：{confirmProduct.name}</div>
            <div>
              价格：<strong>{confirmProduct.points_price} 积分</strong>
            </div>
            <div>兑换成功后立即交付一枚兑换码，不可取消或退回积分。</div>
            <div>兑换码失效后不提供系统内补码、售后或退回积分。</div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default PointsCenterPage;
