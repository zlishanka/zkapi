package com.roy.zooKeeper; 

import java.util.*; 
import java.io.IOException; 

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry; 
import org.apache.zookeeper.CreateMode; 

// use Netflix open source curator to create a zookeeper client 
// (1) select root node like /master_select, 
// (2) multiple machines create the same child under /master_select/lock 
// (3) only one machines is able to create /master_select/lock and it becomes selected master 
// (4) other machines will monitor the /master_select/lock  

public class MasterSelectCuratorRecipes{
  static String master_path = "/curator_recipes_master_path";
  static CuratorFramework client = CuratorFrameworkFactory.builder()
	.connectString("localhost")
        .sessionTimeoutMs(5000)
        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
        .build(); 

  public static void main(String[] args) throws Exception {
    client.start();
    LeaderSelector selector = new LeaderSelector(client, 
                                    master_path, 
                            new LeaderSelectorListenerAdapter() {
                @Override
		public void takeLeadership(CuratorFramework client) throws Exception { 
                  System.out.println("Become master role"); 
                  Thread.sleep(3000); 
                  System.out.println("Finish master operation, release master previlege"); 
                } 
            });  
    selector.autoRequeue();
    selector.start();               
    Thread.sleep(Integer.MAX_VALUE);  
  }
} 
