package com.test.simul.daemon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.test.simul.service.CollectdTask;
import com.test.simul.service.CollectdWinTask;
import com.test.simul.service.CreateValue;
import com.test.simul.service.JsonToVo;
import com.test.simul.service.SetConfig;
import com.test.simul.vo.Counter;
import com.test.simul.vo.MetricVo;
import com.test.simul.vo.SettingsConfigVo;
import com.test.simul.vo.SimulProperties;

public class SimDaemonExecutor implements Runnable, Daemon{

	Logger logger = LoggerFactory.getLogger(SimDaemonExecutor.class);
	//config file 경로
	String configPath = System.getProperty("simul.config.path", null);
	
    SetConfig setConfig = new SetConfig(configPath);
    SimulProperties simulProperties = new SimulProperties();
    SettingsConfigVo settingsConfig = null;
    BufferedReader br = null;
    JsonToVo parser = new JsonToVo();
    
	ExecutorService executorService = null;
	List<Future<Counter>> futureList = null;
	
	Counter counter = null;
	String type = null;
	
	public void init(DaemonContext context) throws DaemonInitException, Exception {
		logger.info("simul Daemon initialized...");

    	configPath = System.getProperty("simul.config.path", null);
    	setConfig = new SetConfig(configPath);
        simulProperties = new SimulProperties();
        settingsConfig = null;
        parser = new JsonToVo();
	}
	
	public void start() throws Exception {
		logger.info("simul Daemon started...");
	}

	public void stop() throws Exception {
		logger.info("simul Daemon stoped...");
		CollectdTask.isStop = false;
		CollectdWinTask.isStop = false;
		executorService.shutdown();
		
		for(int i=0; i<futureList.size(); i++)	{
    		try {
				counter = futureList.get(i).get();
			} catch (InterruptedException e) {
				//e.printStackTrace();
				logger.error("error: " + e.getMessage());
			} catch (ExecutionException e) {
				//e.printStackTrace();
				logger.error("error: " + e.getMessage());
			}
    	}
		logger.info("count: " + counter.getTotalCount());
		if(type.equals("collectd"))
			logger.info("offset: " + counter.getTotalCount());
	}

	public void destroy() {
		logger.info("simul Daemon destroyed...");
	}
	
	//main 에서 실행한 내용들 옮기기 
	public void run() {

        List<String> list = new ArrayList<String>();
        String tmp = null;
        
		try {
	    	simulProperties = setConfig.insertConfig();
			type = simulProperties.getSimulType();
			settingsConfig = simulProperties.getSettingsConfig();
			
			//내부에 있는 파일 읽기 -> 수정
			//br = new BufferedReader(new FileReader(settingsConfig.getFilePath()));
			
			br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("logfile/collectd.log")));
			
	    	while((tmp = br.readLine()) != null)	{
	    		list.add(tmp);
	    	}
	    	
	    	CreateValue createValue = new CreateValue(simulProperties);
	    	//만들어진 결과
	    	List<List<MetricVo>> listOfList = createValue.createSimValue(parser.sampleToMetricVo(list));
	    	
	    	//threadpool
	    	int startNum = settingsConfig.getStartNum();
	    	int threadSize = settingsConfig.getThreadSize();
	    	executorService = Executors.newFixedThreadPool(settingsConfig.getThreadSize());
	    	futureList = new ArrayList<Future<Counter>>();
	    	counter = new Counter();
	    	
	    	//task수만큼 
	    	if(type.equals("collectd"))	{
	    		for(int i=0; i<threadSize; i++)	{
	    			Runnable task = new CollectdTask(listOfList, simulProperties, startNum, counter);
	    			futureList.add(executorService.submit(task, counter));
	    			startNum++;
		    	}
	    	} 
	    	
	    	else if(type.equals("collectdwin"))	{
	    		for(int i=0; i<threadSize; i++)	{
		    		Runnable task = new CollectdWinTask(listOfList, simulProperties, startNum, counter);
	    			futureList.add(executorService.submit(task, counter));
	    			startNum++;
		    	}
	    	}
	    	
		} catch(Exception e)	{
        	//e.printStackTrace();
			logger.error("error: " + e.getMessage());
		}
	}
	
}
