package org.xing.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by xingyuntian on 2018/3/11.
 */
public class RpcCore implements RpcProtocol{

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private ServerSocket serverSocket;
    private Map<String,List<Object>> services=new ConcurrentHashMap<String, List<Object>>();
    private Map<String,Map<String,Object>> interfaceAtrributes=new ConcurrentHashMap<>();

    @Override
    public void export(int port){
        start(port);
    }

    @Override
    public Object refer(final Class interfaceClass,String host, int port){
        connect(host,port);
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String interfaceName=interfaceClass.getName();
                        String fullName= (String) interfaceAtrributes.get(interfaceName).get("fullName");
                        return get(fullName,method,args);
                    }
                });
    }

    public Object get(String interfaceFullName,Method method,Object[] parames){
        Object result=null;
        try {
            objectOutputStream.writeUTF(interfaceFullName);
            objectOutputStream.writeUTF(method.getName());
            objectOutputStream.writeObject(method.getParameterTypes());
            objectOutputStream.writeObject(parames);
            objectOutputStream.flush();
            result=objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectOutputStream!=null) {
                    objectOutputStream.close();
                }
                if (objectInputStream!=null) {
                    objectInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    private void start(int port) {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("localhost", port));
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            System.out.println("server has started success port is --->"+port);

            Socket socket = null;
            try {
                socket = serverSocket.accept();
                new Thread(new Processsor(socket,services)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public  void init(){
        RpcDemo rpcDemo=new RpcDemoImplProvider();
        String group="rpcDemo";
        String version="1.0.0";
        String fullName=RpcDemo.class.getName()+"&"+group+"&"+version;
        List<Object> rpcDemoInstances=services.get(fullName);
        if (rpcDemoInstances==null){
            rpcDemoInstances=new ArrayList();
            rpcDemoInstances.add(rpcDemo);
        }
        services.put(fullName,rpcDemoInstances);
    }

    public void connect(String host, int port) {
        try {
            storeInterface();
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));
            objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            objectInputStream=new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void storeInterface(){
        String group="rpcDemo";
        String version="1.0.0";
        String fullName=RpcDemo.class.getName()+"&"+group+"&"+version;
        Map<String,Object> attributes=interfaceAtrributes.get(fullName);
        if (attributes==null){
            attributes=new ConcurrentHashMap(100);
            attributes.put("group",group);
            attributes.put("version",version);
            attributes.put("fullName",fullName);
        }
        interfaceAtrributes.put(RpcDemo.class.getName(),attributes);

    }

    class Processsor implements Runnable {
        private Socket socket;
        private ObjectInputStream objectInputStream;
        private ObjectOutputStream objectOutputStream;
        private Map<String,List<Object>> services;
        private Processsor(Socket socket,Map<String,List<Object>> services) {
            this.socket = socket;
            this.services=services;
        }

        @Override
        public void run() {
            System.out.println((((InetSocketAddress) socket.getRemoteSocketAddress()).getPort()));

            try {
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                String interfaceFullName=objectInputStream.readUTF();
                String methodName=objectInputStream.readUTF();
                Class[] parameTypes= (Class[]) objectInputStream.readObject();
                Object[] objects= (Object[]) objectInputStream.readObject();
                String interfaceName=interfaceFullName.split("&")[0];
                Class service=Class.forName(interfaceName);
                Method method=service.getMethod(methodName,parameTypes);
                Object instances=services.get(interfaceFullName).get(0);
                Object result = method.invoke(instances, objects);
                objectOutputStream.writeObject(result);
                objectOutputStream.flush();
                objectOutputStream.close();
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
