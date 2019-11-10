package org.xing.rpc;

/**
 * Created by xingyuntian on 2018/3/11.
 */
public class RpcDemoConsumer {
    public static void main(String[] args) {
        RpcCore rpcCore=new RpcCore();
        RpcDemo rpcDemo = (RpcDemo)rpcCore.refer(RpcDemo.class, "127.0.0.1", 8087);
        System.out.println(" 远程调用成功");
        System.out.println("返回的结果是---->"+rpcDemo.getStudent(111,"zhangsan"));
    }
}
