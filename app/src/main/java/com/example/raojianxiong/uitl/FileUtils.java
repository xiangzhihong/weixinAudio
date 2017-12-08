package com.example.raojianxiong.uitl;

import android.os.Environment;

import java.io.File;

public class FileUtils {

  //文件BasePath
  public static String getBasePath() {
    String BASE_PATH=null;
    String BASE_FILE_NAME = "Butler";
    String sdcardState = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
      BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    return BASE_PATH + "/" + BASE_FILE_NAME + "/";
  }

  public static String getChatPath(String filename){
    String path = getChatPath() + filename;
    return path;
  }

  //聊天基础路径
  public static String getChatPath() {
    String fileDir = getBasePath() + "/chat/";
    File file = new File(fileDir);
    if (!file.exists()) {
      mkdir(new File(fileDir));
    }
    return fileDir;
  }

  public static void mkdir(File dir) {
    try {
      if (dir == null) return;
      if (!dir.exists()) {
        mkdir(dir.getParentFile());
        dir.mkdir();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
