#!/bin/sh
dropdb -U ihome ihome
createdb -U ihome ihome --encoding=UNICODE
