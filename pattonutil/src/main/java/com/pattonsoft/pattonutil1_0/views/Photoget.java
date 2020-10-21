package com.pattonsoft.pattonutil1_0.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Photoget {

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1000;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 1001;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 1002;// 结果
    public static File myfile;
    private static File myfile2;

	/*
*/

    private static File getTempfile2() {

        File tempFile2 = new File(Environment.getExternalStorageDirectory()
                .getPath()
                + "/gna/"
                + Calendar.getInstance().getTimeInMillis()
                + ".jpg"); // 以时间秒为文件名
        File temp = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/gna/");// 自已项目 文件夹
        if (!temp.exists()) {
            temp.mkdir();
        }

        myfile2 = tempFile2;
        return tempFile2;
    }

    private static File getTempfile() {
        File tempFile = new File(Environment.getExternalStorageDirectory(),
                getPhotoFileName());
        myfile = tempFile;
        return tempFile;
    }

    // 使用系统当前日期加以调整作为照片的名称
    private static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault());
        return "pic_" + dateFormat.format(date) + ".jpg";
    }

    /**
     * 调用起拍照请求,
     * <p>
     * 请求码
     *
     * @param context
     */
    public static void takephoto(Context context) {
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempfile()));
        Activity activity = (Activity) context;
        activity.startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
    }

    /**
     * 调用起拍照请求,
     *
     * @param requestCode 请求码
     * @param context
     */
    public static void takephoto(Context context, int requestCode) {
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempfile()));
        Activity activity = (Activity) context;
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 调用照片选择请求
     *
     * @param context
     */
    public static void getphoto(Context context) {

        // 选择图片
        Intent intent2 = new Intent(Intent.ACTION_PICK, null);
        intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        Activity activity = (Activity) context;
        activity.startActivityForResult(intent2, PHOTO_REQUEST_GALLERY);

    }

    /**
     * 调用照片选择请求
     *
     * @param requestCode 请求码
     * @param context
     */
    public static void getphoto(Context context, int requestCode) {

        // 选择图片
        Intent intent2 = new Intent(Intent.ACTION_PICK, null);
        intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        Activity activity = (Activity) context;
        activity.startActivityForResult(intent2, requestCode);

    }

    /**
     * 图片裁剪
     *
     * @param uri
     * @param size
     * @param context
     */
    public static void startPhotoZoom(Uri uri, int size, Context context) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        intent.putExtra("output", uri); // 专入目标文件
        Activity activity = (Activity) context;
        activity.startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 图片裁剪
     *
     * @param uri
     * @param size
     * @param context
     * @param requestCode
     */
    public static void startPhotoZoom(Uri uri, int size, Context context,
                                      int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        // intent.putExtra("output", uri); // 专入目标文件
        Activity activity = (Activity) context;
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 选择 裁剪图片
     *
     * @param requestCode 请求码
     * @param data        选择照片的路径
     * @param context
     */
    public static void toPhotoZoom(int requestCode, Intent data,
                                   Context context, Handler handler) {

        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:

                Uri uri = Uri.fromFile(myfile);
                if (myfile.exists()) {
                    startPhotoZoom(uri, 300, context);
                }
                break;

            case PHOTO_REQUEST_GALLERY:
                if (null != data) {
                    startPhotoZoom(data.getData(), 300, context);
                }
                break;

            case PHOTO_REQUEST_CUT:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    Bitmap photo = bundle.getParcelable("data");
                    if (photo == null) {
                        return;
                    }


                    handler.sendEmptyMessage(0);

                }

                break;
        }
    }

    /**
     * 选择 裁剪图片
     *
     * @param requestCode 请求码
     * @param file        选择拍照时的图片保存文件
     * @param data        选择照片的路径
     * @param context
     */
    public static String getSmallPhoto(int requestCode, File file, Intent data,
                                       Context context) {

        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:

                String pathImage1 = file.getAbsolutePath();
                BitmapFactory.Options options1 = new BitmapFactory.Options();

                options1.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false

                // Bitmap bitmap = BitmapFactory.decodeFile(pathImage);
                // image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
                // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                // 获取压缩图片

                Bitmap bitmap1 = ImageUitl.getimage(pathImage1);
                if (bitmap1 == null) {
                    return null;
                }
                // 保存指定位置
                File file1 = new File(getPhotopath());
                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                // 第二个参数影响的是图片的质量，但是图片的宽度与高度是不会受影响滴
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
                // 把数据转为为字节数组
                byte[] byteArray1 = baos1.toByteArray();
                try {
                    FileOutputStream fos1 = new FileOutputStream(file1);
                    BufferedOutputStream bos1 = new BufferedOutputStream(fos1);
                    bos1.write(byteArray1);
                    bos1.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {

                        baos1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                options1.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
                // 释放
                bitmap1.recycle();

                pathImage1 = file1.getAbsolutePath();
                return pathImage1;

            case PHOTO_REQUEST_GALLERY:

                if (null == data)
                    return null;
                Uri uri = data.getData();
                String pathImage = null;

                if (!TextUtils.isEmpty(uri.getAuthority())) {
                    // 查询选择图片
                    Cursor cursor = context.getContentResolver().query(uri,
                            new String[]{MediaStore.Images.Media.DATA}, null,
                            null, null);
                    // 返回 没找到选择图片
                    if (null == cursor) {
                        return null;
                    }
                    // 光标移动至开头 获取图片路径
                    cursor.moveToFirst();

                    pathImage = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false

                    // Bitmap bitmap = BitmapFactory.decodeFile(pathImage);

                    // 获取压缩图片
                    Bitmap bitmap = ImageUitl.getimage(pathImage);

                    // 保存到原位置

                    File file2 = new File(getPhotopath());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    // 第二个参数影响的是图片的质量，但是图片的宽度与高度是不会受影响滴
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    // 把数据转为为字节数组
                    byte[] byteArray = baos.toByteArray();
                    try {
                        FileOutputStream fos = new FileOutputStream(file2);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        bos.write(byteArray);
                        bos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {

                            baos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    options.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
                    // 释放
                    bitmap.recycle();
                    pathImage = file2.getAbsolutePath();

                }
                return pathImage;
        }
        return null;

    }

    /**
     * 拍照图片压缩
     *
     * @param context
     * @param quality 图片质量
     */
    public static String getSmallPhoto1(Context context, int quality) {

        String pathImage1 = myfile.getAbsolutePath();
        BitmapFactory.Options options1 = new BitmapFactory.Options();

        options1.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false

        // Bitmap bitmap = BitmapFactory.decodeFile(pathImage);
        // image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        // 获取压缩图片

        Bitmap bitmap1 = ImageUitl.getimage(pathImage1);
        if (bitmap1 == null) {
            return null;
        }
        // 保存指定位置
        File file1 = new File(getPhotopath());
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        // 第二个参数影响的是图片的质量，但是图片的宽度与高度是不会受影响滴
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, baos1);
        // 把数据转为为字节数组
        byte[] byteArray1 = baos1.toByteArray();
        try {
            FileOutputStream fos1 = new FileOutputStream(file1);
            BufferedOutputStream bos1 = new BufferedOutputStream(fos1);
            bos1.write(byteArray1);
            bos1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                baos1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        options1.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
        // 释放
        bitmap1.recycle();

        pathImage1 = file1.getAbsolutePath();
        return pathImage1;

    }

    /**
     * 压缩选择图片
     *
     * @param data    选择照片的路径
     * @param context
     * @param quality 图片质量
     */
    public static String getSmallPhoto2(Intent data, Context context,
                                        int quality) {

        if (null == data)
            return null;
        Uri uri = data.getData();
        String pathImage = null;

        if (!TextUtils.isEmpty(uri.getAuthority())) {
            // 查询选择图片
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.Media.DATA}, null, null,
                    null);
            // 返回 没找到选择图片
            if (null == cursor) {
                return null;
            }
            // 光标移动至开头 获取图片路径
            cursor.moveToFirst();

            pathImage = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false

            // Bitmap bitmap = BitmapFactory.decodeFile(pathImage);

            // 获取压缩图片
            Bitmap bitmap = ImageUitl.getimage(pathImage);

            // 保存到原位置

            File file2 = new File(getPhotopath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 第二个参数影响的是图片的质量，但是图片的宽度与高度是不会受影响滴
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            // 把数据转为为字节数组
            byte[] byteArray = baos.toByteArray();
            try {
                FileOutputStream fos = new FileOutputStream(file2);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(byteArray);
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {

                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            options.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
            // 释放
            bitmap.recycle();
            pathImage = file2.getAbsolutePath();

        }
        return pathImage;

    }

    /**
     * 获取存储照片的位置
     */
    public static String getPhotopath() {

        // 照片全路径
        String fileName = "";
        // 文件夹路径
        new DateFormat();
        String name = "small"
                + DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA)) + ".jpg";
        String pathUrl = Environment.getExternalStorageDirectory()
                + "/DCIM/Camera/";
        File file = new File(pathUrl);
        file.mkdirs();// 创建文件夹
        fileName = pathUrl + name;
        return fileName;
    }

}
