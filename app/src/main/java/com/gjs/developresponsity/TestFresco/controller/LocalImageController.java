package com.gjs.developresponsity.TestFresco.controller;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.gjs.developresponsity.App;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/07/11
 *     desc    : 本地图片管理
 *     version : 1.0
 * </pre>
 */
public class LocalImageController {

    private String path;
    //图片的占位图
    private Drawable placeDrawble;
    //图片的加载失败图
    private Drawable failDrawble;
    //图片是否已gif形式展示
    private boolean playAnimation;

    /**
     * 私有构造器确保构造器只能由builder构造
     */
    private LocalImageController(){
    }

    /**
     * 本地图片控制器的builder
     */
    public static class LocalImageControllerBuilder{
        private LocalImageController localImageParam;

        public LocalImageControllerBuilder(String path){
            localImageParam = new LocalImageController();
            localImageParam.path = path;
        }

        /**
         * 设置是否支持GIF
         * @param playAnimation 是否支持Gif
         * @return
         */
        public LocalImageControllerBuilder setPlayAnimation(boolean playAnimation){
            localImageParam.playAnimation = playAnimation;
            return this;
        }

        /**
         *设置占位图
         * @param drawable 占位图
         * @return
         */
        public LocalImageControllerBuilder setPlaceDrawable(Drawable drawable){
            localImageParam.placeDrawble = drawable;
            return this;
        }

        /**
         * 设置占位图
         * @param placeId 占位图id
         * @return
         */
        public LocalImageControllerBuilder setPlaceDrawable(int placeId){
            localImageParam.placeDrawble = App.CONTEXT.getResources().getDrawable(placeId);
            return this;
        }

        /**
         *设置占位图
         * @param path 占位图路径
         * @return
         */
        public LocalImageControllerBuilder setPlaceDrawable(String path){
            localImageParam.placeDrawble = Drawable.createFromPath(path);
            return this;
        }

        /**
         * 设置失败图片
         * @param drawable 图片
         * @return
         */
        public LocalImageControllerBuilder setFailDrawable(Drawable drawable){
            localImageParam.failDrawble = drawable;
            return this;
        }

        /**
         * 设置失败图片
         * @param failId 图片id
         * @return
         */
        public LocalImageControllerBuilder setFailDrawable(int failId){
            localImageParam.failDrawble = App.CONTEXT.getResources().getDrawable(failId);
            return this;
        }

        /**
         * 设置失败图片
         * @param failPath 图片路径
         * @return
         */
        public LocalImageControllerBuilder setFailDrawable(String failPath){
            localImageParam.failDrawble = Drawable.createFromPath(failPath);
            return this;
        }

        /**
         * 创建本地图片控制器
         * @return 图片控制器
         */
        public LocalImageController build(){
            return localImageParam;
        }

        /**
         * 使用控制器展示图片
         * @param draweeView 展示图片的控件
         */
        public void showLocalImage(DraweeView<GenericDraweeHierarchy> draweeView){
            localImageParam.showLocalImage(draweeView);
        }

    }

    /**
     * 展示图片
     * @param draweeView 展示图片的控件
     */
    public void showLocalImage(DraweeView<GenericDraweeHierarchy> draweeView){
        if(path == null){
            return;
        }
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        builder.setAutoPlayAnimations(this.playAnimation);

        //设置图片路径
        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + this.path));
        ImageRequest request = requestBuilder.setAutoRotateEnabled(true).build();
        builder.setImageRequest(request);

        //设置图片展示效果
        GenericDraweeHierarchy draweeHierarchy = draweeView.getHierarchy();
        //设置占位图
        if(placeDrawble != null) {
            draweeHierarchy.setPlaceholderImage(placeDrawble);
        }
        //设置失败图
        if(failDrawble != null) {
            draweeHierarchy.setFailureImage(failDrawble);
        }

        //设置是否播放动画
        builder.setAutoPlayAnimations(playAnimation);

        DraweeController controller = builder.build();
        controller.setHierarchy(draweeHierarchy);
        draweeView.setController(controller);
    }

}
