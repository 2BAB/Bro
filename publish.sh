#!/usr/bin/env bash
./gradlew -PbroPublish=bro-common -PdryRun=false :bro-common:clean :bro-common:bintrayUpload
./gradlew -PbroPublish=bro-annotations -PdryRun=false :bro-annotations:clean :bro-annotations:bintrayUpload
./gradlew -PbroPublish=bro-compiler -PdryRun=false :bro-compiler:clean :bro-compiler:bintrayUpload
./gradlew -PbroPublish=bro-gradle-plugin -PdryRun=false :bro-gradle-plugin:clean :bro-gradle-plugin:bintrayUpload
./gradlew -PbroPublish=bro -PdryRun=false :bro:clean :bro:assembleRelease :bro:bintrayUpload
./gradlew deployRelease