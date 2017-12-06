package lock;

import demo.zk.util.ZookeeperUtil;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by yyh on 2017/12/1.
 */
public class LockService {
	final private static Logger logger = LoggerFactory.getLogger(LockService.class);
	public static String GROUP_PATH = "/disLocks";
	public static String SUB_PATH = "/disLocks/sub";
	public void doService(CountDownLatch semaphore,DoTemplate doTemplate) {
		try{

			ZooKeeper zk = ZookeeperUtil.connectServer();
			DistributedLock lock = new DistributedLock(zk,semaphore);
			LockWatcher watcher = new LockWatcher(lock,doTemplate);
			lock.setWatcher(watcher);
			lock.createBasePath();
			boolean rs = lock.getLock();
			if(rs){
				watcher.doSomething();
				lock.unlock();
			}
		}catch (Exception e){

		}
	}
}
