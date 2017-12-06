package demo.zk.service;

import demo.zk.util.ZookeeperUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;

/**
 * Created by yyh on 2017/11/29.
 */
public class ServiceProvider {
	Logger logger = LoggerFactory.getLogger(ServiceProvider.class);


	public ZooKeeper publish(Remote remote,String host,int port){
		String url = publishService(remote,host,port);
		if(StringUtils.isNotEmpty(url)){
			ZooKeeper zk = ZookeeperUtil.createNode(url);//创建Znode节点存入Zookeeper中
			return zk;
		}
		return null;
	}

	private String publishService(Remote remote, String host, int port){
		String url = null;
		try{
			url = String.format("rmi://%s:%d/%s",host,port,remote.getClass().getName());
			LocateRegistry.createRegistry(port);
			Naming.rebind(url,remote);
			logger.info("server start, listen {} ",port);
		}catch (Exception e){
			logger.error("publishService error,",e);
		}
		return url;
	}
}
