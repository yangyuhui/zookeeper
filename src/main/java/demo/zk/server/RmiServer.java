package demo.zk.server;

import demo.zk.service.HelloServiceImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Created by yyh on 2017/11/28.
 */
public class RmiServer {
	public static void main(String[] args) throws Exception {
		int port = 1129;
		String url = "rmi://localhost:1129/demo.zk.service.HelloServiceImpl";
		LocateRegistry.createRegistry(port);
		Naming.rebind(url,new HelloServiceImpl());
		System.out.println("server start, listen "+port);
	}
}
