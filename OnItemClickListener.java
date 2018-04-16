package com.example.an.viewgroup;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 15900 on 2018/4/13.
 */

public interface OnItemClickListener<T> {
    void onItemClick(ViewGroup var1, View var2, T var3, int var4);

    boolean onItemLongClick(ViewGroup var1, View var2, T var3, int var4);
}