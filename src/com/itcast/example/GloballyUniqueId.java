package com.itcast.example;

import com.itcast.watcher.ZKConnectionWatcher;
import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhanghao
 * @date 2020/6/8 - 10:52
 */
public class GloballyUniqueId implements Watcher {

    // zk的IP
    String IP = "192.168.44.139:2181";
    // 连接对象
    ZooKeeper zooKeeper;
    // 计数器
    CountDownLatch countDownLatch = new CountDownLatch(1);
    // 用户生成序号的节点
    String defaultPath = "/uniqueId";

    @Override
    public void process(WatchedEvent event) {
        try {
            // 捕获事件状态
            if (event.getType() == Event.EventType.None) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功");
                    countDownLatch.countDown();
                }else if (event.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("断开连接!");
                }else if (event.getState() == Event.KeeperState.Expired) {
                    System.out.println("会话超时!");
                    // 超时后服务器已经将连接释放，需要冲洗连接服务器
                    Watcher watcher;
                    zooKeeper = new ZooKeeper(IP, 5000, new ZKConnectionWatcher());
                }else if (event.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("认证失败!");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public GloballyUniqueId(){
        try {
            // 打开连接
            zooKeeper = new ZooKeeper(IP, 5000, this);
            // 阻塞线程，等待连接的创建成功
            countDownLatch.await();
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    // 生成id的方法
    public String getUniqueId(){
        String path = "";
        try {
            // 创建临时有序节点
            path = zooKeeper.create(defaultPath,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        }catch (Exception exception){
            exception.printStackTrace();
        }
        // /uniqueId0000000001
        return path.substring(9);
    }

    public static void main(String[] args) {
        GloballyUniqueId globallyUniqueId = new GloballyUniqueId();
        for (int i = 0; i < 5; i++) {
            String uniqueId = globallyUniqueId.getUniqueId();
            System.out.println(uniqueId);
        }
    }
}
