程序说明：
1. ftp2.FtpTest.java 是基本上传、下载
2. ftp.FtpUtils.java 是较全的，包括 上传、下载和删除名称、验证、中文乱码问题
3. jakarta-oro-2.0.8.jar 做保留，目前没有用到


ftp服务器有两种： win7自带IIS 三方软件Serv-U ；关于用户名-匿名的情况就是不用用户名亦可进去，如FlashFXP工具
第一、 IIS
详见
http://jingyan.baidu.com/album/3065b3b6c90ff9becef8a464.html?picindex=5
http://blog.sina.com.cn/s/blog_6cccb1630100q0qg.html

windows的服务名称为：Microsoft FTP Server
IIS的意思为 Internet Information(信息) Server(服务)

添加的用户即为windows的计算机管理用户

第二、ftp服务器端是： Serv-U
说明：
http://blog.163.com/zoulei154@126/blog/static/4645500220093831019355/
下载绿色版：
http://www.piaodown.com/down/soft/154.htm

设置的时候安装好之后最好使用浏览器的管理页面比较好：
http://127.0.0.1:43958/?Session=16890&Language=zh,CN&LocalAdmin=1&Sync=16293540

添加域（即一个ftp节点）的时候
1.把http改为800以表区别
2.用户admin

用户1：
目前ip： 192.168.0.68
用名： admin
密码： admin
端口： 21 ，http即浏览器访问使用800
根目录：D:\work\ftp-Serv-U-root
权限：完全读写下载删除

使用浏览器访问为：

ftp://192.168.0.68/





