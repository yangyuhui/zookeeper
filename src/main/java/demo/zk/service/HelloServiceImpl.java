package demo.zk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by yyh on 2017/11/28.
 */
public class HelloServiceImpl  extends UnicastRemoteObject implements HelloService{
	Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
	public HelloServiceImpl() throws RemoteException {
	}

	@Override
	public String sayHello(String name) throws RemoteException {
		logger.info("{},{}",Thread.currentThread().getName(),name);
		return "hello "+name+"!";
	}
}
