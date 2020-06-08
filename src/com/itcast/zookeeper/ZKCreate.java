package com.itcast.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhanghao
 * @date 2020/6/7 - 16:42
 */
public class ZKCreate {

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
    public void create1() throws Exception {
        //arg1:节点的路径
        //arg2:节点的数据
        //arg3:权限列表 world:anyone:cdrwa
        //arg4:节点类型 持久化节点
        zooKeeper.create("/create/node1","node1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void create4() throws Exception {
        // ip授权模式
        List<ACL> acls = new ArrayList<>();
        Id id = new Id("ip", "192.168.44.139");
        acls.add(new ACL(ZooDefs.Perms.ALL,id));

        zooKeeper.create("/create/node4","node4".getBytes(), acls, CreateMode.PERSISTENT);
    }

    @Test
    public void create5() throws Exception {

        // auth授权模式
        zooKeeper.addAuthInfo("digest","jack:jack".getBytes());
        zooKeeper.create("/create/node5","node5".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
    }

    @Test
    public void create11() throws Exception {
        //异步方式创建节点
        zooKeeper.create("/create/node11", "node11".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                // 0 代表创建成功
                System.out.println(rc);
                //节点的路径
                System.out.println(path);
                // 上下文参数
                System.out.println(ctx);
                // 节点的路径
                System.out.println(name);
            }
        },"i am context");
        Thread.sleep(10000);
        System.out.println("end");
    }
}
