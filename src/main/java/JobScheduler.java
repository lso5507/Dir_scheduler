import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobScheduler implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("## Test Job Call!!");

        String name = context.getJobDetail().getJobDataMap().get("jobName").toString();

        System.out.println("## Job Name : "+name);
    }
}
