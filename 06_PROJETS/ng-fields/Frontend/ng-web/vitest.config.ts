/// <reference types="vitest" />
import { defineConfig } from 'vitest/config';
import angular from '@analogjs/vite-plugin-angular';

export default defineConfig({
  plugins: [angular()],
  test: {
    globals: true,
    environment: 'jsdom',
    include: ['src/**/*.spec.ts'],
    reporters: ['default'],
    setupFiles: ['src/test-setup.ts'],
  },
  resolve: {
    alias: {
      '@app': '/src/app',
      '@env': '/src/environments',
    },
  },
});
