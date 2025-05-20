#!/usr/bin/env bash
export JAVA_HOME="/home/drgxel/.local/share/jdk17"
export ANDROID_HOME="/home/drgxel/Android/Sdk"
export ANDROID_SDK_ROOT="/home/drgxel/Android/Sdk"
export PATH="$JAVA_HOME/bin:$PATH"

exec /home/drgxel/.gradle/wrapper/dists/gradle-8.13-bin/5xuhj0ry160q40clulazy9h7d/gradle-8.13/bin/gradle "$@"
