package com.dw.hikvision.definition;

import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 额外字段转化类
 */
@Slf4j
public class DeviceStatusExtraConverter {
    /**
     * 将状态类转换为标准的额外字段描述
     *
     * @param vmsStatusDescriber /
     * @param <T>/
     * @return /
     */
    public static <T extends DeviceStatusExtraDescriber> List<DeviceStatusExtra> convert(T vmsStatusDescriber) {
        List<DeviceStatusExtra> deviceStatusExtras = new ArrayList<>();
        Class<?> clazz = vmsStatusDescriber.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ApiModelProperty.class)) {
                try {
                    field.setAccessible(true);
                    String fieldValue = (String) field.get(vmsStatusDescriber);

                    ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                    DeviceStatusExtra deviceStatusExtra = new DeviceStatusExtra();
                    deviceStatusExtra.setItem(field.getName());
                    deviceStatusExtra.setName(annotation.name());
                    deviceStatusExtra.setDescription(annotation.value());
                    deviceStatusExtra.setValue(fieldValue);
                    deviceStatusExtra.setValueDesc(vmsStatusDescriber.describeValue(fieldValue));

                    deviceStatusExtras.add(deviceStatusExtra);
                } catch (Exception e) {
                    log.error("情报板状态属性转换时异常！");
                    log.error(e.getMessage(), e);
                }
            }
        }
        return deviceStatusExtras;
    }
}
