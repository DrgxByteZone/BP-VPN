#!/usr/bin/env bash
set -e

PROJECT_DIR="/home/drgxel/Documents/android project/#3"
cd "$PROJECT_DIR"

export JAVA_HOME="/home/drgxel/.local/share/jdk17"
export ANDROID_HOME="/home/drgxel/Android/Sdk"
export ANDROID_SDK_ROOT="/home/drgxel/Android/Sdk"
export PATH="$JAVA_HOME/bin:$PATH"

echo "=================================================="
echo "  Black Panther VPN (BP VPN) - Build System       "
echo "  Created by: Axel & M.B.A                        "
echo "  Optimized for Low-Spec Systems (Anti-Freeze)    "
echo "=================================================="

# Check RAM
echo "[1/3] Checking host memory..."
free -h

echo "[2/3] Compiling BP VPN (Single-worker, capped JVM memory 1024M)..."
./gradlew assembleDebug --no-daemon --max-workers=1 --stacktrace

echo "[3/3] Locating compiled APK..."
APK_FILE=$(find "$PROJECT_DIR/app/build/outputs/apk/debug" -name "*.apk" | head -n 1)

if [ -f "$APK_FILE" ]; then
    cp "$APK_FILE" "$PROJECT_DIR/BP-VPN-v1.0.apk"
    echo ""
    echo "=================================================="
    echo "  BUILD SUCCESS!                                  "
    echo "  Output: $PROJECT_DIR/BP-VPN-v1.0.apk            "
    echo "=================================================="
    ls -lh "$PROJECT_DIR/BP-VPN-v1.0.apk"
else
    echo "ERROR: APK not found in output directory."
    exit 1
fi
