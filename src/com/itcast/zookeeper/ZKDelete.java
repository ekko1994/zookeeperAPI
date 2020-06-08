package com.itcast.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKDelete {

    ZooKeeper zooKeeper;

    String IP = "192.168.44.139:2181";

    @Before
    public void before() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功！");
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws Exception {
        zooKeeper.close();
    }

    @Test
    public void delete1() throws Exception {
        //arg1:删除节点的路径
        //arg2:数据版本信息 -1代表版本不参与删除
        zooKeeper.delete("/delete/node1",-1);
    }

    @Test
    public void delete2() throws Exception {
        //异步方式
        zooKeeper.delete("/delete/node2", -1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx) {
                // 是否删除成功 0?
                System.out.println(rc);
                // 删除路径
                System.out.println(path);
                System.out.println(ctx);
            }
        },"i am context");
        Thread.sleep(10000);
        System.out.println("end");
    }
}
