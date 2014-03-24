@echo off
set log_type="%1"
if %log_type% == "log4j" (
	if exist log4j\Log4jSocketAppenderExample.class (
		del log4j\Log4jSocketAppenderExample.class
	)

	if not exist log4j\log4j-1.2.14.jar (
		set found_maven=
		set found_java=
		for %%e in (%PATHEXT%) do (

		  for %%X in (mvn%%e) do (
			if not defined found_maven (
			  set found_maven=%%~$PATH:X
			)
		  )
		)	
		if [%found_maven%] == [] (
			echo Maven not installed. Aborting.
			exit /b
		)
		mvn org.apache.maven.plugins:maven-dependency-plugin:2.4:get -Dartifact=log4j:log4j:1.2.14 -Ddest=log4j\log4j-1.2.14.jar -Dtransitive=false                   
	)
	javac -cp log4j\*;. log4j\Log4jSocketAppenderExample.java
	if exist log4j\Log4jSocketAppenderExample.class (
		echo Start to run log4j appender to send logs - Press Ctrl + C to stop .
		java -cp log4j\*;. log4j.Log4jSocketAppenderExample
	) else (	
		echo Can not run log4j appender to send logs !
	)
) else (
	if %log_type% == "socket" (
		echo Start to to send logs via socket - Press Ctrl + C to stop .
		ruby socket/socket.rb
	) else (
		echo Incorrect arguments ; Argument must be 'socket' or 'log4j'
		echo Example: C:\logs_generator.bat log4j
	)
)
