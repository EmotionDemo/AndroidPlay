package com.example.test.util;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Field;

public class ImgUtil {

    /**
     * 获取ImageView中的资源Id
     *
     * @param imageView
     * @return
     */
    public static int getImgResId(ImageView imageView) {
        int resId = 0;
        //获取ImageView中声明的字段，包括私有字段
        Field[] imgViewFields = imageView.getClass().getDeclaredFields();
        for (Field field : imgViewFields) {
            if (field.getName().equals("mBackgroundTintHelper")) {
                field.setAccessible(true);
                try {
                    Object obj = field.get(imageView);
                    Field[] fields = obj.getClass().getDeclaredFields();
                    for (Field f : fields) {
                        if (f.getName().equals("mBackgroundResId")) {
                            f.setAccessible(true);
                            resId = f.getInt(obj);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }


        return resId;
    }
}
