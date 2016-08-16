package com.test.simul.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.test.simul.vo.MetricValues;
import com.test.simul.vo.MetricVo;
import com.test.simul.vo.SimulProperties;

public class CreateValue {

	SimulProperties simulProperties;
	MetricValues metricValues;
	Field fields[];
	Method method[];
	// int dfCount;

	public CreateValue(SimulProperties simulProperties) {
		this.simulProperties = simulProperties;
		metricValues = simulProperties.getMetricValues();
		// Declared가 붙을 경우 private 까지 get set 할 수가 있다.
		fields = metricValues.getClass().getDeclaredFields();
		method = metricValues.getClass().getMethods();
		// metricValues.getClass().getDeclaredMethods()
	}

	// 개선 버전 - 아랫거 사용

	public List<List<MetricVo>> createSimValue(List<MetricVo> list) throws Exception {
		// List<MetricVo>를 config.Value 수 만큼 만드는 것

		// prorperties에서 설정한 partition수만큼 새롭게 추가
		setDiskPartition(list);
		
		Gson gson = new Gson();
		
		List<List<MetricVo>> listOfMetricVoList = new ArrayList<List<MetricVo>>();

		MetricVo metricVo;

		int df_freeCount = 0;
		int df_usedCount = 0;
		
		for (int k = 0; k < metricValues.getConfig_value(); k++) {
			List<MetricVo> metricVoList = new ArrayList<MetricVo>();

			for (int i = 0; i < list.size(); i++) {
				metricVo = list.get(i);

				MetricVo simMetricVo = new MetricVo();
				simMetricVo.setDsnames(metricVo.getDsnames());
				simMetricVo.setDstypes(metricVo.getDstypes());
				simMetricVo.setHost(metricVo.getHost());
				simMetricVo.setInterval(metricVo.getInterval());
				// time
				simMetricVo.setMeta(metricVo.getMeta());

				String plugin = metricVo.getPlugin();
				String type_instance = metricVo.getType_instance();
				String plugin_instance = null;
				String prefix = plugin;
				String suffix = type_instance;

				if (metricVo.getPlugin_instance() != null) {
					plugin_instance = metricVo.getPlugin_instance();
					simMetricVo.setPlugin_instance(plugin_instance);
				}

				// partition수에 맞춰서 들어옴
				if (plugin.equals("df")) {
					double free[];
					double used[];
					
					int size = metricValues.getConfig_value() * metricValues.getDf_partitions().size();
					
					if (type_instance.equals("free") && (df_freeCount < size)) {
						free = new double[1];
						free[0] = metricValues.getDf_free().get(df_freeCount);
						simMetricVo.setValues(free);
						System.out.println("free: " + free[0]);
						df_freeCount++;
					} else if (type_instance.equals("used") && (df_usedCount < size)) {
						used = new double[1];
						used[0] = metricValues.getDf_used().get(df_usedCount);
						System.out.println("used: " + used[0]);
						simMetricVo.setValues(used);
						df_usedCount++;
					} else {
						continue;
					}
					simMetricVo.setPlugin(plugin);
					simMetricVo.setType_instance(type_instance);
					simMetricVo.setType(metricVo.getType());
					
					metricVoList.add(simMetricVo);
					continue;
				}
				simMetricVo.setType(metricVo.getType());
				simMetricVo.setPlugin(plugin);
				simMetricVo.setType_instance(type_instance);

				double values[] = setCustomValue(prefix, suffix, k);
				simMetricVo.setValues(values);

				metricVoList.add(simMetricVo);
			}

			listOfMetricVoList.add(metricVoList);
		}

		return listOfMetricVoList;
	}

	public void setDiskPartition(List<MetricVo> list) {

		int count = 0;
		List<String> listPartition = metricValues.getDf_partitions();
		List<String> type_instance = new ArrayList<String>();
		type_instance.add("free");
		type_instance.add("used");
		
		String type = simulProperties.getSimulType();
		
		if(type.equals("collectd"))	{
			count = 3;
			type_instance.add("reserved");
		}
		else if(type.equals("collectdwin"))	{
			count = 2;
		}
		
		for(int i=0; i<listPartition.size(); i++)	{
			for(int k=0; k<count; k++)	{
				
				list.add(makeMetricDf(type_instance.get(k) , listPartition.get(i)));
				
			}
		}
		
	}

	public MetricVo makeMetricDf(String type_instance, String plugin_instance) {
		
		String gauge[] = new String[1];
		String value[] = new String[1];
		
		MetricVo metricVo = new MetricVo();
		
		gauge[0] = "gauge";
		metricVo.setDstypes(gauge);

		value[0] = "value";
		metricVo.setDsnames(value);
		
		metricVo.setHost("test");
		metricVo.setInterval(10.000);
		//metricVo.setMeta(meta);
		metricVo.setPlugin("df");
		metricVo.setPlugin_instance(plugin_instance);
		//metricVo.setTime(time);
		metricVo.setType("percent_bytes");
		metricVo.setType_instance(type_instance);
		//metricVo.setValues();

		return metricVo;
	}

	// 설정값에서 지정해둔 값으로 가져옴
	public double[] setCustomValue(String inPrefix, String inSuffix, int valueNum) throws Exception {

		double values[] = new double[1];
		// System.out.println("prefix: " + inPrefix);
		if (inPrefix.equals("aggregation")) {
			inPrefix = "cpu";
		}

		for (int i = 0; i < method.length; i++) {
			String methodName = method[i].getName().toLowerCase();

			if (methodName.contains(inPrefix) && methodName.endsWith(inSuffix) && methodName.startsWith("get")) {
				//System.out.println("method: " + methodName + ", " + "prefix: " + inPrefix + ", " + "suffix: " + inSuffix);
				// exception들 많이 발생 - 처리?
				Object value = method[i].invoke(metricValues);
				// list로 cast
				List<Double> list = (List<Double>) value;
				values[0] = list.get(valueNum);
			}
		}
		return values;
	}
}
