#!/bin/bash
#
# Filename:    sendSMS.sh
# Revision:    1.0
# Date:        2016/07/15
# Author:      chenzhen
# Email:
# Website:     http://oip.tiens.com/
# Description: send zabbix alarm message
#

LOGFILE="/tmp/SMS.log"
:>"$LOGFILE"
exec 1>"$LOGFILE"
exec 2>&1

Uid="ServerMonitor"
Key="OIPWebService.SMessage.OIPSMService.SendMessage"
 
MOBILE_NUMBER=$1
MESSAGE_UTF8=$3

XXD="/usr/bin/xxd"
CURL="/usr/bin/curl"
TIMEOUT=5
 
MESSAGE_ENCODE=$(echo "$MESSAGE_UTF8" | ${XXD} -ps | sed 's/\(..\)/%\1/g' | tr -d '\n')
# SMS API
URL="http://oip.tiens.com/OIPRestWebApp/RestMethodInvoke.aspx?appKey=SendMessage&ApiMethodName=${Key}&countryCode=CN&toMobile=${MOBILE_NUMBER}&messageText=${MESSAGE_ENCODE}&clientKey=${Uid}&maxSendTimes=1"
 
# URL="http://utf8.sms.webchinese.cn/?Uid=${Uid}&Key=${Key}&smsMob=${MOBILE_NUMBER}&smsText=${MESSAGE_ENCODE}"
# Send it
set -x
${CURL} -s --connect-timeout ${TIMEOUT} "${URL}"