#!/bin/bash
echo ===============================
echo author: sunlightcloud.com
echo email : 
echo ===============================

export JAVA_HOME=/usr/java/jdk1.7.0_71

export JAVA_BIN=/usr/java/jdk1.7.0_71/bin

export PATH=$PATH:$JAVA_HOME/bin

export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

export JAVA_HOME JAVA_BIN PATH CLASSPATH

cd `dirname $0`
BIN_DIR=`pwd`
DEPLOY_DIR=`pwd`/projects/amqReceiver/applicationRoot/WEB-INF
CLASS_DIR=$DEPLOY_DIR/classes
CONF_DIR=$DEPLOY_DIR/config

SERVER_NAME=`sed '/msgCenter.name/!d;s/.*=//' $CONF_DIR/config.properties | tr -d '\r'`

if [ -z "$SERVER_NAME" ]; then
    SERVER_NAME=`hostname`
fi

PIDS=`ps -aux | grep "sunlightMQ.Application" |awk '{print $2}'`
if [ -z "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME does not started!"
    exit 1
fi

if [ "$1" != "skip" ]; then
    $BIN_DIR/dump.sh
fi

echo -e "Stopping the $SERVER_NAME ...\c"
for PID in $PIDS ; do
    kill -9 $PID > /dev/null 2>&1
done

COUNT=0
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1
    COUNT=1
    for PID in $PIDS ; do
        PID_EXIST=`ps -f -p $PID | grep java`
        if [ -n "$PID_EXIST" ]; then
            COUNT=0
            break
        fi
    done
done

echo "OK!"
echo "PID: $PIDS"
