import { useState, type FormEvent } from "react";
import { Link, Navigate } from "react-router-dom";
import { api } from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const { token, login } = useAuth();
  const [loginField, setLoginField] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  if (token) return <Navigate to="/my-builds" replace />;

  async function submit(e: FormEvent) {
    e.preventDefault();
    setErr(null);
    setBusy(true);
    try {
      const res = await api.auth.login({ login: loginField, password });
      login(res);
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Ошибка входа");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="card" style={{ maxWidth: 420, margin: "0 auto" }}>
      <h1>Вход</h1>
      <p className="muted">Используйте логин и пароль от аккаунта.</p>
      {err && <div className="error-banner">{err}</div>}
      <form onSubmit={(e) => void submit(e)}>
        <div className="form-grid">
          <label>
            Логин
            <input
              value={loginField}
              onChange={(e) => setLoginField(e.target.value)}
              autoComplete="username"
            />
          </label>
          <label>
            Пароль
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
            />
          </label>
        </div>
        <div className="btn-row">
          <button type="submit" className="btn btn-primary" disabled={busy}>
            Войти
          </button>
          <Link to="/register" className="btn btn-ghost">
            Регистрация
          </Link>
        </div>
      </form>
    </div>
  );
}
