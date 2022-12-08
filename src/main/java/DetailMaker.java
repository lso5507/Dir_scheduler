import SFTP.SFTPConfig;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;


import static org.quartz.JobBuilder.newJob;

public class DetailMaker {
    public JobDetail getJob() {
        // FTP Path
        final String uploadPath = "/home/ec2-user/scheduler";
        String rootPath = System.getProperty("user.dir");;
        //LocalDir
        String sourceDir = rootPath+"/dir";
        SFTPConfig config = new SFTPConfig();
        JobDataMap SFTP_data = new JobDataMap();

        SFTP_data.put("host", config.host);
        SFTP_data.put("username", config.userName);
        SFTP_data.put("port", config.port);
        SFTP_data.put("privateKey", config.privateKey);
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
