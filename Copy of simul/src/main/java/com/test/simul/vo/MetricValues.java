package com.test.simul.vo;

import java.util.List;

public class MetricValues {
	
	private	int config_value;
	private List<Double> cpu_user;
	private List<Double> cpu_system;
	private List<Double> cpu_idle;
	private List<Double> memory_used;
	private List<String> df_partitions;
	private List<Double> df_free;
	private List<Double> df_used;
	
	public int getConfig_value() {
		return config_value;
	}
	public void setConfig_value(int config_value) {
		this.config_value = config_value;
	}
	public List<Double> getCpu_user() {
		return cpu_user;
	}
	public void setCpu_user(List<Double> cpu_user) {
		this.cpu_user = cpu_user;
	}
	public List<Double> getCpu_system() {
		return cpu_system;
	}
	public void setCpu_system(List<Double> cpu_system) {
		this.cpu_system = cpu_system;
	}
	public List<Double> getCpu_idle() {
		return cpu_idle;
	}
	public void setCpu_idle(List<Double> cpu_idle) {
		this.cpu_idle = cpu_idle;
	}
	public List<Double> getMemory_used() {
		return memory_used;
	}
	public void setMemory_used(List<Double> memory_used) {
		this.memory_used = memory_used;
	}
	public List<Double> getDf_free() {
		return df_free;
	}
	public void setDf_free(List<Double> df_free) {
		this.df_free = df_free;
	}
	public List<Double> getDf_used() {
		return df_used;
	}
	public void setDf_used(List<Double> df_used) {
		this.df_used = df_used;
	}
	public List<String> getDf_partitions() {
		return df_partitions;
	}
	public void setDf_partitions(List<String> df_partitions) {
		this.df_partitions = df_partitions;
	}
}
