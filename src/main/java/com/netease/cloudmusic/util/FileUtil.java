package com.netease.cloudmusic.util;

import com.netease.cloudmusic.model.MethodBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;

import static com.netease.cloudmusic.Main.DECORATION;

public class FileUtil {

    public static void writeFile(ArrayList<MethodBean> list, String filePath) {

        StringBuilder builder = new StringBuilder();

        for (MethodBean methodBean : list) {
            builder.append(methodBean.getThreadId());
            builder.append(DECORATION);
            builder.append(methodBean.getThreadName());
            builder.append(DECORATION);
            builder.append(methodBean.getCostTime());
            builder.append(DECORATION);
            builder.append(methodBean.getFunctionName());
            builder.append("\n");
        }

        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(builder.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("异常:" + e);
        }
    }

    public static HashSet<String> readFile(String filePath) {
        ArrayList<MethodBean> result = new ArrayList<>();
        HashSet<String> names = new HashSet<>();
        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(DECORATION, -1);
                if (split.length != 4) {
                    continue;
                }
                MethodBean methodBean = new MethodBean();
                methodBean.setThreadId(split[0]);
                methodBean.setThreadName(split[1]);
                methodBean.setCostTime(split[2]);
                methodBean.setFunctionName(split[3]);
                result.add(methodBean);
                names.add(methodBean.getFunctionNameWithThread());
            }
        } catch (Exception e) {
            System.out.println("异常:" + e);
        }
        //return result;
        return names;
    }
}
