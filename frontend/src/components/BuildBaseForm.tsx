import type { CatalogData } from "../hooks/useCatalog";

export type BaseFormState = {
  name: string;
  motherboardId: string;
  pcCaseId: string;
  powerUnitId: string;
  processorId: string;
  processorCoolingId: string;
  videoCardId: string;
};

type Props = {
  catalog: CatalogData;
  value: BaseFormState;
  onChange: (next: BaseFormState) => void;
};

export default function BuildBaseForm({ catalog, value, onChange }: Props) {
  const set = (patch: Partial<BaseFormState>) => onChange({ ...value, ...patch });

  return (
    <div className="form-grid cols-2">
      <label style={{ gridColumn: "1 / -1" }}>
        Название сборки
        <input
          value={value.name}
          onChange={(e) => set({ name: e.target.value })}
          placeholder="Например, Игровой ПК 2026"
        />
      </label>
      <label>
        Процессор
        <select
          value={value.processorId}
          onChange={(e) => set({ processorId: e.target.value })}
        >
          <option value="">—</option>
          {catalog.processors.map((p) => (
            <option key={p.id} value={p.id}>
              {p.model} ({p.socket}) — {p.price}
            </option>
          ))}
        </select>
      </label>
      <label>
        Материнская плата
        <select
          value={value.motherboardId}
          onChange={(e) => set({ motherboardId: e.target.value })}
        >
          <option value="">—</option>
          {catalog.motherboards.map((m) => (
            <option key={m.id} value={m.id}>
              {m.model} ({m.socket}, {m.formFactor}) — {m.price}
            </option>
          ))}
        </select>
      </label>
      <label>
        Видеокарта
        <select
          value={value.videoCardId}
          onChange={(e) => set({ videoCardId: e.target.value })}
        >
          <option value="">—</option>
          {catalog.videoCards.map((v) => (
            <option key={v.id} value={v.id}>
              {v.model} — {v.price}
            </option>
          ))}
        </select>
      </label>
      <label>
        Корпус
        <select value={value.pcCaseId} onChange={(e) => set({ pcCaseId: e.target.value })}>
          <option value="">—</option>
          {catalog.pcCases.map((c) => (
            <option key={c.id} value={c.id}>
              {c.model} ({c.formFactor}) — {c.price}
            </option>
          ))}
        </select>
      </label>
      <label>
        Блок питания
        <select
          value={value.powerUnitId}
          onChange={(e) => set({ powerUnitId: e.target.value })}
        >
          <option value="">—</option>
          {catalog.powerUnits.map((u) => (
            <option key={u.id} value={u.id}>
              {u.model} ({u.power} W) — {u.price}
            </option>
          ))}
        </select>
      </label>
      <label>
        Охлаждение CPU
        <select
          value={value.processorCoolingId}
          onChange={(e) => set({ processorCoolingId: e.target.value })}
        >
          <option value="">—</option>
          {catalog.coolings.map((c) => (
            <option key={c.id} value={c.id}>
              {c.modelCooling} — {c.price}
            </option>
          ))}
        </select>
      </label>
    </div>
  );
}

export function emptyBaseForm(): BaseFormState {
  return {
    name: "",
    motherboardId: "",
    pcCaseId: "",
    powerUnitId: "",
    processorId: "",
    processorCoolingId: "",
    videoCardId: "",
  };
}

export function baseFormToRequest(
  f: BaseFormState,
  userId: number
): import("../api/types").PcConfigRequest | null {
  if (
    !f.name.trim() ||
    !f.motherboardId ||
    !f.pcCaseId ||
    !f.powerUnitId ||
    !f.processorId ||
    !f.processorCoolingId ||
    !f.videoCardId
  ) {
    return null;
  }
  return {
    name: f.name.trim(),
    motherboardId: Number(f.motherboardId),
    pcCaseId: Number(f.pcCaseId),
    powerUnitId: Number(f.powerUnitId),
    processorId: Number(f.processorId),
    processorCoolingId: Number(f.processorCoolingId),
    userId,
    videoCardId: Number(f.videoCardId),
  };
}

export function inferBaseFormFromConfig(
  catalog: CatalogData,
  c: import("../api/types").PcConfigResponse
): BaseFormState {
  const proc = catalog.processors.find((x) => x.model === c.processorModel);
  const mb = catalog.motherboards.find((x) => x.model === c.motherboardModel);
  const gpu = catalog.videoCards.find((x) => x.model === c.videoCardModel);
  const pc = catalog.pcCases.find((x) => x.model === c.pcCaseModel);
  const psu = catalog.powerUnits.find((x) => x.model === c.powerUnitModel);
  const cool = catalog.coolings.find((x) => x.modelCooling === c.processorCoolingModel);
  return {
    name: c.name,
    motherboardId: mb ? String(mb.id) : "",
    pcCaseId: pc ? String(pc.id) : "",
    powerUnitId: psu ? String(psu.id) : "",
    processorId: proc ? String(proc.id) : "",
    processorCoolingId: cool ? String(cool.id) : "",
    videoCardId: gpu ? String(gpu.id) : "",
  };
}
