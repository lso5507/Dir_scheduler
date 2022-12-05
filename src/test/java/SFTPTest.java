import SFTP.SFTPControl;
import com.jcraft.jsch.SftpException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SFTPTest {
    final SFTPControl sftpUtil = new SFTPControl();

    final String host = "13.124.251.220";
    final String userName = "ec2-user";
    final int port = 22;
    final String uploadPath = "/home/ec2-user/scheduler";
    final String privateKey = "/Users/leeseokwoon/Documents/lee.pem";


    @Test
    public void 폴더_업로드테스트() throws SftpException, FileNotFoundException {
        sftpUtil.init(host, userName, null, port, privateKey);
        String rootPath = System.getProperty("user.dir");;
        //LocalDir
        String sourceDir = rootPath+"/dir";
        //RemoteDir
        String targetDir = uploadPath;
        sftpUtil.recursiveFolderUpload(sourceDir, targetDir);

    }
    @Test
    public void 파일_업로드테스트(){
        // 접속
        sftpUtil.init(host, userName, null, port, privateKey);

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
