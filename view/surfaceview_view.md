# SurfaceView和View的区别以及一些知识点
## Surface的优势
>SurfaceView中采用了双缓冲机制，保证了UI界面的流畅性，同时SurfaceView不在主线程中绘制，而是另开辟一个线程去绘制，所以它不妨碍UI线程；
## 和View的区别（SurfaceView继承与View）
* View底层没有双缓冲机制，SurfaceView有
* View主要适用于主动更新，而SurfaceView适用与被动的更新，如频繁的刷新
* View会在主线程中去更新UI，而SurfaceView则在子线程中刷新
## 各自的特点
### View
>显示视图，内置画布，提供图形绘制函数、触屏事件、按键事件函数等；必须在UI主线程内更新画面，速度较慢。
### SurfaceView
>基于view视图进行拓展的视图类，更适合2D游戏的开发；是view的子类，类似使用双缓机制，在新的线程中更新画面所以刷新界面速度比view快，Camera预览界面使用SurfaceView
### GLSurfaceView
>基于SurfaceView视图再次进行拓展的视图类，专用于3D游戏开发的视图；是SurfaceView的子类，openGL专用
