package lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yyh on 2017/12/4.
 */
public class DistributedLock {
	final static Logger logger = LoggerFactory.getLogger(DistributedLock.class);
	private String threadName = Thread.currentThread().getName();
	private ZooKeeper zk;
	private String selfPath;
	private String waitPath;
	private Watcher watcher;
	private CountDownLatch semaphore;

	public DistributedLock(){}
	public DistributedLock(ZooKeeper zk,CountDownLatch semaphore){
		this.zk = zk;
		this.semaphore = semaphore;
	}

	public String getSelfPath() {
		return selfPath;
	}

	public void setSelfPath(String selfPath) {
		this.selfPath = selfPath;
	}

	public String getWaitPath() {
		return waitPath;
	}

	public void setWaitPath(String waitPath) {
		this.waitPath = waitPath;
	}

	public Watcher getWatcher() {
		return watcher;
	}

	public void setWatcher(Watcher watcher) {
		this.watcher = watcher;
	}

	/**
	 * 如果父路径不存在就创建一下
	 */
	public void createBasePath() throws KeeperException, InterruptedException {
		if(zk.exists(LockService.GROUP_PATH,null) == null){
			String data = "该节点由"+Thread.currentThread().getName()+"创建";
			String path = zk.create(LockService.GROUP_PATH,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			logger.info("父节点创建成功,Path:{}, content:{}",path,data);
		}else{
			logger.info("父节点已经存在，不再创建");
		}
	}

	public boolean getLock() throws KeeperException, InterruptedException {
		this.selfPath = zk.create(LockService.SUB_PATH,null,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
		logger.info("{} 创建锁路径成功:{}",threadName,this.selfPath);
		return checkMinPath();
	}

	public boolean checkMinPath() throws KeeperException, InterruptedException {
		List<String> subNodes = zk.getChildren(LockService.GROUP_PATH,false);
		Collections.sort(subNodes);
		int index = subNodes.indexOf(this.selfPath.substring(LockService.GROUP_PATH.length()+1));
		switch (index){
			case -1:{
				logger.error("{} {}已经不存在了。",threadName,this.selfPath);
				return false;
			}
			case 0:{
				logger.info("{},{} 目前确实排在第一位",threadName,this.selfPath);
				return true;
			}
			default:{
				this.waitPath = LockService.GROUP_PATH+"/"+subNodes.get(index-1);//需要监控前一个节点
				logger.info("{} 排队中，前面的节点: {}",threadName,this.waitPath);
				try{
					zk.getData(waitPath,this.watcher,new Stat());
					return false;
				}catch (KeeperException e){
					if(zk.exists(waitPath,false)==null){
						logger.info(threadName+",{} 排在前面的节点 {} 已经消失",this.selfPath,this.waitPath);
						return checkMinPath();
					}

				}
			}
		}
		return false;
	}

	public void unlock() {
		try{
			if(zk.exists(this.selfPath,false) == null){
				logger.info("{} 节点已经不存了。",threadName,this.selfPath);
			}else{
				zk.delete(this.selfPath,-1);
				logger.info("{} 执行完毕，已经释放了锁 {}。",threadName,this.selfPath);
			}
			semaphore.countDown();
			zk.close();

		}catch (Exception e){

		}
	}
}
