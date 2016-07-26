package com.test.simul;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    	
    	//config file 경로
    	String configPath = System.getProperty("simul.config.path", null);
    	
        SetConfig setConfig = new SetConfig(configPath);
        SimulProperties simulProperties = new SimulProperties();
        SettingsConfigVo settingsConfig = null;
        BufferedReader br = null;
        JsonToVo parser = new JsonToVo();
        
        List<String> list = new ArrayList<String>();
        String tmp = null;
        
        //String configPath = null;
    	String type = null;
        
        try {
        	simulProperties = setConfig.insertConfig();
			type = simulProperties.getSimulType();
			settingsConfig = simulProperties.getSettingsConfig();
			
			//내부에 있는 파일 읽기 -> 수정
			//br = new BufferedReader(new FileReader(settingsConfig.getFilePath()));
			br = new BufferedReader(new FileReader());
			
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
	    			break;
	    		} else	{
	    			//logger.debug("enter exit");
	    		}
	    	}
	    	
	    	for(int i=0; i<futureList.size(); i++)	{
	    		try {
					counter = futureList.get(i).get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	
	    	sc.close();
	    	
	    	//만들어진 task로 threadPool 동작
        } catch(Exception e)	{
        	e.printStackTrace();
        }
    }
}
