#!/data/data/com.termux/files/usr/bin/bash
# Compila o Meu Contador sem Gradle, usando apenas o Android SDK
# (aapt2, javac, d8, zipalign e apksigner).
#
# Uso:
#   ./build.sh                     -> gera APK nao assinado em build/out/
#   KEYSTORE=minha.jks KS_PASS=123 ./build.sh   -> gera APK assinado
#
# Variaveis opcionais:
#   ANDROID_HOME   (padrao: ~/android-sdk)
#   KEYSTORE       caminho do .jks
#   KS_PASS        senha do keystore
#   KEY_ALIAS      alias da chave (padrao: primeiro alias do keystore)
#   KEY_PASS       senha da chave (padrao: KS_PASS)

set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
SDK="${ANDROID_HOME:-$HOME/android-sdk}"
if [ ! -d "$SDK" ] && [ -d "/data/data/com.termux/files/home/android-sdk" ]; then
    SDK="/data/data/com.termux/files/home/android-sdk"
fi
OUT="$ROOT/build/out"
APP="$ROOT/app/src/main"
MANIFEST="$APP/AndroidManifest.xml"

if [ ! -d "$SDK" ]; then
    echo "ERRO: Android SDK nao encontrado em $SDK" >&2
    echo "Defina ANDROID_HOME ou instale o SDK." >&2
    exit 1
fi

BT="$(ls -d "$SDK"/build-tools/* 2>/dev/null | sort -V | tail -n 1)"
PLATFORM="$(ls -d "$SDK"/platforms/android-* 2>/dev/null | sort -V | tail -n 1)"
ANDROID_JAR="$PLATFORM/android.jar"

# Prefere as ferramentas do Termux (compiladas para ARM); se nao existirem,
# usa as do SDK (uteis em PC/CI, onde o SDK e x86_64).
resolve_tool() {
    local name="$1"
    if command -v "$name" >/dev/null 2>&1; then
        command -v "$name"
    elif [ -x "$BT/$name" ]; then
        echo "$BT/$name"
    else
        echo "ERRO: $name nao encontrado no PATH nem em $BT" >&2
        return 1
    fi
}

AAPT2="$(resolve_tool aapt2)"
D8="$(resolve_tool d8)"
ZIPALIGN="$(resolve_tool zipalign)"
APKSIGNER="$(resolve_tool apksigner)"

if [ ! -f "$ANDROID_JAR" ]; then
    echo "ERRO: android.jar nao encontrado em $PLATFORM" >&2
    exit 1
fi

echo "SDK:         $SDK"
echo "Build-tools: $BT"
echo "Plataforma:  $PLATFORM"

rm -rf "$OUT"
mkdir -p "$OUT/gen" "$OUT/classes" "$OUT/dex"

echo "[1/6] Compilando recursos..."
"$AAPT2" compile --dir "$APP/res" -o "$OUT/res.zip"

echo "[2/6] Linkando recursos e manifest..."
"$AAPT2" link \
    -o "$OUT/base.apk" \
    -I "$ANDROID_JAR" \
    --manifest "$MANIFEST" \
    --java "$OUT/gen" \
    "$OUT/res.zip"

echo "[3/6] Compilando Java..."
find "$APP/java" "$OUT/gen" -name '*.java' > "$OUT/sources.txt"
javac -source 8 -target 8 -encoding UTF-8 \
    -bootclasspath "$ANDROID_JAR" \
    -d "$OUT/classes" \
    @"$OUT/sources.txt"

echo "[4/6] Convertendo para DEX..."
find "$OUT/classes" -name '*.class' > "$OUT/classes.txt"
"$D8" --lib "$ANDROID_JAR" --output "$OUT/dex" @"$OUT/classes.txt"

cp "$OUT/base.apk" "$OUT/unsigned.apk"
(cd "$OUT/dex" && zip -q "$OUT/unsigned.apk" classes.dex)

echo "[5/6] Alinhando..."
"$ZIPALIGN" -f 4 "$OUT/unsigned.apk" "$OUT/aligned.apk"

if [ -n "${KEYSTORE:-}" ]; then
    if [ -z "${KS_PASS:-}" ]; then
        echo "ERRO: defina KS_PASS para usar KEYSTORE." >&2
        exit 1
    fi
    if [ -z "${KEY_ALIAS:-}" ]; then
        KEY_ALIAS="$(keytool -list -keystore "$KEYSTORE" -storepass "$KS_PASS" 2>/dev/null \
            | awk -F, '/PrivateKeyEntry/ {print $1; exit}')"
    fi
    if [ -z "$KEY_ALIAS" ]; then
        echo "ERRO: nao foi possivel descobrir o alias; defina KEY_ALIAS." >&2
        exit 1
    fi
    echo "[6/6] Assinando com $KEYSTORE (alias $KEY_ALIAS)..."
    "$APKSIGNER" sign \
        --ks "$KEYSTORE" \
        --ks-pass "pass:$KS_PASS" \
        --ks-key-alias "$KEY_ALIAS" \
        --key-pass "pass:${KEY_PASS:-$KS_PASS}" \
        --out "$OUT/MeuContador.apk" \
        "$OUT/aligned.apk"
else
    echo "[6/6] Sem KEYSTORE: APK nao assinado."
    cp "$OUT/aligned.apk" "$OUT/MeuContador-unsigned.apk"
fi

echo
echo "Concluido. Arquivos em: $OUT"
ls -1 "$OUT"/*.apk
