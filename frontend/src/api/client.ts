import type {
  AuthResponse,
  PcBuildAssessmentResponse,
  PcConfigRamRequest,
  PcConfigRamResponse,
  PcConfigRequest,
  PcConfigResponse,
  PcConfigStorageRequest,
  PcConfigStorageResponse,
  ReviewResponse,
  UserActivityResponse,
} from "./types";

const TOKEN_KEY = "pc_config_jwt";

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string | null) {
  if (token) localStorage.setItem(TOKEN_KEY, token);
  else localStorage.removeItem(TOKEN_KEY);
}

async function request<T>(
  path: string,
  options: RequestInit & { auth?: boolean } = {}
): Promise<T> {
  const { auth = true, headers: hdrs, ...rest } = options;
  const headers = new Headers(hdrs);
  if (rest.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  if (auth) {
    const t = getToken();
    if (t) headers.set("Authorization", `Bearer ${t}`);
  }
  const res = await fetch(path, { ...rest, headers });
  if (!res.ok) {
    let msg = res.statusText;
    try {
      const j = await res.json();
      if (j && typeof j === "object") {
        if (typeof j.message === "string") msg = j.message;
        else if (typeof j.error === "string") msg = j.error;
      } else if (typeof j === "string") msg = j;
    } catch {
      /* ignore */
    }
    throw new Error(msg || `HTTP ${res.status}`);
  }
  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

export const api = {
  auth: {
    register: (body: {
      nickname: string;
      login: string;
      password: string;
    }) => request<AuthResponse>("/api/auth/register", { method: "POST", body: JSON.stringify(body), auth: false }),
    login: (body: { login: string; password: string }) =>
      request<AuthResponse>("/api/auth/login", { method: "POST", body: JSON.stringify(body), auth: false }),
  },
  pcConfiguration: {
    list: () => request<PcConfigResponse[]>("/pc-configuration"),
    get: (id: number) => request<PcConfigResponse>(`/pc-configuration/${id}`),
    create: (body: PcConfigRequest) =>
      request<PcConfigResponse>("/pc-configuration", { method: "POST", body: JSON.stringify(body) }),
    update: (id: number, body: Partial<PcConfigRequest>) =>
      request<PcConfigResponse>(`/pc-configuration/${id}/up`, { method: "PATCH", body: JSON.stringify(body) }),
    delete: (id: number) => request<PcConfigResponse>(`/pc-configuration/${id}/del`, { method: "DELETE" }),
  },
  pcConfigRam: {
    create: (body: PcConfigRamRequest) =>
      request<PcConfigRamResponse>("/pc-config-ram", { method: "POST", body: JSON.stringify(body) }),
    update: (id: number, body: Partial<PcConfigRamRequest>) =>
      request<PcConfigRamResponse>(`/pc-config-ram/${id}/up`, { method: "PATCH", body: JSON.stringify(body) }),
    delete: (id: number) => request<PcConfigRamResponse>(`/pc-config-ram/${id}/del`, { method: "DELETE" }),
  },
  pcConfigStorage: {
    create: (body: PcConfigStorageRequest) =>
      request<PcConfigStorageResponse>("/pc-config-storage", { method: "POST", body: JSON.stringify(body) }),
    update: (id: number, body: Partial<PcConfigStorageRequest>) =>
      request<PcConfigStorageResponse>(`/pc-config-storage/${id}/up`, { method: "PATCH", body: JSON.stringify(body) }),
    delete: (id: number) =>
      request<PcConfigStorageResponse>(`/pc-config-storage/${id}/del`, { method: "DELETE" }),
  },
  assessment: (pcId: number) =>
    request<PcBuildAssessmentResponse>(`/assessment/${pcId}`),
  review: {
    list: (pcId: number) =>
      request<ReviewResponse[]>(`/review/${pcId}`, { auth: false }),
    add: (body: { pcId: number; rating: number; comment: string }) =>
      request<ReviewResponse>("/review/add", { method: "POST", body: JSON.stringify(body) }),
  },
  userActivity: {
    list: (userId: number) =>
      request<UserActivityResponse[]>(`/user-activity/${userId}`),
  },
  catalog: {
    processors: () => request<Array<{ id: number; model: string; price: string; socket: string }>>("/processor"),
    motherboards: () =>
      request<Array<{ id: number; model: string; price: string; socket: string; formFactor: string }>>("/motherboard"),
    videoCards: () =>
      request<Array<{ id: number; model: string; price: string; graphicsProcessor: string }>>("/videocard"),
    pcCases: () => request<Array<{ id: number; model: string; price: string; formFactor: string }>>("/pc-case"),
    powerUnits: () => request<Array<{ id: number; model: string; price: string; power: number }>>("/power-unit"),
    coolings: () =>
      request<Array<{ id: number; modelCooling: string; price: string }>>("/processor-cooling"),
    ramModules: () =>
      request<
        Array<{
          id: number;
          modelMemory: string;
          price: string;
          capacityGb: number;
          clockFrequency: number;
          typeMemory: string;
        }>
      >("/ram-module"),
    storage: () =>
      request<Array<{ id: number; model: string; price: string; hddCapacity: number }>>("/storage-device"),
  },
};
