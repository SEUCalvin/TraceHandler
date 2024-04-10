package com.netease.cloudmusic;

import com.netease.cloudmusic.handler.HandlerDiff;
import com.netease.cloudmusic.handler.HandlerGenerate;
import com.netease.cloudmusic.handler.HandlerParse;
import com.netease.cloudmusic.util.ParseUtil;

/**
 * Created by Jice on 2024/03/29.
 * Desc: 模式1: 根据基线数据, 给出待检测trace文件是否存在新增函数
 * Desc: 模式2: 根据给出的多个trace文件, 生成去重后的基线数据
 */
public class Main {

    public static final String MODEL_DIFF = "1";
    public static final String MODEL_GENERATE = "2";
    public static final String DECORATION = "   ";
    public static final String[] PACKAGE_NAME_LIST = {"com.netease.cloudmusic", "com/netease/cloudmusic"};
    public static final String[] METHOD_FILTER_LIST = {".<init>", ".<clinit>"};

    public static void main(String[] args) {

        //ParseUtil.parasTrace("/Users/calvin/Downloads/musictrace.trace");

        if (args == null || args.length == 0 || (!args[0].equals(MODEL_DIFF) && !args[0].equals(MODEL_GENERATE))) {
            System.out.println("命令行参数错误: 请输入正确的参数格式, 中间用空格隔开");
            System.out.println("{模式1｜基线文件路径｜待检测的trace文件路径｜diff结果输出文件路径}");
            System.out.println("{模式2｜基线文件输出路径｜Trace1文件路径｜Trace2文件路径｜...}");
            System.out.println("模式1功能: 根据基线数据, 给出待检测trace文件是否存在新增函数");
            System.out.println("模式2功能: 根据给出的多个trace文件, 生成去重后的基线数据");
            return;
        }

        HandlerParse parse;
        if (args[0].equals(MODEL_DIFF)) {
            parse = new HandlerDiff();
        } else {
            parse = new HandlerGenerate();
        }
        parse.handle(args);
    }

}