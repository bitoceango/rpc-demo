package org.xing.rpc;

/**
 * Created by xingyuntian on 2018/3/11.
 */
public interface RpcProtocol  {
     void export(int port);
     Object refer(Class inrerfaceClass,String host, int port);
}
