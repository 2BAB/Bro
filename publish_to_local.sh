#!/usr/bin/env bash

./gradlew -PbroPublish=bro-common :bro-common:clean :bro-common:publishToMavenLocal
./gradlew -PbroPublish=bro-annotations :bro-annotations:clean :bro-annotations:publishToMavenLocal
./gradlew -PbroPublish=bro-compiler :bro-compiler:clean :bro-compiler:publishToMavenLocal
./gradlew -PbroPublish=bro-gradle-plugin :bro-gradle-plugin:clean :bro-gradle-plugin:publishToMavenLocal
./gradlew -PbroPublish=bro :bro:clean :bro:assembleRelease :bro:publishReleasePublicationToMavenLocal

# ./gradlew -PbroPublish=bro-liverealod-plugin :bro-liverealod-plugin:clean :bro-liverealod-plugin:publishToMavenLocal