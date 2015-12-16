#!/bin/sh
pidof steam && echo "please exit steam" && exit 1
exec java -jar $0 "$@"

