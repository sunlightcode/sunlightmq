#!/bin/bash
cd `dirname $0`
./stopReceiver.sh
sleep 1
./stopServer.sh
