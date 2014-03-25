package org.wiperdog.logstat.service;

import java.util.Map;

/**
 * LogStat service
 * @author nguyenxuanluong
 *
 */
public interface LogStat {
	public Map<String,Object> runLogStat(String logStatDir,Map<String,Object> conf);
}
