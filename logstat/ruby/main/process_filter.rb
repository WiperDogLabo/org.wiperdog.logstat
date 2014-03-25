require 'logstat/ruby/main/Filter.rb'
pf = ProcessFilter.new
filter_type = conf['filter']['filter_type']
filter_conf = conf['filter']['filter_conf']
return pf.filter(filter_type, filter_conf, dataInput)
