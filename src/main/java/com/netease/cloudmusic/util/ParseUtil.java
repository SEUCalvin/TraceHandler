package com.netease.cloudmusic.util;

import com.android.tools.perflib.vmtrace.MethodInfo;
import com.android.tools.perflib.vmtrace.TraceAction;
import com.android.tools.perflib.vmtrace.VmTraceHandler;
import com.android.tools.perflib.vmtrace.VmTraceParser;
import com.netease.cloudmusic.model.MethodBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

import static com.netease.cloudmusic.Main.*;

public class ParseUtil {

    public static ArrayList<MethodBean> parasTrace(String tracePath) {
        //结果BeanList
        ArrayList<MethodBean> resultList = new ArrayList<>();
        //线程id和BeanStack
        HashMap<String, Stack<MethodBean>> stackMap = new HashMap<>();
        //线程id和线程name
        LinkedHashMap<Integer, String> threadInfoMap = new LinkedHashMap<>();
        //方法信息
        LinkedHashMap<Long, MethodInfo> methodInfoMap = new LinkedHashMap<>();

        VmTraceParser parser = new VmTraceParser(new File(tracePath), new VmTraceHandler() {
            @Override
            public void setVersion(int version) {
            }

            @Override
            public void setProperty(String key, String value) {
            }

            @Override
            public void addThread(int id, String name) {
                threadInfoMap.put(id, name);
                stackMap.put("" + id, new Stack<>());
            }

            @Override
            public void addMethod(long id, MethodInfo info) {
                methodInfoMap.put(id, info);
            }

            @Override
            public void addMethodAction(int threadId, long methodId, TraceAction methodAction, int threadTime, int globalTime) {
                MethodInfo methodInfo = methodInfoMap.get(methodId);
                if (methodInfo == null) {
                    return;
                }
                String functionName = methodInfo.toString();

                if ((methodAction == TraceAction.METHOD_ENTER || methodAction == TraceAction.METHOD_EXIT)
                        && (funNameContainsPackageName(functionName))) {
                    MethodBean method = new MethodBean();
                    method.setTime("" + threadTime);
                    method.setThreadId("" + threadId);
                    method.setThreadName(threadInfoMap.get(threadId));
                    method.setFunctionName(functionName);

                    Stack<MethodBean> stack = stackMap.get(method.getThreadId());
                    if (methodAction == TraceAction.METHOD_ENTER) {
                        resultList.add(method);
                        stack.push(method);
                    }

                    if (methodAction == TraceAction.METHOD_EXIT) {
                        if (!stack.isEmpty()) {
                            MethodBean peek = stack.peek();
                            if (peek.getFunctionName().equals(method.getFunctionName())) {
                                MethodBean pop = stack.pop();
                                String costTime = "" + Math.abs(Long.parseLong(method.getTime()) - Long.parseLong(pop.getTime()));
                                pop.setCostTime(costTime);
                            }
                        }
                    }
                }
            }

            @Override
            public void setStartTimeUs(long startTimeUs) {
            }
        });
        try {
            parser.parse();
        } catch (Exception e) {
            System.out.println("异常:" + e);
        }
        return resultList;
    }

    private static boolean funNameContainsPackageName(String functionName) {
        for (String item : PACKAGE_NAME_LIST) {
            if (functionName.contains(item)) {
                return true;
            }
        }
        return false;
    }

}
