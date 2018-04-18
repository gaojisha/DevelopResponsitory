package com.gjs.developresponsity.utils;

import android.util.Log;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/04/08
 *     desc    : 冒泡排序
 *     version : 1.0
 * </pre>
 */

public class SortUtils {

    private static SortUtils mBubbleSort = new SortUtils();

    public static SortUtils getInstance() {
        return mBubbleSort;
    }

    /**
     * 简单排序
     *
     * @param data 需要排序的数据
     * @return 排序完成的数据
     */
    public int[] simpleSort(int[] data) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (data[i] < data[j]) {
                    swap(data, i, j);
                }
            }
        }
        return data;
    }

    /**
     * 冒泡排序
     *
     * @param data 需要排序的数据
     * @return 排序完成的数据
     */
    public int[] bubbleSort(int[] data) {
        for (int i = 0; i < data.length; i++) {
            for (int j = data.length - 1; j > i; j--) {
                if (data[j - 1] < data[j]) {
                    swap(data, j, j - 1);
                }
            }
        }
        return data;
    }

    /**
     * 优化冒泡算法，如果没有交换说明顺序正确不需要继续遍历，否则继续排序
     *
     * @param data 需要进行排序的数据
     * @return 排序完成的数据
     */
    public int[] optimizeBubbleSort(int[] data) {
        boolean flag = true;
        for (int i = 0; i < data.length && flag; i++) {
            flag = false;
            for (int j = data.length - 1; j > i; j--) {
                if (data[j - 1] < data[j]) {
                    swap(data, j, j - 1);
                    flag = true;
                }
            }
        }
        return data;
    }

    /**
     * 简单选择排序
     *
     * @param data 需要排序的数据
     * @return 排序完成的数据
     */
    public int[] simpleSelectedSort(int[] data) {
        for (int i = 0; i < data.length; i++) {
            int min = i;
            for (int j = data.length - 1; j > i; j--) {
                if (data[j] < data[min]) {
                    min = j;
                }
            }
            if (min != i) {
                swap(data, min, i);
            }
        }
        return data;
    }

    private void swap(int[] data, int i, int j) {
        int temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    /**
     * 直接插入排序
     *
     * @param data 需要排序的数据
     * @return 排序完成的数据
     */
    public int[] straightInsertionSort(int[] data) {
        int j, i, temp;
        for (i = 1; i < data.length; i++) {
            if (data[i] < data[i - 1]) {
                temp = data[i];
                for (j = i - 1; j >= 0 && data[j] >= temp; j--) {
                    data[j + 1] = data[j];
                }
                data[j + 1] = temp;
            }

        }
        return data;
    }

    /**
     * 希尔排序算法
     *
     * @param data 需要进行排序的数据
     * @return 排序完成的数据
     */
    public int[] shellSort(int[] data) {
        int i, j;
        int temp;
        int increment = data.length;
        do {
            increment = increment / 3 + 1;
            for (i = increment; i < data.length; i++) {
                if (data[i] < data[i - increment]) {
                    temp = data[i];
                    for (j = i - increment; j >= 0 && data[j] > temp; j -= increment) {
                        data[j + increment] = data[j];
                    }
                    data[j + increment] = temp;
//                    Log.i("developutil", "list data : " + data[0] +
//                            " " + data[1] +
//                            " " + data[2] +
//                            " " + data[3] +
//                            " " + data[4] +
//                            " " + data[5] +
//                            " " + data[6] +
//                            " " + data[7] +
//                            " " + data[8] +
//                            " " + data[9] );
                }
            }
        } while (increment > 1);
        return data;
    }

    /**
     * 堆排序
     *
     * @param data 排序的数据
     * @return 排序完成的数据
     */
    public int[] heapSort(int[] data) {
        int i;
        for (i = data.length / 2; i >= 0; i--) {
            heapAjust(data, i, data.length);
        }
//        Log.i("developutil", "list data : " + data[0] +
//                " " + data[1] +
//                " " + data[2] +
//                " " + data[3] +
//                " " + data[4] +
//                " " + data[5] +
//                " " + data[6] +
//                " " + data[7] +
//                " " + data[8] +
//                " " + data[9] );
        for (i = data.length - 1; i >= 0; i--) {
            swap(data, 0, i);
            heapAjust(data, 0, i);
        }
        return data;
    }

    /**
     * 把data构建成一个大顶堆
     *
     * @param data   需要构建的数据
     * @param i
     * @param length 数据的长度
     */
    private void heapAjust(int[] data, int i, int length) {
        int temp, j;
        temp = data[i];
        for (j = 2 * i; j < length; j *= 2) {
            if (j < length - 1 && data[j] < data[j + 1]) {
                ++j;
            }
            if (temp >= data[j]) {
                break;
            }
            data[i] = data[j];
            i = j;
        }
        data[i] = temp;
    }

    /**
     * 归并排序
     *
     * @param data 需要排序的数据
     * @return 排序完成的数据
     */
    public int[] mergingSort(int[] data) {
        mSort(data, data, 1, data.length - 1);
        return data;
    }

    private int[] mSort(int[] sdata, int[] tData, int s, int t) {
        int m;
        int[] data = {};
        int[] tData2 = new int[t + 1];
        if (s == t) {
            tData[s] = sdata[s];
        } else {
            m = (s + t) / 2;
            mSort(sdata, tData2, s, m);
            mSort(sdata, tData2, m + 1, t);
            data = merge(tData, tData2, s, m, t);
        }
        return data;
    }

    /**
     * 希尔排序
     * @param sData
     * @param tData
     * @param i
     * @param m
     * @param n
     * @return
     */
    private int[] merge(int[] sData, int[] tData, int i, int m, int n) {
        //TODO 希尔排序
        int j, k, l;
        for (j = m, k = i; i < m && j < n; k++) {
            if (sData[i] < sData[j]) {
                tData[k] = sData[i++];
            } else {
                tData[k] = sData[j++];
            }
        }
        if (i < m) {
            for (l = 0; l <= m - i; l++) {
                tData[k + l] = sData[i + l];
            }
        }
        if (j < n) {
            for (l = 0; l <= n - j; l++) {
                tData[k + 1] = sData[j + 1];
            }
        }
        return tData;
    }

    /**
     * 快速排序
     *
     * @param data 需要排序的数据
     * @return 排序完成的数据
     */
    public int[] quickSort(int[] data) {
        //TODO 快速排序
        return data;
    }

}
