package com.netease.cloudmusic.model;

/**
 * Created by Jice on 2024/03/29.
 */
public class MethodBean implements Comparable<MethodBean> {

    private String threadId;
    private String threadName;
    private String time;
    private String costTime = "0";
    private String functionName;
    private long order;

    public MethodBean() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isMainThread() {
        return getThreadName().equals("main");
    }

    public String getFunctionNameWithThread() {
        if (getThreadName().equals("main")) {
            return "main#" + getFunctionCleanName();
        } else {
            return "subThread#" + getFunctionCleanName();
        }
    }

    //clean class $+d
    public String getFunctionCleanName() {
        if (functionName == null || functionName.isEmpty()) {
            return "";
        }
        return functionName.replaceAll("\\$\\d+", "");
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    @Override
    public int compareTo(MethodBean o) {
        return (int) (order - o.order);
    }

    public static MethodBean getTitleDescModel() {
        MethodBean result = new MethodBean();
        result.setThreadId("ThreadId");
        result.setThreadName("ThreadName");
        result.setCostTime("CostTime(μs)");
        result.setFunctionName("FunctionName");
        return result;
    }
}
