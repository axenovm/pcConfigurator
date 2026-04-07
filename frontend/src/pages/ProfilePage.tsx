import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api/client";
import type { UserActivityResponse } from "../api/types";
import { useAuth } from "../context/AuthContext";

const ACTION_LABELS: Record<string, string> = {
  BUILD_CREATE: "Создание сборки",
  BUILD_UPDATE: "Изменение сборки",
  BUILD_DELETE: "Удаление сборки",
  ADD_RAM: "Добавление ОЗУ",
  ADD_STORAGE: "Добавление накопителя",
};

export default function ProfilePage() {
  const { user } = useAuth();
  const [items, setItems] = useState<UserActivityResponse[]>([]);
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    setErr(null);
    try {
      const data = await api.userActivity.list(user.id);
      setItems(data);
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Не удалось загрузить активность");
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    void load();
  }, [load]);

  if (!user) return null;

  return (
    <div>
      <div className="btn-row" style={{ marginBottom: "1rem" }}>
        <Link to="/" className="btn btn-ghost">
          ← Каталог
        </Link>
        <Link to="/my-builds" className="btn btn-ghost">
          Мои сборки
        </Link>
      </div>
      <h1>Профиль</h1>
      <div className="card" style={{ marginBottom: "1rem" }}>
        <p style={{ margin: 0 }}>
          <strong>{user.nickname}</strong>
        </p>
        <p className="muted" style={{ margin: "0.35rem 0 0" }}>
          Логин: {user.login}
        </p>
        {user.description ? (
          <p style={{ marginTop: "0.75rem" }}>{user.description}</p>
        ) : null}
      </div>
      <h2>Журнал активности</h2>
      <p className="muted">Действия в конфигураторе привязаны к вашему аккаунту.</p>
      {err && <div className="error-banner">{err}</div>}
      {loading ? (
        <p className="muted">Загрузка…</p>
      ) : items.length === 0 ? (
        <div className="card">
          <p className="muted">Записей пока нет.</p>
        </div>
      ) : (
        <ul style={{ listStyle: "none", padding: 0, margin: 0 }}>
          {items.map((a, i) => (
            <li
              key={`${a.timestamp}-${i}`}
              className="card"
              style={{ marginBottom: "0.65rem", padding: "0.85rem 1rem" }}
            >
              <div style={{ display: "flex", flexWrap: "wrap", gap: "0.5rem", alignItems: "center" }}>
                <span className="tag">{ACTION_LABELS[a.actionType] ?? a.actionType}</span>
                <span className="muted" style={{ fontSize: "0.85rem" }}>
                  {new Date(a.timestamp).toLocaleString("ru-RU")}
                </span>
              </div>
              {a.activityInfo ? (
                <p style={{ margin: "0.5rem 0 0", fontSize: "0.9rem" }}>{a.activityInfo}</p>
              ) : null}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
