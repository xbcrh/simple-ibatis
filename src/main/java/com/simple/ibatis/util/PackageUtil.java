package com.simple.ibatis.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @Author xiabing
 * @Desc 解析package下的文件
 **/
public class PackageUtil {

    private static final String CLASS_SUFFIX = ".class";
    private static final String CLASS_FILE_PREFIX = "classes" + File.separator;
    private static final String PACKAGE_SEPARATOR = ".";

    /**
     * 查找包下的所有类的名字
     * @param packageName
     * @param showChildPackageFlag 是否需要显示子包内容
     * @return List集合，内容为类的全名
     */
    public static List<String> getClazzName(String packageName, boolean showChildPackageFlag){
        List<String> classNames = new ArrayList<>();
        String suffixPath = packageName.replaceAll("\\.", "/");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try{
            Enumeration<URL> urls = loader.getResources(suffixPath);
            while(urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if(url != null){
                    if ("file".equals(url.getProtocol())) {
                        String path = url.getPath();
                        classNames.addAll(getAllClassName(new File(path),showChildPackageFlag));
                    }
                }
            }
        }catch (IOException e){
            throw new RuntimeException("load resource is error , resource is "+packageName);
        }
        return classNames;
    }

    private static List<String> getAllClassName(File file,boolean flag){

        List<String> classNames = new ArrayList<>();

        if(!file.exists()){
            return classNames;
        }
        if(file.isFile()){
            String path = file.getPath();
            if(path.endsWith(CLASS_SUFFIX)){
                path = path.replace(CLASS_SUFFIX,"");
                String clazzName = path.substring(path.indexOf(CLASS_FILE_PREFIX) + CLASS_FILE_PREFIX.length()).replace(File.separator, PACKAGE_SEPARATOR);
                classNames.add(clazzName);
            }
        }else {
            File[] listFiles = file.listFiles();
            if(listFiles != null && listFiles.length > 0){
                for (File f : listFiles){
                    if(flag) {
                        classNames.addAll(getAllClassName(f, flag));
                    }else {
                        if(f.isFile()){
                            String path = f.getPath();
                            if(path.endsWith(CLASS_SUFFIX)) {
                                path = path.replace(CLASS_SUFFIX, "");
                                String clazzName = path.substring(path.indexOf(CLASS_FILE_PREFIX) + CLASS_FILE_PREFIX.length()).replace(File.separator,PACKAGE_SEPARATOR);
                                classNames.add(clazzName);
                            }
                        }
                    }
                }
            }
        }
        return classNames;
    }
}