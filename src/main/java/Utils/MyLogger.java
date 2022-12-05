package Utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class MyLogger {

    // 디폴트 Logger 생성
    public static Logger logger;
    public static Logger getLogger(){
        if(logger == null){
            logger = (Logger) LogManager.getLogger();
            // Log Level: DEBUG
            logger.setLevel(Level.DEBUG);
        }
        return logger;
    }


}
