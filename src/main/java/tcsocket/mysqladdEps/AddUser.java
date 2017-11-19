package tcsocket.mysqladdEps;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.tc.tcsocket.socket.MySqlSessionFactory;

public class AddUser {

	private static SqlSessionFactory sqlSessionFactory;

	public static void main(String[] args) throws Exception {

		for (int j = 0; j < 100; j++) {
			final String thname = "th" + j;
			new Thread(() -> {
				Thread.currentThread().setName("th" + thname);
				sqlSessionFactory = MySqlSessionFactory.getInstance().getSqlSessionFactory();
				final SqlSession sqlSession = sqlSessionFactory.openSession();

				int c = 0;
				for (int i = 0; i < 10000; i++) {
					Map<String, String> map = new HashMap();
					String name = "name" + Math.random();

					map.put("name", name);
					map.put("password", "pw22a55" + Math.random());

					sqlSession.insert("com.tc.tcsocket.dao.IMemberDao.addUser", map);
				
					if (c>100)
					{
						sqlSession.commit();
						System.out.println(Thread.currentThread().getName() + " commit:" + c);
						c =0;
						
					}
					c++;
				//	System.out.println(Thread.currentThread().getName() + ":" + i);
					/*try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				}
				
				sqlSession.commit();
				
			}).start();

		}
		
		
		

		/*
		 * Map<String, String> getMap = new HashMap(); getMap.put("name", name);
		 */
		sqlSessionFactory = MySqlSessionFactory.getInstance().getSqlSessionFactory();
		final SqlSession sqlSession = sqlSessionFactory.openSession();
		int userCount = sqlSession.selectOne("com.tc.tcsocket.dao.IMemberDao.getCount");
		//System.out.print("curent users count:" + userCount);

	}

}
