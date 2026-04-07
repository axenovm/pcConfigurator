import { useCallback, useEffect, useState } from "react";
import { api } from "../api/client";

export type CatalogData = {
  processors: Awaited<ReturnType<typeof api.catalog.processors>>;
  motherboards: Awaited<ReturnType<typeof api.catalog.motherboards>>;
  videoCards: Awaited<ReturnType<typeof api.catalog.videoCards>>;
  pcCases: Awaited<ReturnType<typeof api.catalog.pcCases>>;
  powerUnits: Awaited<ReturnType<typeof api.catalog.powerUnits>>;
  coolings: Awaited<ReturnType<typeof api.catalog.coolings>>;
  ramModules: Awaited<ReturnType<typeof api.catalog.ramModules>>;
  storage: Awaited<ReturnType<typeof api.catalog.storage>>;
};

export function useCatalog() {
  const [data, setData] = useState<CatalogData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [
        processors,
        motherboards,
        videoCards,
        pcCases,
        powerUnits,
        coolings,
        ramModules,
        storage,
      ] = await Promise.all([
        api.catalog.processors(),
        api.catalog.motherboards(),
        api.catalog.videoCards(),
        api.catalog.pcCases(),
        api.catalog.powerUnits(),
        api.catalog.coolings(),
        api.catalog.ramModules(),
        api.catalog.storage(),
      ]);
      setData({
        processors,
        motherboards,
        videoCards,
        pcCases,
        powerUnits,
        coolings,
        ramModules,
        storage,
      });
    } catch (e) {
      setError(e instanceof Error ? e.message : "Ошибка загрузки каталога");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  return { data, loading, error, reload: load };
}
