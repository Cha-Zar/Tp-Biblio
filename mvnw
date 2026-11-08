#!/bin/sh
set -e

MVNW_BASEDIR=$(cd "$(dirname "$0")" && pwd)
MVNW_PROPS="$MVNW_BASEDIR/.mvn/wrapper/maven-wrapper.properties"

if [ ! -f "$MVNW_PROPS" ]; then
  echo "Cannot find $MVNW_PROPS" >&2
  exit 1
fi

MVNW_URL=$(sed -n 's/^distributionUrl=//p' "$MVNW_PROPS" | tr -d '\r')
MVNW_NAME=$(basename "$MVNW_URL" | sed 's/-bin\.zip$//; s/-bin\.tar\.gz$//')
MVNW_DEST="${MAVEN_USER_HOME:-$HOME/.m2}/wrapper/dists/$MVNW_NAME"
MVNW_MVN="$MVNW_DEST/$MVNW_NAME/bin/mvn"

download() {
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL -o "$2" "$1"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$2" "$1"
  else
    echo "curl or wget is required to download Maven" >&2
    exit 1
  fi
}

if [ ! -x "$MVNW_MVN" ]; then
  echo "Downloading $MVNW_NAME ..." >&2
  MVNW_TMP=$(mktemp -d)
  trap 'rm -rf "$MVNW_TMP"' EXIT
  mkdir -p "$MVNW_DEST"
  if command -v unzip >/dev/null 2>&1; then
    download "$MVNW_URL" "$MVNW_TMP/maven.zip"
    unzip -q "$MVNW_TMP/maven.zip" -d "$MVNW_TMP"
  else
    download "$(echo "$MVNW_URL" | sed 's/\.zip$/.tar.gz/')" "$MVNW_TMP/maven.tar.gz"
    tar -xzf "$MVNW_TMP/maven.tar.gz" -C "$MVNW_TMP"
  fi
  rm -rf "${MVNW_DEST:?}/$MVNW_NAME"
  mv "$MVNW_TMP/$MVNW_NAME" "$MVNW_DEST/"
  chmod +x "$MVNW_MVN"
fi

exec "$MVNW_MVN" "$@"
