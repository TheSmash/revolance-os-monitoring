#!/bin/sh

sh status.sh > /dev/null
if [ $? -eq 0 ]; then
    PID=`echo .pid`
    kill ${PID}
fi
