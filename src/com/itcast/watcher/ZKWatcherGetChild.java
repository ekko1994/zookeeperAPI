package com.itcast.watcher;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKWatcherGetChild {

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
    public void watcherGetChild1() throws Exception {
        zooKeeper.getChildren("/watcher3",true);
        Thread.sleep(50000);
        System.out.println("end");
    }

    @Test
    public void watcherGetChild2() throws Exception {
        zooKeeper.getChildren("/watcher3", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("path = " + event.getPath());
                System.out.println("eventType = " + event.getType());
            }
        });
        Thread.sleep(50000);
        System.out.println("end");
    }
    @Test
    public void watcherGetChild3() throws Exception {
        // watcher是一次性的
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("path = " + event.getPath());
                System.out.println("eventType = " + event.getType());

                if (event.getType() == Event.EventType.NodeChildrenChanged) {
                    try {
                        zooKeeper.getChildren("/watcher3",this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        zooKeeper.getChildren("/watcher3",watcher);
        Thread.sleep(50000);
        System.out.println("end");
    }

    @Test
    public void watcherGetChild4() throws Exception {
        // 注册多个监听器对象
        zooKeeper.getChildren("/watcher3", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("1");
                System.out.println("path = " + event.getPath());
                System.out.println("eventType = " + event.getType());

                if (event.getType() == Event.EventType.NodeChildrenChanged) {
                    try {
                        zooKeeper.getChildren("/watcher3",this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        zooKeeper.getChildren("/watcher3", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("2");
                System.out.println("path = " + event.getPath());
                System.out.println("eventType = " + event.getType());

                if (event.getType() == Event.EventType.NodeChildrenChanged) {
                    try {
                        zooKeeper.getChildren("/watcher3",this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread.sleep(50000);
        System.out.println("end");
    }
}
