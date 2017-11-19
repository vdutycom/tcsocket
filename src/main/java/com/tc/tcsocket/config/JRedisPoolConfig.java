package com.tc.tcsocket.config;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.jfinal.kit.PathKit;

public class JRedisPoolConfig {
    protected static Logger log = LoggerFactory.getLogger(JRedisPoolConfig.class);
    public static String REDIS_IP;
    public static int REDIS_PORT;
    public static String REDIS_PASSWORD;
    public static int MAX_ACTIVE;
    public static int MAX_IDLE;
    public static long MAX_WAIT;
    public static boolean TEST_ON_BORROW;
    public static boolean TEST_ON_RETURN;

    static {
        init();
    }

    public static void init() {
        try {
            //String fullFile = PathKit.getWebRootPath() + File.separator + "WEB-INF" + File.separator + "redis.properties";
            
        	//File file = new File(fullFile);
        	
            //if(file.exists()){
        	 
                log.info("loading redis config from redis.properties.......");
                //InputStream in = new FileInputStream(fullFile);
               // InputStream in = JRedisPoolConfig.class.getClassLoader().getResourceAsStream("redis.properties");
               // Properties p = new Properties();
                //InputStream in = Config.getConfigStream("redis.properties");
                InputStream in = Config.getConfigStream("redis.properties");
                
                Properties p = new Properties();
                p.load(in);
                REDIS_IP = p.getProperty("redis.ip");
                REDIS_PORT = Integer.parseInt(p.getProperty("redis.port"));
                REDIS_PASSWORD = p.getProperty("redis.password");
                MAX_ACTIVE = Integer.parseInt(p.getProperty("redis.pool.maxActive"));
                MAX_IDLE = Integer.parseInt(p.getProperty("redis.pool.maxIdle"));
                MAX_WAIT = Integer.parseInt(p.getProperty("redis.pool.maxWait"));
                TEST_ON_BORROW = Boolean.parseBoolean(p.getProperty("redis.pool.testOnBorrow"));
                TEST_ON_RETURN = Boolean.parseBoolean(p.getProperty("redis.pool.testOnReturn"));
                log.info("redis config load Completed。");
                in.close();
                in=null;
            //}else{
              //  log.error("redis.properties is not found!");
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
