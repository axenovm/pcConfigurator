import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api/client";
import type { PcConfigResponse } from "../api/types";
import { useAuth } from "../context/AuthContext";
import { formatPrice } from "../util/money";

export default function DashboardPage() {
  const { user } = useAuth();
  const [list, setList] = useState<PcConfigResponse[]>([]);
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    setErr(null);
    try {
      const all = await api.pcConfiguration.list();
      const mine = all.filter((b) => b.userName === user?.nickname);
      setList(mine);
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Не удалось загрузить сборки");
    } finally {
      setLoading(false);
    }
  }, [user?.nickname]);

  useEffect(() => {
    void load();
  }, [load]);

  return (
    <div>
      <h1>Мои сборки</h1>
      <p className="muted">
        Сначала создаётся базовая конфигурация ПК, затем добавляются модули ОЗУ и накопители.
      </p>
      <div className="btn-row" style={{ marginBottom: "1.5rem" }}>
        <Link to="/" className="btn btn-ghost">
          Каталог всех сборок
        </Link>
        <Link to="/profile" className="btn btn-ghost">
          Профиль
        </Link>
        <Link to="/builds/new" className="btn btn-primary">
          Новая сборка
        </Link>
      </div>
      {err && <div className="error-banner">{err}</div>}
      {loading ? (
        <p className="muted">Загрузка…</p>
      ) : list.length === 0 ? (
        <div className="card">
          <p>У вас пока нет сборок. Создайте первую по кнопке выше.</p>
        </div>
      ) : (
        <div className="build-list">
          {list.map((b) => (
            <div key={b.id} className="build-row">
              <div>
                <strong>{b.name}</strong>
                <span className="muted" style={{ marginLeft: "0.5rem" }}>
                  {formatPrice(b.totalPrice)}
                </span>
                <div className="muted" style={{ fontSize: "0.85rem", marginTop: "0.25rem" }}>
                  {b.processorModel} · {b.videoCardModel}
                </div>
              </div>
              <div className="btn-row" style={{ marginTop: 0 }}>
                <Link to={`/builds/${b.id}`} className="btn btn-ghost">
                  Открыть
                </Link>
                <Link to={`/builds/${b.id}/edit`} className="btn btn-ghost">
                  Изменить
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
