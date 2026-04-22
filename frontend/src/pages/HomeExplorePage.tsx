import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api/client";
import type { PcConfigResponse } from "../api/types";
import { formatPrice } from "../util/money";

export default function HomeExplorePage() {
  const [list, setList] = useState<PcConfigResponse[]>([]);
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    setErr(null);
    try {
      const all = await api.pcConfiguration.listPublic();
      setList(all);
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Не удалось загрузить сборки");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  return (
    <div>
      <h1>Все сборки</h1>
      <p className="muted">
        Конфигурации пользователей: откройте карточку, чтобы посмотреть состав и отзывы.
      </p>
      {err && <div className="error-banner">{err}</div>}
      {loading ? (
        <p className="muted">Загрузка…</p>
      ) : list.length === 0 ? (
        <div className="card">
          <p>Пока нет ни одной публичной сборки.</p>
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
                  Автор: {b.userName} · {b.processorModel} · {b.videoCardModel}
                </div>
              </div>
              <Link to={`/builds/${b.id}`} className="btn btn-primary">
                Открыть
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
