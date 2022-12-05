import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoggerTest {
//    final static Logger log = LogManager.getLogger(LoggerTest.class);

    @Test
    public void 로거테스트(){
        // 디폴트 Logger 생성
        Logger logger = (Logger) LogManager.getLogger();
        // Log Level: DEBUG
        logger.setLevel(Level.DEBUG);

        // 설정 변경 후: DEBUG Level 이상 로그가 file(promatic.log)에 출력됨
        logger.debug("변경 후: [DEBUG] Test log4j 2.");
        logger.info("변경 후: [INFO] Test log4j 2.");
        logger.warn("변경 후: [WARN] Test log4j 2.");
        logger.error("변경 후: [ERROR] Test log4j 2.");
        logger.fatal("변경 후: [FATAL] Test log4j 2.");

	/*
	출력결과
	===================
	변경 후: [DEBUG] Test log4j 2.
	변경 후: [INFO] Test log4j 2.
	변경 후: [WARN] Test log4j 2.
	변경 후: [ERROR] Test log4j 2.
	변경 후: [FATAL] Test log4j 2.
	*/

    }
}
