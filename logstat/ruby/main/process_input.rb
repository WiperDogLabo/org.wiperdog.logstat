#process input raw logs and return logs from source (file,socket...)
require 'ruby/main/Input.rb'
pi = ProcessInput.new
return pi.getInputData(conf['input'], mapDefaultInput)
