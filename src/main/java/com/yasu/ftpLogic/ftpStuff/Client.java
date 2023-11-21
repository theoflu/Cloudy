package com.yasu.ftpLogic.ftpStuff;

import com.yasu.ftpLogic.entity.FileDetail;
import com.yasu.ftpLogic.errorHandling.ErrorMessage;
import org.aspectj.bridge.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private void basla(){
        try{
            Socket socket = new Socket(serverAddress, serverPort);
            socket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    public ErrorMessage sendFile(String filePath, String username){
        try  (Socket socket = new Socket(serverAddress, serverPort)) {
            try {

                Thread.sleep(2000); // Millisaniye cinsinden süreyi belirtir (5 saniye = 5000 milisaniye)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            OutputStream os = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(os, true);

            File file = new File(filePath);

            long a=2147483648L;
            File file2 = new File("C:\\user\\"+username);
            if(folderSize(file2)>= a)
            {

              System.out.println(folderSize(file2) +" bayttt");
              Cancel(writer,"InsufficientStorageSpace");


             }
            else{
                if (file.exists() && file.isFile()) {
                    long fileSize = file.length();
                    sendFileNameAndSize(writer, filePath, fileSize,file.getName(),"C:\\user\\"+username+"\\");
                    sendFileData(os, file);
                    long capcity=(a-folderSize(file2));
                    System.out.println("Kalan depolama alanı : " + capcity  );
                    System.out.println("Dosya gönderildi: " + "C:\\user\\"+username+"\\"+file.getName());
                    listFiles("C:\\user\\"+username);
                    return new ErrorMessage("Dosya gönderildi: " + "C:\\user\\"+username+"\\"+file.getName(),"200");



                } else {
                    System.out.println("Belirtilen dosya bulunamadı veya bir dosya değil.");
                    notFound(writer,"notFoundFile");
                    return new ErrorMessage("Dosya gönderilmedi.Belirtilen dosya bulunamadı veya bir dosya değil. "+ "C:\\user\\"+username+"\\"+file.getName(),"");

                }
            }

            //  System.out.println(folderSize(file2) +" bayttt");
            //214 748 36480 20gb
            //214 748 3648 2gb

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ErrorMessage("Dosya gönderilemedi ","InsufficientStorageSpace");

    }

    public FileDetail downloadFile(String filePath, String username) {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            OutputStream os = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(os, true);
            InputStream is = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // Sunucuya dosya yolunu gönder
            writer.println(filePath);

            // Sunucudan gelen yanıtı oku
            String response = reader.readLine();

            // Dosya bulunamazsa veya depolama alanı yetersizse işlemi sonlandır
            if (response.equals("notFoundFile") || response.equals("InsufficientStorageSpace")) {
                System.out.println("İndirme başarısız: " + response);
                return null;
            }

            // Dosya bilgilerini oku
            long fileSize = Long.parseLong(reader.readLine());
            String fileName = reader.readLine();
            String targetDirectory = reader.readLine();

            // Dosya indirme yolu
            String downloadPath = "C:\\user\\" + username + "\\" + fileName;

            // Dosyayı sunucudan indir
            try (FileOutputStream fos = new FileOutputStream(downloadPath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;

                System.out.println("Dosya indiriliyor: " + fileName);
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }

                System.out.println("Dosya indirme tamamlandı: " + fileName);
                fos.close();

                // İndirilen dosyanın detaylarını oluştur
                File downloadedFile = new File(downloadPath);
                FileDetail fileDetail = FileDetail.builder()
                        .filename(downloadedFile.getName())
                        .filepath(downloadPath)
                        .filesize(downloadedFile.length())
                        .build();

                return fileDetail;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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



    public static void main(String[] args) {
        //Client client = new Client("localhost", 3456);
       //client.sendFile("C:\\Users\\yusuf\\OneDrive\\Masaüstü\\haftalık.rar","as3d");

    }
}
