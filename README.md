# 租租侠租赁平台

> 让闲置物品流动起来 —— 相机、投影仪、露营装备、电动工具，用一次不必买一件。

「租租侠」是一个面向社会大众的**闲置物品租赁平台**，围绕物品发布、检索、下单、归还与信用评价构建完整闭环。

本项目为**软件工程课程实践项目**，交付形态为可在本地离线运行与演示的完整系统，不涉及公网部署与上线运营。

---

## 技术栈

| 层次 | 技术 | 版本 |
|---|---|---|
| 前端框架 | Vue 3（组合式 API） | 3.5 |
| 前端构建 | Vite | 8.x |
| 前端语言 | JavaScript | ES2020+ |
| UI 组件库 | Element Plus | 2.14 |
| 状态管理 | Pinia | 4.x |
| 路由 | Vue Router | 5.x |
| HTTP 客户端 | axios | 1.x |
| 后端框架 | Spring Boot | **3.5.3** |
| 后端语言 | Java | **21** |
| 构建工具 | Maven Wrapper | 3.9.16（无需安装 Maven） |
| 数据访问 | MyBatis-Plus | 3.5.17 |
| 认证 | JWT（jjwt） | 0.12.7 |
| 接口文档 | Knife4j + 手写 OpenAPI 3.0 | 4.5.0 |
| 数据库 | MySQL | 8.0 |

---

## 环境要求

| 组件 | 版本 | 说明 |
|---|---|---|
| JDK | **21** | 项目使用 Java 21 语法特性 |
| Node.js | **≥ 18**（建议 22） | 前端构建 |
| MySQL | **8.0** | 需启动 `MySQL80` 服务 |
| Maven | **无需安装** | 使用仓库自带的 `mvnw.cmd` |

> **验证过的环境**：JDK 21.0.7 (Oracle) + Node v22.19.0 + npm 10.9.3 + MySQL 8.0.46，在此环境下后端单元测试 9/9 通过、前端构建成功、`/api/ping` 连通。

---

## 快速开始

### 1. 初始化数据库

```bat
scripts\init-db.bat
```

脚本会引导你输入 MySQL 用户名密码，然后依次执行 `docs/db/schema.sql`（建 9 张表）与 `docs/db/data.sql`（导入演示数据）。

> ⚠️ **脚本会 DROP 已有的 `zuzuxia` 库**，请勿在有数据的库上执行。
> 若 MySQL80 服务未启动，脚本会尝试启动，此步骤需要管理员权限。

### 2. 配置数据库密码

后端默认从环境变量 `MYSQL_PASSWORD` 读取密码，未设置时用 `root`。

若你的密码不是 `root`，任选一种方式：

```bat
:: 方式一：设置环境变量
set MYSQL_PASSWORD=你的密码

:: 方式二：复制模板并填写（推荐，该文件已被 .gitignore 忽略）
copy backend\src\main\resources\application-local.yml.example ^
     backend\src\main\resources\application-local.yml
```

### 3. 一键启动

```bat
scripts\start-all.bat
```

脚本会依次启动 MySQL 服务 → 后端(:8080) → 前端(:5173)，并自动打开浏览器。

**服务地址**

| 服务 | 地址 |
|---|---|
| 前端 | http://localhost:5173 |
| 后端连通性探测 | http://localhost:8080/api/ping |
| 接口文档（Knife4j） | http://localhost:8080/doc.html |

### 4. 停止服务

```bat
scripts\stop-all.bat
```

### 演示账号

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | `admin` | `admin123` |
| 普通用户 | `wujiahao` / `hejinquan` / `wanghaoming` / `zhangmingliang` / `liangxingyu` | `123456` |

---

## 手动启动（不用脚本时）

```bat
:: 后端
cd backend
mvnw.cmd spring-boot:run

:: 前端（另开一个终端）
cd frontend
npm install
npm run dev
```

---

## 目录结构

```
zuzuxia/
├── frontend/                  # Vue 3 + Vite
│   ├── src/
│   │   ├── api/               # 接口封装，按模块一个文件
│   │   │   └── request.js     # axios 实例 + 统一拦截器
│   │   ├── layouts/           # UserLayout / AdminLayout 两套外壳
│   │   ├── router/            # 路由 + 权限守卫
│   │   ├── stores/            # Pinia
│   │   ├── utils/             # 常量与枚举
│   │   └── views/             # 页面（user/ 与 admin/ 分开）
│   └── vite.config.js         # proxy: /api → :8080
│
├── backend/                   # Spring Boot 3
│   ├── src/main/java/com/zuzuxia/
│   │   ├── common/            # Result、ResultCode、BizException、全局异常处理
│   │   ├── config/            # MyBatis-Plus、CORS、拦截器、静态资源
│   │   ├── security/          # JWT、UserContext、登录/管理员拦截器
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   ├── entity/
│   │   └── dto/
│   └── pom.xml
│
├── docs/
│   ├── api/openapi.yaml       # ★ 接口唯一依据
│   ├── db/schema.sql          # 9 张表建表脚本
│   ├── db/data.sql            # 演示数据（覆盖全部 7 种订单状态）
│   ├── db/er.md               # ER 图与设计决策说明
│   ├── specs/                 # 设计文档
│   ├── plans/                 # 实施计划
│   └── test-cases.md          # 边界测试用例
│
└── scripts/                   # init-db / start-all / stop-all
```

---

## 三条全局约定（全队强制遵守）

1. **所有接口以 `/api` 开头**
   前端 axios `baseURL = '/api'`，由 Vite proxy 转发到 `http://localhost:8080`。前端代码中**不写死后端地址**。

2. **所有响应走统一结构**
   ```json
   { "code": 200, "message": "success", "data": { } }
   ```
   前端只在 `request.js` 的响应拦截器里判定一次 `code`，业务代码拿到的直接是 `data`。

3. **`docs/api/openapi.yaml` 是唯一对接依据**
   代码与文档不一致时，**以文档为准并修改代码**。接口变更须当天同步文档并通知前后端全员。

---

## 分支约定

| 分支 | 用途 |
|---|---|
| `main` | 稳定可演示，只接受经组长确认的合并 |
| `feat/前端-物品大厅` 等 | 各人功能分支，命名 `feat/<角色>-<模块>` |

提交前请确保：后端 `mvnw.cmd test` 通过、前端 `npm run build` 通过。

---

## 成员分工

| 成员 | 角色 | 负责范围 |
|---|---|---|
| 伍嘉豪 | 组长 / 技术兜底 | 接口规范、公共底座、钱包模块、管理端、联调 |
| 何金泉 | 前端 A | 物品大厅、物品详情、检索筛选 |
| 王浩名 | 前端 B | 发布物品、订单中心、钱包、个人中心 |
| 张明亮 | 后端 A | 用户、物品、分类模块接口；建表脚本与测试数据 |
| 梁兴宇 | 后端 B | 订单、租借、归还、评价模块接口；状态机 |

---

## 关键文档

| 文档 | 内容 |
|---|---|
| [`docs/specs/2026-09-17-zuzuxia-底座设计.md`](docs/specs/2026-09-17-zuzuxia-底座设计.md) | 技术选型、目录结构、9 张表设计、订单状态机与资金时序、45 个接口清单、错误码 |
| [`docs/plans/2026-09-17-zuzuxia-底座实施计划.md`](docs/plans/2026-09-17-zuzuxia-底座实施计划.md) | 17 个可执行任务的实施计划 |
| [`docs/api/openapi.yaml`](docs/api/openapi.yaml) | **接口唯一依据** |
| [`docs/db/er.md`](docs/db/er.md) | ER 图 + 关键设计决策（为什么 `item` 不存"租用中"、为什么金额要快照） |
| [`docs/test-cases.md`](docs/test-cases.md) | 6 个边界测试用例 + 2 个补充用例 |

---

## 订单状态机

```
                    ┌──(出租人拒绝)──▶ REJECTED ✗
                    │
PENDING ────────────┼──(租用人取消)──▶ CANCELLED ✗
待确认               │
                    └──(出租人同意)──▶ RESERVED ──(确认取件)──▶ RENTING
                                    待取件                     租用中
                                                                 │
                                                    (发起归还)    │
                                                                 ▼
                                                             RETURNING
                                                            待归还确认
                                                                 │
                                                    (出租人确认归还)
                                                                 ▼
                                                             FINISHED ✓
```

**资金动作时机**

| 节点 | 资金动作 |
|---|---|
| 提交申请 | 不动钱，仅校验余额是否够（租金 + 押金） |
| 出租人同意 | 扣租金（托管）+ 冻结押金 |
| 拒绝 / 取消 | 退还租金 + 解冻押金 |
| 确认归还 | 押金退还租用人；租金入账出租人 |

---

## 常见问题

**Q：后端启动报 `Communications link failure` / `Access denied`**

MySQL 未启动或密码不对。检查：
```bat
sc query MySQL80
```
并用 `application-local.yml` 配置正确密码。

**Q：前端页面显示"后端连接失败 ❌"**

按页面提示依次检查：后端是否启动 → 是否监听 8080 → MySQL 是否启动。

**Q：`mvnw.cmd` 下载依赖很慢**

`pom.xml` 中已配置阿里云镜像。若仍慢，检查网络或代理设置。

**Q：不想用脚本，怎么单独启动？**

见上文「手动启动」小节。

---

## 已知限制（本次底座范围外）

1. **站内消息通知**（申请、同意、归还提醒）—— 未实现
2. **逾期自动处理** —— 仅前端标红展示，无定时任务自动扣押金
3. **文件存储** —— 本地 `uploads/` 目录，未接对象存储
4. **MySQL 服务启动** —— 需要管理员权限，脚本会提示
