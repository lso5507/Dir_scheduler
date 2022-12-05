import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Main {
    //메소드의 parameter로 package명.class명을 적용해서 해당 class에서 로그 기록
    private static final Logger LOGGER = LogManager.getLogger();
    Trigger trigger = newTrigger()
            .withIdentity("HelloTrigger", "HelloGroup")
            .startNow()
            .withSchedule(simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever())
            .build();
    public static JobDetail getJob() {

        JobDetail job = newJob(JobScheduler.class)
                .withIdentity("HelloJob", "HelloGroup")
                .withDescription("simple hello job")
                .usingJobData("num", 0)
                .build();
        return job;
    }
    public static void main(String[] args) {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();

            scheduler.scheduleJob(new DetailMaker().getJob(), new TriggerMaker().getTrigger());
            scheduler.start();

            Thread.sleep(60000);
            scheduler.shutdown();
        } catch (SchedulerException | InterruptedException e) {

        }
    }
}
