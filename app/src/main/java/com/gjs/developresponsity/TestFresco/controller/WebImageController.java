package com.gjs.developresponsity.TestFresco.controller;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.gjs.developresponsity.App;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/07/11
 *     desc    : 网络图片下载管理器
 *     version : 1.0
 * </pre>
 */
public class WebImageController {

    /**
     * 私有构造方法，确保只有builder可以穿件WebImageController
     */
    private WebImageController(){
    }

    //图片的网络地址
    private String url;
    //图片的占位图
    private Drawable placeDrawble;
    //图片的加载失败图
    private Drawable failDrawble;
    //图片是否已gif形式展示
    private boolean playAnimation;
    //图片加载成功回调
    private ImageDownloadSuccess mImageDownloadSuccess;
    //图片加载失败回调
    private ImageDownloadFail mImageDownloadFail;
    //图片下载进度回调
    private ImageDownloadProgress mImageDownloadProgress;

    /**
     * 图片下载成功接口
     */
    public interface ImageDownloadSuccess{
        /**
         * 图片下载成功回调接口
         */
        void onDownloadSuccess();
    }

    /**
     * 图片下载失败接口
     */
    public interface ImageDownloadFail{
        /**
         * 图片下载失败回调接口
         */
        void onDownloadFail();
    }

    /**
     * 图片下载进度接口
     */
    public interface ImageDownloadProgress{
        /**
         * 图片下载进度的回调接口
         * @param progress 进度范围0-10000
         */
        void progress(int progress);
    }

    /**
     * 网络图片控制器
     */
    public static class WebImageControllerBuilder{

        private WebImageController webImageParam;

        public WebImageControllerBuilder(String url){
            webImageParam = new WebImageController();
            webImageParam.url = url;
        }

        public WebImageControllerBuilder setUrl(String url){
            webImageParam.url = url;
            return this;
        }

        /**
         * 设置占位图
         * @param placeDrawable 占位图图片
         * @return
         */
        public WebImageControllerBuilder setPlaceDrawable(Drawable placeDrawable){
            webImageParam.placeDrawble = placeDrawable;
            return this;
        }

        /**
         * 设置占位图
         * @param placeId 占位图资源id
         * @return
         */
        public WebImageControllerBuilder setPlaceDrawable(int placeId){
            webImageParam.placeDrawble = App.CONTEXT.getResources().getDrawable(placeId);
            return this;
        }

        /**
         * 设置占位图
         * @param placePath 占位图路径
         * @return
         */
        public WebImageControllerBuilder setPlaceDrawable(String placePath){
            webImageParam.placeDrawble = Drawable.createFromPath(placePath);
            return this;
        }

        /**
         * 设置失败图片
         * @param failPath 失败展示图片路径
         * @return
         */
        public WebImageControllerBuilder setFailDrawable(String failPath){
            webImageParam.failDrawble = Drawable.createFromPath(failPath);
            return this;
        }

        /**
         * 设置失败图片
         * @param faildDrawable 失败图图片
         * @return
         */
        public WebImageControllerBuilder setFailDrawable(Drawable faildDrawable){
            webImageParam.failDrawble = faildDrawable;
            return this;
        }

        /**
         * 设置失败图片
         * @param faildDid 失败图片资源id
         * @return
         */
        public WebImageControllerBuilder setFailDrawable(int faildDid){
            webImageParam.failDrawble = App.CONTEXT.getResources().getDrawable(faildDid);
            return this;
        }

        /**
         * 是否还支持Gif
         * @param playAnimation
         * @return
         */
        public WebImageControllerBuilder setPlayAnimation(boolean playAnimation){
            webImageParam.playAnimation = playAnimation;
            return this;
        }

        /**
         * 创建网络图片控制器
         * @return 网络图片控制器
         */
        public WebImageController build() {
            return webImageParam;
        }

        /**
         * 展示图片
         * @param draweeView 展示图片的控件
         */
        public void showWebImage(DraweeView<GenericDraweeHierarchy> draweeView){
            webImageParam.showWebImage(draweeView);
        }
    }

    /**
     * 展示网络图片
     * @param draweeView 展示控件
     */
    public void showWebImage(DraweeView<GenericDraweeHierarchy> draweeView){
        if(url == null){
            return;
        }
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(this.playAnimation)
                .setOldController(draweeView.getController())
                .setUri(url)
                .setControllerListener(new BaseControllerListener<ImageInfo>(){
                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        if(mImageDownloadFail != null){
                            mImageDownloadFail.onDownloadFail();
                        }
                    }

                    @Override
                    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if(mImageDownloadSuccess != null){
                            mImageDownloadSuccess.onDownloadSuccess();
                        }
                    }
                }).setAutoPlayAnimations(true).build();

        GenericDraweeHierarchy genericDraweeHierarchy = draweeView.getHierarchy();
        if(placeDrawble != null) {
            genericDraweeHierarchy.setPlaceholderImage(placeDrawble);
        }
        if(failDrawble != null){
            genericDraweeHierarchy.setFailureImage(failDrawble);
        }
        genericDraweeHierarchy.setProgressBarImage(new ProgressBarDrawable(){
            @Override
            protected boolean onLevelChange(int level) {
                if(mImageDownloadProgress!=null){
                    mImageDownloadProgress.progress(level);
                }
                return super.onLevelChange(level);
            }
        });
        controller.setHierarchy(genericDraweeHierarchy);
        draweeView.setController(controller);
    }

}
