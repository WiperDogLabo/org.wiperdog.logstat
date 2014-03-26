package org.wiperdog.logstat.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.wiperdog.jrubyrunner.JrubyRunner;
import org.wiperdog.logstat.libs.Common;
import org.wiperdog.logstat.service.LogStat;

/**
 * Implement of LogStat service
 * 
 * @author nguyenxuanluong
 * 
 */
public class LogStatImpl implements LogStat {
	JrubyRunner jrService;
	
	public LogStatImpl(JrubyRunner jrService) {
		this.jrService = jrService;
	}

	/**
	 * Monitoring logs
	 * 
	 * @param args
	 *            : An array of paramters
	 */
	public Map<String, Object> runLogStat(String logStatDir, Map<String, Object> conf) {
		Map<String, Object> dataFinal = null;
		try {
			// Get default values
			HashMap<String, Object> mapDefaultInput = new HashMap<String, Object>();
			HashMap<String, Object> mapDefaultOutput = new HashMap<String, Object>();
			Common common = new Common(logStatDir + "/conf/defaultInput.properties");
			HashMap<String, Object> mapDefault = common.getInputConfig();
			mapDefaultInput = (HashMap<String, Object>) mapDefault.get("input");
			mapDefaultOutput = (HashMap<String, Object>) mapDefault.get("output");
			Map<String, Object> inputData = new HashMap<String, Object>();
			// Ruby process
			System.out.println("LogStartService Running ...");
			inputData.put("conf", conf);
			inputData.put("log_stat_dir", logStatDir);
			inputData.put("mapDefaultInput", mapDefaultInput);
			inputData.put("mapDefaultOutput", mapDefaultOutput);
			String procInputFile = logStatDir + "/ruby/main/process_input.rb";
			String procFilterFile = logStatDir + "/ruby/main/process_filter.rb";
			String procOutputFile = logStatDir + "/ruby/main/process_output.rb";
			List<String> libpaths = new ArrayList<String>();
			libpaths.add(logStatDir);
			Object dataInput = jrService.execute(procInputFile, inputData,libpaths);
			if (dataInput != null) {
				inputData.put("dataInput", dataInput);
				Object dataFiltered = jrService.execute(procFilterFile, inputData,libpaths);
				if (dataFiltered != null) {
					inputData.put("dataFiltered", dataFiltered);
					dataFinal = (Map<String, Object>) jrService.execute(procOutputFile, inputData,libpaths);
				}
			}
			System.out.println("LogStartService Completed ...");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dataFinal;
	}

}
