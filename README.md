# Projeto_1 — Meu Contador

Contador de tempo progressivo para Android. Mostra quanto tempo passou desde
uma data de referência, com atualização a cada 10 ms. Um toque na tela alterna
entre o modo escuro (desde 22/05/2025) e o modo claro (desde 12/08/2025).

## Baixar e instalar

- Download direto do APK:
  <https://github.com/diegoafmotta2006/Projeto_1/raw/main/dist/MeuContador-1.0.apk>
- Releases (quando houver): <https://github.com/diegoafmotta2006/Projeto_1/releases>

No celular, baixe o APK, abra o arquivo e permita "instalar apps desconhecidos"
para o navegador/gerenciador de arquivos. Requer Android 4.1 (API 16) ou newer.

> Se você já tem uma versão antiga instalada (assinada com outra chave), o
> Android vai recusar a atualização. Desinstale a versão antiga antes de
> instalar esta. Da versão 1.0 em diante, todas as atualizações usam a mesma
> chave e instalam por cima normalmente.

## O que o app faz

- Conta anos, meses, dias, horas, minutos, segundos e milissegundos.
- Toque na tela para alternar modo escuro/claro.
- Interface minimalista em Java puro, sem bibliotecas externas.
- Compatível com Android antigo: usa `java.util.Calendar` (não `java.time`).

## Estrutura

```
app/src/main/AndroidManifest.xml   manifesto (versão, minSdk, tema)
app/src/main/java/...              código Java
app/src/main/res/...               ícones e temas
build.sh                           build sem Gradle (Termux/SDK)
dist/                              APK pronto para download
```

## Compilar

### Termux (sem Gradle)

Precisa do Android SDK (`~/android-sdk`) e de `aapt2`, `d8`, `zipalign` e
`apksigner` no PATH (pacotes do Termux). O script também aceita as ferramentas
do SDK quando elas rodam na arquitetura do aparelho.

```bash
./build.sh                      # gera APK não assinado em build/out/
KEYSTORE=release.jks KS_PASS=suaSenha ./build.sh   # gera APK assinado
```

O alias da chave é detectado automaticamente; use `KEY_ALIAS=...` se precisar.

### Gradle / Android Studio

```bash
./gradlew assembleDebug      # APK de debug
./gradlew assembleRelease    # APK de release (não assinado)
```

Requer JDK 17 e Android SDK com a plataforma 33.

## Assinatura e atualizações

- Guarde `release.jks` e a senha em local seguro. Sem essa chave você não
  consegue publicar atualizações que instalem por cima.
- A chave de release **não** é versionada (veja `.gitignore`).
- Para um APK de release assinado, rode `build.sh` com `KEYSTORE`/`KS_PASS`
  apontando para essa chave.

## Publicar uma nova versão

1. Aumente `versionCode` e `versionName` em `app/src/main/AndroidManifest.xml`
   (e em `app/build.gradle`).
2. Gere e assine o APK com `build.sh`.
3. Copie para `dist/MeuContador-<versao>.apk`.
4. Commit/push e crie uma Release anexando o APK:

```bash
git add .
git commit -m "Versao 1.1"
git push origin main
gh release create v1.1 dist/MeuContador-1.1.apk \
  --title "Meu Contador 1.1" --notes "Novidades..."
```

Sem `gh`, crie a Release pela página do GitHub em **Releases → Draft a new
release** e anexe o APK.

## Tecnologias

Java, Android SDK, Termux, Git e GitHub.
