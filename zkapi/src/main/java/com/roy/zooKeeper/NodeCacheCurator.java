package com.roy.zooKeeper; 

import java.util.*; 
import java.io.IOException; 

import org.apache.curator.RetryPolicy; 
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;


import org.apache.curator.retry.ExponentialBackoffRetry; 
import org.apache.zookeeper.CreateMode; 

// use Netflix open source curator to create a zookeeper client 
// NodeCache is used to monitor the state change of zookeeper node  
// NodeCacheListener() callback 
// getListenable().addListener()  
public class NodeCacheCurator{
  static String path = "/zk-book/nodecache";
  static CuratorFramework client = CuratorFrameworkFactory.builder()
	.connectString("localhost")
        .sessionTimeoutMs(5000)
        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
        .build(); 

  public static void main(String[] args) throws Exception {
    client.start();
    // transfer to self-defined Executor  
    client.create().creatingParentsIfNeeded()
                   .withMode(CreateMode.EPHEMERAL)
                   .forPath(path, "init".getBytes());

    final NodeCache cache = new NodeCache(client, path, false);
    cache.start();  
    cache.getListenable().addListener(new NodeCacheListener() {
        @Override 
        public void nodeChanged() throws Exception {
           System.out.println("Node data udpate, new stat: " + 
             new String(cache.getCurrentData().getData())); 
        }
        });
    client.setData().forPath(path, "u".getBytes()); 
    Thread.sleep(1000); 
    //client.delete().deletingChildrenIfNeeded().forPath(path);
    Thread.sleep(Integer.MAX_VALUE);  
  }
} 
