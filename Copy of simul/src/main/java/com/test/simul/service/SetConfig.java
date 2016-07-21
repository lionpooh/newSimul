package com.test.simul.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.test.simul.vo.AggConfigVo;
import com.test.simul.vo.MetricValues;
import com.test.simul.vo.SettingsConfigVo;
import com.test.simul.vo.SimulProperties;

public class SetConfig {
	
	Properties prop;
	String configPath;
	SimulProperties simProp;
	
	Logger logger = LoggerFactory.getLogger(SetConfig.class);
	
	public SetConfig()	{
		this.prop = new Properties();
	}
	
	public SetConfig(String configPath)	{
		this();
		this.configPath = configPath;
	}
	
	public SimulProperties insertConfig() throws Exception{
		
		InputStream is;
		simProp = new SimulProperties();
		
		if(configPath != null)	{
			is = new FileInputStream(configPath);
		} else {
			//configPath가 비어 있으면
			logger.debug("property filePath set to default");
			//return null;
			is = ClassLoader.getSystemResourceAsStream("setting.properties");
		}
		//property 파일 load
		prop.load(is);
		String type = prop.getProperty("simulator.type", null);
		
		if(type == null)	{
			//비어 있기 때문에
			logger.error("type value is null(valid value = collectd or collectdwin)");
			System.exit(1);
		}
		//각 value들 유효한지 확인
		setPrefixObject("simulator", prop);
		setPrefixObject("test", prop);
		
		//collectd - to kafka config
		if(type.equals("collectd"))	{
			setPrefixObject("producer", prop);
		}
		//collectdwin - to aggregator config
		else if(type.equals("collectdwin"))	{
			setPrefixObject("aggregator", prop);
		} else {
			//올바른 타입을 입력
			logger.error("invalid type");
			return null;
		}
		
		simProp.setSimulType(type);
		
		return simProp;
	}
	
	public void setPrefixObject(String inPrefix, Properties inProp)	throws Exception{
		//Object prefixedObj = null;
		/*Iterator<Object> keyIterator = inProp.keySet().iterator();
		String tmp;*/
		
		if(inPrefix.equals("simulator"))	{
			
			String time = "10";
			String filepath = null;
			String hostname = null;
			int convertTime = 10000;
			int threadSize = 1;
			
			//common-config
			SettingsConfigVo settingsConfig = new SettingsConfigVo();
			//file path => null인 경우의 처리
			filepath = inProp.getProperty("simulator.filepath", null);
			if(filepath != null)
				settingsConfig.setFilePath(filepath);
			else	{
				logger.error("sample filepath or file is not valid!");
				System.exit(0);
			}
			
			//hostName enable일경우의 기능
			settingsConfig.setEnablehostname(inProp.getProperty("simulator.enablehostname", "disable"));
			
			hostname = inProp.getProperty("simulator.hostname", null);
			if(hostname != null)	{
				settingsConfig.setHostname(hostname);
			}
			//default 10second, more than 1sec
			time = inProp.getProperty("simulator.interval", "10");
			convertTime = Integer.parseInt(time)*1000;
			
			if(convertTime > 0)
				settingsConfig.setInterval(convertTime);
			else	{
				logger.debug("setting interval to default value: 10000ms");
				settingsConfig.setInterval(10000);
			}
			
			threadSize = Integer.parseInt(inProp.getProperty("simulator.thread.size", "1"));
			if(threadSize > 0)	
				settingsConfig.setThreadSize(threadSize);
			else	{
				logger.debug("setting threadSize to default value: create 1 thread");
				settingsConfig.setThreadSize(1);
			}
			
			simProp.setSettingsConfig(settingsConfig);
		}
				
		else if(inPrefix.equals("test"))	{
			MetricValues metricValues = new MetricValues();
			metricValues.setConfig_value(Integer.parseInt(inProp.getProperty("test.config.value", "4")));
			metricValues.setCpu_system(spliterToList(inProp.getProperty("test.cpu.system"), metricValues.getConfig_value()));
			metricValues.setCpu_user(spliterToList(inProp.getProperty("test.cpu.user"), metricValues.getConfig_value()));
			metricValues.setCpu_idle(spliterToList(inProp.getProperty("test.cpu.idle"), metricValues.getConfig_value()));
			metricValues.setMemory_used(spliterToList(inProp.getProperty("test.memory.used"), metricValues.getConfig_value()));
			
			metricValues.setDf_partitions(splitePartitionToList(inProp.getProperty("test.df.partition"), metricValues.getConfig_value()));
			
			//df - start
			int partitionValueSize = (metricValues.getConfig_value() * metricValues.getDf_partitions().size());
			
			metricValues.setDf_used(
					spliterToList(inProp.getProperty("test.df.used"), partitionValueSize));
			
			metricValues.setDf_free(
					spliterToList(inProp.getProperty("test.df.free"), partitionValueSize));
			//df - end
			
			simProp.setMetricValues(metricValues);
		}
		
		else if(inPrefix.equals("producer"))	{
			Properties producerProp = new Properties();
			producerProp.setProperty("topic", inProp.getProperty("producer.topic"));
			producerProp.setProperty("bootstrap.servers", "producer.bootstrap.servers");
			producerProp.setProperty("acks", inProp.getProperty("producer.acks"));
			producerProp.setProperty("retries", inProp.getProperty("producer.retries"));
			producerProp.setProperty("retry.backoff.ms", inProp.getProperty("producer.retry.backoff.ms"));
			producerProp.setProperty("batch.size", inProp.getProperty("producer.batch.size"));
			producerProp.setProperty("linger.ms", inProp.getProperty("producer.linger.ms"));
			producerProp.setProperty("buffer.memory", inProp.getProperty("producer.buffer.memory"));
			producerProp.setProperty("send.buffer.bytes", inProp.getProperty("producer.send.buffer.bytes"));
			producerProp.setProperty("key.serializer", inProp.getProperty("producer.key.serializer"));
			producerProp.setProperty("value.serializer", inProp.getProperty("producer.value.serializer"));
			
			simProp.setProducerProp(producerProp);
		}
		
		else if(inPrefix.equals("aggregator"))	{
			AggConfigVo aggConfig = new AggConfigVo();
			
			String ip = inProp.getProperty("aggregator.ip");
			String port = inProp.getProperty("aggregator.port");
			
			aggConfig.setAggIp(ip);
			aggConfig.setAggPort(port);
			simProp.setAggConfig(aggConfig);
		}
		
	}
	
	public List<Double> spliterToList(String inStr, int value)	throws Exception	{
	
		List<Double> list = new ArrayList<Double>();
		
		if(inStr != null)	{
			//split
			logger.debug("before valueSet: " + inStr);
			StringTokenizer st = new StringTokenizer(inStr, ",");
			
			while(st.hasMoreTokens())	{
				String temp = st.nextToken();
				//number format exception
				list.add(Double.parseDouble(temp));
			}
			
			if((list.size() != value))	{

				if(list.size() > value)	{
					int check = value;					
					while(list.size() != value)	{		
						list.remove(check);
						check = check - 1;
					}
				}
				else if(list.size() < value)	{
					int check = list.size();
					while(list.size() != value)	{
						list.add(check, (double)0);
						check = check + 1;
					}
				}
				logger.debug("listSize, value: " + list.size() + ", " + value);
				logger.debug("after valueSet: " + list);
			}
			
		} else {
			
			return null;
		}
		
		return list;
	}
	
	public List<String> splitePartitionToList(String inStr, int value)	throws Exception	{
		List<String> list = new ArrayList<String>();
		
		if(inStr != null)	{
			StringTokenizer st = new StringTokenizer(inStr, ",");
			while(st.hasMoreTokens())	{
				String temp = st.nextToken();
				list.add(temp);
			}
			//value보다 사이즈가 크면 그 사이즈만큼은 자르기 -> 조금 다르게 고칠방법
			if((list.size() > value))	{
				int check = value;
				while(list.size() != value)	{
					list.remove(check);
				}
			}
		}
		
		return list;
	}
	
}
