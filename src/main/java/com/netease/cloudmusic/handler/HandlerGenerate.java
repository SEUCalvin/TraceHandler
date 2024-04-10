package com.netease.cloudmusic.handler;

import com.netease.cloudmusic.model.MethodBean;
import com.netease.cloudmusic.util.FileUtil;
import com.netease.cloudmusic.util.ParseUtil;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jice on 2024/03/29.
 * Desc: 根据给出的多个trace文件, 生成去重后的基线数据
 */
public class HandlerGenerate implements HandlerParse {

    @Override
    public void handle(String[] args) {

        if (args == null || args.length < 3) {
            System.out.println("命令行参数错误: 请输入正确的参数 {2|基线文件输出路径｜Trace1文件路径｜Trace2文件路径｜...}");
            return;
        }

        String outputPath = args[1];
        if (outputPath.isEmpty()) {
            System.out.println("命令行参数错误: 基线文件输出路径不存在");
            return;
        }

        try {
            Paths.get(outputPath);
        } catch (InvalidPathException e) {
            System.out.println("命令行参数错误: 基线文件输出路径不合法 - " + e.getMessage());
            return;
        }

        for (int i = 2; i < args.length; i++) {
            if (args[i].isEmpty() ||
                    !args[i].toLowerCase().endsWith(".trace") ||
                    !(new File(args[i]).exists())) {
                System.out.println("命令行参数错误: trace文件不存在或不是trace文件 ->");
                System.out.println(args[i]);
                return;
            }
        }

        ArrayList<MethodBean> resultList = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            ArrayList<MethodBean> list = ParseUtil.parasTrace(args[i]);
            System.out.println(args[i] + "提供了" + list.size() + "个函数");
            resultList.addAll(list);
        }

        try {
            //distinct
            ArrayList<MethodBean> distinctList = new ArrayList<>();
            Set<String> names = new HashSet<>();
            for (MethodBean model : resultList) {
                if (names.add(model.getFunctionNameWithThread())) {
                    distinctList.add(model);
                }
            }


            if (distinctList.isEmpty()) {
                System.out.println("trace文件中无任何有效函数");
            } else {
                FileUtil.writeFile(distinctList, outputPath);
                System.out.println("聚合以及去重后生成" + distinctList.size() + "个函数, 函数集合已输出到文件 ->");
                System.out.println(outputPath);
            }

        } catch (Exception e) {
            System.out.println("异常:" + e);
        }
    }
}