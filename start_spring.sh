#!/bin/bash
# shellcheck disable=SC2164
cd spring
gradle clean build
nohup java -jar build/libs/mytybe-0.0.1.jar > spring.txt &
