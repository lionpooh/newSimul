package com.test.simul;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

public class SimulMain {
    public static void main( String[] args ) {
    	
    	Logger logger = LoggerFactory.getLogger(SimulMain.class);
    	
    	//config file 경로
    	String configPath = System.getProperty("simul.config.location", null);
    	
        SetConfig setConfig = new SetConfig(configPath);
        SimulProperties simulProperties = new SimulProperties();
        SettingsConfigVo settingsConfig = null;
        BufferedReader br = null;
        JsonToVo parser = new JsonToVo();
        
        List<String> list = new ArrayList<String>();
        String tmp = null;
    	String type = null;
        
        try {
        	simulProperties = setConfig.insertConfig();
			type = simulProperties.getSimulType();
			settingsConfig = simulProperties.getSettingsConfig();
			
			if(type.equals("collectd"))	
				br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("logfile/collectd.log")));
			else if(type.equals("collectdwin"))
				br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("logfile/collectdwin.log")));
			else	{
				logger.error("type is not valid...");
				System.exit(1);
			}
			
	    	while((tmp = br.readLine()) != null)	{
	    		list.add(tmp);
	    	}
	    	
	    	CreateValue createValue = new CreateValue(simulProperties);
	    	//만들어진 결과
	    	List<List<MetricVo>> listOfList = createValue.createSimValue(parser.sampleToMetricVo(list));
	    	
	    	//threadpool
	    	int startNum = settingsConfig.getStartNum();
	    	int threadSize = settingsConfig.getThreadSize();
	    	ExecutorService executorService = Executors.newFixedThreadPool(settingsConfig.getThreadSize());
	    	List<Future<Counter>> futureList = new ArrayList<Future<Counter>>();
	    	Counter counter = new Counter();
	    	logger.info("----------------------------------------------------------");
	    	logger.info("------------------start simulator!!!----------------------");
	    	logger.info("----------------------------------------------------------");
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
	    	
	    	Scanner sc = new Scanner(System.in);
	    	String cmd;
	    	
	    	while(true)	{
	    		cmd = sc.nextLine();	
	    		if(cmd.equals("exit"))	{
	    			CollectdTask.isStop = false;
	    			CollectdWinTask.isStop = false;
	    			executorService.shutdown();
	    			break;
	    		} else	{
	    			//logger.debug("enter exit");
	    		}
	    	}
	    	
	    	for(int i=0; i<futureList.size(); i++)	{
	    		try {
					counter = futureList.get(i).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
	    	}
	    	
	    	logger.info("count: " + counter.getTotalCount());
	    	//offset if collectd
	    	if(type.equals("collectd"))
	    		logger.info("offset: " + counter.getTotaloffset());
	    	
	    	logger.info("----------------------------------------------------------");
	    	logger.info("-----------------shutdown simulator!!!--------------------");
	    	logger.info("----------------------------------------------------------");
	    	
	    	sc.close();
	    	
        } catch(Exception e)	{
        	e.printStackTrace();
        }
    }
}
