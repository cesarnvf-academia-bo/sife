#!/usr/bin/env bash
set -euo pipefail

NOMBRE_BASE_DATOS="${NOMBRE_BASE_DATOS:-sife}"
USUARIO_BASE_DATOS="${USUARIO_BASE_DATOS:-${PGUSER:-postgres}}"
DIRECTORIO_SCRIPT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

dropdb --if-exists --force --username "$USUARIO_BASE_DATOS" "$NOMBRE_BASE_DATOS"
createdb --username "$USUARIO_BASE_DATOS" "$NOMBRE_BASE_DATOS"
psql --username "$USUARIO_BASE_DATOS" --dbname "$NOMBRE_BASE_DATOS" --file "$DIRECTORIO_SCRIPT/tablas.sql"
psql --username "$USUARIO_BASE_DATOS" --dbname "$NOMBRE_BASE_DATOS" --file "$DIRECTORIO_SCRIPT/datos.sql"
