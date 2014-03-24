package org.wiperdog.logstat.service;

import java.util.HashMap;

/**
 * LogStat service
 * @author nguyenxuanluong
 *
 */
public interface LogStat {
	public String runLogStat(HashMap<String, Object> conf);
}
