# Activity跳转问题
***
## 假如A-B-C进行跳转，那么如何直接从C回到A？
```
核心原理，使用startActivityForResult进行跳转，然后在B中进行回调处理，即：
```
```java
//A启动B的代码：
Intent intent = new Intent(A.this,B.class); 
startActivityForResult(intent,0); 

//B中的代码
Intent intent = new Intent(B.this,C.class); 
startActivityForResult(intent,1); 

//在B中要处理，C返回的情况
@Override 
protected void onActivityResult(int requestCode, int resultCode, Intent data) 
  super.onActivityResult(requestCode, resultCode, data); 
  if(resultCode==RESULT_OK){ 
    setResult(RESULT_OK); 
    finish(); 
  } 
} 

//C中的代码
setResult(RESULT_OK); 
finish(); 
```

















































