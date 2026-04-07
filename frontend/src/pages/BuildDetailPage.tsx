import { useCallback, useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { api } from "../api/client";
import type { PcBuildAssessmentResponse, PcConfigResponse, ReviewResponse } from "../api/types";
import { useAuth } from "../context/AuthContext";
import { formatPrice } from "../util/money";

export default function BuildDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, token } = useAuth();
  const [config, setConfig] = useState<PcConfigResponse | null>(null);
  const [reviews, setReviews] = useState<ReviewResponse[]>([]);
  const [assessment, setAssessment] = useState<PcBuildAssessmentResponse | null>(null);
  const [assessErr, setAssessErr] = useState<string | null>(null);
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [assessBusy, setAssessBusy] = useState(false);
  const [reviewRating, setReviewRating] = useState("5");
  const [reviewComment, setReviewComment] = useState("");
  const [reviewBusy, setReviewBusy] = useState(false);
  const [reviewErr, setReviewErr] = useState<string | null>(null);
  const [reviewOk, setReviewOk] = useState<string | null>(null);

  const loadConfig = useCallback(async () => {
    if (!id) return;
    setLoading(true);
    setErr(null);
    try {
      const c = await api.pcConfiguration.get(Number(id));
      setConfig(c);
      setAssessment(null);
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Ошибка загрузки");
      setConfig(null);
    } finally {
      setLoading(false);
    }
  }, [id]);

  const loadReviews = useCallback(async () => {
    if (!id) return;
    try {
      const r = await api.review.list(Number(id));
      setReviews(r);
    } catch {
      setReviews([]);
    }
  }, [id]);

  useEffect(() => {
    void loadConfig();
  }, [loadConfig]);

  useEffect(() => {
    void loadReviews();
  }, [loadReviews]);

  async function runAssessment() {
    if (!id) return;
    setAssessErr(null);
    setAssessBusy(true);
    try {
      const a = await api.assessment(Number(id));
      setAssessment(a);
    } catch (e) {
      setAssessErr(e instanceof Error ? e.message : "Не удалось получить оценку");
    } finally {
      setAssessBusy(false);
    }
  }

  async function removeBuild() {
    if (!id || !config) return;
    if (!confirm(`Удалить сборку «${config.name}»? Это действие необратимо.`)) return;
    try {
      await api.pcConfiguration.delete(Number(id));
      navigate("/my-builds");
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Не удалось удалить");
    }
  }

  async function submitReview(e: FormEvent) {
    e.preventDefault();
    if (!id || !user) return;
    setReviewErr(null);
    setReviewOk(null);
    setReviewBusy(true);
    try {
      await api.review.add({
        pcId: Number(id),
        rating: Number(reviewRating),
        comment: reviewComment.trim() || "—",
      });
      setReviewComment("");
      setReviewOk("Отзыв опубликован.");
      await loadReviews();
    } catch (e) {
      setReviewErr(e instanceof Error ? e.message : "Не удалось отправить отзыв");
    } finally {
      setReviewBusy(false);
    }
  }

  if (loading) return <p className="muted">Загрузка…</p>;
  if (err && !config) return <div className="error-banner">{err}</div>;
  if (!config) return null;

  const isOwner = Boolean(user && config.userName === user.nickname);

  return (
    <div>
      <div className="btn-row" style={{ marginBottom: "1rem" }}>
        <Link to="/" className="btn btn-ghost">
          ← Каталог
        </Link>
        {token ? (
          <Link to="/my-builds" className="btn btn-ghost">
            Мои сборки
          </Link>
        ) : null}
        {isOwner ? (
          <>
            <Link to={`/builds/${config.id}/edit`} className="btn btn-primary">
              Изменить
            </Link>
            <button type="button" className="btn btn-danger" onClick={() => void removeBuild()}>
              Удалить сборку
            </button>
          </>
        ) : null}
      </div>
      {err && isOwner ? <div className="error-banner">{err}</div> : null}
      <h1>{config.name}</h1>
      <p className="muted">
        Автор: {config.userName} · Итого: {formatPrice(config.totalPrice)}
      </p>

      <div className="card">
        <h2>Компоненты</h2>
        <ul style={{ margin: 0, paddingLeft: "1.2rem" }}>
          <li>Процессор: {config.processorModel}</li>
          <li>Материнская плата: {config.motherboardModel}</li>
          <li>Видеокарта: {config.videoCardModel}</li>
          <li>Охлаждение: {config.processorCoolingModel}</li>
          <li>Корпус: {config.pcCaseModel}</li>
          <li>БП: {config.powerUnitModel}</li>
        </ul>
        <h2 style={{ marginTop: "1.25rem" }}>ОЗУ</h2>
        <ul style={{ margin: 0, paddingLeft: "1.2rem" }}>
          {(config.ramModules ?? []).length === 0 ? (
            <li className="muted">Не добавлено</li>
          ) : (
            (config.ramModules ?? []).map((r) => (
              <li key={r.id}>
                {r.moduleName} × {r.quantity} — {formatPrice(r.price)}
              </li>
            ))
          )}
        </ul>
        <h2 style={{ marginTop: "1.25rem" }}>Накопители</h2>
        <ul style={{ margin: 0, paddingLeft: "1.2rem" }}>
          {(config.storageDevices ?? []).length === 0 ? (
            <li className="muted">Не добавлено</li>
          ) : (
            (config.storageDevices ?? []).map((s) => (
              <li key={s.id}>
                {s.storageModel} × {s.quantity} ({s.storageCapacity} ГБ) — {formatPrice(s.price)}
              </li>
            ))
          )}
        </ul>
      </div>

      {isOwner ? (
        <div className="card">
          <h2>Оценка сборки</h2>
          <p className="muted">
            Расчёт производительности и рекомендаций доступен только владельцу сборки.
          </p>
          {assessErr && <div className="error-banner">{assessErr}</div>}
          <button
            type="button"
            className="btn btn-primary"
            disabled={assessBusy}
            onClick={() => void runAssessment()}
          >
            {assessBusy ? "Считаем…" : "Получить оценку"}
          </button>
          {assessment && (
            <div className="assessment-block">
              <p>
                <span className="tag">Балл {assessment.performanceScore}/100</span>{" "}
                <span className="tag">{assessment.performanceLevel}</span>{" "}
                <span className="tag">TDP {assessment.totalTdp} Вт</span>
              </p>
              <p style={{ marginTop: "0.75rem" }}>{assessment.bottleneckAnalysis}</p>
              <p className="muted" style={{ fontSize: "0.85rem" }}>
                {new Date(assessment.createdAtAssessment).toLocaleString("ru-RU")}
              </p>
              <strong>Рекомендации</strong>
              <ul>
                {assessment.recommendations.map((r, i) => (
                  <li key={i}>{r}</li>
                ))}
              </ul>
            </div>
          )}
        </div>
      ) : null}

      <div className="card">
        <h2>Отзывы</h2>
        {reviews.length === 0 ? (
          <p className="muted">Пока нет отзывов.</p>
        ) : (
          <ul style={{ listStyle: "none", padding: 0, margin: 0 }}>
            {reviews.map((r, i) => (
              <li
                key={`${r.userId}-${r.createdAt}-${i}`}
                style={{
                  padding: "0.75rem 0",
                  borderBottom: "1px solid var(--border)",
                }}
              >
                <strong>{r.authorNickname}</strong>{" "}
                <span className="muted">
                  {r.rating}/5 · {new Date(r.createdAt).toLocaleString("ru-RU")}
                </span>
                <p style={{ margin: "0.35rem 0 0" }}>{r.comment}</p>
              </li>
            ))}
          </ul>
        )}

        {user && isOwner ? (
          <p className="muted" style={{ marginTop: "1rem" }}>
            На свою сборку отзыв оставить нельзя — отзывы пишут другие пользователи.
          </p>
        ) : user ? (
          <form style={{ marginTop: "1.25rem" }} onSubmit={(e) => void submitReview(e)}>
            <h3 style={{ fontSize: "1rem", margin: "0 0 0.75rem" }}>Оставить отзыв</h3>
            {reviewErr && <div className="error-banner">{reviewErr}</div>}
            {reviewOk && <div className="success-banner">{reviewOk}</div>}
            <div className="form-grid cols-2">
              <label>
                Оценка (1–5)
                <select value={reviewRating} onChange={(e) => setReviewRating(e.target.value)}>
                  {[5, 4, 3, 2, 1].map((n) => (
                    <option key={n} value={n}>
                      {n}
                    </option>
                  ))}
                </select>
              </label>
            </div>
            <label style={{ marginTop: "0.75rem", display: "block" }}>
              Комментарий
              <textarea
                rows={3}
                value={reviewComment}
                onChange={(e) => setReviewComment(e.target.value)}
                placeholder="Ваше мнение о сборке"
                style={{ width: "100%", marginTop: "0.35rem" }}
              />
            </label>
            <div className="btn-row">
              <button type="submit" className="btn btn-primary" disabled={reviewBusy}>
                {reviewBusy ? "Отправка…" : "Отправить"}
              </button>
            </div>
          </form>
        ) : (
          <p className="muted" style={{ marginTop: "1rem" }}>
            <Link to="/login">Войдите</Link>, чтобы оставить отзыв.
          </p>
        )}
      </div>
    </div>
  );
}
