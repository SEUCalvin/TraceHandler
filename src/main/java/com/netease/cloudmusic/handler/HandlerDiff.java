package com.netease.cloudmusic.handler;

import com.netease.cloudmusic.Main;
import com.netease.cloudmusic.model.MethodBean;
import com.netease.cloudmusic.util.FileUtil;
import com.netease.cloudmusic.util.ParseUtil;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;


/**
 * Created by Jice on 2024/03/29.
 * Desc: 根据基线数据, 给出待检测trace文件是否存在新增函数
 */
public class HandlerDiff implements HandlerParse {

    @Override
    public void handle(String[] args) {

        //String tracePath = "/Users/calvin/Downloads/trace/yxtrace.trace";
        //String tracePath = "/Users/calvin/Downloads/trace/yxtrace_base.trace";
        //String baseLinePath = "/Users/calvin/Downloads/trace/Base.txt";
        //String outputPath = "/Users/calvin/Downloads/trace/diffTrace.txt";

        if (args.length < 4) {
            System.out.println("命令行参数错误: 请输入正确的参数 {1｜基线文件路径｜diff结果输出文件路径｜待检测的trace1文件路径｜待检测的trace2文件路径｜...}");
            return;
        }
        String baseLinePath = args[1];
        String outputPath = args[2];

        if (baseLinePath.isEmpty() ||
                !baseLinePath.toLowerCase().endsWith(".txt") ||
                !(new File(baseLinePath).exists())) {
            System.out.println("命令行参数错误: 基线文件不存在或不是txt文件");
            return;
        }

        if (outputPath.isEmpty()) {
            System.out.println("命令行参数错误: diff结果输出文件路径不存在");
            return;
        }

        try {
            Paths.get(outputPath);
        } catch (InvalidPathException e) {
            System.out.println("命令行参数错误: diff结果输出文件路径不合法 - " + e.getMessage());
            return;
        }

        for (int i = 3; i < args.length; i++) {
            if (args[i].isEmpty() ||
                    !args[i].toLowerCase().endsWith(".trace") ||
                    !(new File(args[i]).exists())) {
                System.out.println("命令行参数错误: trace文件不存在或不是trace文件 ->");
                System.out.println(args[i]);
                return;
            }
        }

        ArrayList<MethodBean> resultList = new ArrayList<>();
        for (int i = 3; i < args.length; i++) {
            ArrayList<MethodBean> list = ParseUtil.parasTrace(args[i]);
            resultList.addAll(list);
        }

        try {
            //sort
            Collections.sort(resultList);
            //distinct and filter
            ArrayList<MethodBean> distinctList = new ArrayList<>();
            Set<String> names = new HashSet<>();
            for (MethodBean model : resultList) {
                String functionNameWithThread = model.getFunctionNameWithThread();
                //filter
                if (filterMethod(functionNameWithThread)) {
                    continue;
                }
                if (!model.isMainThread()) {
                    continue;
                }
                //distinct
                if (names.add(functionNameWithThread)) {
                    distinctList.add(model);
                }
            }
            //parse data from baseline
            HashSet<String> baseFunName = FileUtil.readFile(baseLinePath);
            //diff
            ArrayList<MethodBean> diffList = new ArrayList<>();
            //diffList.add(MethodBean.getTitleDescModel());
            for (MethodBean model : distinctList) {
                if (!baseFunName.contains(model.getFunctionNameWithThread())) {
                    diffList.add(model);
                }
            }
            //cost time sort
            diffList.sort((o1, o2) -> {
                int costTime1 = 0;
                int costTime2 = 0;
                try {
                    costTime1 = Integer.parseInt(o1.getCostTime());
                } catch (NumberFormatException e) {
                    //ignore
                }
                try {
                    costTime2 = Integer.parseInt(o2.getCostTime());
                } catch (NumberFormatException e) {
                    //ignore
                }
                return Integer.compare(costTime2, costTime1);
            });
            //output
            if (diffList.isEmpty()) {
                System.out.println("本次校验没有发现启动劣化");
            } else {
                FileUtil.writeFile(diffList, outputPath);
                //int costDiff = getAllCost(diffList);
                int largeCostCount = getLargeCostCount(diffList);
                if (largeCostCount > 0) {
                    System.out.println("本次校验发现启动劣化, 主线程新增函数" + diffList.size() + "个, 耗时较大的函数" + largeCostCount + "个");
                } else {
                    System.out.println("没有检测到耗时较大的新增函数, 可能未发生启动劣化, 请check主线程新增函数" + diffList.size() + "个是业务引入或基线波动导致");
                }
                System.out.println("新增函数与劣化时间(单位μs)已输出到文件 ->" + outputPath);
            }

        } catch (Exception e) {
            System.out.println("异常:" + e);
        }
    }

    private static boolean filterMethod(String functionName) {
        for (String item : Main.METHOD_FILTER_LIST) {
            if (functionName.contains(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean getCostValid(MethodBean model) {
        try {
            int timeValue = Integer.parseInt(model.getCostTime());
            if (timeValue > 100) {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    private int getAllCost(ArrayList<MethodBean> diffList) {
        int result = 0;
        for (MethodBean model : diffList) {
            try {
                int timeValue = Integer.parseInt(model.getCostTime());
                result += timeValue;
            } catch (NumberFormatException e) {
                //ignore
            }
        }
        return result / 1000;
    }

    private int getLargeCostCount(ArrayList<MethodBean> diffList) {
        int count = 0;
        for (MethodBean model : diffList) {
            try {
                int timeValue = Integer.parseInt(model.getCostTime());
                if (timeValue > 1000) {
                    count++;
                }
            } catch (NumberFormatException e) {
                //ignore
            }
        }
        return count;
    }
}
