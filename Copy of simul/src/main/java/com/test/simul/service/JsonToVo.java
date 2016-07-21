package com.test.simul.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.test.simul.vo.MetricVo;

public class JsonToVo {
	
	MetricVo metricVo;
	Gson gson;
	
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
	
	public void voToJson()	{
		
	}
}
