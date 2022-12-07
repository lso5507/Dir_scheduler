package SFTP;

import Utils.MyLogger;
import org.apache.logging.log4j.Logger;

public class SFTPConfig {
    public static  String host = null;
    public static  String userName = null;
    public static  Integer port = null;
    public static  String privateKey = null;
    static Logger logger = MyLogger.getLogger();

    public SFTPConfig(){
        String property = System.getProperty("os.name");
        if(property.contains("Mac"))
            setMac();
        else if(property.contains("Window"))
            setWin();
        else{
            logger.error("NOT Supported OS");
            throw new RuntimeException();
        }






    }
    public void setMac(){
          host = "13.124.251.220";
          userName = "ec2-user";
          port = 22;
          privateKey = "/Users/leeseokwoon/Documents/lee.pem";
    }
    public void setWin(){
         host = "13.124.251.220";
         userName = "ec2-user";
         port = 22;
         privateKey = "C:/Users/swlee/Documents/lee.pem";
    }
}
