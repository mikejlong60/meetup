#!/usr/bin/env bash
##SBT_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -Dlogger.file=conf/logback.xml"
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled"
java $SBT_OPTS -jar `dirname $0`/sbt-launch.jar "$@"