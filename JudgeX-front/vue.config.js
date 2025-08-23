const {defineConfig} = require("@vue/cli-service");
const MonacoEditorWebpackPlugin = require("monaco-editor-webpack-plugin");

module.exports = defineConfig({
    transpileDependencies: true,
    chainWebpack(config) {
        config.plugin("monaco").use(MonacoEditorWebpackPlugin, [
            {
                languages: ["java", "cpp", "python"]
            }
        ]);
    },
    devServer: {
        client: {
            overlay: false
        },
    }
});
