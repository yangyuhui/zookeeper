package lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by yyh on 2017/11/30.
 */
public class LockTest {
	private final static Logger logger = LoggerFactory.getLogger(LockTest.class);
	private static Integer LOCK_TEST_NUM = 10;
	public static void main(String[] args) throws Exception {
		final CountDownLatch testSemaphore = new CountDownLatch(LOCK_TEST_NUM);
		for (int i = 0; i < LOCK_TEST_NUM; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					new LockService().doService(testSemaphore,new DoTemplate() {
						@Override
						public void dodo() {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							logger.info("正在修改文件");
						}
					});
				}
			}).start();
		}
		testSemaphore.await();
		logger.info("测试完毕");
	}
}
