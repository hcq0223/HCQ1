# Resume Builder - 个人简历前端

基于 Vue 3 + Vite + Axios 的桌面端个人简历管理系统前端。

## 技术栈

- Vue 3 (Composition API)
- Vue Router 5
- Pinia
- Axios
- SortableJS（拖拽排序）
- Vite 8

## 快速开始

### 前置条件

- Node.js 18+
- 后端 Spring Boot 服务运行在 `http://localhost:8080`

### 安装与运行

```powershell
cd d:\demo\qd
npm install
npm run dev
```

访问 http://localhost:5173

### 构建

```powershell
npm run build
npm run preview
```

## 页面说明

| 路由 | 页面 |
|------|------|
| `/login` | 登录 |
| `/register` | 注册 |
| `/forget-password` | 找回密码 |
| `/dashboard` | 简历列表（仪表盘） |
| `/editor/:id` | 简历编辑（三栏布局） |
| `/preview/:id` | 简历预览 |
| `/trash` | 回收站 |
| `/chat` | AI 聊天助手 |
| `/public/:token` | 公开分享查看 |

## 功能特性

- Session 认证（Cookie 跨域携带）
- 401 自动跳转登录
- 简历 CRUD、软删除、回收站
- 8 大编辑模块（基本信息、工作、教育、技能、项目、证书、语言、自定义）
- 模板选择与切换
- 分享链接管理
- 可拖拽 AI 助手浮窗
- 实时简历预览

## API 代理

开发环境下 Vite 将 `/hcq` 和 `/upload` 代理至 `http://localhost:8080`。
