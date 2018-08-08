package com.gmail.ivanjermakov1.collageprocessor.util;

import java.util.Date;

public class Stopwatch {
	
	enum Status {
		SLEEP,
		STARTED,
		STOPPED;
	}
	
	Date start;
	Date end;
	Status status = Status.SLEEP;
	
	public void start() {
		if (status != Status.SLEEP) throw new IllegalStateException("com.gmail.ivanjermakov1.utilgmail.ivanjermakov1.Stopwatch is already started");
		
		status = Status.STARTED;
		start = new Date();
	}
	
	public long getMilliseconds() {
		if (status != Status.STARTED) throw new IllegalStateException("Unable to get time");
		
		return (new Date().getTime() - start.getTime());
	}
	
	public long stop() {
		if (status != Status.STARTED) throw new IllegalStateException("Unable to get time");

		status = Status.STOPPED;
		end = new Date();
		
		return end.getTime() - start.getTime();
	}
	
	public void reset() {
		if (status == Status.STARTED) throw new IllegalStateException("Unable to reset ongoing stopwatch");

		start = null;
		end = null;
		status = Status.SLEEP;
	}
	
}
