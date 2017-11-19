package com.tc.tcsocket.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.tc.tcsocket.utils.MyPath;

public class Config {
	
	public static InputStream getConfigStream(String filename)
	{
			
		// 配置文件初始化  
	    //PropertyConfigurator.configure(filepath);  
		InputStream inputStream  = null; 
		
        try {  
        	
             inputStream = Config.class.getClassLoader().getResourceAsStream(filename); 
            if (inputStream ==null)
            {
            	String userdir  = MyPath.getProjectPath();
            	String configfile = userdir + File.separatorChar +  "resources" +File.separatorChar   +filename;
            	System.out.println("getConfigProperties configfilepath:" + configfile);
            	inputStream = new FileInputStream(configfile);
            	if (inputStream ==null )
            	{
            		System.out.println("tc.properties is not found");
            		
            	}
            }
                     
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return inputStream;  
		}
	
	public static String getValue(String key) {  
		Properties property = new Properties();
        try {  
        	
            InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("tc.properties"); 
            
            property.load(inputStream);  
            
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return (String) property.get(key);  
    }  

}
