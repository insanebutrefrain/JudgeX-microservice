
const fs = require('fs');
const path = require('path');

const fromPath = path.join(__dirname, 'OpenAPI.ts');
const toPath = path.join(__dirname, '../generated/core/OpenAPI.ts');


if (fs.existsSync(toPath)) {
    fs.writeFileSync(toPath, fs.readFileSync(fromPath));
    console.log('✅ OpenAPI 配置已自动更新');
} else {
    console.error('❌ OpenAPI 配置文件未找到');
}
