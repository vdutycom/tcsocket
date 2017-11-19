package com.tc.tcsocket.socket;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.tc.tcsocket.utils.JedisUtil;
import com.tc.tcsocket.utils.MyPath;

public class MySqlSessionFactory  {

	 private static final MySqlSessionFactory mySqlSessionFactory = new MySqlSessionFactory();
	 private static  SqlSessionFactory sessionFactory = null;  
	 private static Reader reader = null;
	 private MySqlSessionFactory() { 
		 
	 }
	 /**
      * 获取JedisUtil实例
      * @return
      */
     public static MySqlSessionFactory getInstance() {
     	
         return mySqlSessionFactory;
     }
	 static {
    	 initSqlsessionFactory();
     }
	 
	 public  SqlSessionFactory getSqlSessionFactory()
	 {
		 return sessionFactory;
	 }
	 
	 
	 private static void initSqlsessionFactory()
	 {
		
		 try {
				// String resource = "bean/resources/configration.xml";
				// File file = new File(resource);

				String dbconfigfile = "dbconfigration.xml";
				String dbconfigfilename="dbconfigration.xml";
				File file = new File(dbconfigfile);
				if (file.exists() && !file.isDirectory())
				{
					reader = new FileReader(dbconfigfile);//开发环境运行
				System.out.println("开发环境运行的数据库配置文件");
				// Resources.getResourceAsReader(dbconfigfile);
				}
				else {
					String userdir = MyPath.getProjectPath();
					//发布后环境运行读取路径
					dbconfigfile = userdir + File.separatorChar + "resources" + File.separatorChar + dbconfigfilename;
					System.out.println("dbconfig.xml filepath:" + dbconfigfile);
					file = new File(dbconfigfile);
					if (file.exists() && !file.isDirectory())
					reader = new FileReader(dbconfigfile);
					else
					{
						//开发环境运行路径
						dbconfigfile = userdir + File.separatorChar + dbconfigfilename;
						System.out.println("dbconfig.xml filepath:" + dbconfigfile);
						file = new File(dbconfigfile);
						if (file.exists() && !file.isDirectory())
						reader  = new FileReader(dbconfigfile);
						else
						{
						  System.out.println("dbconfile is no found");
						}
					}
						
				}
				
				
				if (reader != null)
					sessionFactory = new SqlSessionFactoryBuilder().build(reader);
				else {
					System.out.println("dbconfig reader is null");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		 
	 }
	
	
	
	
}
