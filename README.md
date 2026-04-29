# DailyQ - 每日一问微信小程序

一个温暖风格的微信小程序，用于记录用户日常。每天回答一个问题，记录生活的美好瞬间。

## 项目结构

```
DailyQuestion/
├── backend/                    # Java后端项目
│   ├── pom.xml                 # Maven配置
│   └── src/
│       └── main/
│           ├── java/com/dailyq/
│           │   ├── DailyQApplication.java      # 启动类
│           │   ├── config/                     # 配置类
│           │   │   ├── CorsConfig.java
│           │   │   ├── FileConfig.java
│           │   │   ├── GlobalExceptionHandler.java
│           │   │   ├── JwtConfig.java
│           │   │   ├── SchedulingConfig.java
│           │   │   ├── SecurityConfig.java
│           │   │   ├── WebMvcConfig.java
│           │   │   └── WeChatConfig.java
│           │   ├── controller/                 # 控制器
│           │   │   ├── AdminController.java
│           │   │   ├── AnswerController.java
│           │   │   ├── AuthController.java
│           │   │   ├── DailyQuestionController.java
│           │   │   └── FileController.java
│           │   ├── dto/                        # 数据传输对象
│           │   ├── entity/                     # 实体类
│           │   ├── repository/                 # 数据访问层
│           │   ├── security/                   # 安全相关
│           │   ├── service/                    # 业务逻辑层
│           │   └── util/                       # 工具类
│           └── resources/
│               ├── application.yml             # 应用配置
│               └── static/admin/
│                   └── index.html              # 管理员后台页面
├── database/
│   └── schema.sql              # 数据库初始化脚本
├── miniprogram/                # 微信小程序前端
│   ├── app.js
│   ├── app.json
│   ├── app.wxss
│   ├── project.config.json
│   ├── sitemap.json
│   ├── assets/                 # 资源文件
│   │   └── icons/              # 图标文件（需要自行添加）
│   └── pages/
│       ├── login/              # 登录页
│       ├── index/              # 首页
│       ├── calendar/           # 日历页
│       ├── history/            # 历史记录页
│       └── detail/             # 详情页
└── README.md
```

## 技术栈

- **前端**: 微信小程序原生开发
- **后端**: Spring Boot 2.7.18 + Spring Security + JPA
- **数据库**: MySQL 8.0+
- **认证**: JWT + 微信授权登录

## 功能特性

### 用户端
- ✅ 微信一键登录
- ✅ 首页显示当日问题
- ✅ 文字回答 + 配图片
- ✅ 日历视图展示（已回答日期高亮标记）
- ✅ 历史记录翻看
- ✅ 回答详情查看

### 管理端
- ✅ 管理员登录
- ✅ 问题库管理（增删改查）
- ✅ 问题分类管理
- ✅ 每日问题自动轮换

### UI 风格
- 温暖柔和的暖橘色主色调
- 充足留白设计
- 圆角卡片式布局
- 渐变色彩点缀

## 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- 微信开发者工具
- 微信小程序账号（开发版也可以测试）

## 快速开始

### 1. 数据库初始化

1. 登录 MySQL，执行数据库初始化脚本：

```bash
mysql -u root -p < database/schema.sql
```

或者在 MySQL 客户端中执行 `database/schema.sql` 文件的内容。

这会创建：
- 数据库 `dailyq`
- 5张数据表：users, questions, daily_questions, answers, admins
- 预置15个示例问题
- 管理员账号：admin / admin123

### 2. 后端配置

1. 编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dailyq?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root                    # 修改为你的MySQL用户名
    password: 123456                  # 修改为你的MySQL密码

# 微信小程序配置（需要在微信公众平台申请）
wechat:
  miniapp:
    appid: your-appid                 # 小程序AppID
    secret: your-secret                # 小程序AppSecret

# 文件上传配置
file:
  upload:
    path: ./uploads/                   # 图片存储路径
    url-prefix: http://localhost:8080/uploads/  # 图片访问URL前缀
```

### 3. 微信小程序配置

1. 需要准备 tabBar 图标文件，放置在 `miniprogram/assets/icons/` 目录下：

```
miniprogram/assets/icons/
├── today.png           # 今日图标（未选中）
├── today-active.png    # 今日图标（选中）
├── calendar.png        # 日历图标（未选中）
├── calendar-active.png # 日历图标（选中）
├── history.png         # 历史图标（未选中）
└── history-active.png  # 历史图标（选中）
```

图标要求：
- 尺寸：81px × 81px
- 格式：PNG 支持透明背景
- 未选中状态：灰度或浅色
- 选中状态：暖橘色 (#FF8C42)

2. 编辑 `miniprogram/project.config.json`：

```json
{
  "appid": "wx1234567890abcdef",  // 修改为你的小程序AppID
  "projectname": "DailyQ"
}
```

3. 编辑 `miniprogram/app.js` 中的后端地址：

```javascript
globalData: {
  baseUrl: 'http://你的服务器IP:8080/api'  // 开发阶段可以使用本机IP
}
```

**注意**：微信小程序要求使用 HTTPS，开发阶段可以在微信开发者工具中勾选"不校验合法域名"选项。

### 4. 启动后端服务

方式一：使用 Maven 命令

```bash
cd backend
mvn clean package -DskipTests
java -jar target/dailyq-backend-1.0.0.jar
```

方式二：使用 IDE 运行
- 使用 IntelliJ IDEA 打开 `backend` 目录
- 找到 `DailyQApplication.java` 类
- 右键运行 main 方法

服务启动后访问 `http://localhost:8080`

### 5. 启动小程序

1. 打开微信开发者工具
2. 导入项目：选择 `miniprogram` 目录
3. 点击"编译"按钮运行

### 6. 管理员后台

管理员后台是一个简单的 Web 页面，用于管理问题库。

访问地址：`http://localhost:8080/admin/index.html`

默认账号：`admin`
默认密码：`admin123`

**功能：**
- 登录/退出
- 查看问题列表
- 添加新问题
- 编辑现有问题
- 启用/禁用问题
- 删除问题
- 设置问题分类

## API 接口说明

### 认证相关

**POST** `/api/auth/login` - 微信登录
- 请求：`{ code: string, nickname?: string, avatarUrl?: string, gender?: number }`
- 响应：`{ token: string, userId: number, nickname: string, avatarUrl: string, isNewUser: boolean }`

### 问题相关

**GET** `/api/daily-question/today` - 获取当日问题
- 响应：`{ id, questionId, questionDate, content, category, hasAnswered }`

**GET** `/api/daily-question/date/{date}` - 获取指定日期的问题

### 回答相关

**POST** `/api/answers` - 提交回答
- 请求：`{ dailyQuestionId: number, content?: string, imageUrl?: string }`
- 响应：返回 Answer 对象

**GET** `/api/answers/date/{date}` - 获取指定日期的回答

**GET** `/api/answers/history` - 获取历史回答列表

**GET** `/api/answers/calendar/{year}/{month}` - 获取某月的回答情况（用于日历）

**GET** `/api/answers/{id}` - 获取回答详情

### 文件上传

**POST** `/api/files/upload` - 上传图片
- Content-Type: `multipart/form-data`
- 参数：`file` (文件)
- 响应：`{ url: string }`

### 管理员接口

**POST** `/api/admin/login` - 管理员登录
- 请求：`{ username: string, password: string }`
- 响应：`{ token: string }`

**CRUD** `/api/admin/questions` - 问题管理

## 验证步骤

### 1. 后端验证

启动服务后，使用 Postman 或 curl 测试接口：

```bash
# 测试健康
curl http://localhost:8080/admin/index.html
# 应该返回管理员登录页面
```

### 2. 管理员后台验证

1. 打开浏览器访问 `http://localhost:8080/admin/index.html`
2. 使用账号 `admin` / `admin123` 登录
3. 尝试添加一个新问题
4. 验证问题列表显示正确

### 3. 小程序验证

1. 在微信开发者工具中运行小程序
2. 点击"微信一键登录"（首次会模拟登录）
3. 验证首页显示当日问题
4. 尝试回答问题（填写文字 + 上传图片）
5. 切换到日历页面，查看当前日期是否高亮
6. 切换到历史记录页面，查看刚提交的回答
7. 点击历史记录进入详情页

### 4. 日历功能验证

1. 回答多个日期的问题（可以修改服务器日期或手动创建数据）
2. 在日历页面查看已回答的日期是否显示为暖橘色圆形
3. 点击已回答的日期，查看问题和回答详情

### 5. 自动轮换验证

后端有定时任务，每天 0 点自动生成当日问题：

```java
@Scheduled(cron = "0 0 0 * * ?")
public void generateTodayQuestion() {
    // 从问题库中轮换选择问题
}
```

可以通过修改数据库 `daily_questions` 表来测试不同日期的问题。

## 常见问题

### 1. 微信登录测试

由于微信登录需要真实的小程序 AppID 和密钥，开发阶段可以：

方式一：使用真实小程序账号
- 在微信公众平台注册小程序
- 获取 AppID 和 AppSecret
- 配置到 `application.yml`

方式二：模拟登录
- 在开发阶段可以修改 `UserService` 类，跳过微信验证
- 直接返回模拟的用户数据

### 2. 图片上传问题

确保：
- `./uploads/` 目录有写入权限
- `application.yml` 中的 `url-prefix` 配置正确
- 小程序端可以访问该 URL

### 3. 数据库连接失败

检查：
- MySQL 服务是否启动
- 用户名密码是否正确
- 是否创建了 `dailyq` 数据库
- 执行了 `schema.sql` 脚本

### 4. 小程序无法访问后端

微信小程序开发阶段：
1. 在开发者工具中勾选"详情" -> "本地设置" -> "不校验合法域名"
2. 如果使用真机调试，需要后端使用 HTTPS 或内网穿透

## 项目配置清单

启动项目前，请确保完成以下配置：

| 配置项 | 文件位置 | 说明 |
|--------|----------|------|
| MySQL账号密码 | `application.yml` | 数据库连接 |
| 微信小程序AppID | `application.yml`, `project.config.json` | 登录功能 |
| 微信小程序Secret | `application.yml` | 登录功能 |
| 文件上传路径 | `application.yml` | 图片存储 |
| tabBar图标 | `miniprogram/assets/icons/` | 小程序底部导航 |

## 扩展建议

- 添加消息推送功能，提醒用户每日回答
- 增加心情标签功能
- 添加回答统计和图表
- 支持多图片上传
- 添加分享卡片生成
- 增加用户数据导出功能
- 添加夜间模式

## 技术支持

如有问题，请检查：
1. JDK 版本是否为 11+
2. MySQL 版本是否为 8.0+
3. 所有配置文件中的占位符是否已替换
4. 数据库表是否已正确初始化

---

**Enjoy DailyQ! 🌅**
