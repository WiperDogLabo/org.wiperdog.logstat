##
# get logs from Log4j via socket
# @param host
# @param port
# @param timeout - the job running interval
# @return listLogs
##
def getDataLog4j(port,timeout,host)

  require 'socket'
  require "java"
  require "jruby/serialization"
  require "timeout"
  $LOAD_PATH.each {|path|
    if path.include? "log4j"
      require path
    end
  }

  if(timeout.is_a? String)
    timeout = timeout.to_i
  end
  if(port.is_a? String)
    port = timeout.to_i
  end
  begin
    server = TCPServer.open(host,port)  # Socket to listen on @port
    listLogs = Array.new
    start_time = Time.now

    end_time = start_time
    #Check if process not timeout
    while(end_time - start_time < timeout ) do
      
    begin
      Timeout.timeout(timeout) do
        Thread.start(server.accept) do |socket|
          begin
            #Read object from InputStream
            ois = JRubyObjectInputStream.new(java.io.BufferedInputStream.new(socket.to_inputstream))
            event = Hash.new
            #log4j_obj is org.apache.log4j.spi.LoggingEvent
            log4j_obj = ois.readObject
            event["message"] = log4j_obj.getRenderedMessage
            event["path"] = log4j_obj.getLoggerName
            event["priority"] = log4j_obj.getLevel.toString
            event["logger_name"] = log4j_obj.getLoggerName
            event["thread"] = log4j_obj.getThreadName
            event["time"] = Time.at(log4j_obj.timeStamp/1000).strftime("%F %T").to_s
            #logs_string = event["priority"]  +" [" + event['time'] + "]" +" [" + event["thread"] + "] " + " [" + event["logger_name"] +"] " + " : " + event["message"]
            listLogs << event
          rescue Exception => e
            puts "[Logstat]  :  #{e}"
          ensure
            socket.close
          end
        end        
      end
    rescue Timeout::Error
      puts "[Logstat] : Timeout error !"      
    end
      end_time = Time.now
    end
  rescue Exception => ex
    puts "[Logstat]  :  #{ex}"
  ensure
      if(!server.nil?)
        server.close
  end
  end
  finalData = Hash.new
  finalData["list_logs"] = listLogs
  return finalData
end
