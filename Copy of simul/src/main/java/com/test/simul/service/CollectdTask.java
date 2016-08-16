package com.test.simul.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.test.simul.vo.Counter;
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
	String topic;
	JsonToVo parser;
	Counter counter;
	//제어변수
	public static boolean isStop = true;
	
	//부여 받은 넘버
	private int number;
	
	public CollectdTask(List<List<MetricVo>> list, SimulProperties simulProperties, int number, Counter counter)	{
		this.list = list;
		this.simulProperties = simulProperties;
		this.counter = counter;
		properties = simulProperties.getProducerProp();
		settingsConfig = simulProperties.getSettingsConfig();
		hostname = settingsConfig.getHostname();
		topic = settingsConfig.getTopic();
		parser = new JsonToVo();
		this.number = number;
	}
	
	public void run() {
		List<List<MetricVo>> simulList = initList(list);
		List<List<String>> listOfJsonList = new ArrayList<List<String>>();
		listOfJsonList = parser.voToJson(simulList, "collectd");

		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

		while(isStop)	{
			
			try {
				for(int i=0; i<listOfJsonList.size(); i++)	{
					listOfJsonList = parser.voToJson(simulList, "collectd");
					List<String> list = listOfJsonList.get(i);
					for(int k=0; k<list.size(); k++)	{

						producer.send(new ProducerRecord<String, String>(topic, "[" + list.get(k) + "]"), new Callback(){

							public void onCompletion(RecordMetadata metadata, Exception exception) {
								if(exception != null)	{
									//logger.error(exception.getMessage());
									//System.out.println(exception.getMessage());
								}
								counter.addCount(1, metadata);
							}
						});
					}
					Thread.sleep(settingsConfig.getInterval());
					if(!isStop)	{
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		producer.close();
	}
	
	//받은 리스트를 다시 자기 걸로 가공
	public List<List<MetricVo>> initList(List<List<MetricVo>> list)	{
		String host = "";
		if(settingsConfig.getEnablehostname().equals("enable"))	{
			host = hostname;
		}
		else	{
			host = hostname + String.format("%03d", number);
		}
		List<List<MetricVo>> newListList = new ArrayList<List<MetricVo>>();
		try {
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
				System.out.println("plugin: " + copyVo.getPlugin());
				if(copyVo.getPlugin().equals("memory"))
					metricVo.setPlugin_instance("");
				
				metricVo.setType(copyVo.getType());
				metricVo.setType_instance(copyVo.getType_instance());
				metricVo.setValues(copyVo.getValues());
				
				newList.add(metricVo);
			}
			newListList.add(newList);
			
		}
		} catch(Exception e)	{
			e.printStackTrace();
		}
		return newListList;
	}
	
}
