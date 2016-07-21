package com.test.simul;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.test.simul.service.CreateValue;
import com.test.simul.service.JsonToVo;
import com.test.simul.service.SetConfig;
import com.test.simul.vo.MetricVo;
import com.test.simul.vo.SettingsConfigVo;
import com.test.simul.vo.SimulProperties;

public class SimulMain {
    public static void main( String[] args ) {
        SetConfig setConfig = new SetConfig();
        SimulProperties simulProperties = new SimulProperties();
        SettingsConfigVo settingsConfig = null;
        BufferedReader br = null;
        JsonToVo parser = new JsonToVo();
        
        List<String> list = new ArrayList<String>();
        String tmp = null;
        
        String configPath = null;
    	String type = null;
        
        try {
        	simulProperties = setConfig.insertConfig();
			type = simulProperties.getSimulType();
			settingsConfig = simulProperties.getSettingsConfig();
			//System.out.println("type: " + type);
			br = new BufferedReader(new FileReader(settingsConfig.getFilePath()));
			
	    	while((tmp = br.readLine()) != null)	{
	    		list.add(tmp);
	    	}
	    	
	    	CreateValue createValue = new CreateValue(simulProperties);
	    	//만들어진 결과
	    	List<List<MetricVo>> listOfList = createValue.createSimValue(parser.sampleToMetricVo(list));
        	
	    	/*for(int i=0; i<listOfList.size(); i++)	{
	    		List<MetricVo> mList = listOfList.get(i);
	    		for(int k=0; k<mList.size(); k++)	{
	    			System.out.println("main: " + mList.get(k).getPlugin() 
	    					+ " - " + mList.get(k).getType_instance() 
	    					+ " - " + mList.get(k).getPlugin_instance()
	    					+ " - value: " + mList.get(k).getValues()[0]);
	    		}
	    	}*/
	    	
	    	if(type.equals("collectd"))	{
	    		
	    	} 
	    	
	    	else if(type.equals("collectdwin"))	{
	    		
	    	}
	    	
	    	//만들어진 task로 threadPool 동작
        } catch(Exception e)	{
        	e.printStackTrace();
        }
    }
}
