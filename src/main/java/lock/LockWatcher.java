package lock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yyh on 2017/12/4.
 */
public class LockWatcher implements Watcher {
	Logger logger = LoggerFactory.getLogger(LockWatcher.class);

	private DistributedLock lock;
	private DoTemplate doTemplate;
	public LockWatcher(){}
	public LockWatcher(DistributedLock lock, DoTemplate doTemplate) {
		this.lock = lock;
		this.doTemplate = doTemplate;
	}

	public void process(WatchedEvent event) {
		if(event.getType() == Event.EventType.NodeDeleted
				&&event.getPath().equals(lock.getWaitPath())){
			logger.info("{} 收到通知，前面的节点不再排队了，检查一下",Thread.currentThread().getName());
			try{

				if(lock.checkMinPath()){
					doSomething();
					lock.unlock();
				}
			}catch (Exception e){
				logger.error("LockWatcher process error:",e);
			}
		}
	}

	public void doSomething() {
		logger.info("{} 获得了锁,正在工作......",Thread.currentThread().getName());
		this.doTemplate.dodo();

	}
}
