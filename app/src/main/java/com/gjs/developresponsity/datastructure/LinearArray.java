package com.gjs.developresponsity.datastructure;

import android.util.Log;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/03/22
 *     desc    : 线性表顺序存储结构(例：数组)：一段地址连续的存储单元依次存储线性表的数据元素。
 *     version : 1.0
 * </pre>
 */

public class LinearArray {

    public static final int MAXSIZE = 20;
    public static final int ERROR = 0;
    public static final int SUCCESS = 1;
    int[] data = new int[MAXSIZE];//存储空间初始分配量，数组长度不变，线性表长度随数据的增加删除发生改变。
    private static LinearArray mLinearArray;
    private static final String TAG = "LinearArray";

    public static LinearArray getInstance(){
        if(mLinearArray == null){
            synchronized (LinearArray.class){
                if(mLinearArray == null){
                    mLinearArray = new LinearArray();
                }
            }
        }
        return mLinearArray;
    }


    /**
     * 添加数据
     * 初始条件：线性顺序结构列表已存在，1 =< position =< elementList.length;
     * 操作结果：线性顺序结构列表第i个位置前添加数据element，L的长度加1
     *
     * @param elementList 进行操作的列表
     * @param position    操作的列表位置
     * @param element     添加的数据元素
     */
    public int listInsert(int[] elementList, int position, int element) {
        //顺序线性表已经满了
        if (elementList == null || elementList.length == MAXSIZE) {
            return ERROR;
        }
        // position 不在范围内
        if (position < 1 || position > elementList.length) {
            return ERROR;
        }
        //插入数据的位置不在表尾
        if (elementList.length >= position) {
            for (int i = elementList.length - 1; i >= position - 1; i--) {
                elementList[i] = elementList[i - 1];
            }
        }
        elementList[position - 1] = element;
        return SUCCESS;
    }

    public int listDelete(int[] elementList, int position){
        if(elementList.length == 0){
            return ERROR;
        }
        if (position < 1 || position > elementList.length){
            return ERROR;
        }
        if(position <= elementList.length){
            for (int j = position - 1 ; j < elementList.length - 1; j++){
                Log.i(TAG, "elementList[j] : " + elementList[j]);
                elementList[j] = elementList[j + 1];
            }
        }
        return SUCCESS;
    }

}
