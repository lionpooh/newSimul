package com.test.simul.vo;

public class MetricVo {

	private String host;
	private String plugin;
	private String plugin_instance;
	private String type;
	private String type_instance;
	private String time;
	private Double interval;
	private String dstypes[];
	private String dsnames[];
	private double values[];
	private Object meta;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPlugin() {
		return plugin;
	}
	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}
	public String getPlugin_instance() {
		return plugin_instance;
	}
	public void setPlugin_instance(String plugin_instance) {
		this.plugin_instance = plugin_instance;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType_instance() {
		return type_instance;
	}
	public void setType_instance(String type_instance) {
		this.type_instance = type_instance;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Double getInterval() {
		return interval;
	}
	public void setInterval(Double interval) {
		this.interval = interval;
	}
	public String[] getDstypes() {
		return dstypes;
	}
	public void setDstypes(String[] dstypes) {
		this.dstypes = dstypes;
	}
	public String[] getDsnames() {
		return dsnames;
	}
	public void setDsnames(String[] dsnames) {
		this.dsnames = dsnames;
	}
	public double[] getValues() {
		return values;
	}
	public void setValues(double[] values) {
		this.values = values;
	}
	public Object getMeta() {
		return meta;
	}
	public void setMeta(Object meta) {
		this.meta = meta;
	}

}
