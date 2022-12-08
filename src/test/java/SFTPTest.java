import SFTP.SFTPConfig;
import SFTP.SFTPControl;
import Utils.MyLogger;
import com.jcraft.jsch.SftpException;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SFTPTest {
    final SFTPControl sftpUtil = new SFTPControl();
    Logger log = MyLogger.getLogger();
    private String host ;
    private String username;
    private Integer port ;
    private String privateKey;
    private String password;
    private String sourceDir;
    private String uploadPath;
    SFTPConfig config;
    @BeforeEach
    public void before() {
        config = new SFTPConfig();
        host = config.host;
        username = config.userName;
        port = config.port;
        privateKey = config.privateKey;
        String rootPath = System.getProperty("user.dir");;
        //LocalDir
        sourceDir = rootPath+"/dir";
        uploadPath="/home/ec2-user/scheduler/";
        if(sftpUtil.getChannelSftp()== null) {
            sftpUtil.init(host, username, password, port, privateKey);
        }
        sftpUtil.init(host, username, password, port, privateKey);
        log.info("## SFTP Job Call!! host : {}, port : {}, username : {}, priKey : {} sourcePath : {} uploadPath :{}"
                , host, port, username, privateKey,sourceDir,uploadPath);
    }

    @Test
    public void 폴더_생성테스트(){
        sftpUtil.mkdir(uploadPath+"/test");
    }
    @Test
    public void 폴더_삭제테스트() throws SftpException, FileNotFoundException {

        //RemoteDir
        sftpUtil.rmdir(uploadPath);
   }
    @Test
    public void 폴더_업로드테스트() throws SftpException, FileNotFoundException {
        sftpUtil.run(sourceDir, uploadPath);

    }

    @Test
    public void 파일_업로드테스트(){
        // 접속
        sftpUtil.init(host, username, null, port, privateKey);

        // 업로드 테스트
        String rootPath = System.getProperty("user.dir");;
        System.out.println("현재 프로젝트의 경로 : "+rootPath );

        File uploadfile = new File(rootPath+"/file/test.txt"); // 파일 객체 생성
        System.out.println("uploadfile = " + uploadfile);
//        sftpUtil.mkdir(uploadPath, mkdirPath); // 업로드경로에 현재날짜 년월일 폴더 생성
        boolean isUpload = sftpUtil.upload(uploadPath, uploadfile); //업로드
        System.out.println("isUpload -" + isUpload); // 업로드 여부 확인


        // 업로드  수행 후 꼭 연결을 끊어줘야 한다!!
        sftpUtil.disconnection();
    }
}
