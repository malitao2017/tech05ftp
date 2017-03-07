/*
 * FtpTest.java
 * Copyright: TsingSoft (c) 2015
 * Company: 北京清软创新科技有限公司
 */
package ftp2;
import org.apache.commons.io.IOUtils; 
import org.apache.commons.net.ftp.FTPClient; 

import java.io.File; 
import java.io.FileInputStream; 
import java.io.IOException; 
import java.io.FileOutputStream; 

/** 
* Apache commons-net 试用一把，看看FTP客户端工具做的好用不 
* 
* @author : leizhimin，2008-8-20 14:00:38。<p> 
*/ 
public class FtpTest { 
    public static void main(String[] args) { 
    	System.out.println("begin");
        testUpload(); 
        testDownload(); 
    } 

//    static String ip ="192.168.0.68";
    static String ip ="192.168.1.112";
    
    /** 
     * FTP上传单个文件测试 
     */ 
    public static void testUpload() { 
        FTPClient ftpClient = new FTPClient(); 
        FileInputStream fis = null; 

        try { 
            ftpClient.connect(ip); 
            ftpClient.login("admin", "admin"); 

            File srcFile = new File("D:\\work\\ftp-Serv-U-root\\admin\\pic\\test.jpg"); 
            fis = new FileInputStream(srcFile); 
            //设置上传目录 
            ftpClient.changeWorkingDirectory("/admin/pic"); //若没有该路径则会上传到根目录
            ftpClient.setBufferSize(1024); 
            ftpClient.setControlEncoding("GBK"); 
            //设置文件类型（二进制） 
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 
            ftpClient.storeFile("theUpload.gif", fis); 
        } catch (IOException e) { 
            e.printStackTrace(); 
            throw new RuntimeException("FTP客户端出错！", e); 
        } finally { 
            IOUtils.closeQuietly(fis); 
            try { 
                ftpClient.disconnect(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
                throw new RuntimeException("关闭FTP连接发生异常！", e); 
            } 
        } 
    } 

    /** 
     * FTP下载单个文件测试 
     */ 
    public static void testDownload() { 
        FTPClient ftpClient = new FTPClient(); 
        FileOutputStream fos = null; 

        try { 
            ftpClient.connect(ip); 
            ftpClient.login("admin", "admin"); 

            String remoteFileName = "/admin/pic/theUpload.gif"; 
            fos = new FileOutputStream("D:\\work\\ftp-Serv-U-root\\admin\\pic\\theDown.gif"); 

            ftpClient.setBufferSize(1024); 
            //设置文件类型（二进制） 
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 
            ftpClient.retrieveFile(remoteFileName, fos); 
        } catch (IOException e) { 
            e.printStackTrace(); 
            throw new RuntimeException("FTP客户端出错！", e); 
        } finally { 
            IOUtils.closeQuietly(fos); 
            try { 
                ftpClient.disconnect(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
                throw new RuntimeException("关闭FTP连接发生异常！", e); 
            } 
        } 
    } 
} 