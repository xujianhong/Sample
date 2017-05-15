package com.example.administrator.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    private static final int REQUIRED_BITMAP_SIZE = 100;

    static final int UPLOAD_BITMAP_SIZE = 720;
    static final int UPLOAD_BITAMP_SIZE_SMALL=80;

    /**
     * 上传照片普通
     * @param f
     * @return
     */
    public static Bitmap uploadDecodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = UPLOAD_BITMAP_SIZE;
            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp <= REQUIRED_SIZE
                        || height_tmp <=REQUIRED_SIZE * height_tmp / width_tmp)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale += Math.round((float) width_tmp / (float) REQUIRED_SIZE);
            }
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /**
     * 上传照片普通
     * @param f
     * @return
     */
    public static Bitmap uploadSmallDecodeFile(File f) {
        Bitmap result = null;
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = UPLOAD_BITAMP_SIZE_SMALL;
            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp <= REQUIRED_SIZE
                        || height_tmp <=REQUIRED_SIZE * height_tmp / width_tmp)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale += Math.round((float) width_tmp / (float) REQUIRED_SIZE);
            }
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            //从图中截取正中间的正方形部分。
//            int xTopLeft = (bitmap.getWidth() - REQUIRED_SIZE) / 2;
            int yTopLeft = (bitmap.getHeight() - REQUIRED_SIZE) / 2;

            try{
                result = Bitmap.createBitmap(bitmap, 0, yTopLeft, REQUIRED_SIZE, REQUIRED_SIZE);
                bitmap.recycle();
            }
            catch(Exception e){
                return null;
            }
        } catch (FileNotFoundException e) {
        }
        return result;
    }
    /**
     * Base64转bitmap
     *
     * @param icon
     * @return
     */
    public static Bitmap byteToDrawable(String icon) {
        byte[] img = Base64.decode(icon.getBytes(), Base64.DEFAULT);
        Bitmap bitmap;
        if (img != null) {
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            return bitmap;
        }
        return null;

    }

    /**
     * bitmap 转Base4
     *
     * @param bitmap
     * @return
     */
    public static String drawableToByte(Bitmap bitmap) {

        if (bitmap != null) {
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;

            // 创建一个字节数组输出流,流的大小为size    
            ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中    
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            // 将字节数组输出流转化为字节数组byte[]    
            byte[] imagedata = baos.toByteArray();

            String icon = Base64.encodeToString(imagedata, Base64.DEFAULT);
            return icon;
        }
        return null;
    }

    /**
     * 文件转bitmap
     *
     * @param f
     * @return
     */
    public static Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = REQUIRED_BITMAP_SIZE;
            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE
                        || height_tmp < REQUIRED_SIZE * height_tmp / width_tmp)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale += Math.round((float) width_tmp / (float) REQUIRED_SIZE);
            }
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /**
     * bitmap转file
     *
     * @param path
     * @param bitmap
     * @return
     */
    public static File saveBitmap(String path, Bitmap bitmap) {
        File f = new File(path, MStringUtils.getRandomName("jpg"));
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return f;
    }


    public static Bitmap getSmallBitmap(String filePath) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 100, 100 * options.outHeight / options.outWidth);


        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;


        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        if (bm == null) {
            return null;
        }
        int degree = readPictureDegree(filePath);
        bm = rotateBitmap(bm, degree);
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;

    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
//            final int heightRatio = Math.round((float) height
//                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
//            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
            inSampleSize = widthRatio;
        }

        return inSampleSize;
    }

    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * encodeBase64File:(将文件转成base64 字符串). <br/>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static String encodeBase64File(String path) {
        try {
            File file = new File(path);
            FileInputStream inputFile = null;

            inputFile = new FileInputStream(file);

            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param rfile 文件
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(File rfile) {
        try {
            File file = rfile;
            FileInputStream inputFile = null;

            inputFile = new FileInputStream(file);

            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * decoderBase64File:(将base64字符解码保存文件). <br/>
     *
     * @param base64Code 编码后的字串
     * @param savePath   文件保存路径
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static void decoderBase64File(String base64Code, String savePath) {
//byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        try {
            byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
            FileOutputStream out = null;

            out = new FileOutputStream(savePath);

            out.write(buffer);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
