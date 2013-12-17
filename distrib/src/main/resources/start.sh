#!/bin/sh

sh status.sh > /dev/null
if [ $? -eq 1 ]; then
    java -jar lib/jetty-runner-${jetty-runner.version}.jar bin/os-monitoring-server-${project.version}.war >> server.log & echo $! > .pid
fi
