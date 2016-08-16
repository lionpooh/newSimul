package com.test.simul.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.test.simul.vo.MetricVo;

public class JsonToVo {
	
	MetricVo metricVo;
	Gson gson;
	Logger logger = LoggerFactory.getLogger(JsonToVo.class);
	
	public JsonToVo()	{
		//널값을 허용하고 싶다면
		//gson = new GsonBuilder().serializeNulls().create();
		gson = new Gson();
	}
	
	public List<MetricVo> sampleToMetricVo(List<String> list)	{
		List<MetricVo> metricList = new ArrayList<MetricVo>();
		
		for(int i=0; i < list.size(); i++)	{
			Object obj[] = new Object[1];
			obj = gson.fromJson(list.get(i), Object[].class);

			Map<String, String> map = (Map) obj[0];
			if(map.get("plugin_instance").isEmpty())	{
				map.put("plugin_instance", null);
			}
			
			//aggregation:true -> 안먹힘 - 파싱하는 데서 문제가 발생
			if(map.get("meta") != null)	{
				map.remove("meta");
			}
			//System.out.println("json: " + map.toString());
			metricVo = gson.fromJson(map.toString(), MetricVo.class);
			metricList.add(metricVo);
		}
		
		return metricList;
	}
	
	public List<List<String>> voToJson(List<List<MetricVo>> list, String type)	{
		Gson gson = new Gson();
		List<List<String>> jsonListList = new ArrayList<List<String>>();
		
		//time 설정
		double time = System.currentTimeMillis();
		time = time/1000;
		String jsonTime = null;
		
		if(type.equals("collectd"))	{
			DecimalFormat formatter = new DecimalFormat("0.000");
			jsonTime = formatter.format(time);
		}
		
		else if(type.equals("collectdwin"))	{
			jsonTime = String.valueOf(time);
		}
		
		for(int i=0; i<list.size(); i++)	{
			
			List<String> jsonList = new ArrayList<String>();
			List<MetricVo> metricList = list.get(i);
			
			for(int k=0; k<metricList.size(); k++)	{
				MetricVo metricVo = metricList.get(k);
				metricVo.setTime(jsonTime);
				String json = gson.toJson(metricVo);
				
				/*if(!json.contains("\"plugin\":\"df\""))	{
					
					if(type.equals("collectd"))	{
						
						if(json.contains("\"plugin\":\"memory\""))	{
							
						}
					}
					else if(type.equals("collectdwin"))	{
						
					}
					
				}*/
				
				json = json.replaceAll("\"time.*(?=,\"interval\")", "\"time\":" + jsonTime);
				jsonList.add(json);
			}
			
			jsonListList.add(jsonList);
		}
		
		return jsonListList; 
	}
}
