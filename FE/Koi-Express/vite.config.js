import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import NodePolyfills from "rollup-plugin-polyfill-node";

export default defineConfig({
  plugins: [
    react(),
    NodePolyfills(),  // Add polyfills for Node modules
  ],
  server: {
    port: 5173,
    strictPort: true,
  },
  resolve: {
    alias: {
      "@emotion/react": "@emotion/react",
      "@emotion/styled": "@emotion/styled",
    },
  },
  optimizeDeps: {
    include: ["@stomp/stompjs", "sockjs-client"],
    force: true,
  },
});
