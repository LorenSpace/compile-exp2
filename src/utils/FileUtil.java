package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

// 读取文本文件内容
public class FileUtil {
    public static BufferedReader readFile(String fileName) {

        BufferedReader bufferedReader = null;
        try {
            File myFile = new File(fileName);
            // 通过字符串创建File类型对象，指向该字符串路径下的文件
            if (myFile.isFile() && myFile.exists()) { // 判断文件是否存在
                InputStreamReader Reader = new InputStreamReader(new FileInputStream(myFile), StandardCharsets.UTF_8);
                // 考虑到编码格式，new FileInputStream(myFile) 文件字节输入流，以字节为单位对文件中的数据进行读取
                // 将文件字节输入流转换为文件字符输入流并给定编码格式
                bufferedReader = new BufferedReader(Reader);
                // BufferedReader 从字符输入流中读取文本，缓冲各个字符，从而实现字符、数组和行的高效读取。
                // 通过 BufferedReader 包装实现高效读取
            } else {
                System.out.println("找不到指定的文件");
            }

        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return bufferedReader;
    }
}