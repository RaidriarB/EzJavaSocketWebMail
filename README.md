# 计算机网络实验2——Socket
## 介绍
一个简单的Socket实现WebMail的小作业。by RaidriarB  
请web开发人员将网页结构放在web文件夹中。默认目录资源是index.html，不支持基于应用程序服务器的解析功能。  
提供了验证和发送邮件的api，分别是/checkLogin和/sendMail。
非常欢迎对代码的讨论和建议，请发送至邮箱raidriarb@foxmail.com，我会随时关注。

## 使用方法
源代码
```bash
git clone https://github.com/RaidriarB/JavaServerTest.git
```
或者在out文件夹下，把web目录拷贝过来，输入命令  
```
java MainServer
```
或者下载压缩包，解压即可。  
请不要在没有git使用经验的情况下进行提交操作！如果发现bug请添加issue  
本项目由`Intellij idea`构建，使用idea直接可以打开。


## 最新进展

2019.12.18 除保存日志模块，已全部完成。  
2019.12.18 支持了群发  
2019.12.18 解决了换行符不匹配、输入过快导致线程安全问题等bug  
2019.12.10.20:10 增加了index.html，准备编写邮件处理逻辑  
2019.12.10.13:07 完善了http服务器的route功能，并增加了404页面  
2019.12.6.12:09 解决了HTTPParser中GET请求无参数导致数组越界的bug  
2019.12.6.12:08 找到了协议边界的发现方法，即line为空，而不是回车换行  

## 模块说明
已全部规范注释。