#!/bin/bash
# shellcheck disable=SC2164
apt update
apt install zip
apt install openjdk-19-jdk
curl -s "https://get.sdkman.io" | bash
source "/root/.sdkman/bin/sdkman-init.sh"
sdk install gradle 8.3
gradle -version
java -version
