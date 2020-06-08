package com.itcast.watcher;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKWatcherGetData {

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
                System.out.println("path = " + event.getPath());
                System.out.println("eventType = " + event.getType());
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws Exception {
        zooKeeper.close();
    }

    @Test
    public void watcherGetData1() throws Exception {
        //arg1:节点的路径
        //arg2:使用连接对象的watcher
        zooKeeper.getData("/watcher2",true,null);
        Thread.sleep(50000);
        System.out.println("end");
    }

    @Test
    public void watcherGetData2() throws Exception {
        zooKeeper.getData("/watcher2", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("path = " + event.getPath());
                System.out.println("eventType = " + event.getType());
            }
        },null);
        Thread.sleep(50000);
        System.out.println("end");
    }

    @Test
    public void watcherGetData3() throws Exception {
        // watcher是一次性的
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path = " + event.getPath());
                System.out.println("eventType = " + event.getType());

                try {
                    if (event.getType() == Event.EventType.NodeDataChanged){
                        zooKeeper.getData("/watcher2",this,null);
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        zooKeeper.getData("/watcher2",watcher,null);
        Thread.sleep(60000);
        System.out.println("end");
    }

    @Test
    public void watcherGetData4() throws Exception {
        // 注册多个监听器对象
        zooKeeper.getData("/watcher2", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("1");
                System.out.println("path = " + event.getPath());
                System.out.println("eventType = " + event.getType());
                try {
                    if (event.getType() == Event.EventType.NodeDataChanged){
                        zooKeeper.getData("/watcher2",this,null);
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },null);

        zooKeeper.getData("/watcher2", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("2");
                System.out.println("path = " + event.getPath());
                System.out.println("eventType = " + event.getType());
                try {
                    if (event.getType() == Event.EventType.NodeDataChanged){
                        zooKeeper.getData("/watcher2",this,null);
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },null);

        Thread.sleep(60000);
        System.out.println("end");
    }


}
