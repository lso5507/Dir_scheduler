import SFTP.SFTPControl;
import Utils.MyLogger;
import com.jcraft.jsch.SftpException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.FileNotFoundException;

public class JobScheduler implements Job {
    Logger log = MyLogger.getLogger();
    String host ="";
    Integer port =null;
    String username ="";
    String priKey="";
    String sourcePath="";
    String uploadPath="";
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("## SFTP Job Call!!");
        // SFTP 정보 INIT
        run(context);
    }

    private void run(JobExecutionContext context) {
        host = context.getJobDetail().getJobDataMap().getString("host");
        port = context.getJobDetail().getJobDataMap().getInt("port");
        username = context.getJobDetail().getJobDataMap().getString("username");
        priKey = context.getJobDetail().getJobDataMap().getString("privateKey");
        sourcePath = context.getJobDetail().getJobDataMap().getString("sourcepath");
        uploadPath = context.getJobDetail().getJobDataMap().getString("uploadpath");
        log.info("## SFTP Job Call!! host : {}, port : {}, username : {}, priKey : {} sourcePath : {} uploadPath :{}"
                , host, port, username, priKey,sourcePath,uploadPath);

        SFTPControl control= new SFTPControl();
        control.init(host, username,"",port,priKey);

        try {
            control.recursiveFolderUpload(sourcePath,uploadPath);
        } catch (SftpException e) {
            log.error("SftpException : {}",e.getMessage());
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException : {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
