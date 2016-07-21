package com.test.simul.vo;

import java.util.Properties;

public class SimulProperties {

	private Properties producerProp;
	private String simulType;
	private int threadSize;
	private MetricValues metricValues;
	private AggConfigVo aggConfig;
	private SettingsConfigVo settingsConfig;
	
	public SimulProperties() {
		this.metricValues = new MetricValues();
	}

	public Properties getProducerProp() {
		return producerProp;
	}

	public void setProducerProp(Properties producerProp) {
		this.producerProp = producerProp;
	}

	public String getSimulType() {
		return simulType;
	}

	public void setSimulType(String simulType) {
		this.simulType = simulType;
	}

	public int getThreadSize() {
		return threadSize;
	}

	public void setThreadSize(int threadSize) {
		this.threadSize = threadSize;
	}

	public MetricValues getMetricValues() {
		return metricValues;
	}

	public void setMetricValues(MetricValues metricValues) {
		this.metricValues = metricValues;
	}

	public AggConfigVo getAggConfig() {
		return aggConfig;
	}

	public void setAggConfig(AggConfigVo aggConfig) {
		this.aggConfig = aggConfig;
	}

	public SettingsConfigVo getSettingsConfig() {
		return settingsConfig;
	}

	public void setSettingsConfig(SettingsConfigVo settingsConfig) {
		this.settingsConfig = settingsConfig;
	}
	
	
}
