import { useCallback, useEffect, useState, type FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import { api } from "../api/client";
import type { PcConfigRequest, PcConfigResponse } from "../api/types";
import BuildBaseForm, {
  baseFormToRequest,
  inferBaseFormFromConfig,
  type BaseFormState,
} from "../components/BuildBaseForm";
import { RamEditor, StorageEditor } from "../components/RamStorageEditors";
import { useAuth } from "../context/AuthContext";
import { useCatalog } from "../hooks/useCatalog";

export default function BuildEditPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const { data: catalog, loading: catLoading, error: catErr } = useCatalog();
  const [config, setConfig] = useState<PcConfigResponse | null>(null);
  const [base, setBase] = useState<BaseFormState | null>(null);
  const [err, setErr] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const refresh = useCallback(async () => {
    if (!id) return;
    const c = await api.pcConfiguration.get(Number(id));
    setConfig(c);
    return c;
  }, [id]);

  useEffect(() => {
    if (!id) return;
    let cancelled = false;
    void (async () => {
      setErr(null);
      try {
        const c = await api.pcConfiguration.get(Number(id));
        if (cancelled) return;
        setConfig(c);
      } catch (e) {
        if (!cancelled) setErr(e instanceof Error ? e.message : "Ошибка загрузки");
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [id]);

  useEffect(() => {
    if (config && catalog) {
      setBase(inferBaseFormFromConfig(catalog, config));
    }
  }, [config, catalog]);

  async function saveBase(e: FormEvent) {
    e.preventDefault();
    if (!id || !base) return;
    const full = baseFormToRequest(base, user?.id ?? 0);
    if (!full) {
      setErr("Заполните все поля базовой конфигурации");
      return;
    }
    const patch: Partial<PcConfigRequest> = {
      name: full.name,
      isPrivate: full.isPrivate,
      motherboardId: full.motherboardId,
      pcCaseId: full.pcCaseId,
      powerUnitId: full.powerUnitId,
      processorId: full.processorId,
      processorCoolingId: full.processorCoolingId,
      videoCardId: full.videoCardId,
    };
    setBusy(true);
    setErr(null);
    setOk(null);
    try {
      await api.pcConfiguration.update(Number(id), patch);
      await refresh();
      setOk("Изменения базовой конфигурации сохранены.");
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Ошибка сохранения");
    } finally {
      setBusy(false);
    }
  }

  if (catLoading || !catalog) {
    return (
      <div className="card">
        <p>{catErr ?? "Загрузка…"}</p>
      </div>
    );
  }

  if (err && !config) return <div className="error-banner">{err}</div>;
  if (!config || !base) return <p className="muted">Загрузка…</p>;

  if (config.userName !== user?.nickname) {
    return (
      <div className="card">
        <p>Нет доступа к этой сборке.</p>
        <Link to="/my-builds">Мои сборки</Link>
      </div>
    );
  }

  return (
    <div>
      <div className="btn-row" style={{ marginBottom: "1rem" }}>
        <Link to={`/builds/${config.id}`} className="btn btn-ghost">
          ← К просмотру
        </Link>
      </div>
      <h1>Изменение: {config.name}</h1>
      {err && <div className="error-banner">{err}</div>}
      {ok && <div className="success-banner">{ok}</div>}

      <form className="card" onSubmit={(e) => void saveBase(e)}>
        <h2>Базовая конфигурация</h2>
        <BuildBaseForm catalog={catalog} value={base} onChange={setBase} />
        <div className="btn-row">
          <button type="submit" className="btn btn-primary" disabled={busy}>
            Сохранить компоненты
          </button>
        </div>
      </form>

      <div className="card">
        <h2>ОЗУ</h2>
        <RamEditor
          pcConfigurationId={config.id}
          catalog={catalog}
          items={config.ramModules ?? []}
          onChanged={() => void refresh()}
        />
      </div>

      <div className="card">
        <h2>Накопители</h2>
        <StorageEditor
          pcConfigurationId={config.id}
          catalog={catalog}
          items={config.storageDevices ?? []}
          onChanged={() => void refresh()}
        />
      </div>
    </div>
  );
}
