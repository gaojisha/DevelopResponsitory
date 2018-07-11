package com.gjs.developresponsity.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.gjs.developresponsity.video.controller.SdcardImageController;

import java.io.File;

/**
 * User: zhonghang zhonghang@feinno.com
 * Date: 2017-04-25
 * Time: 10:09
 * 用于加载图片的统一入口，目前图片分为三种类型:本地图片，网络图片，云存储图片。
 * 所有的加载图片的方法使用这个共有的入口。
 */
public class FrescoImageUtils {
    //单例
    private static FrescoImageUtils ourInstance;

    public static FrescoImageUtils getInstance() {
        if (ourInstance == null) {
            ourInstance = new FrescoImageUtils();
        }
        return ourInstance;
    }

    private FrescoImageUtils() {

    }

    /**
     * 创建加载本地图片的Builder
     *
     * @param path 加载本地图片必须传递进来path
     * @return SdcardControllerBuilder
     */
    public SdcardImageController.SdcardControllerBuilder createSdcardBuilder(@NonNull String path) {
        SdcardImageController.SdcardControllerBuilder builder = new SdcardImageController.SdcardControllerBuilder(path);
        return builder;
    }
//
//    /**
//     * 创建加载网络图片的builder
//     *
//     * @param url 加载
//     * @return WebImageControllerBuilder
//     */
//    public WebImageController.WebImageControllerBuilder createWebImageParamsBuilder(String url) {
//        WebImageController.WebImageControllerBuilder builder = new WebImageController.WebImageControllerBuilder(url);
//        return builder;
//    }
//
//    /**
//     * 创建加载云存储图片的builder
//     *
//     * @param fileName 图片在云存储中的名称
//     * @param filePath 图片将要保存到sdcard的路径
//     * @return CloudImageControllerBuilder
//     */
//    public CloudImageController.CloudImageControllerBuilder createCloudImageParamsBuilder(@NonNull String fileName, @NonNull String filePath) {
//        CloudImageController.CloudImageControllerBuilder builder = new CloudImageController.CloudImageControllerBuilder(fileName, filePath);
//        return builder;
//    }
//
//    /**
//     * 创建加载头像图片的builder
//     *
//     * @param portraitId 图片在云存储中的名称
//     * @return CloudImageControllerBuilder
//     */
//    public PortraitImageController.PortTraitImageControllerBuilder createPortraitImageParamsBuilder(@NonNull long portraitId) {
//        PortraitImageController.PortTraitImageControllerBuilder builder = new PortraitImageController.PortTraitImageControllerBuilder(portraitId);
//        return builder;
//    }

    /**
     * 判断该文件是否是图片
     *
     * @param imagePath 文件路径
     * @return true 是图片 false 图片文件损坏
     */
    public static boolean isImage(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return false;
        }
        File file = new File(imagePath);
        if (!file.exists()) {
            return false;
        }
        //只读取图片的宽高，不将该文件读取到内存中，目的是下方设置图片的宽高时会使用，加载图片更改成了fresco
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        if (options.mCancel || options.outWidth == -1
                || options.outHeight == -1 || options.outWidth == 0 || options.outHeight == 0) {
            //表示图片已损毁
            return false;
        }
        return true;
    }

//    /**
//     * 带ProgresssBar的加载云存储图片
//     *
//     * @param fileName
//     * @param filePath
//     * @param progressBar
//     * @return
//     */
//    public CloudImageController.CloudImageControllerBuilder createCloudImageParamsBuilderWithProgressBar(@NonNull String fileName, @NonNull String filePath, final ProgressBar progressBar) {
//        CloudImageController.CloudImageControllerBuilder builder = new CloudImageController.CloudImageControllerBuilder(fileName, filePath);
//        builder.setOnImageDownloadSuccessListener(new CloudImageController.ImageDownloadSuccess() {
//            @Override
//            public void onDownloadSuccess() {
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//        builder.setOnImageDownloadFailListener(new CloudImageController.ImageDownloadFail() {
//            @Override
//            public void onDwonloadFail() {
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//        builder.setOnImageDownloadProgressListener(new CloudImageController.ImageDownloadProgress() {
//            @Override
//            public void progress(int progress) {
//                progressBar.setVisibility(View.VISIBLE);
//            }
//        });
//        return builder;
//    }


    /**
     * 提供给外部调用资源文件的展示图片的方法
     * 此方法运行在主线程
     *
     * @param mContext   展示图片的context
     * @param resId      展示图片的资源ID
     * @param draweeView 展示图片的控件
     */
    public void showCloudImage(Context mContext, int resId, final DraweeView<GenericDraweeHierarchy> draweeView) {
        Uri uri = Uri.parse("res://" + mContext.getPackageName() + "/" + resId);
        draweeView.setImageURI(uri);
    }

    public static void clearCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
    }


    public static void clearCache(String path) {

        Uri uri = Uri.parse("file://" + path);

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(uri);
        imagePipeline.evictFromDiskCache(uri);

        // combines above two lines
        imagePipeline.evictFromCache(uri);
    }

//    public static void clearCache(long peerId) {
////
////        String path1 = UserEnvCreator.ABS_USER_CARD_GROUP_Portrait + peerId + UserCard.THUMB_EXT;
////        String path2 = UserEnvCreator.ABS_USER_CARD_Portrait_Employee + peerId + UserCard.THUMB_EXT;
////        String path3 = new UserCard(peerId).getPortraitPath(peerId, true);
////
////        File file1 = new File(path1);
////        if (file1.exists()) {
////            boolean a = file1.delete();
////            clearCache(path1);
////        }
////        File file2 = new File(path2);
////        if (file2.exists()) {
////            boolean a2 = file2.delete();
////            clearCache(path2);
////        }
////        File file3 = new File(path3);
////        if (file3.exists()) {
////            boolean a3 = file3.delete();
////            clearCache(path3);
////        }
////
////    }
}
