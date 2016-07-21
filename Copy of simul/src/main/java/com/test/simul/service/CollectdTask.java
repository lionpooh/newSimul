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
	
	CollectdTask(List<List<MetricVo>> list, SimulProperties simulProperties)	{
		this.list = list;
		this.simulProperties = simulProperties;
		properties = simulProperties.getProducerProp();
	}
	
	public void run() {
		
	}
	
	//받은 리스트를 다시 자기 걸로 가공
	public List<List<MetricVo>> initList(List<List<MetricVo>> list)	{
		
		List<List<MetricVo>> copyListList = new ArrayList<List<MetricVo>>();
		
		for(int k=0; k<list.size(); k++)	{
			
			List<MetricVo> copyList = copyListList.get(k);
			List<MetricVo> newList = new ArrayList<MetricVo>();
			
			for(int i=0; i<copyList.size(); i++)	{
				MetricVo copyVo = copyList.get(i);
				MetricVo metricVo = new MetricVo();
				
				metricVo.setDsnames(copyVo.getDsnames());
				metricVo.setDstypes(copyVo.getDstypes());
				//metricVo.setHost(host);
			}
		}
		
		return list;
	}
	
	public void setProducer()	{
		
	}
}
