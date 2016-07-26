package com.test.simul.daemon;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimDaemonExecutor implements Runnable, Daemon{

	Logger logger = LoggerFactory.getLogger(SimDaemonExecutor.class);
	
	//main 에서 실행한 내용들 옮기기 
	public void run() {
		
	}

	public void init(DaemonContext context) throws DaemonInitException, Exception {
		logger.info("simul Daemon initialized...");
	}
	
	public void start() throws Exception {
		logger.info("simul Daemon started...");
	}

	public void stop() throws Exception {
		logger.info("simul Daemon stoped...");
	}

	public void destroy() {
		logger.info("simul Daemon destroyed...");
	}
	
}
