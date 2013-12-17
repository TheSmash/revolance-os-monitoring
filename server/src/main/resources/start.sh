#!/bin/sh

sh status.sh > /dev/null
if [ $? -eq 1 ]; then
    java -jar lib/jetty-runner-${jetty-runner.version}.jar bin/${project.artifactId}-${project.version}.war >> server.log & echo $! > .pid
fi
