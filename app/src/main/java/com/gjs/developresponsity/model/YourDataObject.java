package com.gjs.developresponsity.model;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/04/12
 *     desc    : 必须说明该类的作用
 *     version : 1.0
 * </pre>
 */

public class YourDataObject {
    private String mText;

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getId(){
        return this.toString();
    }
}
