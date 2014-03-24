trap("SIGINT") { throw :ctrl_c }
 catch :ctrl_c do
require 'socket'
while (true) 
	begin
	sleep(3)
	s = TCPSocket.new 'localhost', 2809
	s.puts "[Socket] : This is a log message from socket !"
	puts "Logs sent : [Socket] : This is a log message from socket !"
	rescue Exception => ex
	puts "Error to sent logs ! May be the connection to server not established ."
	puts ex
	end
end
end
