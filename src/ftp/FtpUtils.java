/*
 * FtpUtils.java
 * Copyright: TsingSoft (c) 2015
 * Company: 北京清软创新科技有限公司
 */
package ftp;

/*
 * http://dengli19881102.iteye.com/blog/1028957
 * 
 * 项目需要，网上搜了搜，很多，但问题也不少，估计转来转去，少了不少东西，而且也情况也不太一样。没办法，只能自己去写一个。
 一，    安装sserv-u ftp服务器 版本10.1.0.1 
 我所设服务器配置：
 用户名：shiyanming
 密码：123
 端口：21
 跟目录：D:/ftpindex

 二、所需jar包：common-net-1.4.1.jar
 jakarta-oro-2.0.8.jar
 注意：第二个jar包必须要存在，不然在列举ftp服务器中文件是出错

 三、中文传输问题
 3.1、 默认情况下，FtpClient使用的是UTF_8字符集作为服务器通讯的编码集。而FTP服务器SERV-U在windowsXP上，使用GBK字符集作为服务器通讯。
 // 下面三行代码必须要，而且不能改变编码格式，否则不能正确下载中文文件
 ftp.setControlEncoding("GBK");
 FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
 conf.setServerLanguageCode("zh");
 3.2、同时还要设置服务器
 设置步骤：
 1、打开Serv-U 8.0控制台，点击“限制和设置”--“为域配置高级FTP命令设置和行为”。
 2、在FTP设置中找到OPTS UTF8命令，右击禁用此命令。3、点击下面的“全局属性”。
 4、在出来的FTP命令属性选项卡中，“高级选项”里，把“对所有收发的路径和文件名使用UFT-8编码”前面的钩去掉！
 5、以后再上传中文文件，就不会出现乱码问题啦。

 四、具体程序   com.ftp. FtpUtils.java
 上边程序完成了文件的上传和下载
 重命名判断问题，如果重名，在后边加（n）。
 存在问题：如果jsp页面中读取from表单的值，只能获取主机的地址，不能上传客户端文件。

 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @author shiyanming
 * 
 */

public class FtpUtils {
	/**
	 * 
	 * @param args
	 * 
	 * @throws FileNotFoundException
	 * 
	 *             测试程序
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws FileNotFoundException {
		String url = "192.168.0.68"; //172.25.5.193
		int port = 21;
		String username = "admin";
		String password = "admin";
		/**
		 * 以下的三个过程正好是：在一个路径里把 test.jpg 
		 * 上传为 theUpload.gif 下载为 theDown.gif 再删除 theUpload.gif
		 */
		FtpUtils a = new FtpUtils();
		String ftppath = "/admin/pic";
		String filename = "theUpload.gif";
		InputStream input = new FileInputStream("D:\\work\\ftp-Serv-U-root\\admin\\pic\\test.jpg");
		//上传 包含辅助方法，对重名的处理 changeName 及其所需 isDirExist
		a.uploadFile(url, port, username, password, ftppath, 
				filename,input);
		
		OutputStream fos = new FileOutputStream("D:\\work\\ftp-Serv-U-root\\admin\\pic\\theDown.gif");//要输出的文件名 
		String filename2 =filename; // 从服务器上要取的文件名
		//下载
		a.downFile(url, port, username, password, ftppath,
				filename2, fos,null);
		//删除
//		a.deleteFile(url, port, username, password, ftppath, 
//				filename);
	}
	/**
	 * 上传程序方法
	 * 
	 */
	public boolean uploadFile(String url, int port, String username,
			String password, String path, String filename, InputStream input) {
		// filename:要上传的文件
		// path :上传的路径
		// 初始表示上传失败
		boolean success = false;
		// 创建FTPClient对象
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.connect(url, port);
			// 下面三行代码必须要，而且不能改变编码格式，否则不能正确下载中文文件
			ftp.setControlEncoding("GBK");
			FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
			conf.setServerLanguageCode("zh");
			// 登录ftp
			ftp.login(username, password);
			// 看返回的值是不是230，如果是，表示登陆成功
			reply = ftp.getReplyCode();
			// 以2开头的返回值就会为真
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				System.out.println("连接服务器失败");
				return success;
			}
			System.out.println("登陆服务器成功");
			ftp.changeWorkingDirectory(path);// 转移到FTP服务器目录
			FTPFile[] fs = ftp.listFiles(); // 得到目录的相应文件列表
			System.out.println(fs.length);
			System.out.println(filename);
			String filename1 = FtpUtils.changeName(filename, fs);
			String filename2 = new String(filename1.getBytes("GBK"), "ISO-8859-1");
			String path1 = new String(path.getBytes("GBK"), "ISO-8859-1");
			// 转到指定上传目录
			ftp.changeWorkingDirectory(path1);
			// 将上传文件存储到指定目录
			// ftp.appendFile(new String(filename.getBytes("GBK"),"ISO-8859-1"),
			// input);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			// 如果缺省该句 传输txt正常 但图片和其他格式的文件传输出现乱码
			ftp.storeFile(filename2, input);
			// 关闭输入流
			input.close();
			// 退出ftp
			ftp.logout();
			// 表示上传成功
			success = true;
			System.out.println("上传成功。。。。。。");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return success;
	}

	/**
	 * 
	 * 删除程序
	 * 
	 * 
	 */
	public boolean deleteFile(String url, int port, String username,
	String password, String path, String filename) {
		// filename:要上传的文件
		// path :上传的路径
		// 初始表示上传失败
		boolean success = false;
		// 创建FTPClient对象
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.connect(url, port);
			// 下面三行代码必须要，而且不能改变编码格式，否则不能正确下载中文文件
			ftp.setControlEncoding("GBK");
			FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
			conf.setServerLanguageCode("zh");
			// 登录ftp
			ftp.login(username, password);
			// 看返回的值是不是230，如果是，表示登陆成功
			reply = ftp.getReplyCode();
			// 以2开头的返回值就会为真
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				System.out.println("连接服务器失败");
				return success;
			}
			System.out.println("登陆服务器成功");
			String filename2 = new String(filename.getBytes("GBK"),"ISO-8859-1");
			String path1 = new String(path.getBytes("GBK"), "ISO-8859-1");
			// 转到指定上传目录
			ftp.changeWorkingDirectory(path1);
//			FTPFile[] fs = ftp.listFiles(); // 得到目录的相应文件列表
			ftp.deleteFile(filename2);
			ftp.logout();
			success = true;
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return success;
	}
	/**
	 * 
	 * 下载程序
	 * 
	 * 
	 */
	public static boolean downFile(String ip, int port, String username,
	String password, String remotePath, String fileName,
	OutputStream outputStream, HttpServletResponse response) {
		boolean success = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(ip, port);
			// 下面三行代码必须要，而且不能改变编码格式
			ftp.setControlEncoding("GBK");
			FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
			conf.setServerLanguageCode("zh");
			// 如果采用默认端口，可以使用ftp.connect(url) 的方式直接连接FTP服务器
			ftp.login(username, password);// 登录
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			System.out.println("登陆成功。。。。");
			ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
			FTPFile[] fs = ftp.listFiles(); // 得到目录的相应文件列表
			// System.out.println(fs.length);//打印列表长度
			for (int i = 0; i < fs.length; i++) {
				FTPFile ff = fs[i];
				if (ff.getName().equals(fileName)) {
					String filename = fileName;
					System.out.println(filename);
					// 这个就就是弹出下载对话框的关键代码
//					response.setHeader("Content-disposition",
//					"attachment;filename="
//					+ URLEncoder.encode(filename, "utf-8"));
					// 将文件保存到输出流outputStream中
					ftp.retrieveFile(new String(ff.getName().getBytes("GBK"),"ISO-8859-1"), outputStream);
					outputStream.flush();
					outputStream.close();
				}
			}
			ftp.logout();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return success;
	}

	// 判断是否有重名方法
	public static boolean isDirExist(String fileName, FTPFile[] fs) {
		for (int i = 0; i < fs.length; i++) {
			FTPFile ff = fs[i];
			if (ff.getName().equals(fileName)) {
				return true; // 如果存在返回 正确信号
			}
		}
		return false; // 如果不存在返回错误信号
	}
	// 根据重名判断的结果 生成新的文件的名称
	public static String changeName(String filename, FTPFile[] fs) {
		int n = 0;
		// 创建一个可变的字符串对象 即StringBuffer对象，把filename值付给该对象
		StringBuffer filename1 = new StringBuffer("");
		filename1 = filename1.append(filename);
		System.out.println(filename1);
		while (isDirExist(filename1.toString(), fs)) {
			n++;
			String a = "[" + n + "]";
			System.out.println("字符串a的值是：" + a);
			int b = filename1.lastIndexOf(".");// 最后一出现小数点的位置
			int c = filename1.lastIndexOf("[");// 最后一次"["出现的位置
			if (c < 0) {
				c = b;
			}
			StringBuffer name = new StringBuffer(filename1.substring(0, c));// 文件的名字
			StringBuffer suffix = new StringBuffer(filename1.substring(b + 1));// 后缀的名称
			filename1 = name.append(a).append(".").append(suffix);
		}
		return filename1.toString();

	}
}
