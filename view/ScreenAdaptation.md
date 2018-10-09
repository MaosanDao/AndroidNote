# 屏幕适配方案

### 下载
* [ladingwu/dimens_sw](https://github.com/ladingwu/dimens_sw)
### 步骤
* 在**DimenGenerator**文件中输入设计图的宽度尺寸（px）
* 在**DimenTypes**文件中输入你想生成的尺寸文件
>具体算法，公式为：px = dp / (dpi/160)，生成的就是Dp_sw__**px**(**px**)
```java
public enum DimenTypes {

    //适配Android 3.2以上   大部分手机的sw值集中在  300-460之间
    
  /**
   * 小米5的dpi是480,横向像素是1080px，根据px=dp(dpi/160)，
   * 横向的dp值是1080/(480/160),也就是360dp
   * 系统就会去寻找是否存在value-sw360dp的文件夹以及对应的资源文件
   */

  // values-sw300z
   DP_sw__300(300),
   DP_sw__320(320),
   DP_sw__340(340),
   DP_sw__360(360),
   DP_sw__380(380),
   DP_sw__400(400),
   DP_sw__420(420),
   DP_sw__440(440),
   DP_sw__460(460);
	//想生成多少自己以此类推
  
  //...OtherCode
}
```
* 编译生成后，将生成的文件放入到res目录下
* 如何使用？
>在xml中直接调用即可
```xml
<ImageView
      android:layout_width="@dimen/qb_px_10"
      android:layout_height="@dimen/qb_px_10" />
```
## 转载并整理至
* [拉丁吴 -- Android 目前最稳定和高效的UI适配方案](https://juejin.im/post/5ae9cc3a5188253dc612842b)
