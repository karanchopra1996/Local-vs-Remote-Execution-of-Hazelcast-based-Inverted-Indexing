#!/bin/sh

java -cp $HOME/hazelcast-5.1.3/lib/hazelcast-5.1.3.jar:. -Dhazelcast.config=$HOME/hazelcast-5.1.3/config/hazelcast.xml com.hazelcast.console.ConsoleApp
