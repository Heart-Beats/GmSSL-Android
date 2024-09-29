# GmSSL-Android
Android 平台开发可直接使用的 GMSSL 依赖库， 是基于  [GmSSL-Java](https://github.com/GmSSL/GmSSL-Java#gmssl-java)  进行改造封装的，依赖的 GmSSL 密码库版本： GmSSL 3.1.0。



1. 依赖方式：

   - 首先根项目下加入仓库地址

     ```groovy
     repositories {
             // ...
             maven {
                 name = "GitHubPackages"
                 url 'https://maven.pkg.github.com/Heart-Beats/BaseProject'
                 credentials {
                     username = 'Heart-Beats'
                     password = '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'   // github 检测会导致失效，可以私下询问或者克隆代码自己编译上传私库
                 }
             }
         }
     ```

   - 使用该库的模块中加入以下依赖即可

     ```groovy
     dependencies {
         // ...
     	implementation "com.hl.gmssl:android:3.1.0"
     }
     ```

     

2. 相关的接口使用见 GmSSL-Java 的 [开发手册](https://github.com/GmSSL/GmSSL-Java#%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C) 。



最后祝您使用愉快！

