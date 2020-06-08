package com.itcast.example;

import sun.security.krb5.internal.Ticket;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author zhanghao
 * @date 2020/6/8 - 14:50
 */
public class TicketSeller {

    private void sell(){
        System.out.println("售票开始");
        //线程随机休眠毫秒，模拟现实中的费时操作
        int sleepMillis = 5000;
        try {
            try { TimeUnit.MILLISECONDS.sleep(sleepMillis);} catch (InterruptedException e) {e.printStackTrace();}
        }catch (Exception exception){
            exception.printStackTrace();
        }
        System.out.println("售票结束");
    }

    public void sellTicketWithLock()throws Exception{
        MyLock myLock = new MyLock();
        // 获得锁
        myLock.acquireLock();
        sell();
        // 释放锁
        myLock.releaseLock();
    }

    public static void main(String[] args)throws Exception {
        TicketSeller ticketSeller = new TicketSeller();
        for (int i = 0; i < 10; i++) {
            ticketSeller.sellTicketWithLock();
        }
    }
}
