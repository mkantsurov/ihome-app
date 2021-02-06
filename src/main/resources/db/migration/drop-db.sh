#!/bin/sh
dropdb -U $1 $2
createdb -U $1 $2 --encoding=UNICODE
