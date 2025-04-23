package com.dw.hikvision.sdk.structure;

import com.sun.jna.Structure;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:27
 */
public class SdkStructure extends Structure {
    public SdkStructure() {
    }

    @Override
    protected List<String> getFieldOrder() {
        List<String> fieldOrderList = new ArrayList<>();
        List<Class<?>> hierarchy = new ArrayList<>();

        // 收集从当前类到 HCNetSDK.SdkStructure 的所有类（包括 HCNetSDK.SdkStructure）
        Class<?> currentCls = this.getClass();
        while (currentCls != SdkStructure.class && currentCls != Object.class) {
            hierarchy.add(currentCls);
            currentCls = currentCls.getSuperclass();
        }
        if (currentCls == SdkStructure.class) {
            hierarchy.add(currentCls);
        }

        // 反转列表，确保父类字段在前，子类字段在后
        Collections.reverse(hierarchy);

        // 遍历所有类，收集字段
        for (Class<?> cls : hierarchy) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
                    fieldOrderList.add(field.getName());
                }
            }
        }
        return fieldOrderList;
    }

    public int fieldOffset(String name) {
        return super.fieldOffset(name);
    }
}