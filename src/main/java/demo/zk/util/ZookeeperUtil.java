package demo.zk.util;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yyh on 2017/11/29.
 */
public class ZookeeperUtil {
	final static Logger logger = LoggerFactory.getLogger(ZookeeperUtil.class);
	public static String ZK_CONNECT_LISTS = "hostMaster:2181,host1:2181,host2:2181";
	public static Integer ZK_CONNECT_TIMEOUT = 1000;
	public static String ZK_RMI_SERVER_PATH = "/registry";
	public static String ZK_RMI_PROVIDER_PATH = ZK_RMI_SERVER_PATH + "/provider";
	static CountDownLatch latch = null;
	public static List<String> urlList = new ArrayList<String>();
	public static ZooKeeper connectServer() {
		latch = new CountDownLatch(1);
		ZooKeeper zk = null;
		try{
			zk = new ZooKeeper(ZookeeperUtil.ZK_CONNECT_LISTS, ZookeeperUtil.ZK_CONNECT_TIMEOUT, new Watcher() {
				@Override
				public void process(WatchedEvent watchedEvent) {
					if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
						latch.countDown();
					}
				}
			});
			latch.await();
		}catch (Exception e){
			e.printStackTrace();

		}
		return zk;
	}
	public static ZooKeeper createNode(String url) {
		ZooKeeper zk = null;
		try{
			zk = ZookeeperUtil.connectServer();
			if(zk != null){
				byte[] data = url.getBytes();
				String path = zk.create(ZK_RMI_PROVIDER_PATH,data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
				logger.info("创建zk服务节点成功:{}",path);
			}
		}catch (Exception e){
			logger.error("createNode error:",e);
		}
		return zk;
	}

	public static void watchNode() {
		try{
			ZooKeeper zk = ZookeeperUtil.connectServer();
			if(zk != null){
				List<String> nodeList = zk.getChildren(ZK_RMI_SERVER_PATH, new Watcher() {
					@Override
					public void process(WatchedEvent event) {
						if(event.getType() == Event.EventType.NodeChildrenChanged){
							logger.info("节点发生改变");
							watchNode();
						}
					}
				});
				List<String> dataList = new ArrayList<String>();
				for(String node:nodeList){
					byte[] data = zk.getData(ZK_RMI_SERVER_PATH+"/"+node,false,null);
					dataList.add(new String(data));
				}
				urlList = dataList;
			}
		}catch (Exception e){
			logger.error("watchNode error:",e);
		}
	}
}
