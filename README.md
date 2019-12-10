# 计算机网络实验2——Socket

##最新进展

2019.12.10.20:10 增加了index.html，准备编写邮件处理逻辑  
2019.12.10.13:07 完善了http服务器的route功能，并增加了404页面  
2019.12.6.12:09 解决了HTTPParser中GET请求无参数导致数组越界的bug  
2019.12.6.12:08 找到了协议边界的发现方法，即line为空，而不是回车换行  
## 使用方法
如果你有git的使用经验，直接输入如下命令
```bash
git clone https://github.com/RaidriarB/JavaServerTest.git
```
或者下载压缩包，解压即可。  
请不要在没有git使用经验的情况下进行提交操作！如果发现bug请添加issue  
本项目由`Intellij idea`构建，使用idea直接可以打开。

## 介绍
一个简单的Socket实现WebMail的小作业。  
请web开发人员将网页结构放在web文件夹中。默认目录资源是index.html，不支持基于应用程序服务器的解析功能。  

  
目前完成的部分：http解析器、http对象、http响应对象、mainserver  
正在处理的部分： SMTP部分、HTTPServerThread  
还需完成的部分：SMTP部分以及网页部分。  
##各部分说明

### 解析器(HTTPParser)
解析函数原型
```$xslt
public HttpObject Parse(ArrayList<String> httpstr)
```
httpstr是http协议的多行字串表示。

### HTTPServerThread
功能1：读取客户端的http流，存入ArrayList
```$xslt
ArrayList<String> getHttpRequest(Socket incoming)
```
功能2：根据http请求得到响应，还包括404页面  
### HTTPRespObject
封装了http的响应。