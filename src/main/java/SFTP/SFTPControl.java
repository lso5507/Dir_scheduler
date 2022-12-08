package SFTP;
import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class SFTPControl {
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;
    public ChannelSftp getChannelSftp(){
        return channelSftp;
    }
    /**
     * 서버와 연결에 필요한 값들을 가져와 초기화 시킴
     *
     * @param host 서버 주소
     * @param userName 아이디
     * @param password 패스워드
     * @param port 포트번호
     * @param privateKey 개인키
     */

    public void init(String host, String userName, String password, Integer port, String privateKey) {

        JSch jSch = new JSch();

        try {
            if(privateKey != null) {//개인키가 존재한다면
                jSch.addIdentity(privateKey);
            }
            session = jSch.getSession(userName, host, port);

            if(privateKey == null && password != null) {//개인키가 없다면 패스워드로 접속
                session.setPassword(password);
            }

            // 프로퍼티 설정
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no"); // 접속 시 hostkeychecking 여부
            session.setConfig(config);
            session.connect();
            //sftp로 접속
            channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        }

        channelSftp = (ChannelSftp) channel;
    }

    /**
     *
     * @param dir 대상경로
     * @throws SftpException
     */
   public void rmdir(String dir) throws SftpException {
        // 폴더안에 파일이 있을경우 파일들을 삭제
        Vector fileList = channelSftp.ls(dir);
        for (int i = 0; i < fileList.size(); i++) {
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) fileList.get(i);
            //검색된 .(현재경로)..(이전경로)는 continue
            if (entry.getFilename().equals(".") || entry.getFilename().equals("..")) {
                continue;
            }
            //검색된게 폴더일경우 재귀함수로 들어가서 파일들을 삭제
            if (entry.getAttrs().isDir()) {
                rmdir(dir + "/" + entry.getFilename());
                //폴더 내 파일들을 삭제 완료했을경우
                channelSftp.rmdir(dir + "/" + entry.getFilename());
            }
            //검색된게 파일일경우 파일 삭제
            else {
                channelSftp.rm(dir + "/" + entry.getFilename());
            }
        }
    }



    public void run(String sourcePath, String destinationPath) throws SftpException, FileNotFoundException {
        //업로드 전 이전 폴더 삭제후 재생성
        //destinationPath 이전 디렉터리
        //생성할 디렉터리
        String dirName = destinationPath+sourcePath.substring(sourcePath.lastIndexOf("/")+1);
        if(exists(dirName)){
            //sourcePath 마지막 경로 뺴오기
            rmdir(dirName);
            //업로드 대상경로도 삭제
            channelSftp.rmdir(dirName);
//            mkdir(dirName);
        }else{
            mkdir(dirName);
        }
        recursiveFolderUpload(sourcePath,destinationPath);
    }

    /**
     * 디렉토리 생성
     *
     * @param dir 이동할 주소
     * @param mkdirName 생성할 디렉토리명
     */
//    public void mkdir(String dir, String mkdirName) {
//        if (!this.exists(dir + "/" + mkdirName)) {
//            try {
//                channelSftp.cd(dir);
//                channelSftp.mkdir(mkdirName);
//            } catch (SftpException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
    public void mkdir(String dir) {
        if (!this.exists(dir)) {
            try {
                channelSftp.mkdir(dir);
            } catch (SftpException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 디렉토리( or 파일) 존재 여부
     * @param path 디렉토리 (or 파일)
     * @return
     */
    public boolean exists(String path) {
        Vector res = null;
        try {
            res = channelSftp.ls(path);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
        }
        return res != null && !res.isEmpty();
    }

    /**
     * 파일 업로드
     *
     * @param dir 저장할 디렉토리
     * @param file 저장할 파일
     * @return 업로드 여부
     */
    public boolean upload(String dir, File file) {
        boolean isUpload = false;
        SftpATTRS attrs;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            channelSftp.cd(dir);
            channelSftp.put(in, file.getName());

            // 업로드했는지 확인
            if (this.exists(dir +"/"+file.getName())) {
                isUpload = true;
            }
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isUpload;
    }

    /**
     * 파일 다운로드
     *
     * @param dir 다운로드 할 디렉토리
     * @param downloadFileName 다운로드 할 파일
     * @param path 다운로드 후 로컬에 저장될 경로(파일명)
     */
    public void download(String dir, String downloadFileName, String path) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            channelSftp.cd(dir);
            in = channelSftp.get(downloadFileName);
        } catch (SftpException e) {
            e.printStackTrace();
        }

        try {
            out = new FileOutputStream(new File(path));
            int i;

            while ((i = in.read()) != -1) {
                out.write(i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private  void copyDirectory(String sourceDir, String targetDir) {
        File source = new File(sourceDir);
        File target = new File(targetDir);

        if (!source.exists()) {
            System.out.println("복사할 디렉토리가 존재하지 않습니다.");
            return;
        }

        if (!target.exists()) {
            target.mkdirs();
        }
        //Local Directory
        File[] files = source.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                //폴더일경우 폴더생성

                copyDirectory(file.getAbsolutePath(), target.getAbsolutePath() + File.separator + file.getName());
            } else {
//                copyFile(file.getAbsolutePath(), target.getAbsolutePath() + File.separator + file.getName());
                //파일일경우 업로드
                upload(file.getAbsolutePath(), new File(target.getAbsolutePath() + File.separator + file.getName()));
            }
        }
    }
    /**
     * 연결 종료
     */
    public void disconnection() {
        channelSftp.quit();
        session.disconnect();
    }

    /**
     * This method is called recursively to Upload the local folder content to SFTP
     * server
     *
     * @param sourcePath
     * @param destinationPath
     * @throws SftpException
     * @throws FileNotFoundException
     */
    private   void recursiveFolderUpload(String sourcePath, String destinationPath)
            throws SftpException, FileNotFoundException {
        File sourceFile = new File(sourcePath);
        //sourceFile이(백업대상) 파일인경우
        if (sourceFile.isFile()) {
            //업로드 대상경로로 이동
            channelSftp.cd(destinationPath);
            // "."일경우 (숨김폴더) 업로드하지않음
            if (!sourceFile.getName().startsWith("."))
                channelSftp.put(new FileInputStream(sourceFile), sourceFile.getName(), ChannelSftp.OVERWRITE);
        }
        //sourceFile이(백업대상) 폴더인경우
        else {
            // sourceFile.isFile() == false 일경우 디렉토리
            System.out.println("inside else " + sourceFile.getName());
            //해당 sourceFile에 대한 파일 또는 폴더목록 배열화
            File[] files = sourceFile.listFiles();
            if (files != null && !sourceFile.getName().startsWith(".")) {
                //업로드 대상경로로 이동
                channelSftp.cd(destinationPath);
                SftpATTRS attrs = null;
                // check if the directory is already existing
                try {
                    //해당 디렉토리가 존재하는지 확인
                    attrs = channelSftp.stat(destinationPath + "/" + sourceFile.getName());
                }
                // 해당 디렉토리가 존재하지 않는다면 attrs = null
                catch (Exception e) {
                    System.out.println(destinationPath + "/" + sourceFile.getName() + " not found");
                }
                // attrs가 null이 아닐경우 (디렉토리가 존재할경우)
                if (attrs != null) {
                    System.out.println("Directory exists IsDir=" + attrs.isDir());
                }
                // attrs가 null일경우 (디렉토리가 존재하지않을경우)
                else {
                    System.out.println("Creating dir " + sourceFile.getName());
                    channelSftp.mkdir(sourceFile.getName());
                }
                // 업로드 대상 파일 배열화 후 재귀함수 이용해 업로드 진행
                for (File f : files) {
                    recursiveFolderUpload(f.getAbsolutePath(), destinationPath + "/" + sourceFile.getName());
                }
            }
        }
    }
}
