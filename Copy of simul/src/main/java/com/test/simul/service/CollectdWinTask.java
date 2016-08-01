package com.test.simul.service;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.test.simul.vo.AggConfigVo;
import com.test.simul.vo.Counter;
import com.test.simul.vo.MetricVo;
import com.test.simul.vo.SettingsConfigVo;
import com.test.simul.vo.SimulProperties;

public class CollectdWinTask implements Runnable{

	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String CONTENT_TYPE = "application/json";
	private String POST_URL = null;
	private int number;
	private String hostname;
	
	List<List<MetricVo>> list;
	SimulProperties simulProperties;
	SettingsConfigVo settingsConfig;
	AggConfigVo aggConfigVo;
	Counter counter;
	JsonToVo parser;
	
	HttpURLConnection conn;
	URL obj;
	
	//제어변수
	public static boolean isStop = true;
	
	public CollectdWinTask(List<List<MetricVo>> list, SimulProperties simulProperties, int number, Counter counter)	{
		this.list = list;
		this.simulProperties = simulProperties;
		this.number = number;
		settingsConfig = simulProperties.getSettingsConfig();
		this.aggConfigVo = simulProperties.getAggConfig();
		this.hostname = settingsConfig.getHostname();
		this.counter = counter;
		parser = new JsonToVo();
	}
	
	public void run() {
		String ip = aggConfigVo.getAggIp();
		String port = aggConfigVo.getAggPort();
		OutputStream os = null;
		List<List<MetricVo>> simulList = initList(list);
		List<List<String>> listOfJsonList = new ArrayList<List<String>>();
		listOfJsonList = parser.voToJson(simulList, "collectd");
		
		try {
			POST_URL = initHttpURL(ip, port);
			
			while(isStop)	{
				
				for(int i=0; i<listOfJsonList.size(); i++)	{
					List<String> list = listOfJsonList.get(i);
					
					obj = new URL(POST_URL);
					conn = (HttpURLConnection) obj.openConnection();
					conn.setRequestMethod("POST");
					conn.setDoOutput(true);
					conn.setRequestProperty("User-Agent", USER_AGENT);
					conn.setRequestProperty("Content-Type", CONTENT_TYPE);						
					os = conn.getOutputStream();
					DataOutputStream dos = new DataOutputStream(os);
					System.out.println("send: " + list.toString());
					counter.addCount(list.size());
					dos.writeBytes(list.toString());
					dos.flush();
					dos.close();
					
					int responseCode = conn.getResponseCode();
					
					Thread.sleep(settingsConfig.getInterval());
					if(!isStop)	{
						break;
					}
				}
				
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}
	
	public String initHttpURL(String ip, String port) throws Exception	{
		String POST_URL = "http://" + ip + ":" + port + "/cdw";
		
		return POST_URL;
	}
	
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
				metricVo.setValue(copyVo.getValue());
				
				newList.add(metricVo);
			}
			
			newListList.add(newList);
			
		}
		
		return newListList;
	}
}
