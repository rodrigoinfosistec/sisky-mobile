const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');
const readline = require('readline');

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
});

const pkg = JSON.parse(fs.readFileSync(path.join(__dirname, '../package.json'), 'utf8'));

rl.question(`Versão atual: ${pkg.version}\nNova versão: `, (version) => {
    rl.close();

    if (!version.match(/^\d+\.\d+\.\d+$/)) {
        console.error('Versão inválida! Use o formato: 1.2.3');
        process.exit(1);
    }

    // Atualiza o package.json
    pkg.version = version;
    fs.writeFileSync(
        path.join(__dirname, '../package.json'),
        JSON.stringify(pkg, null, 2) + '\n'
    );

    console.log(`✅ package.json atualizado para ${version}`);

    // Sync Capacitor
    execSync('npx cap sync android', { stdio: 'inherit' });
    console.log('✅ Capacitor sincronizado!');

    // Commit e tag
    execSync('git add .');
    execSync(`git commit -m "chore: bump version to ${version}"`);
    execSync(`git tag v${version}`);
    execSync('git push origin main');
    execSync(`git push origin v${version}`);

    console.log(`✅ Tag v${version} criada e enviada!`);
    console.log('🚀 GitHub Actions vai gerar o APK automaticamente.');
});