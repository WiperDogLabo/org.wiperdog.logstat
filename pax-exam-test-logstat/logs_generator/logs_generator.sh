#!/bin/bash

if [ "$1" == "log4j" ] ;then
	if [ -f log4j/Log4jSocketAppenderExample.class ]; then	
		 rm -rf log4j/Log4jSocketAppenderExample.class
	fi
	if [ ! -f log4j/log4j-1.2.14.jar ]; then	
		
		 #command -v mvn >/dev/null 2>&1 || { echo "Maven not installed. Aborting." >&2; exit 1;}
		 mvn org.apache.maven.plugins:maven-dependency-plugin:2.4:get \
                   -Dartifact=log4j:log4j:1.2.14 \
                   -Ddest=log4j/log4j-1.2.14.jar \
                   -Dtransitive=false \
                   
	fi
	#command -v java >/dev/null 2>&1 || { echo "Java not installed. Aborting." >&2; exit 1;}
	javac -cp log4j/*:.  log4j/Log4jSocketAppenderExample.java
	if [ -f log4j/Log4jSocketAppenderExample.class ]; then
			chmod 755 log4j/Log4jSocketAppenderExample.class	
			echo "Start to run log4j appender to send logs - Press Ctrl + C to stop ."
			java -cp log4j/*:. log4j.Log4jSocketAppenderExample
	else 
			echo "Can not run log4j appender to send logs !"
	fi
else
	if [ "$1" == "socket" ] ;then
		echo "Start to to send logs via socket - Press Ctrl + C to stop ."
		 ruby socket/socket.rb
	else
		echo "Incorrect arguments : Argument must be 'socket' or 'log4j'"
		echo "Example : /logs_generator.sh log4j"
	fi
fi
