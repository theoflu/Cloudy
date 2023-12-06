package com.yasu.ftpLogic.ftpStuff;

import co.elastic.clients.util.DateTime;
import com.yasu.ftpLogic.entity.FileDetail;
import com.yasu.ftpLogic.errorHandling.ErrorMessage;
import com.yasu.ftpLogic.repository.TrashCanRepository;
import org.aspectj.bridge.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Client {
    private  final  String serverAddress;
    private final int serverPort;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

    }

    public  long folderSize(File directory) {

        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public void check(){
        String folderPath = "C:\\user\\as3d\\TrashCan"; // Silinecek dosyaların bulunduğu klasör yolu

        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            long currentTime = System.currentTimeMillis();

            if (files != null) {
                for (File file : files) {
                    long lastModified = file.lastModified();
                    long thirtyDaysInMillis =  30L * 24 * 60 * 60 * 1000; // 30 günün milisaniye cinsinden değeri

                    if (currentTime - lastModified > thirtyDaysInMillis) {
                        boolean isDeleted = file.delete();
                        if (isDeleted) {
                            System.out.println("Dosya başarıyla silindi: " + file.getAbsolutePath());
                        } else {
                            System.out.println("Dosya silinemedi: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        } else {
            System.out.println("Belirtilen klasör bulunamadı veya bir klasör değil.");
        }
    }
    public List<FileDetail> moveFile(String filename, String username) {
        String sourcePath, destinationPath;

        sourcePath = "C:\\user\\" + username + "\\" + filename;
        destinationPath = "C:\\user\\" + username + "\\TrashCan\\" + filename;
        File sourceFile = new File(sourcePath);
        File destinationFile = new File(destinationPath);
        long currentTime = System.currentTimeMillis();
        //1 ay sonrasını hesapladık futuretime
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.add(Calendar.MONTH, 1);
        long futureTime = calendar.getTimeInMillis();
        System.out.println("1 ay öncesinin zamanı: " + currentTime);
        System.out.println("1 ay sonrasının zamanı: " + futureTime);
        if (sourceFile.exists()) {
            sourceFile.setLastModified(currentTime);
            sourceFile.renameTo(destinationFile);
           return listFiles("C:\\user\\" + username + "\\TrashCan");
        }
        else if(destinationFile.exists()){
            destinationFile.renameTo(sourceFile);
            return listFiles("C:\\user\\" + username + "\\TrashCan");

        }
        else {
            System.out.println("Kaynak dosya bulunamadı.");
            return null;
        }
    }

    public ErrorMessage sendFile(String filePath, String username,String orginalName){
        try  (Socket socket = new Socket(serverAddress, serverPort)) {
            try {

                Thread.sleep(2000); // Millisaniye cinsinden süreyi belirtir (5 saniye = 5000 milisaniye)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            OutputStream os = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(os, true);

            File file = new File(filePath);

            long a=20000036480L;
            File file2 = new File("C:\\user\\"+username);
            FileDetail fileDetail= FileDetail.
                    builder()
                    .filepath(file.getPath())
                    .filename(orginalName)
                    .filesize(folderSize(file2))
                    .build();
            if(folderSize(file2)>= a)
            {

              System.out.println(folderSize(file2) +" bayttt");
              Cancel(writer,"InsufficientStorageSpace");


             }
            else{
                if (file.exists() && file.isFile()) {
                    long fileSize = file.length();
                    sendFileNameAndSize(writer, filePath, fileSize,orginalName,"C:\\user\\"+username+"\\");
                    sendFileData(os, file);
                    long capcity=(a-folderSize(file2));
                    System.out.println("Kalan depolama alanı : " + capcity  );
                    System.out.println("Dosya gönderildi: " + "C:\\user\\"+username+"\\"+file.getName());
                    listFiles("C:\\user\\"+username);

                    return new ErrorMessage("Dosya gönderildi: " + "C:\\user\\"+username+"\\"+orginalName,"200",fileDetail,capcity);
//error mesage ile dosya detaylarını döncez


                } else {
                    System.out.println("Belirtilen dosya bulunamadı veya bir dosya değil.");
                    notFound(writer,"notFoundFile");
                    return new ErrorMessage("Dosya gönderilmedi.Belirtilen dosya bulunamadı veya bir dosya değil. "+ "C:\\user\\"+username+"\\"+file.getName(),"",fileDetail,0L);

                }
            }

            //  System.out.println(folderSize(file2) +" bayttt");
            //214 748 36480 20gb
            //214 748 3648 2gb

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ErrorMessage("Dosya gönderilemedi ","InsufficientStorageSpace",null,0L);

    }


    private void sendFileNameAndSize(PrintWriter writer, String filepath, long fileSize, String fileName,String targetDirectory) {
        writer.println(filepath);
        writer.println(fileSize);
        writer.println(fileName);
        writer.println(targetDirectory);
    }
    private void Cancel(PrintWriter writer, String cancel) {
        writer.println(cancel);

    }
    private void notFound(PrintWriter writer, String cancel) {
        writer.println(cancel);

    }
    private  void  sendFileData(OutputStream os, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

        }
    }


    public List<FileDetail> listFiles(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            List<FileDetail>  a = new ArrayList<FileDetail>();

            if (files != null) {
                System.out.println("Klasördeki Dosyalar:");
                for (File file : files) {
                  if (file.isFile()) {

                      System.out.println(file.getName());
                      FileDetail fileDetail=FileDetail.builder()
                              .filesize(file.length())
                              .filepath(file.getPath())
                              .filename(file.getName())
                              .lastmodified(file.lastModified())
                              .build();
                     a.add(fileDetail);

                    }

                }
                return a;
            } else {
                System.out.println("Klasör boş.");
            }
        } else {
            System.out.println("Belirtilen klasör bulunamadı veya bir klasör değil.");
        }
        return null;
    }



}
