package com.example.lujun858.myplugindemo;

import android.os.Build;

import com.example.mypluginlibrary.RefInvoke;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * 由于应用程序使用的ClassLoader为PathClassLoader
 * 最终继承自 BaseDexClassLoader
 * 查看源码得知,这个BaseDexClassLoader加载代码根据一个叫做
 * dexElements的数组进行, 因此我们把包含代码的dex文件插入这个数组
 * 系统的classLoader就能帮助我们找到这个类
 * <p>
 * 这个类用来进行对于BaseDexClassLoader的Hook
 */
public final class BaseDexClassLoaderHookHelper {

    public static void patchClassLoader(ClassLoader cl, File apkFile, File optDexFile) throws  IOException {

        // 获取 BaseDexClassLoader : pathList
        Object pathListObj = RefInvoke.getFieldObject(DexClassLoader.class.getSuperclass(), cl, "pathList");

        // 获取 PathList: Element[] dexElements
        Object[] dexElements = (Object[]) RefInvoke.getFieldObject(pathListObj, "dexElements");

        // Element 类型
        Class<?> elementClass = dexElements.getClass().getComponentType();

        // 创建一个数组, 用来替换原始的数组
        Object[] newElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);

        // 构造插件Element(File file, boolean isDirectory, File zip, DexFile dexFile) 这个构造函数
        Class[] p1 = {File.class, boolean.class, File.class, DexFile.class};
        Object[] v1 = {apkFile, false, apkFile, DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(), 0)};
        Object o = RefInvoke.createObject(elementClass, p1, v1);

        Object[] toAddElementArray = new Object[]{o};
        // 把原始的elements复制进去
        System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
        // 插件的那个element复制进去
        System.arraycopy(toAddElementArray, 0, newElements, dexElements.length, toAddElementArray.length);

        // 替换
        RefInvoke.setFieldObject(pathListObj, "dexElements", newElements);
    }


    public static Object getPathList(Object classLoader) {
        Class cls = null;
        String pathListName = "pathList";
        try {
            cls = Class.forName("dalvik.system.BaseDexClassLoader");
            Field declaredField = cls.getDeclaredField(pathListName);
            declaredField.setAccessible(true);
            return declaredField.get(classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 合并宿主和插件的 so 路径集，需要替换 BaseDexClassLoader.DexPathList 中的 nativeLibraryPathElements 和 nativeLibraryDirectories
     * 参考 DexPathList 源码 http://androidxref.com/6.0.1_r10/xref/libcore/dalvik/src/main/java/dalvik/system/DexPathList.java
     */
    public static synchronized void insertNativeLibrary(DexClassLoader dexClassLoader, ClassLoader baseClassLoader, File nativeLibsDir) throws Exception {
        //获取宿主的PathList
        Object basePathList = getPathList(baseClassLoader);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            Reflector reflector = Reflector.with(basePathList);

            //往宿主的库集合中加入插件的 so 路径
            List<File> nativeLibraryDirectories = reflector.field("nativeLibraryDirectories").get();
            nativeLibraryDirectories.add(nativeLibsDir);
            //获取到宿主的so集合
            Object baseNativeLibraryPathElements = reflector.field("nativeLibraryPathElements").get();
            final int baseArrayLength = Array.getLength(baseNativeLibraryPathElements);

            Object newPathList = getPathList(dexClassLoader);
            //获取到插件的so集合
            Object newNativeLibraryPathElements = reflector.get(newPathList);
            Class<?> elementClass = newNativeLibraryPathElements.getClass().getComponentType();
            Object allNativeLibraryPathElements = Array.newInstance(elementClass, baseArrayLength + 1);
            //将原来宿主的so集合拷贝到新集合中
            System.arraycopy(baseNativeLibraryPathElements, 0, allNativeLibraryPathElements, 0, baseArrayLength);

            Field soPathField;
            if (Build.VERSION.SDK_INT >= 26) {
                soPathField = elementClass.getDeclaredField("path");
            } else {
                soPathField = elementClass.getDeclaredField("dir");
            }
            soPathField.setAccessible(true);
            //将插件的so集合拷贝到新集合中
            final int newArrayLength = Array.getLength(newNativeLibraryPathElements);
            for (int i = 0; i < newArrayLength; i++) {
                Object element = Array.get(newNativeLibraryPathElements, i);
                String dir = ((File)soPathField.get(element)).getAbsolutePath();
                if (dir.contains("com.example.lujun858")) {
                    Array.set(allNativeLibraryPathElements, baseArrayLength, element);
                    break;
                }
            }
            //将宿主和插件so的合集替换上去
            reflector.set(allNativeLibraryPathElements);
        } else {
            Reflector reflector = Reflector.with(basePathList).field("nativeLibraryDirectories");
            File[] nativeLibraryDirectories = reflector.get();
            final int N = nativeLibraryDirectories.length;
            File[] newNativeLibraryDirectories = new File[N + 1];
            System.arraycopy(nativeLibraryDirectories, 0, newNativeLibraryDirectories, 0, N);
            newNativeLibraryDirectories[N] = nativeLibsDir;
            reflector.set(newNativeLibraryDirectories);
        }
    }

}
