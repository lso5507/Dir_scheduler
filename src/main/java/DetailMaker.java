import SFTP.SFTPConfig;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

import static SFTP.SFTPConfig.*;
import static org.quartz.JobBuilder.newJob;

public class DetailMaker {
    public JobDetail getJob() {
        // FTP Path
        final String uploadPath = "/home/ec2-user/scheduler";
        String rootPath = System.getProperty("user.dir");;
        //LocalDir
        String sourceDir = rootPath+"/dir";

        JobDataMap SFTP_data = new JobDataMap();
        SFTP_data.put("host", host);
        SFTP_data.put("username", userName);
        SFTP_data.put("port", port);
        SFTP_data.put("privateKey", privateKey);
        SFTP_data.put("uploadpath", uploadPath);
        SFTP_data.put("sourcepath", sourceDir);




        JobDetail job = newJob(JobScheduler.class)
                .withIdentity("HelloJob", "HelloGroup")
                .withDescription("simple hello job")
                .usingJobData(SFTP_data)
                .build();
        return job;
    }
}
