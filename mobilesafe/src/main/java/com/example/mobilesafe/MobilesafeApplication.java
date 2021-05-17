package com.example.mobilesafe;

import android.app.Application;

/**
 * Created by Administrator on 2017/6/10.
 */

public class MobilesafeApplication extends Application {

    //应用程序创建之前执行的第一个方法
    //适合做应用程序的初始化操作
    @Override
    public void onCreate() {
        //重写系统的异常处理器
        super.onCreate();
//        Thread.currentThread().setUncaughtExceptionHandler(new MyExceptionHandler());
    }


//    private class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
//        //当发现了未捕获异常的时候调用的方法
//        @Override
//        public void uncaughtException(Thread thread, Throwable ex) {
//            System.out.println("发生了异常，但是被哥捕获了。。。");
//            StringBuffer sb = new StringBuffer();
//            sb.append("time:");
//            sb.append(System.currentTimeMillis()+"\n");
//            Field[] fields = Build.class.getDeclaredFields();
//            for(Field field:fields){
//                try {
//                    String value = (String) field.get(null);
//                    String name = field.getName();
//                    sb.append(name+"="+value+"\n");
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            ex.printStackTrace(pw);
//            sb.append(sw.toString());
//            try {
//                File file = new File(Environment.getExternalStorageDirectory(),"error.log");
//                FileOutputStream fos = new FileOutputStream(file);
//                fos.write(sb.toString().getBytes());
//                fos.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            //自杀的方法. 早死早超生
//            android.os.Process.killProcess(android.os.Process.myPid());
//        }
//    }
}
