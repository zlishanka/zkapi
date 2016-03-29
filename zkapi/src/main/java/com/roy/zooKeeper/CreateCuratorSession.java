package com.roy.zooKeeper; 

import java.util.*; 
import java.io.IOException; 
import java.util.concurrent.CountDownLatch; 
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors; 

import org.apache.curator.RetryPolicy; 
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback; 
import org.apache.curator.framework.api.CuratorEvent; 

import org.apache.curator.retry.ExponentialBackoffRetry; 

import org.apache.zookeeper.CreateMode; 
import org.apache.zookeeper.data.Stat; 

// use Netflix open source curator to create a zookeeper client 
// use asynchronous interface (backgroundCallback) 
 
public class CreateCuratorSession {
  static String path = "/zk-book";
  static CuratorFramework client = CuratorFrameworkFactory.builder()
	.connectString("localhost")
        .sessionTimeoutMs(5000)
        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
        .build(); 
  static CountDownLatch semaphore = new CountDownLatch(2); 
  static ExecutorService tp = Executors.newFixedThreadPool(2); 


  public static void main(String[] args) throws Exception {
    client.start();
    System.out.println("Main thread: " + Thread.currentThread().getName()); 
    // transfer to self-defined Executor  
    client.create().creatingParentsIfNeeded()
                   .withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
                   @Override 
                   public void processResult(CuratorFramework client, CuratorEvent event) 
                   throws Exception { 
                     System.out.println("event[code " + event.getResultCode() + ", type: "
                    + event.getType() + "]"); 
                   System.out.println("Thread of processResult: " + Thread.currentThread().
			getName()); 
                        semaphore.countDown(); 
                     }
                   }, tp).forPath(path, "init".getBytes());
 
    // no thread pool (tp), use zookeeper default EventThread to process  
    client.create().creatingParentsIfNeeded()
                   .withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
                   @Override 
                   public void processResult(CuratorFramework client, CuratorEvent event) 
			throws Exception {
                      System.out.println("event[Code: " + event.getResultCode() + ", type: " +
                        event.getType() + "]"); 
                      System.out.println("Thread of processResult: " + Thread.currentThread().
			getName()); 
                        semaphore.countDown(); 
                     }
                   }).forPath(path, "init".getBytes());
    semaphore.await(); 
    tp.shutdown(); 
  }
} 
