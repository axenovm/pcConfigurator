import type { ReactElement, ReactNode } from "react";
import { Link, Navigate, Route, Routes } from "react-router-dom";
import { useAuth } from "./context/AuthContext";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import HomeExplorePage from "./pages/HomeExplorePage";
import DashboardPage from "./pages/DashboardPage";
import ProfilePage from "./pages/ProfilePage";
import NewBuildPage from "./pages/NewBuildPage";
import BuildDetailPage from "./pages/BuildDetailPage";
import BuildEditPage from "./pages/BuildEditPage";

function ProtectedRoute({ children }: { children: ReactElement }) {
  const { token } = useAuth();
  if (!token) return <Navigate to="/login" replace />;
  return children;
}

function Layout({ children }: { children: ReactNode }) {
  const { user, logout } = useAuth();
  return (
    <div className="app-shell">
      <header className="top-nav">
        <Link className="brand" to="/">
          Конфигуратор ПК
        </Link>
        <nav className="nav-links">
          <Link to="/">Каталог</Link>
          {user ? (
            <>
              <Link to="/my-builds">Мои сборки</Link>
              <Link to="/builds/new">Новая сборка</Link>
              <Link to="/profile">Профиль</Link>
              <span className="muted">{user.nickname}</span>
              <button type="button" className="btn btn-ghost" onClick={logout}>
                Выйти
              </button>
            </>
          ) : (
            <>
              <Link to="/login">Вход</Link>
              <Link to="/register">Регистрация</Link>
            </>
          )}
        </nav>
      </header>
      {children}
    </div>
  );
}

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<HomeExplorePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          path="/my-builds"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/builds/new"
          element={
            <ProtectedRoute>
              <NewBuildPage />
            </ProtectedRoute>
          }
        />
        <Route path="/builds/:id" element={<BuildDetailPage />} />
        <Route
          path="/builds/:id/edit"
          element={
            <ProtectedRoute>
              <BuildEditPage />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Layout>
  );
}
