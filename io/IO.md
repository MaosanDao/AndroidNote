# 数据写入的一些常用方法

# 列表
* [将byte[]数据保存到文件并存储到SD卡根目录](#将byte[]数据保存到文件并存储到sd卡根目录)
* [将图片保存的手机的图库](#将图片保存的手机的图库)
* [保存录像到图库](#保存录像到图库)

## 将byte[]数据保存到文件并存储到SD卡根目录
```java
/*
 * 此方法为android程序写入sd文件文件，用到了android-annotation的支持库@
 *
 * @param buffer   写入文件的内容
 * @param folder   保存文件的文件夹名称,如log；可为null，默认保存在sd卡根目录
 * @param fileName 文件名称，默认app_log.txt
 * @param append   是否追加写入，true为追加写入，false为重写文件
 * @param autoLine 针对追加模式，true为增加时换行，false为增加时不换行
 */
public synchronized static void writeFileToSDCard(@NonNull final byte[] buffer, @Nullable final String folder,
                                                  @Nullable final String fileName, final boolean append, final boolean autoLine) {
    new Thread(new Runnable() {
        @Override
        public void run() {
            boolean sdCardExist = Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED);
            String folderPath = "";
            if (sdCardExist) {
                //TextUtils为android自带的帮助类
                if (TextUtils.isEmpty(folder)) {
                    //如果folder为空，则直接保存在sd卡的根目录
                    folderPath = Environment.getExternalStorageDirectory()
                            + File.separator;
                } else {
                    folderPath = Environment.getExternalStorageDirectory()
                            + File.separator + folder + File.separator;
                }
            } else {
                return;
            }


            File fileDir = new File(folderPath);
            if (!fileDir.exists()) {
                if (!fileDir.mkdirs()) {
                    return;
                }
            }
            File file;
            //判断文件名是否为空
            if (TextUtils.isEmpty(fileName)) {
                file = new File(folderPath + "test456.dat");
            } else {
                file = new File(folderPath + fileName);
            }
            RandomAccessFile raf = null;
            FileOutputStream out = null;
            try {
                if (append) {
                    //如果为追加则在原来的基础上继续写文件
                    raf = new RandomAccessFile(file, "rw");
                    raf.seek(file.length());
                    raf.write(buffer);
                    if (autoLine) {
                        raf.write("\n".getBytes());
                    }
                } else {
                    //重写文件，覆盖掉原来的数据
                    out = new FileOutputStream(file);
                    out.write(buffer);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }).start();
}

```
## 将图片保存的手机的图库
```java
fun saveImageToGallery(context: Context, bmp: ByteArray, listener: SaveStatusListener) {
    // 首先保存图片
    val appDir = File(Environment.getExternalStorageDirectory(),
            "VENII")
    if (!appDir.exists()) {
        appDir.mkdir()
    }
    val fileName = System.currentTimeMillis().toString() + ".jpg"
    val file = File(appDir, fileName)
    try {
        val fos = FileOutputStream(file)
        bytes2Bimap(bmp).compress(Bitmap.CompressFormat.JPEG, 50, fos)
        fos.flush()
        fos.close()
        runOnUiThread {
            listener.saveSuccess()
        }
    } catch (e: FileNotFoundException) {
        listener.saveFaild()
        LogTrack.d("保存失败")
        e.printStackTrace()
    } catch (e: IOException) {
        listener.saveFaild()
        LogTrack.d("保存失败")
        e.printStackTrace()
    }

    // 最后通知图库更新
    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
            Uri.fromFile(File(file.path))))
}
```
## 保存录像到图库
```java
fun saveVideoToGallery(bmp: File, listener: SaveStatusListener) {
  val appDir = File(Environment.getExternalStorageDirectory(),
          "VENII")
  if (!appDir.exists()) {
      appDir.mkdir()
  }
  val fileName = System.currentTimeMillis().toString() + ".mp4"
  val file = File(appDir, fileName)
  try {
      val oos = FileOutputStream(file)
      //写入
      oos.write(bmp.readBytes())
      oos.flush()
      oos.close()
      runOnUiThread {
          listener.saveSuccess()
      }
  } catch (e: FileNotFoundException) {
      listener.saveFaild()
      LogTrack.d("保存失败")
      e.printStackTrace()
  } catch (e: IOException) {
      listener.saveFaild()
      LogTrack.d("保存失败")
      e.printStackTrace()
  }
  }
```
