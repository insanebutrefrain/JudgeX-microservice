# judgex_front 基于vue3的前端页面

## 下载远程调用生成相关依赖

```
npm install openapi-typescript-codegen --save-dev
```

## 生成远程调用接口代码

```shell
openapi --input http://localhost:8121/api/v2/api-docs --output ./generated --client axios
```

## Project setup

```shell
npm install
```

### Compiles and hot-reloads for development

```shell
npm run serve
```

### Compiles and minifies for production

```shell
npm run build
```

### Lints and fixes files

```shell
npm run lint
```