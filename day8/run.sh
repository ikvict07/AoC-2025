#!/usr/bin/env bash

java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -XX:TieredStopAtLevel=1 -jar day8.jar $1