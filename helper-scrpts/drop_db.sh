#!/bin/sh
#set -x #echo on
PGPASSWORD=$4 dropdb --if-exists -h "$1" -p "$2" -U "$3" "$4"
echo "Database drop"
PGPASSWORD=$4 createdb -h "$1" -p "$2" -U "$3" "$4" --encoding=UNICODE
echo "Database create"

