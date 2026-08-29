#!/usr/bin/env bash
# .verify.sh — L0 ground-truth gate for argentum-engine.
# Runs the repo's real checks in order, stops on first failure.
set -uo pipefail
cd "$(dirname "$0")"

FAILED=0
run_step() {  # run_step <label> <cmd...>
  local label="$1"; shift
  echo "── $label ──"
  if "$@"; then
    echo "   ✓ $label"
  else
    rc=$?
    echo "   ✗ $label (exit $rc)"
    FAILED=1
    return $rc
  fi
}

if ! command -v just >/dev/null 2>&1; then
  echo "just not found — brew install just"
  exit 1
fi

# JDK 21 required (Gradle toolchain). Hint early if missing.
_jdk21_home() {
  if /usr/libexec/java_home -v 21 >/dev/null 2>&1; then
    /usr/libexec/java_home -v 21
  elif [ -d "/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ]; then
    echo "/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
  else
    return 1
  fi
}

if ! _jdk21_home >/dev/null; then
  echo "JDK 21 not found. Install: brew install openjdk@21"
  echo 'Then: export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"'
  exit 1
fi

export JAVA_HOME="${JAVA_HOME:-$(_jdk21_home)}"

run_step "just build" just build
run_step "just test-rules" just test-rules

if [ "$FAILED" -ne 0 ]; then
  echo ""
  echo "verify gate FAILED"
  exit 1
fi

echo ""
echo "verify gate PASSED"
exit 0
