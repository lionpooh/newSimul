package com.test.simul.vo;

import org.apache.kafka.clients.producer.RecordMetadata;

public class Counter {

	long totalCount;
	long totaloffset;
	long p1;
	long p2;
	long p3;
	
	public synchronized void addCount(long value)	{
		totalCount+=value;
	}
	
	public synchronized void addCount(long value, RecordMetadata metadata)	{
		totalCount+=value;
		
		switch(metadata.partition())	{
		
		case 0:
			p1 = metadata.offset();
			break;
		case 1:
			p2 = metadata.offset();
			break;
		case 2:
			p3 = metadata.offset();
			break;
		}
		
		totaloffset = p1+p2+p3 +1;
	}
}
