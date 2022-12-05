import Utils.MyLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Main {
    //메소드의 parameter로 package명.class명을 적용해서 해당 class에서 로그 기록
    static Logger logger = MyLogger.getLogger();
    public static void main(String[] args) {

        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            // Listener 설정
            ListenerManager listenrManager = scheduler.getListenerManager();
            listenrManager.addJobListener(new MyJobListener());
//            listenrManager.addTriggerListener(new MyTriggerListener());

            scheduler.scheduleJob(new DetailMaker().getJob(), new TriggerMaker().getTrigger());
            scheduler.start();
            //트리거 종료상태 확인


        } catch (SchedulerException e ) {
            logger.error("SchedulerException : {}", e.getMessage());
        }
    }
}
