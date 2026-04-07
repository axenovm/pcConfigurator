import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api/client";
import type { PcConfigResponse } from "../api/types";
import BuildBaseForm, {
  baseFormToRequest,
  emptyBaseForm,
  type BaseFormState,
} from "../components/BuildBaseForm";
import { RamEditor, StorageEditor } from "../components/RamStorageEditors";
import { useAuth } from "../context/AuthContext";
import { useCatalog } from "../hooks/useCatalog";

export default function NewBuildPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { data: catalog, loading: catLoading, error: catErr } = useCatalog();
  const [step, setStep] = useState<1 | 2 | 3>(1);
  const [base, setBase] = useState<BaseFormState>(() => emptyBaseForm());
  const [pcId, setPcId] = useState<number | null>(null);
  const [config, setConfig] = useState<PcConfigResponse | null>(null);
  const [err, setErr] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function refreshConfig(id: number) {
    const c = await api.pcConfiguration.get(id);
    setConfig(c);
  }

  async function submitBase(e: FormEvent) {
    e.preventDefault();
    setErr(null);
    setOk(null);
    if (!user) return;
    const body = baseFormToRequest(base, user.id);
    if (!body) {
      setErr("Заполните все поля базовой конфигурации");
      return;
    }
    setBusy(true);
    try {
      const created = await api.pcConfiguration.create(body);
      setPcId(created.id);
      setConfig(created);
      setOk(`Сборка «${created.name}» создана. Добавьте ОЗУ или перейдите дальше.`);
      setStep(2);
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Ошибка создания");
    } finally {
      setBusy(false);
    }
  }

  if (catLoading || !catalog) {
    return (
      <div className="card">
        <p>{catErr ?? "Загрузка каталога…"}</p>
      </div>
    );
  }

  return (
    <div>
      <h1>Новая сборка</h1>
      <p className="muted">
        Шаг 1 — основные компоненты. Шаг 2 — ОЗУ. Шаг 3 — накопители. Шаги 2–3 можно пропустить.
      </p>
      {err && <div className="error-banner">{err}</div>}
      {ok && <div className="success-banner">{ok}</div>}

      {step === 1 && (
        <form className="card" onSubmit={(e) => void submitBase(e)}>
          <span className="step-badge">Шаг 1 из 3</span>
          <h2>Базовая конфигурация</h2>
          <BuildBaseForm catalog={catalog} value={base} onChange={setBase} />
          <div className="btn-row">
            <button type="submit" className="btn btn-primary" disabled={busy}>
              Создать и перейти к ОЗУ
            </button>
            <Link to="/my-builds" className="btn btn-ghost">
              Отмена
            </Link>
          </div>
        </form>
      )}

      {step === 2 && pcId !== null && config && (
        <div className="card">
          <span className="step-badge">Шаг 2 из 3</span>
          <h2>Оперативная память</h2>
          <RamEditor
            pcConfigurationId={pcId}
            catalog={catalog}
            items={config.ramModules ?? []}
            onChanged={() => void refreshConfig(pcId)}
          />
          <div className="btn-row">
            <button
              type="button"
              className="btn btn-primary"
              onClick={() => {
                void refreshConfig(pcId).then(() => setStep(3));
              }}
            >
              Далее: накопители
            </button>
            <button
              type="button"
              className="btn btn-ghost"
              onClick={() => {
                void refreshConfig(pcId).then(() => setStep(3));
              }}
            >
              Пропустить
            </button>
          </div>
        </div>
      )}

      {step === 3 && pcId !== null && config && (
        <div className="card">
          <span className="step-badge">Шаг 3 из 3</span>
          <h2>Накопители</h2>
          <StorageEditor
            pcConfigurationId={pcId}
            catalog={catalog}
            items={config.storageDevices ?? []}
            onChanged={() => void refreshConfig(pcId)}
          />
          <div className="btn-row">
            <button
              type="button"
              className="btn btn-primary"
              onClick={() => navigate(`/builds/${pcId}`)}
            >
              Готово, открыть сборку
            </button>
            <Link to="/my-builds" className="btn btn-ghost">
              Мои сборки
            </Link>
          </div>
        </div>
      )}
    </div>
  );
}
