package com.kh.dndncare.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;

import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

public class ImageUtil {
	
	public static File base64ToFile(String copyName, String base64) throws MimeTypeException {
        int colon = base64.indexOf(":");
        int semicolon = base64.indexOf(";");
        String mimeType = base64.substring(colon + 1, semicolon);
        String base64WithoutHeader = base64.substring(semicolon + 8);
        String extension = MimeTypes.getDefaultMimeTypes().forName(mimeType).getExtension();

        byte[] bytes = Base64.getDecoder().decode(base64WithoutHeader);
        copyName = copyName + extension;
        File folder = new File("C:\\uploadFinalFiles/");
//        File folder = new File("\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board");
        folder.mkdirs();
        File file = new File("C:\\uploadFinalFiles/" + copyName);
//        File file = new File("\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + copyName); // 공유 폴더 내 파일명(확장자X)으로 지정한다.
        try (OutputStream outputStream = new BufferedOutputStream((new FileOutputStream(file)))) {
            outputStream.write(bytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }
}
