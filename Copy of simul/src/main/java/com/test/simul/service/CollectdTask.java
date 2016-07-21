package com.test.simul.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.test.simul.vo.MetricVo;
import com.test.simul.vo.SettingsConfigVo;
import com.test.simul.vo.SimulProperties;

public class CollectdTask implements Runnable{

	//가공된 데이터
	List<List<MetricVo>> list;
	SimulProperties simulProperties;
	SettingsConfigVo settingsConfig;
	Properties properties;
	String hostname;
	JsonToVo parser;
	
	//제어변수
	static boolean isStop = true;
	
	//부여 받은 넘버
	private int number;
	
	CollectdTask(List<List<MetricVo>> list, SimulProperties simulProperties, int number)	{
		this.list = list;
		this.simulProperties = simulProperties;
		properties = simulProperties.getProducerProp();
		settingsConfig = simulProperties.getSettingsConfig();
		hostname = settingsConfig.getHostname();
		parser = new JsonToVo();
	}
	
	public void run() {
		
		List<List<MetricVo>> simulList = initList(list);
		List<List<String>> listOfJsonList = new ArrayList<List<String>>();
		//vo -> json / 할때 마다 시간이 바뀌게
		parser.voToJson();
		//
		/*while(isStop)	{
			
		}*/
		
	}
	
	//받은 리스트를 다시 자기 걸로 가공
	public List<List<MetricVo>> initList(List<List<MetricVo>> list)	{
		
		String host = hostname + String.format("%03d", number);
		
		List<List<MetricVo>> newListList = new ArrayList<List<MetricVo>>();
		
		for(int k=0; k<list.size(); k++)	{
			
			//list
			List<MetricVo> copyList = list.get(k);
			List<MetricVo> newList = new ArrayList<MetricVo>();
			
			for(int i=0; i<copyList.size(); i++)	{
				MetricVo copyVo = copyList.get(i);
				MetricVo metricVo = new MetricVo();
				
				metricVo.setDsnames(copyVo.getDsnames());
				metricVo.setDstypes(copyVo.getDstypes());
				metricVo.setHost(host);
				metricVo.setInterval(copyVo.getInterval());
				metricVo.setPlugin(copyVo.getPlugin());
				metricVo.setPlugin_instance(copyVo.getPlugin_instance());
				metricVo.setType(copyVo.getType());
				metricVo.setType_instance(copyVo.getType_instance());
				metricVo.setValues(copyVo.getValues());
				
				newList.add(metricVo);
			}
			
			newListList.add(newList);
			
		}
		
		return list;
	}
	
	public void setProducer()	{
		
	}
}
