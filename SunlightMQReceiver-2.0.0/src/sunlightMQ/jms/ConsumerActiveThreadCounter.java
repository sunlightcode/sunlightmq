package sunlightMQ.jms;

public class ConsumerActiveThreadCounter {
	private int activeThreadCounters = 0;
	
	public ConsumerActiveThreadCounter(int initValue) {
		activeThreadCounters = initValue;
	}
	
	public Integer addCounter() {
		return activeThreadCounters = activeThreadCounters + 1;
	}
	
	public Integer reduceCounter() {
		return activeThreadCounters = activeThreadCounters - 1;
	}
	
	public Integer getCounter() {
		return activeThreadCounters;
	}
}
