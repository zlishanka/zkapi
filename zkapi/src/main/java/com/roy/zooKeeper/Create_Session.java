package com.roy.zooKeeper; 

import java.util.*; 
import java.io.IOException; 
import org.I0Itec.zkclient.ZkClient; 
import org.I0Itec.zkclient.IZkChildListener; 

// use ZKClient to create a zookeeper client 

public class Create_Session {
  public static void main(String[] args) throws IOException, InterruptedException {
    ZkClient zkClient = new ZkClient("localhost", 5000);
    String path = "/zk-book";

    // register IZkChildLister to monitor the change of child nodes
    // even the node does not exist, you can still register/monitor 
    zkClient.subscribeChildChanges(path, new IZkChildListener() {
       public void handleChildChange(String parentPath, List<String> currentChilds) throws
           Exception {
             System.out.println(parentPath + " 's child changed, currentChilds:" + 
			currentChilds); 
           } 
       }); 

    zkClient.createPersistent(path, true);  
    Thread.sleep(1000); 
    System.out.println(zkClient.getChildren(path)); 
    Thread.sleep(1000); 
    zkClient.createPersistent(path+"/c1"); 
    Thread.sleep(1000); 
    zkClient.delete(path + "/c1"); 
    Thread.sleep(1000); 
    zkClient.delete(path); 
    Thread.sleep(Integer.MAX_VALUE);
  }
} 
