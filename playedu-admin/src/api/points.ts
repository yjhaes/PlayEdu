import client from "./internal/httpClient";

export function productList(
  page: number,
  size: number,
  name: string,
  status?: string
) {
  return client.get("/backend/v1/points/products/index", {
    page,
    size,
    name,
    status,
  });
}

export function createProduct(name: string, pointsPrice: number) {
  return client.post("/backend/v1/points/products/create", {
    name,
    points_price: pointsPrice,
  });
}

export function updateProduct(id: number, name: string, pointsPrice: number) {
  return client.put(`/backend/v1/points/products/${id}`, {
    name,
    points_price: pointsPrice,
  });
}

export function setProductStatus(id: number, status: "ON_SALE" | "OFF_SALE") {
  return client.put(`/backend/v1/points/products/${id}/status`, { status });
}

export function deleteProduct(id: number) {
  return client.destroy(`/backend/v1/points/products/${id}`);
}

export function importCodes(productId: number, codes: string) {
  return client.post(`/backend/v1/points/products/${productId}/codes/import`, {
    codes,
  });
}

export function codeList(
  page: number,
  size: number,
  productId?: number,
  status?: string,
  code?: string
) {
  return client.get("/backend/v1/points/codes/index", {
    page,
    size,
    product_id: productId,
    status,
    code,
  });
}

export function revealCode(id: number) {
  return client.get(`/backend/v1/points/codes/${id}`, {});
}

export function deleteCode(id: number) {
  return client.destroy(`/backend/v1/points/codes/${id}`);
}

export function adjustPoints(
  userId: number,
  delta: number,
  reason: string,
  requestKey?: string
) {
  return client.post("/backend/v1/points/adjust", {
    user_id: userId,
    delta,
    reason,
    request_key: requestKey,
  });
}

export function ledgerList(
  page: number,
  size: number,
  userId?: number,
  type?: string,
  keyword?: string
) {
  return client.get("/backend/v1/points/ledgers/index", {
    page,
    size,
    user_id: userId,
    type,
    keyword,
  });
}

export function redemptionList(
  page: number,
  size: number,
  userId?: number,
  productId?: number,
  codeId?: number
) {
  return client.get("/backend/v1/points/redemptions/index", {
    page,
    size,
    user_id: userId,
    product_id: productId,
    code_id: codeId,
  });
}
