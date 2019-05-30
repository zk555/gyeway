package com.gy.gyeway.rpc.dataBridge;

import java.io.Serializable;
import java.util.List;
/**
 * 封装请求参数
 * @Description:
 * @author  zk
 */
public class RequestData implements Serializable{
    private static final long serialVersionUID = -497072374733332517L;

    private String className;
    private String methodName;
    private Class<?>[] paramTyps;
    private Object[] args;
    private String requestNum;

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTyps() {
        return paramTyps;
    }

    public void setParamTyps(Class<?>[] paramTyps) {
        this.paramTyps = paramTyps;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getRequestNum() {
        return requestNum;
    }

    public void setRequestNum(String requestNum) {
        this.requestNum = requestNum;
    }
}
