# 介绍
>本篇文章将讲述怎么把本地已经包装好的lib库上传打包到jCenter代码库中，以及如何进行更新等操作。如有错误，恳请纠正。

## 准备工作
### 准备好一个开发者账号（按步骤进行）
* 点击这个[网址](https://bintray.com/signup/oss)
* 按照提示的信息进行填写完毕（最好使用google、outlook等国外的邮箱进行注册）
* 进入到主界面后会出现以下界面

![步骤1](https://github.com/MaosanDao/AndroidQuickCheckList/blob/master/uploadJcenter/2.png)

* 然后点击**Add New Repository**,会进入到以下界面

![步骤2](https://github.com/MaosanDao/AndroidQuickCheckList/blob/master/uploadJcenter/5.png)

* 新建完成后，点击个人右上方的用户名，进入API Key界面获取user和key

![步骤3](https://github.com/MaosanDao/AndroidQuickCheckList/blob/master/uploadJcenter/1.png)

* 开始获取发布必须的username和key

![步骤4](https://github.com/MaosanDao/AndroidQuickCheckList/blob/master/uploadJcenter/3.png)

* 获取key

![步骤5](https://github.com/MaosanDao/AndroidQuickCheckList/blob/master/uploadJcenter/4.png)

### 截止目前准备工作已经完成，接下来开始配置项目了
#### 首先在项目级别的**build.gradle**中配置以下内容：
* 在dependencies{}中增加：
```java
  classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.3'
  classpath 'com.github.dcendents:android-maven-gradle-plugin:1.5'
``` 

* 在allproject{}中增加：
```Java
tasks.withType(Javadoc) {
        options.addStringOption('Xdoclint:none', '-quiet')
        options.addStringOption('encoding', 'UTF-8')
}
```
#### 然后在**lib**级别的**build.gradle**配置以下内容
* 在顶部增加：
```java
apply plugin: 'com.github.dcendents.android-maven'
apply plugin: 'com.jfrog.bintray'
```
* 在最底部增加以下内容：
```Java
def siteUrl = 'https://github.com/xxxxx/xxxx.git'      // 项目的主页(可以写自己的库的GitHub地址)
def gitUrl = 'https://github.com/xxxxx/xxxx.git'      // Git仓库的url  这个是说明，可随便填
group = "xx.xxxxxx"  // 这里是groupId ,必须填写  一般填你唯一的包名，对应com.squareup.okhttp3:okhttp:3.4.1中的com.squareup.okhttp3部分

install {
    repositories.mavenInstaller {
        pom {
            project {
                packaging 'aar'
                name 'FastHeadPhone'     //项目名字
                url siteUrl
                licenses {
                    license {
                        name 'The CommonUtil Software License, Version 1.0.0'
                        url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                    }
                }
                developers {
                    developer {
                        id 'xxxxx'        //填写开发者的一些基本信息
                        name 'xxxxx'    //填写开发者的一些基本信息
                        email 'xxxxxx@xxxxx'   //填写开发者的一些基本信息
                    }
                }
                scm {
                    connection gitUrl
                    developerConnection gitUrl
                    url siteUrl
                }
            }
        }
    }
}
task sourcesJar(type: Jar) {
    from android.sourceSets.main.java.srcDirs
    classifier = 'sources'
}
task javadoc(type: Javadoc) {
    source = android.sourceSets.main.java.srcDirs
    classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
}
task javadocJar(type: Jar, dependsOn: javadoc) {
    classifier = 'javadoc'
    from javadoc.destinationDir
}
artifacts {
    archives javadocJar
    archives sourcesJar
}

Properties properties = new Properties()
properties.load(project.rootProject.file('local.properties').newDataInputStream())
bintray {
//    userOrg=properties.getProperty("bintray.user")
    user = properties.getProperty("bintray.user")    //读取 local.properties 文件里面的 bintray.user
    key = properties.getProperty("bintray.apikey")   //读取 local.properties 文件里面的 bintray.apikey
    configurations = ['archives']
    pkg {
        repo = "maven"
        name = "xxxxxx"    //（慎重填写）发布到JCenter上的项目名字，必须填写，对应com.squareup.okhttp3:okhttp:3.4.1中的okhttp
        websiteUrl = siteUrl
        vcsUrl = gitUrl
        licenses = ["Apache-2.0"]
        publish = true
    }
}
```
#### 最后在**local.properties**中配置以下内容（千万不要把这个也上传到github中，会造成信息泄露）
```Java
bintray.user=XXX//这里写入在bintray中注册时候的用户名
bintray.apikey=XXX//这里写入刚才获取到的key值
```
## 最后发布
* 在Android Studio的Terminal中敲击以下命令即可发布：
```Java
gradle install
grable bintrayUpload
```
## 最后上传代码库到jCenter中央库中
* 进入自己的jCenter的仓库中，找到自己才上传的库。你会看到以下界面：

![步骤6](https://github.com/MaosanDao/AndroidQuickCheckList/blob/master/uploadJcenter/6.png)

* 继续

![步骤7](https://github.com/MaosanDao/AndroidQuickCheckList/blob/master/uploadJcenter/7.png)

* 获取地址

![步骤8](https://github.com/MaosanDao/AndroidQuickCheckList/blob/master/uploadJcenter/8.png)

## 升级库的版本
* 在android{}标签的上方增加以下内容：

```Java
version = "0.0.1" //这里上传到jcenter是版本的控制
```
* 如何升级
```
只需要将上述的版本号进行修改再上传就行了
```

## 温馨提示
* 代码库提交到jCenter的时候，可能会有4-6个小时的审核时间
* gradle的环境需要自己进行配置

