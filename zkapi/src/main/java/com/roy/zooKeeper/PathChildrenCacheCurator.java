package com.roy.zooKeeper; 

import java.util.*; 
import java.io.IOException; 

import org.apache.curator.RetryPolicy; 
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import org.apache.curator.retry.ExponentialBackoffRetry; 
import org.apache.zookeeper.CreateMode; 

// use Netflix open source curator to create a zookeeper client 
// PathChildrenCache is used to monitor the state change of zookeeper child's node  
// PathChildrenCacheListener() callback 

public class PathChildrenCacheCurator{
  static String path = "/zk-book";
  static CuratorFramework client = CuratorFrameworkFactory.builder()
	.connectString("localhost")
        .sessionTimeoutMs(5000)
        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
        .build(); 

  public static void main(String[] args) throws Exception {
    client.start();
    PathChildrenCache cache = new PathChildrenCache(client, path, true);
    cache.start(StartMode.POST_INITIALIZED_EVENT); 
    cache.getListenable().addListener(new PathChildrenCacheListener() {
        public void childEvent(CuratorFramework client, 
               PathChildrenCacheEvent event) throws Exception {
		 switch(event.getType()) {
                 case CHILD_ADDED: 
                   System.out.println("CHILD_ADDED,"+event.getData().getPath()); 
                   break; 
                 case CHILD_UPDATED: 
                   System.out.println("CHILD_UPDATD,"+event.getData().getPath()); 
 		   break; 
                 case CHILD_REMOVED: 
                   System.out.println("CHILD_REMOVED,"+event.getData().getPath()); 
 		   break;
                 default: 
                   break; 
                } 
              }
            });  
                  
    client.create().withMode(CreateMode.PERSISTENT).forPath(path);
    Thread.sleep(1000); 
    client.create().withMode(CreateMode.PERSISTENT).forPath(path+"/c1");
    Thread.sleep(1000); 
    client.delete().forPath(path+"/c1");
    Thread.sleep(1000); 
    client.delete().forPath(path);
    Thread.sleep(Integer.MAX_VALUE);  
  }
} 
