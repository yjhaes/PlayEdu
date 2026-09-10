import React, { useEffect, useState } from "react";
import {
  Button,
  Form,
  Input,
  InputNumber,
  Modal,
  Select,
  Space,
  Table,
  Tabs,
  Tag,
  message,
} from "antd";
import type { ColumnsType } from "antd/es/table";
import { ExclamationCircleFilled, PlusOutlined } from "@ant-design/icons";
import { points } from "../../api";

type Product = {
  id: number;
  name: string;
  points_price: number;
  status: "ON_SALE" | "OFF_SALE";
  available_count: number;
  delivered_count: number;
};

type Code = {
  id: number;
  product_id: number;
  status: "AVAILABLE" | "DELIVERED";
  delivered_at?: string;
  created_at: string;
};

type Ledger = {
  id: number;
  user_id: number;
  delta: number;
  balance_after: number;
  type: string;
  source_key: string;
  reason?: string;
  operator_admin_id?: number;
  created_at: string;
};

type Redemption = {
  id: number;
  user_id: number;
  product_id: number;
  code_id: number;
  points_cost: number;
  created_at: string;
};

const ledgerTypes = [
  ["", "全部流水"],
  ["COURSE_COMPLETION", "课程完成"],
  ["HISTORICAL_COURSE_COMPLETION", "历史补发"],
  ["REDEMPTION", "兑换扣分"],
  ["MANUAL_ADJUSTMENT", "人工调整"],
];

const PointsAdminPage = () => {
  const [activeTab, setActiveTab] = useState("products");
  const [productForm] = Form.useForm();
  const [adjustmentForm] = Form.useForm();
  const [productModalOpen, setProductModalOpen] = useState(false);
  const [adjustmentModalOpen, setAdjustmentModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [importProduct, setImportProduct] = useState<Product | null>(null);
  const [codeText, setCodeText] = useState("");
  const [importing, setImporting] = useState(false);
  const [importReport, setImportReport] = useState<any>(null);
  const [revealedCode, setRevealedCode] = useState<string | null>(null);

  const [products, setProducts] = useState<Product[]>([]);
  const [productPage, setProductPage] = useState(1);
  const [productSize, setProductSize] = useState(10);
  const [productTotal, setProductTotal] = useState(0);
  const [productNameInput, setProductNameInput] = useState("");
  const [productName, setProductName] = useState("");
  const [productStatus, setProductStatus] = useState("");

  const [codes, setCodes] = useState<Code[]>([]);
  const [codePage, setCodePage] = useState(1);
  const [codeSize, setCodeSize] = useState(10);
  const [codeTotal, setCodeTotal] = useState(0);
  const [codeProductId, setCodeProductId] = useState<number>();
  const [codeStatus, setCodeStatus] = useState("");
  const [codeSearch, setCodeSearch] = useState("");
  const [codeSearchInput, setCodeSearchInput] = useState("");

  const [ledgers, setLedgers] = useState<Ledger[]>([]);
  const [ledgerPage, setLedgerPage] = useState(1);
  const [ledgerSize, setLedgerSize] = useState(10);
  const [ledgerTotal, setLedgerTotal] = useState(0);
  const [ledgerUserInput, setLedgerUserInput] = useState("");
  const [ledgerUserId, setLedgerUserId] = useState<number>();
  const [ledgerType, setLedgerType] = useState("");
  const [ledgerKeyword, setLedgerKeyword] = useState("");

  const [redemptions, setRedemptions] = useState<Redemption[]>([]);
  const [redemptionPage, setRedemptionPage] = useState(1);
  const [redemptionSize, setRedemptionSize] = useState(10);
  const [redemptionTotal, setRedemptionTotal] = useState(0);
  const [redemptionUserInput, setRedemptionUserInput] = useState("");
  const [redemptionUserId, setRedemptionUserId] = useState<number>();
  const [redemptionProductInput, setRedemptionProductInput] = useState("");
  const [redemptionProductId, setRedemptionProductId] = useState<number>();
  const [redemptionCodeInput, setRedemptionCodeInput] = useState("");
  const [redemptionCodeId, setRedemptionCodeId] = useState<number>();

  const loadProducts = () => {
    points
      .productList(productPage, productSize, productName, productStatus)
      .then((res: any) => {
        setProducts(res.data.data || []);
        setProductTotal(res.data.total || 0);
      });
  };

  const loadCodes = () => {
    points
      .codeList(codePage, codeSize, codeProductId, codeStatus, codeSearch)
      .then((res: any) => {
        setCodes(res.data.data || []);
        setCodeTotal(res.data.total || 0);
      });
  };

  const loadLedgers = () => {
    points
      .ledgerList(ledgerPage, ledgerSize, ledgerUserId, ledgerType, ledgerKeyword)
      .then((res: any) => {
        setLedgers(res.data.data || []);
        setLedgerTotal(res.data.total || 0);
      });
  };

  const loadRedemptions = () => {
    points
      .redemptionList(
        redemptionPage,
        redemptionSize,
        redemptionUserId,
        redemptionProductId,
        redemptionCodeId
      )
      .then((res: any) => {
        setRedemptions(res.data.data || []);
        setRedemptionTotal(res.data.total || 0);
      });
  };

  useEffect(() => {
    if (activeTab === "products") loadProducts();
  }, [activeTab, productPage, productSize, productName, productStatus]);

  useEffect(() => {
    if (activeTab === "inventory") loadCodes();
  }, [activeTab, codePage, codeSize, codeProductId, codeStatus, codeSearch]);

  useEffect(() => {
    if (activeTab === "ledgers") loadLedgers();
  }, [activeTab, ledgerPage, ledgerSize, ledgerUserId, ledgerType, ledgerKeyword]);

  useEffect(() => {
    if (activeTab === "redemptions") loadRedemptions();
  }, [
    activeTab,
    redemptionPage,
    redemptionSize,
    redemptionUserId,
    redemptionProductId,
    redemptionCodeId,
  ]);

  const showCreateProduct = () => {
    setEditingProduct(null);
    productForm.resetFields();
    setProductModalOpen(true);
  };

  const showEditProduct = (product: Product) => {
    setEditingProduct(product);
    productForm.setFieldsValue({
      name: product.name,
      points_price: product.points_price,
    });
    setProductModalOpen(true);
  };

  const submitProduct = () => {
    productForm.validateFields().then((values) => {
      const request = editingProduct
        ? points.updateProduct(editingProduct.id, values.name, values.points_price)
        : points.createProduct(values.name, values.points_price);
      request.then(() => {
        message.success("操作成功");
        setProductModalOpen(false);
        loadProducts();
      });
    });
  };

  const changeProductStatus = (product: Product) => {
    points
      .setProductStatus(
        product.id,
        product.status === "ON_SALE" ? "OFF_SALE" : "ON_SALE"
      )
      .then(() => {
        message.success("操作成功");
        loadProducts();
      });
  };

  const deleteProduct = (product: Product) => {
    Modal.confirm({
      title: "操作确认",
      icon: <ExclamationCircleFilled />,
      content: "只允许删除从未发放兑换码的商品，确认删除？",
      okText: "确认",
      cancelText: "取消",
      onOk: () =>
        points.deleteProduct(product.id).then(() => {
          message.success("操作成功");
          loadProducts();
        }),
    });
  };

  const showImport = (product: Product) => {
    setImportProduct(product);
    setCodeText("");
    setImportReport(null);
  };

  const submitImport = () => {
    if (!importProduct || !codeText.trim()) {
      message.error("请粘贴兑换码");
      return;
    }
    setImporting(true);
    points
      .importCodes(importProduct.id, codeText)
      .then((res: any) => {
        setImportReport(res.data);
        setCodeText("");
        loadProducts();
        if (activeTab === "inventory") loadCodes();
      })
      .finally(() => setImporting(false));
  };

  const reveal = (id: number) => {
    points.revealCode(id).then((res: any) => setRevealedCode(res.data.code));
  };

  const deleteCode = (id: number) => {
    Modal.confirm({
      title: "删除未交付兑换码",
      content: "已发放兑换码不能删除，确认删除此库存码？",
      okText: "确认",
      cancelText: "取消",
      onOk: () =>
        points.deleteCode(id).then(() => {
          message.success("操作成功");
          loadCodes();
          loadProducts();
        }),
    });
  };

  const submitAdjustment = () => {
    adjustmentForm.validateFields().then((values) => {
      points
        .adjustPoints(values.user_id, values.delta, values.reason)
        .then(() => {
          message.success("积分调整成功");
          setAdjustmentModalOpen(false);
          adjustmentForm.resetFields();
          if (activeTab === "ledgers") loadLedgers();
        });
    });
  };

  const productColumns: ColumnsType<Product> = [
    { title: "ID", dataIndex: "id", width: 70 },
    { title: "商品名称", dataIndex: "name" },
    { title: "积分价格", dataIndex: "points_price", width: 110 },
    { title: "可兑换库存", dataIndex: "available_count", width: 120 },
    { title: "已发放数量", dataIndex: "delivered_count", width: 110 },
    {
      title: "状态",
      dataIndex: "status",
      width: 90,
      render: (status: Product["status"]) =>
        status === "ON_SALE" ? <Tag color="green">在售</Tag> : <Tag>已下架</Tag>,
    },
    {
      title: "操作",
      key: "action",
      width: 330,
      render: (_, product) => (
        <Space size="small">
          <Button type="link" onClick={() => showEditProduct(product)}>
            编辑
          </Button>
          <Button type="link" onClick={() => changeProductStatus(product)}>
            {product.status === "ON_SALE" ? "下架" : "上架"}
          </Button>
          <Button type="link" onClick={() => showImport(product)}>
            补货
          </Button>
          <Button type="link" danger onClick={() => deleteProduct(product)}>
            删除
          </Button>
        </Space>
      ),
    },
  ];

  const codeColumns: ColumnsType<Code> = [
    { title: "兑换码ID", dataIndex: "id", width: 110 },
    { title: "商品ID", dataIndex: "product_id", width: 100 },
    {
      title: "状态",
      dataIndex: "status",
      width: 100,
      render: (status: Code["status"]) =>
        status === "AVAILABLE" ? <Tag color="green">待发放</Tag> : <Tag>已发放</Tag>,
    },
    { title: "创建时间", dataIndex: "created_at" },
    { title: "交付时间", dataIndex: "delivered_at" },
    {
      title: "操作",
      width: 180,
      render: (_, code) => (
        <Space size="small">
          <Button type="link" onClick={() => reveal(code.id)}>
            查看兑换码
          </Button>
          {code.status === "AVAILABLE" && (
            <Button type="link" danger onClick={() => deleteCode(code.id)}>
              删除
            </Button>
          )}
        </Space>
      ),
    },
  ];

  const ledgerColumns: ColumnsType<Ledger> = [
    { title: "流水ID", dataIndex: "id", width: 90 },
    { title: "学员ID", dataIndex: "user_id", width: 90 },
    {
      title: "变化",
      dataIndex: "delta",
      render: (delta: number) => (
        <span style={{ color: delta > 0 ? "#389e0d" : "#cf1322" }}>
          {delta > 0 ? `+${delta}` : delta}
        </span>
      ),
    },
    { title: "变更后余额", dataIndex: "balance_after" },
    { title: "类型", dataIndex: "type" },
    { title: "原因", dataIndex: "reason" },
    { title: "操作管理员ID", dataIndex: "operator_admin_id" },
    { title: "时间", dataIndex: "created_at" },
  ];

  const redemptionColumns: ColumnsType<Redemption> = [
    { title: "兑换ID", dataIndex: "id", width: 90 },
    { title: "学员ID", dataIndex: "user_id", width: 90 },
    { title: "商品ID", dataIndex: "product_id", width: 90 },
    { title: "兑换码ID", dataIndex: "code_id", width: 100 },
    { title: "扣除积分", dataIndex: "points_cost", width: 100 },
    { title: "兑换时间", dataIndex: "created_at" },
  ];

  const pagination = (
    current: number,
    pageSize: number,
    total: number,
    onChange: (page: number, size: number) => void
  ) => ({
    current,
    pageSize,
    total,
    showSizeChanger: true,
    onChange,
  });

  return (
    <div className="playedu-main-body">
      <div className="d-flex j-b-flex mb-24">
        <h2>积分运营</h2>
        <Button type="primary" onClick={() => setAdjustmentModalOpen(true)}>
          积分调整
        </Button>
      </div>
      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        items={[
          {
            key: "products",
            label: "商品管理",
            children: (
              <>
                <div className="d-flex j-b-flex mb-24">
                  <Button type="primary" icon={<PlusOutlined />} onClick={showCreateProduct}>
                    新建商品
                  </Button>
                  <Space>
                    <Input
                      value={productNameInput}
                      onChange={(e) => setProductNameInput(e.target.value)}
                      placeholder="商品名称"
                      allowClear
                    />
                    <Select
                      value={productStatus || undefined}
                      onChange={(value) => setProductStatus(value || "")}
                      allowClear
                      placeholder="商品状态"
                      style={{ width: 120 }}
                      options={[
                        { value: "ON_SALE", label: "在售" },
                        { value: "OFF_SALE", label: "已下架" },
                      ]}
                    />
                    <Button
                      type="primary"
                      onClick={() => {
                        setProductPage(1);
                        setProductName(productNameInput);
                      }}
                    >
                      查询
                    </Button>
                  </Space>
                </div>
                <Table
                  rowKey="id"
                  columns={productColumns}
                  dataSource={products}
                  pagination={pagination(productPage, productSize, productTotal, (page, size) => {
                    setProductPage(page);
                    setProductSize(size);
                  })}
                />
              </>
            ),
          },
          {
            key: "inventory",
            label: "兑换码库存",
            children: (
              <>
                <div className="d-flex j-end-flex mb-24">
                  <Space>
                    <Select
                      value={codeProductId}
                      onChange={(value) => {
                        setCodePage(1);
                        setCodeProductId(value);
                      }}
                      allowClear
                      placeholder="商品ID"
                      style={{ width: 120 }}
                      options={products.map((product) => ({
                        value: product.id,
                        label: `${product.id} - ${product.name}`,
                      }))}
                    />
                    <Select
                      value={codeStatus || undefined}
                      onChange={(value) => {
                        setCodePage(1);
                        setCodeStatus(value || "");
                      }}
                      allowClear
                      placeholder="兑换码状态"
                      style={{ width: 120 }}
                      options={[
                        { value: "AVAILABLE", label: "待发放" },
                        { value: "DELIVERED", label: "已发放" },
                      ]}
                    />
                    <Input
                      value={codeSearchInput}
                      onChange={(e) => setCodeSearchInput(e.target.value)}
                      placeholder="兑换码精确搜索"
                      allowClear
                    />
                    <Button
                      type="primary"
                      onClick={() => {
                        setCodePage(1);
                        setCodeSearch(codeSearchInput);
                      }}
                    >
                      查询
                    </Button>
                  </Space>
                </div>
                <Table
                  rowKey="id"
                  columns={codeColumns}
                  dataSource={codes}
                  pagination={pagination(codePage, codeSize, codeTotal, (page, size) => {
                    setCodePage(page);
                    setCodeSize(size);
                  })}
                />
              </>
            ),
          },
          {
            key: "ledgers",
            label: "积分流水",
            children: (
              <>
                <div className="d-flex j-end-flex mb-24">
                  <Space>
                    <Input
                      value={ledgerUserInput}
                      onChange={(e) => setLedgerUserInput(e.target.value)}
                      placeholder="学员ID"
                      allowClear
                    />
                    <Select
                      value={ledgerType || undefined}
                      onChange={(value) => {
                        setLedgerPage(1);
                        setLedgerType(value || "");
                      }}
                      style={{ width: 150 }}
                      options={ledgerTypes.map(([value, label]) => ({ value, label }))}
                    />
                    <Input
                      value={ledgerKeyword}
                      onChange={(e) => setLedgerKeyword(e.target.value)}
                      placeholder="原因或来源键"
                      allowClear
                    />
                    <Button
                      type="primary"
                      onClick={() => {
                        setLedgerPage(1);
                        setLedgerUserId(
                          ledgerUserInput.trim() ? Number(ledgerUserInput) : undefined
                        );
                      }}
                    >
                      查询
                    </Button>
                  </Space>
                </div>
                <Table
                  rowKey="id"
                  columns={ledgerColumns}
                  dataSource={ledgers}
                  pagination={pagination(ledgerPage, ledgerSize, ledgerTotal, (page, size) => {
                    setLedgerPage(page);
                    setLedgerSize(size);
                  })}
                />
              </>
            ),
          },
          {
            key: "redemptions",
            label: "兑换记录",
            children: (
              <>
                <div className="d-flex j-end-flex mb-24">
                  <Space>
                    <Input
                      value={redemptionUserInput}
                      onChange={(e) => setRedemptionUserInput(e.target.value)}
                      placeholder="学员ID"
                      allowClear
                    />
                    <Input
                      value={redemptionProductInput}
                      onChange={(e) => setRedemptionProductInput(e.target.value)}
                      placeholder="商品ID"
                      allowClear
                    />
                    <Input
                      value={redemptionCodeInput}
                      onChange={(e) => setRedemptionCodeInput(e.target.value)}
                      placeholder="兑换码ID"
                      allowClear
                    />
                    <Button
                      type="primary"
                      onClick={() => {
                        setRedemptionPage(1);
                        setRedemptionUserId(
                          redemptionUserInput.trim()
                            ? Number(redemptionUserInput)
                            : undefined
                        );
                        setRedemptionProductId(
                          redemptionProductInput.trim()
                            ? Number(redemptionProductInput)
                            : undefined
                        );
                        setRedemptionCodeId(
                          redemptionCodeInput.trim()
                            ? Number(redemptionCodeInput)
                            : undefined
                        );
                      }}
                    >
                      查询
                    </Button>
                  </Space>
                </div>
                <Table
                  rowKey="id"
                  columns={redemptionColumns}
                  dataSource={redemptions}
                  pagination={pagination(
                    redemptionPage,
                    redemptionSize,
                    redemptionTotal,
                    (page, size) => {
                      setRedemptionPage(page);
                      setRedemptionSize(size);
                    }
                  )}
                />
              </>
            ),
          },
        ]}
      />

      <Modal
        title={editingProduct ? "编辑商品" : "新建商品"}
        open={productModalOpen}
        onCancel={() => setProductModalOpen(false)}
        onOk={submitProduct}
        okText="保存"
        cancelText="取消"
      >
        <Form form={productForm} layout="vertical">
          <Form.Item name="name" label="商品名称" rules={[{ required: true, message: "请输入商品名称" }]}>
            <Input maxLength={191} />
          </Form.Item>
          <Form.Item
            name="points_price"
            label="积分价格"
            rules={[{ required: true, message: "请输入正整数积分价格" }]}
          >
            <InputNumber min={1} precision={0} style={{ width: "100%" }} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`补货${importProduct ? `：${importProduct.name}` : ""}`}
        open={!!importProduct}
        onCancel={() => setImportProduct(null)}
        onOk={submitImport}
        confirmLoading={importing}
        okText="导入"
        cancelText="关闭"
      >
        <Input.TextArea
          value={codeText}
          onChange={(e) => setCodeText(e.target.value)}
          autoSize={{ minRows: 8, maxRows: 16 }}
          placeholder="每行粘贴一个兑换码，空行会被忽略"
        />
        {importReport && (
          <div style={{ marginTop: 16 }}>
            本次新增 <b>{importReport.new_count}</b> 个，重复跳过 <b>{importReport.duplicate_count}</b> 个。
          </div>
        )}
      </Modal>

      <Modal
        title="查看兑换码"
        open={revealedCode !== null}
        onCancel={() => setRevealedCode(null)}
        footer={null}
      >
        <p>兑换码仅在本次查看中显示，请按需复制。</p>
        <Input value={revealedCode || ""} readOnly />
      </Modal>

      <Modal
        title="人工调整积分"
        open={adjustmentModalOpen}
        onCancel={() => setAdjustmentModalOpen(false)}
        onOk={submitAdjustment}
        okText="提交调整"
        cancelText="取消"
      >
        <Form form={adjustmentForm} layout="vertical">
          <Form.Item
            name="user_id"
            label="学员ID"
            rules={[{ required: true, message: "请输入学员ID" }]}
          >
            <InputNumber min={1} precision={0} style={{ width: "100%" }} />
          </Form.Item>
          <Form.Item
            name="delta"
            label="调整值"
            rules={[{ required: true, message: "请输入非零调整值" }]}
            extra="正数为增加，负数为扣减；扣减允许余额变为负数。"
          >
            <InputNumber precision={0} style={{ width: "100%" }} />
          </Form.Item>
          <Form.Item
            name="reason"
            label="调整原因"
            rules={[{ required: true, whitespace: true, message: "请填写调整原因" }]}
          >
            <Input.TextArea maxLength={500} rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default PointsAdminPage;
