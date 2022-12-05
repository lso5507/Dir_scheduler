import org.quartz.JobDetail;

import static org.quartz.JobBuilder.newJob;

public class DetailMaker {
    public JobDetail getJob() {

        JobDetail job = newJob(JobScheduler.class)
                .withIdentity("HelloJob", "HelloGroup")
                .withDescription("simple hello job")
                .usingJobData("num", 0)
                .build();
        return job;
    }
}
