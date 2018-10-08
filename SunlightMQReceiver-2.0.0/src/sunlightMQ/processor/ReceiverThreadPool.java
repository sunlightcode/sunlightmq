package sunlightMQ.processor;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import SunlightFrame.config.AppConfig;
import SunlightFrame.log.AppLogger;
import sunlightMQ.AppKeys;

public class ReceiverThreadPool {
	private static ReceiverThreadPool instance = new ReceiverThreadPool();
	private ThreadPoolExecutor threadPool;
	private HashMap<String, Integer> queueThreadCounterMap = new HashMap<String, Integer>();
	private HashMap<String, Integer> messageAcknowledgeMap = new HashMap<String, Integer>();//0：未签收 ；1：已签收

	private ReceiverThreadPool() {
		int corePoolSize = Integer
				.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("threadPoolExecutor.corePoolSize"));
		int maximumPoolSize = Integer.valueOf(
				AppConfig.getInstance().getParameterConfig().getParameter("threadPoolExecutor.maximumPoolSize"));
		Long keepAliveTime = Long
				.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("threadPoolExecutor.keepAliveTime"));
		Integer queueCapacity = Integer.valueOf(AppConfig.getInstance().getParameterConfig()
				.getParameter("threadPoolExecutor.linkedBlockingQueue.capacity"));

		if (0 >= corePoolSize) {
			corePoolSize = 300;
		}
		if (0 >= maximumPoolSize) {
			maximumPoolSize = 400;
		}
		if (null == keepAliveTime || 0 >= keepAliveTime) {
			keepAliveTime = (long) 20;
		}
		if (null == queueCapacity || 0 >= queueCapacity) {
			queueCapacity = 1000;
		}

		AppLogger.getInstance().infoLog("ReceiverThreadPool: corePoolSize=" + corePoolSize + "; maximumPoolSize="
				+ maximumPoolSize + "; keepAliveTime=" + keepAliveTime + "; queueCapacity=" + queueCapacity);

		threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, AppKeys.UNIT,
				new LinkedBlockingQueue<Runnable>(queueCapacity));
		// threadPoolExecutor.corePoolSize
		// 为消息服务器队列数量的倍数（根据不同数据中心实际情况确定），每个队列至少能分到一个线程处理
		// threadPool = new ThreadPoolExecutor(50, 100, 20, AppKeys.UNIT, new
		// LinkedBlockingQueue<Runnable>(1000));
		// threadPool = new ThreadPoolExecutor(300, 400, 20, AppKeys.UNIT, new
		// LinkedBlockingQueue<Runnable>(1000));

		int activeSize = threadPool.getActiveCount();
		int poolSize = threadPool.getPoolSize();
		int maxSize = threadPool.getMaximumPoolSize();
		int waitSize = threadPool.getQueue().size();
		AppLogger.getInstance().infoLog(
				"线程池初始化状态，执行中的线程数：" + activeSize + "，当前线程数：" + poolSize + ", 最大线程数：" + maxSize + "，等待中线程数：" + waitSize);
		// System.out.println("线程池初始化状态，执行中的线程数：" + activeSize + "，当前线程数：" +
		// poolSize + ", 最大线程数：" + maxSize + "，等待中线程数：" + waitSize);

	}

	public static ReceiverThreadPool getInstance() {
		return instance;
	}

	public void execute(Thread t) {
		threadPool.execute(t);

		int activeSize = threadPool.getActiveCount();
		int poolSize = threadPool.getPoolSize();
		int maxSize = threadPool.getMaximumPoolSize();
		int waitSize = threadPool.getQueue().size();
		AppLogger.getInstance().infoLog("线程池运行中状态，" + "，执行中的线程数：" + activeSize + "，当前线程数：" + poolSize + ", 最大线程数："
				+ maxSize + "，等待中线程数：" + waitSize);
		// System.out.println("线程池运行中状态，执行中的线程数：" + activeSize + "，当前线程数：" +
		// poolSize + ", 最大线程数：" + maxSize + "，等待中线程数：" + waitSize);

	}

	public int getActiveSize() {
		return threadPool.getActiveCount();
	}

	public int getWaitSize() {
		return threadPool.getQueue().size();
	}

	public HashMap<String, Integer> getQueueThreadCounterMap() {
		return queueThreadCounterMap;
	}
	
	public Integer getQueueCounter(String queueName) {
		return queueThreadCounterMap.get(queueName);
	}
	
	public void addQueueCounter(String queueName) {
		queueThreadCounterMap.put(queueName, queueThreadCounterMap.get(queueName) + 1);
	}
	
	public void reduceQueueCounter(String queueName) {
		queueThreadCounterMap.put(queueName, queueThreadCounterMap.get(queueName) - 1);
	}

	public void setMessageAcknowledge(String msgId, Integer ackType) {
		messageAcknowledgeMap.put(msgId, ackType);
	}
	
	public Integer getMessageAcknowledge(String msgId) {
		return messageAcknowledgeMap.get(msgId);
	}
	
	public void removeMessageAcknowledge(String msgId) {
		messageAcknowledgeMap.remove(msgId);
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 50; ++i) {
			instance.execute(new Thread() {
				public void run() {
					System.out.println("----");
					try {
						sleep(1000);
					} catch (Exception e) {
					}
				}
			});
		}
	}

}
