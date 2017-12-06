package demo.zk.service;

import demo.zk.util.ZookeeperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.Remote;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by yyh on 2017/11/29.
 * get RMI Service
 */
public class ServiceConsumer {

	Logger logger = LoggerFactory.getLogger(ServiceConsumer.class);
	public ServiceConsumer(){
		ZookeeperUtil.watchNode();
	}
	public <T extends Remote> T lookup(){
		T service = null;
		int size = ZookeeperUtil.urlList.size();
		if(size > 0){
			String url ;
			if(size == 1){
				url = ZookeeperUtil.urlList.get(0);
				logger.info("lookup only one url:{}",url);
			}else{
				url = ZookeeperUtil.urlList.get(ThreadLocalRandom.current().nextInt(size));
				logger.info("lookup random url:{}",url);
			}
			service = lookupService(url);
		}
		return service;
	}

	private <T extends Remote> T lookupService(String url) {
		T service = null;
		try{
			service = (T) Naming.lookup(url);
		}catch (Exception e){
			logger.error("lookupService error:",e);
		}
		return service;
	}
}
