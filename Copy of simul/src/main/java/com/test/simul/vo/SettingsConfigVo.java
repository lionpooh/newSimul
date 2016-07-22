package com.test.simul.vo;

public class SettingsConfigVo {

	private String filePath;
	
	private int threadSize;
	private int interval;
	private int startNum;
	
	private String enablehostname;
	private String hostname;
	private String topic;
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public int getThreadSize() {
		return threadSize;
	}
	public void setThreadSize(int threadSize) {
		this.threadSize = threadSize;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public int getStartNum() {
		return startNum;
	}
	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}
	public String getEnablehostname() {
		return enablehostname;
	}
	public void setEnablehostname(String enablehostname) {
		this.enablehostname = enablehostname;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	
}
