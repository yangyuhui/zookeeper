package demo.zk.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by yyh on 2017/11/28.
 */
public interface HelloService extends Remote {
	String sayHello(String name) throws RemoteException;
}
