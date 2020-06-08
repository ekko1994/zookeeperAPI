package com.itcast.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhanghao
 * @date 2020/6/5 - 22:12
 */
public class ZookeeperConnection {

    public static void main(String[] args) {
        try{
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ZooKeeper zooKeeper = new ZooKeeper("192.168.44.139:2181,192.168.44.139:2182,192.168.44.139:2183",
                    5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState()== Event.KeeperState.SyncConnected) {
                        System.out.println("连接创建成功！");
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            System.out.println(zooKeeper.getSessionId());
            zooKeeper.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
