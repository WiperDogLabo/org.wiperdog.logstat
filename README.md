org.wiperdog.logstat
====================

(Source code + testcase + any stuff for wiperdog's logs monitoring)

logstat bundle for monitoring log
Process has 3 parts: Process input, process filter and process output
For details of process, you can see in folder process

Usage : 
- Build logstat with maven
- Call logstat service :
		
		   LogStat ls ;
          //logstat directory is seperated from  org.wiperdog.logstat bundle ,this directory contains ruby script to process logs monitoring
        	   String logstatDir = "/path/to/logstat/directory"
        	   ls.runLogStat(logstatDir,configuration)
 
