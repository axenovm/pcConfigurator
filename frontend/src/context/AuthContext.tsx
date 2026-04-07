import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import type { AuthResponse } from "../api/types";
import { getToken, setToken as persistToken } from "../api/client";

const AUTH_USER_KEY = "pc_config_user";

type AuthState = AuthResponse | null;

type AuthContextValue = {
  user: AuthState;
  token: string | null;
  login: (data: AuthResponse) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

function loadUser(): AuthState {
  try {
    const raw = localStorage.getItem(AUTH_USER_KEY);
    if (!raw) return null;
    return JSON.parse(raw) as AuthResponse;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthState>(() => loadUser());
  const [token, setTokenState] = useState<string | null>(() => getToken());

  const login = useCallback((data: AuthResponse) => {
    persistToken(data.token);
    setTokenState(data.token);
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify(data));
    setUser(data);
  }, []);

  const logout = useCallback(() => {
    persistToken(null);
    setTokenState(null);
    localStorage.removeItem(AUTH_USER_KEY);
    setUser(null);
  }, []);

  const value = useMemo(
    () => ({ user, token, login, logout }),
    [user, token, login, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth outside AuthProvider");
  return ctx;
}
