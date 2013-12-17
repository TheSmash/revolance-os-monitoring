#!/bin/sh

ps -ef|grep ${project.artifactId}|grep -v grep

if [ $? -eq 0 ]; then
    echo "Started"
    return 0
else
    echo "Stopped"
    return 1
fi