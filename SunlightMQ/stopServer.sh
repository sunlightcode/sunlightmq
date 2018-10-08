#!/bin/bash


echo ===============================
echo author: sunlightcloud.com
echo email : 
echo ===============================

NAME=tomcat
echo $NAME
ID=`ps -ef | grep "$NAME" | grep -v "$0" | grep -v "grep" | awk '{print $2}'`
IDNAME=`ps -ef | grep "$NAME" | grep -v "$0" | grep -v "grep" | awk '{print $8}'`
echo process ID   list: $ID
echo process NAME list: $IDNAME
echo "--start kill-------------"
for id in $ID
do
kill -9 $id
echo "killed $id"
done
echo "--kill_process finished!-"