# Gradle多渠道打包
## 多渠道打包
1.在AndroidManifest.xml中使用placeholder
```java
<meta-data
 android:name="UMENG_CHANNEL"
 android:value="${UMENG_CHANNEL_VALUE}" />
```
2.在app的gradle.build中添加以下内容：
```xml
android {  
  productFlavors {
      xiaomi {
           manifestPlaceholders = [UMENG_CHANNEL_VALUE: "xiaomi"]
      }
      _360 {
           manifestPlaceholders = [UMENG_CHANNEL_VALUE: "_360"]
      }
      baidu {
           manifestPlaceholders = [UMENG_CHANNEL_VALUE: "baidu"]
      }
  }  
}
```
3.或者批量修改
```xml
 android {  
     productFlavors {
         xiaomi {}
         _360 {}
         baidu {}
         wandoujia {}
     }  

     productFlavors.all { 
         flavor -> flavor.manifestPlaceholders = [UMENG_CHANNEL_VALUE: name] 
     }
}
```
4.执行打包命令
```java
gradle assembleDebug
gradle assembleRelease

//如果我们只打wandoujia渠道版本
gradlew assembleWandoujiaRelease 
//此命令会生成wandoujia渠道的Release和Debug版本
gradlew assembleWandoujia
//这条命令会把Product Flavor下的所有渠道的Release版本都打出来。
gradlew assembleRelease
```
## 完整的Gradle示例
```java
apply plugin: 'com.android.application'

def releaseTime() {
    return new Date().format("yyyy-MM-dd", TimeZone.getTimeZone("UTC"))
}

android {
    compileSdkVersion 21
    buildToolsVersion '21.1.2'

    defaultConfig {
        applicationId "com.boohee.*"
        minSdkVersion 14
        targetSdkVersion 21
        versionCode 1
        versionName "1.0"
        
        // dex突破65535的限制
        multiDexEnabled true
        // 默认是umeng的渠道
        manifestPlaceholders = [UMENG_CHANNEL_VALUE: "umeng"]
    }

    lintOptions {
        abortOnError false
    }

    signingConfigs {
        debug {
            // No debug config
        }

        release {
            storeFile file("../yourapp.keystore")
            storePassword "your password"
            keyAlias "your alias"
            keyPassword "your password"
        }
    }

    buildTypes {
        debug {
            // 显示Log
            buildConfigField "boolean", "LOG_DEBUG", "true"

            versionNameSuffix "-debug"
            minifyEnabled false
            zipAlignEnabled false
            shrinkResources false
            signingConfig signingConfigs.debug
        }

        release {
            // 不显示Log
            buildConfigField "boolean", "LOG_DEBUG", "false"

            minifyEnabled true
            zipAlignEnabled true
            // 移除无用的resource文件
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release

            applicationVariants.all { variant ->
                variant.outputs.each { output ->
                    def outputFile = output.outputFile
                    if (outputFile != null && outputFile.name.endsWith('.apk')) {
                    	// 输出apk名称为boohee_v1.0_2015-01-15_wandoujia.apk
                        def fileName = "boohee_v${defaultConfig.versionName}_${releaseTime()}_${variant.productFlavors[0].name}.apk"
                        output.outputFile = new File(outputFile.parent, fileName)
                    }
                }
            }
        }
    }

```
## 友盟多渠道打包
```java
productFlavors {
        wandoujia {}
        _360 {}
        baidu {}
        xiaomi {}
        tencent {}
        taobao {}
        ...
    }

    productFlavors.all { flavor ->
        flavor.manifestPlaceholders = [UMENG_CHANNEL_VALUE: name]
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:support-v4:21.0.3'
    compile 'com.jakewharton:butterknife:6.0.0'
    ...
}
```
## 参考文章
>* [手把手教你AndroidStudio多渠道打包](https://blog.csdn.net/mynameishuangshuai/article/details/51783303)
