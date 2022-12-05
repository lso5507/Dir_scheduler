package SFTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    private static void copyDirectory(String sourceDir, String targetDir) {
        File source = new File(sourceDir);
        File target = new File(targetDir);

        if (!source.exists()) {
            System.out.println("복사할 디렉토리가 존재하지 않습니다.");
            return;
        }

        if (!target.exists()) {
            target.mkdirs();
        }

        File[] files = source.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                copyDirectory(file.getAbsolutePath(), target.getAbsolutePath() + File.separator + file.getName());
            } else {
                copyFile(file.getAbsolutePath(), target.getAbsolutePath() + File.separator + file.getName());
            }
        }
    }
    //copyFile
    private static void copyFile(String sourceFile, String targetFile) {
        try {
            File source = new File(sourceFile);
            File target = new File(targetFile);

            if (!target.exists()) {
                target.createNewFile();
            }

            FileInputStream fileInputStream = new FileInputStream(source);
            FileOutputStream fileOutputStream = new FileOutputStream(target);

            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
