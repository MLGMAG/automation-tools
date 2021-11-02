#!/bin/bash

if [ -n "$1" ] && [ -n "$2" ]
then
	echo "Start branch is '$1' and end branch is '$2'"
else
	echo "Programm requires 2 arguments: start branch, end branch!"
	exit 1
fi

git status &> /dev/null

if [ $? -ne 0 ]
then
        echo "No repository or Git is not installed!"
        exit 1
fi

echo "Fetching information..."

git fetch &> /dev/null

git checkout $1 &> /dev/null

if [ $? -ne 0 ]
then
        echo "No branch with name $1 found!"
        exit 1
fi

git checkout $2 &> /dev/null

if [ $? -ne 0 ]
then
        echo "No branch with name $2 found!"
        exit 1
fi

git log --oneline $1..$2 \
| sed '/Pull request #/d' \
| sed '/Merge branch /d' \
| sed '/Merge remote-tracking branch /d' \
| sed '/Merge branches/d' \
| grep -Eo "FP-[0-9]{5}" \
| sort \
| uniq \
| sed ':a;N;$!ba;s/\n/, /g' \
| xargs echo "Difference:"


