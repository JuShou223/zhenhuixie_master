'use strict';

module.exports = {
  root: true,
  parser: 'vue-eslint-parser',
  parserOptions: {
    ecmaVersion: 2022,
    sourceType: 'module',
  },
  env: {
    browser: true,
    es2022: true,
  },
  plugins: ['vue'],
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-essential',
  ],
  globals: {
    uni: 'readonly',
    wx: 'readonly',
    getCurrentPages: 'readonly',
    getApp: 'readonly',
    Page: 'readonly',
    Component: 'readonly',
    App: 'readonly',
    plus: 'readonly',
    global: 'readonly',
    process: 'readonly',
  },
  rules: {
    'no-undef': 'error',
    'vue/multi-word-component-names': 'off',
    'no-console': 'off',
    'no-unused-vars': 'off',
  },
  ignorePatterns: [
    'node_modules/',
    'dist/',
    'unpackage/',
    'src/static/',
    '*.config.js',
    'src/manifest.json',
    'src/pages.json',
  ],
};
