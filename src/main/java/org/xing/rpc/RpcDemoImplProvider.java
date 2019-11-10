package org.xing.rpc;

/**
 * Created by xingyuntian on 2018/3/11.
 */
public class RpcDemoImplProvider implements RpcDemo{
    public Student getStudent(Integer id,String name){
        return new Student(1234,"zhangsan",20,true);
    }

    public static void main(String[] args) {
        RpcCore rpcCore=new RpcCore();
        rpcCore.export(8087);
    }

}
