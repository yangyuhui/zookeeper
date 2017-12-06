package demo.zk.client;

import demo.zk.service.HelloService;

import java.rmi.Naming;

/**
 * Created by yyh on 2017/11/28.
 */
public class RmiClient {
	public static void main(String[] args) throws Exception {
		String url = "rmi://localhost:1129/demo.zk.service.HelloServiceImpl";
		HelloService helloService = (HelloService)Naming.lookup(url);
		String str = helloService.sayHello("习近平");
		System.out.println(str);
	}
}
