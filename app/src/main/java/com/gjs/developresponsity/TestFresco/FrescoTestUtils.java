package com.gjs.developresponsity.TestFresco;

import com.gjs.developresponsity.TestFresco.controller.LocalImageController;
import com.gjs.developresponsity.TestFresco.controller.WebImageController;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/07/11
 *     desc    : 用于加载图片的统一入口，包括本队图片，网络图片，所有图片加载共用这一个入口
 *     version : 1.0
 * </pre>
 */
public class FrescoTestUtils {

    private static FrescoTestUtils mInstance;

    public static FrescoTestUtils getFrescoTest() {
        if(mInstance == null){
            mInstance = new FrescoTestUtils();
        }
        return mInstance;
    }

    public WebImageController.WebImageControllerBuilder createWebImageControllerBuilder(String url){
        WebImageController.WebImageControllerBuilder builder = new WebImageController.WebImageControllerBuilder(url);
        return builder;
    }

    public LocalImageController.LocalImageControllerBuilder createLocalImageControllerBuilder(String url){
        LocalImageController.LocalImageControllerBuilder builder = new LocalImageController.LocalImageControllerBuilder(url);
        return builder;
    }

}
