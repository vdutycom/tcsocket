package tcsocket.mysqladdEps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.tc.tcsocket.socket.MySqlSessionFactory;

public class AddUser {
	static int tn = 0;

	// private static SqlSessionFactory sqlSessionFactory;

	private static int threadNum = 200;

	private static CountDownLatch threadSignal = new CountDownLatch(threadNum);// 初始化countDown

	public static void main(String[] args) throws Exception {

		String name = "name" + Math.random();
		Map<String, String> map = new HashMap();
		map.put("name", name);
		map.put("password", "pw22a55" + Math.random());
		SqlSessionFactory sqlSessionFactory = MySqlSessionFactory.getInstance().getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		sqlSession.insert("com.tc.tcsocket.dao.IMemberDao.addUser", map);
		sqlSession.commit();

		for (int ii = 0; ii < threadNum; ii++) {// 开threadNum个线程
			// final Iterator<String> itt = it.get(ii);
			Thread t = new ImportThread(threadSignal);
			t.start();
		}
		threadSignal.await();// 等待所有子线程执行完
		System.out.println(Thread.currentThread().getName() + "结束.");// 打印结束标记

		/*
		 * for (int j = 0; j < 200; j++) { final String thname = "th" + j; new
		 * Thread(() -> { Thread.currentThread().setName("th" + thname);
		 * sqlSessionFactory =
		 * MySqlSessionFactory.getInstance().getSqlSessionFactory(); final
		 * SqlSession sqlSession = sqlSessionFactory.openSession();
		 * 
		 * 
		 * for (int i = 0; i < 100; i++) { Map<String, String> map = new
		 * HashMap(); String name = "name" + Math.random();
		 * 
		 * map.put("name", name); map.put("password", "pw22a55" +
		 * Math.random());
		 * 
		 * sqlSession.insert("com.tc.tcsocket.dao.IMemberDao.addUser", map);
		 * 
		 * sqlSession.commit();
		 * 
		 * if (c>100) { sqlSession.commit();
		 * System.out.println(Thread.currentThread().getName() + " commit:" +
		 * c); c =0;
		 * 
		 * } c++; // System.out.println(Thread.currentThread().getName() + ":" +
		 * i); try { Thread.sleep(100); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * if (i>=99) { tn ++; System.out.println(thname+" "+i); if (tn >= 199)
		 * { System.out.println("thread completed !"); }
		 * map.put(Thread.currentThread().getName(),
		 * Thread.currentThread().getName()); } }
		 * 
		 * 
		 * 
		 * }).start();
		 * 
		 * }
		 */

		/*
		 * Map<String, String> getMap = new HashMap(); getMap.put("name", name);
		 */
		/*
		 * sqlSessionFactory =
		 * MySqlSessionFactory.getInstance().getSqlSessionFactory(); final
		 * SqlSession sqlSession = sqlSessionFactory.openSession(); int
		 * userCount =
		 * sqlSession.selectOne("com.tc.tcsocket.dao.IMemberDao.getCount");
		 */
		// System.out.print("curent users count:" + userCount);

	}

	public static class ImportThread extends Thread {
		private CountDownLatch threadsSignal;

		public ImportThread(CountDownLatch threadsSignal) {
			this.threadsSignal = threadsSignal;

		}

		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() + "开始...");
			// Do somethings
			SqlSessionFactory sqlSessionFactory = MySqlSessionFactory.getInstance().getSqlSessionFactory();
			SqlSession sqlSession = sqlSessionFactory.openSession();
			for (int i = 0; i < 10000; i++) {
				String name = "name" + Math.random();
				Map<String, String> map = new HashMap();
				map.put("name", name);
				map.put("password", "pw22a55" + Math.random());

				sqlSession.insert("com.tc.tcsocket.dao.IMemberDao.addUser", map);
				sqlSession.commit();
			}
		/*	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}*/
			threadsSignal.countDown();// 线程结束时计数器减1
			sqlSession.close();
			System.out.println(Thread.currentThread().getName() + "结束. 还有" + threadsSignal.getCount() + " 个线程");
		}
	}

}
