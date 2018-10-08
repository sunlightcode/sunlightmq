#!/bin/bash
cd `dirname $0`
./stopReceiver.sh
sleep 1
./stopServer.sh
sleep 1
./startReceiver.sh
sleep 1
./startServer.sh