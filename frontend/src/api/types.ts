export type AuthResponse = {
  token: string;
  id: number;
  nickname: string;
  login: string;
  description: string | null;
};

export type PcConfigRequest = {
  name: string;
  isPrivate: boolean;
  motherboardId: number;
  pcCaseId: number;
  powerUnitId: number;
  processorId: number;
  processorCoolingId: number;
  userId: number;
  videoCardId: number;
};

export type PcConfigRamRequest = {
  ramQuantity: number;
  pcConfigurationId: number;
  ramModuleId: number;
};

export type PcConfigStorageRequest = {
  quantity: number;
  pcConfigurationId: number;
  storageDeviceId: number;
};

export type PcConfigRamResponse = {
  id: number;
  price: string;
  quantity: number;
  pcConfigurationId: number;
  moduleName: string;
};

export type PcConfigStorageResponse = {
  id: number;
  price: string;
  quantity: number;
  pcConfigurationId: number;
  storageModel: string;
  storageCapacity: number;
};

export type PcConfigResponse = {
  id: number;
  name: string;
  isPrivate: boolean;
  totalPrice: string;
  motherboardModel: string;
  pcCaseModel: string;
  powerUnitModel: string;
  processorModel: string;
  processorCoolingModel: string;
  userName: string;
  videoCardModel: string;
  ramModules?: PcConfigRamResponse[];
  storageDevices?: PcConfigStorageResponse[];
};

export type ReviewResponse = {
  userId: number;
  authorNickname: string;
  pcId: number;
  rating: number;
  comment: string;
  createdAt: string;
};

export type UserActivityResponse = {
  actionType: string;
  activityInfo: string;
  timestamp: string;
};

export type PcBuildAssessmentResponse = {
  pcConfigurationId: number;
  buildName: string;
  totalPrice: string;
  createdAtAssessment: string;
  performanceScore: number;
  bottleneckAnalysis: string;
  totalTdp: number;
  performanceLevel: string;
  recommendations: string[];
};
