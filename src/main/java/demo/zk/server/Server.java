package demo.zk.server;

import demo.zk.service.HelloService;
import demo.zk.service.HelloServiceImpl;
import demo.zk.service.ServiceProvider;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by yyh on 2017/11/29.
 */
public class Server {
	public static void main(String[] args) throws Exception {
		ZooKeeper zk = null;
		try{
			CountDownLatch latch = new CountDownLatch(1);
			int port = 11232;
			ServiceProvider provider = new ServiceProvider();
			HelloService service = new HelloServiceImpl();
			zk = provider.publish(service,"localhost",port);
			System.out.println("执行完毕"+Thread.activeCount());
			latch.await(30000, TimeUnit.MILLISECONDS);

		}finally {
			if(zk != null){
				System.out.println("最终销毁zk");
				zk.close();
			}
		}

	}
}
