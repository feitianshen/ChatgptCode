# ChatgptCode 本地以图搜图示例

这是一个简单的 Java 17 应用，支持：

- 选择本地图片建立图片库（默认目录为 `library`）。
- 上传（指定路径）图片后在本地库内搜索相似图片。
- 使用平均哈希（aHash）与汉明距离衡量相似度。

## 构建与运行

```bash
mvn clean package
```

运行命令：

```bash
# 添加图片到库
java -jar target/image-search-1.0.0.jar add <imagePath> [--lib <libraryDir>]

# 以图搜图
java -jar target/image-search-1.0.0.jar search <imagePath> [--lib <libraryDir>] [--top <count>] [--threshold <maxDistance>]
```

- `--top`：返回的最大匹配数量（默认 5）。
- `--threshold`：最大允许的汉明距离（默认 10），值越小匹配越严格。

## 实现简介

- `ImageHashService`：将图片缩放为 8x8，计算平均哈希，并提供汉明距离计算。
- `ImageLibrary`：管理图片库和元数据，支持新增图片与搜索。
- `App`：简单 CLI，封装了 `add` 与 `search` 两个命令。

## 测试

```bash
mvn test
```
