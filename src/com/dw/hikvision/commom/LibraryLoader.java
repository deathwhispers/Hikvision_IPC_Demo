package com.dw.hikvision.commom;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/17 14:25
 */
@Slf4j
public class LibraryLoader {

    /**
     * 根据操作系统和库名加载本地库文件
     *
     * @param libName 库文件名（不带扩展名，如 "HCNetSDK"）
     * @return 加载的库文件的绝对路径
     */
    public static String load(String libName) {
        // 根据操作系统构造资源路径
        String resourcePath = Objects.requireNonNull(LibraryLoader.class.getClassLoader().getResource("")).toString();
        if (!resourcePath.endsWith("/")) {
            resourcePath += "/";
        }
        String fileExtension;
        String fileNamePrefix = ""; // Linux下通常需要lib前缀

        if (OsSelect.isWindows()) {
            resourcePath += "libs/hikvision/win/";
            fileExtension = ".dll";
        } else if (OsSelect.isLinux()) {
            resourcePath += "libs/hikvision/linux/";
            fileNamePrefix = "lib"; // Linux库文件通常以lib开头
            fileExtension = ".so";
        } else {
            throw new UnsupportedOperationException("Unsupported operating system");
        }

        // 构造完整的资源路径（包含文件名）
        return resourcePath + fileNamePrefix + libName + fileExtension;
    }

    // 获取加载SDK库
    public static String load() {
        return load("HCNetSDK");
    }
}
