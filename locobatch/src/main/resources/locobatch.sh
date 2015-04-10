#!/bin/sh

JAVA=/opt/app/java/jdk/jdk180/bin/java
PROGRAM="./locobatch-1.0.0-RELEASE.jar"
HADOOP_CLIENT_JARS="/usr/lib/hadoop/lib/htrace-core-3.0.4.jar,/usr/lib/hadoop/client/jersey-core-1.9.jar,/usr/lib/hadoop/client/jersey-client-1.9.jar,/usr/lib/hadoop/client/hadoop-yarn-client.jar,/usr/lib/hadoop/client/hadoop-yarn-api.jar,/usr/lib/hadoop/client/hadoop-yarn-common.jar,/usr/lib/hadoop/client/commons-cli-1.2.jar,/usr/lib/hadoop/client/hadoop-hdfs.jar,/usr/lib/hadoop/client/commons-configuration-1.6.jar,/usr/lib/hadoop/client/hadoop-mapreduce-client-core.jar,/usr/lib/hadoop/client/hadoop-mapreduce-client-jobclient.jar,/usr/lib/hadoop/client/hadoop-mapreduce-client-common.jar,/usr/lib/hadoop/client/commons-collections-3.2.1.jar,/usr/lib/hadoop/lib/jackson-jaxrs-1.9.13.jar"
HIVE_JARS="/usr/lib/hive/lib/eigenbase-properties-1.1.4.jar,/usr/lib/hive/lib/calcite-avatica-0.9.1.2.2.0.0-2041.jar,/usr/lib/hive/lib/linq4j-0.4.jar,/usr/lib/hive/lib/calcite-core-0.9.1.2.2.0.0-2041.jar,/usr/lib/hive/lib/libfb303-0.9.0.jar,/usr/lib/hive/lib/hive-metastore.jar,/usr/lib/hcatalog/share/hcatalog/hive-hcatalog-pig-adapter.jar,/usr/lib/hcatalog/share/hcatalog/hive-hcatalog-core.jar,/usr/lib/hive/lib/jdo-api-3.0.1.jar,/usr/lib/hive/lib/datanucleus-core-3.2.10.jar,/usr/lib/hive/lib/datanucleus-api-jdo-3.2.6.jar,/usr/lib/hive/lib/commons-compress-1.4.1.jar,/usr/lib/hive/conf/,/usr/lib/hive/lib/hive-cli.jar"
PIG_JARS="./jars/pig-0.14.0-SNAPSHOT-core-h2.jar,./jars/antlr-runtime-3.4.jar,./jars/joda-time-2.1.jar,./jars/automaton-1.11-8.jar,/usr/lib/hadoop/lib/commons-io-2.4.jar,/usr/lib/hadoop/lib/commons-codec-1.4.jar"
LOADER_PATH="/etc/hadoop/conf,${PROGRAM},/usr/lib/hadoop,${HADOOP_CLIENT_JARS},${PIG_JARS},./jars,${HIVE_JARS}"

cd ../Uploader
svn update
cp loco* ../loco/
cd ../loco

$JAVA \
  -Dloader.path=$LOADER_PATH \
  -Djava.library.path=/usr/lib/hadoop/lib/native \
  -Doffer.file=input/offer.csv \
  -Dpreproc.file=./preproc.csv \
  -Djob="complete" \
  -jar ${PROGRAM}