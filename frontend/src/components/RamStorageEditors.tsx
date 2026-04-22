import { useState } from "react";
import { api } from "../api/client";
import type { PcConfigRamResponse, PcConfigStorageResponse } from "../api/types";
import type { CatalogData } from "../hooks/useCatalog";
import { formatPrice } from "../util/money";

const MAX_STORAGE_DEVICES_PER_BUILD = 6;

type RamProps = {
  pcConfigurationId: number;
  catalog: CatalogData;
  items: PcConfigRamResponse[];
  onChanged: () => void;
};

export function RamEditor({ pcConfigurationId, catalog, items, onChanged }: RamProps) {
  const [ramModuleId, setRamModuleId] = useState("");
  const [qty, setQty] = useState("2");
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);

  async function add() {
    setErr(null);
    setOk(null);
    if (!ramModuleId || !qty) {
      setErr("Выберите модуль ОЗУ и количество");
      return;
    }
    setBusy(true);
    try {
      await api.pcConfigRam.create({
        pcConfigurationId,
        ramModuleId: Number(ramModuleId),
        ramQuantity: Number(qty),
      });
      setRamModuleId("");
      setOk("ОЗУ добавлено.");
      onChanged();
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Ошибка");
    } finally {
      setBusy(false);
    }
  }

  async function remove(id: number) {
    if (!confirm("Удалить эту позицию ОЗУ?")) return;
    setBusy(true);
    setErr(null);
    setOk(null);
    try {
      await api.pcConfigRam.delete(id);
      setOk("Позиция ОЗУ удалена.");
      onChanged();
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Ошибка");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div>
      {err && <div className="error-banner">{err}</div>}
      {ok && <div className="success-banner">{ok}</div>}
      <div className="form-grid cols-2" style={{ marginBottom: "1rem" }}>
        <label>
          Модуль RAM
          <select value={ramModuleId} onChange={(e) => setRamModuleId(e.target.value)}>
            <option value="">—</option>
            {catalog.ramModules.map((r) => (
              <option key={r.id} value={r.id}>
                {r.modelMemory} {r.capacityGb} ГБ {r.typeMemory} — {r.price}
              </option>
            ))}
          </select>
        </label>
        <label>
          Количество модулей
          <input
            type="number"
            min={1}
            value={qty}
            onChange={(e) => setQty(e.target.value)}
          />
        </label>
      </div>
      <button type="button" className="btn btn-primary" disabled={busy} onClick={() => void add()}>
        Добавить ОЗУ
      </button>
      <ul style={{ marginTop: "1rem", paddingLeft: "1.2rem" }}>
        {items.map((r) => (
          <li key={r.id} style={{ marginBottom: "0.5rem" }}>
            {r.moduleName} × {r.quantity} — {formatPrice(r.price)}{" "}
            <button
              type="button"
              className="btn btn-ghost"
              style={{ marginLeft: "0.5rem", padding: "0.2rem 0.5rem", fontSize: "0.8rem" }}
              disabled={busy}
              onClick={() => void remove(r.id)}
            >
              Удалить
            </button>
          </li>
        ))}
        {items.length === 0 && <li className="muted">Пока нет модулей ОЗУ</li>}
      </ul>
    </div>
  );
}

type StProps = {
  pcConfigurationId: number;
  catalog: CatalogData;
  items: PcConfigStorageResponse[];
  onChanged: () => void;
};

export function StorageEditor({ pcConfigurationId, catalog, items, onChanged }: StProps) {
  const [storageId, setStorageId] = useState("");
  const [qty, setQty] = useState("1");
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);

  async function add() {
    setErr(null);
    setOk(null);
    if (!storageId || !qty) {
      setErr("Выберите накопитель и количество");
      return;
    }
    const requestedQty = Number(qty);
    const existingCount = items.reduce((sum, item) => sum + item.quantity, 0);
    if (existingCount + requestedQty > MAX_STORAGE_DEVICES_PER_BUILD) {
      setErr(`Максимум ${MAX_STORAGE_DEVICES_PER_BUILD} накопителей в одной сборке`);
      return;
    }
    setBusy(true);
    try {
      await api.pcConfigStorage.create({
        pcConfigurationId,
        storageDeviceId: Number(storageId),
        quantity: requestedQty,
      });
      setStorageId("");
      setOk("Накопитель добавлен.");
      onChanged();
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Ошибка");
    } finally {
      setBusy(false);
    }
  }

  async function remove(id: number) {
    if (!confirm("Удалить этот накопитель из сборки?")) return;
    setBusy(true);
    setErr(null);
    setOk(null);
    try {
      await api.pcConfigStorage.delete(id);
      setOk("Накопитель убран из сборки.");
      onChanged();
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Ошибка");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div>
      {err && <div className="error-banner">{err}</div>}
      {ok && <div className="success-banner">{ok}</div>}
      <div className="form-grid cols-2" style={{ marginBottom: "1rem" }}>
        <label>
          Накопитель
          <select value={storageId} onChange={(e) => setStorageId(e.target.value)}>
            <option value="">—</option>
            {catalog.storage.map((s) => (
              <option key={s.id} value={s.id}>
                {s.model} {s.hddCapacity} ГБ — {s.price}
              </option>
            ))}
          </select>
        </label>
        <label>
          Количество
          <input
            type="number"
            min={1}
            value={qty}
            onChange={(e) => setQty(e.target.value)}
          />
        </label>
      </div>
      <button type="button" className="btn btn-primary" disabled={busy} onClick={() => void add()}>
        Добавить накопитель
      </button>
      <p className="muted" style={{ marginTop: "0.6rem" }}>
        Лимит: до {MAX_STORAGE_DEVICES_PER_BUILD} накопителей на одну сборку.
      </p>
      <ul style={{ marginTop: "1rem", paddingLeft: "1.2rem" }}>
        {items.map((s) => (
          <li key={s.id} style={{ marginBottom: "0.5rem" }}>
            {s.storageModel} × {s.quantity} ({s.storageCapacity} ГБ) — {formatPrice(s.price)}{" "}
            <button
              type="button"
              className="btn btn-ghost"
              style={{ marginLeft: "0.5rem", padding: "0.2rem 0.5rem", fontSize: "0.8rem" }}
              disabled={busy}
              onClick={() => void remove(s.id)}
            >
              Удалить
            </button>
          </li>
        ))}
        {items.length === 0 && <li className="muted">Пока нет накопителей</li>}
      </ul>
    </div>
  );
}
