/// <reference types="vite/client" />

//<editor-fold desc="add vite env params types">
interface ViteTypeOptions {
  // 添加这行代码，你就可以将 ImportMetaEnv 的类型设为严格模式，
  // 这样就不允许有未知的键值了。
  strictImportMetaEnv: unknown
}

interface ImportMetaEnv {
  readonly VITE_SERVER: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
//</editor-fold>

declare global {
  interface AliyunUpload {

  }
}