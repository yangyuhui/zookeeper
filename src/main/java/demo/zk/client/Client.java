package demo.zk.client;

import demo.zk.service.HelloService;
import demo.zk.service.ServiceConsumer;

/**
 * Created by yyh on 2017/11/29.
 * client url by zookeeper
 */
public class Client {
	public static void main(String[] args) throws Exception {
		ServiceConsumer consumer = new ServiceConsumer();
		while (true){
			HelloService service = consumer.lookup();
			if(service == null){
				return ;
			}
			String str = service.sayHello("周杰伦");
			System.out.println(str);
			Thread.sleep(3000);
		}

	}
}
