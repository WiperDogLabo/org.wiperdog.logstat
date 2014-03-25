require 'logstat/ruby/main/Output.rb'

po = ProcessOutput.new
 dataFromOutput = po.output(dataFiltered,conf['output'], mapDefaultOutput)
if(!dataFromOutput.nil?)
  listLogs = java.util.ArrayList.new
  dataFromOutput['list_logs'].each do |e|
    if(e.is_a? Hash)
      e = java.util.HashMap.new(e)
    end
    listLogs.add(e)
  end
  dataFinal = java.util.HashMap.new
  dataFinal.put("persistent_data", dataFromOutput['persistent_data']);
  dataFinal.put("list_logs", listLogs);
  return  dataFinal
end