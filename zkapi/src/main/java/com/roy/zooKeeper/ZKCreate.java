
package com.roy.zooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.*; 

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

public class ZKCreate {
   // create static instance for zookeeper class.
   private static ZooKeeper zk;

   // create static instance for ZooKeeperConnection class.
   private static ZooKeeperConnection conn;

   // Method to create znode in zookeeper ensemble
   public static void create(String path, byte[] data) throws 
      KeeperException,InterruptedException {
      zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
      CreateMode.PERSISTENT);
   }

   // Method to check existence of znode and its status, if znode is available.
   public static Stat znode_exists(String path) throws
      KeeperException,InterruptedException {
      return zk.exists(path, true);
   }
   public static void main(String[] args) {

      // znode path
      String path = "/MyFirstZnode"; // Assign path to znode
      final CountDownLatch connectedSignal = new CountDownLatch(1); 
      // data in byte array
      String str1 = "My first zookeeper app";
      byte[] data = str1.getBytes(); // Declare data
		
      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost"); 

         Stat stat = znode_exists(path); 
         if (stat != null) {
           System.out.println("Node exists and node version is " + stat.getVersion());
           // set the node with new metadata 
           zk.setData(path, data, stat.getVersion()); 

           // get all children of znode 
            List <String> children = zk.getChildren(path, false);
            for(int i = 0; i < children.size(); i++)
              System.out.println(children.get(i)); //Print children's 
            
           byte[] b = zk.getData(path, new Watcher() {
             // override Watcher()'s process method
             public void process(WatchedEvent we) {
               if (we.getType() == Event.EventType.None) {
                 switch (we.getState()) {
                   case Expired: 
                     connectedSignal.countDown();
                     break;
                 } 
               } else {
                 try {
                   String path = "/MyFirstZnode";
                   byte[] bn = zk.getData(path, false, null);
                   String data = new String(bn, "UTF-8");
                   System.out.println(data);
                   connectedSignal.countDown();
                 } catch(Exception ex) {
                   System.out.println(ex.getMessage());
                 } 
              }
           }   
         },null); 

         String data1 = new String(b, "UTF-8"); 
         System.out.println(data1);
         // set watcher to wait for node update
         connectedSignal.await();  
         } else {
           System.out.println("Node does not exists");           
           create(path, data); // Create the data to the specified path
         }
         conn.close();
      } catch (Exception e) {
         System.out.println(e.getMessage()); //Catch error message
      }
   }
}
