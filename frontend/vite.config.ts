import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

const backend = "http://localhost:8080";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      "/api": backend,
      "/pc-configuration": backend,
      "/pc-config-ram": backend,
      "/pc-config-storage": backend,
      "/assessment": backend,
      "/processor": backend,
      "/motherboard": backend,
      "/videocard": backend,
      "/pc-case": backend,
      "/power-unit": backend,
      "/processor-cooling": backend,
      "/ram-module": backend,
      "/storage-device": backend,
      "/review": backend,
      "/user-activity": backend,
    },
  },
});
