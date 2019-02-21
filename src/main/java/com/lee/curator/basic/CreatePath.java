package com.lee.curator.basic;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;

public class CreatePath {

    //目录

    /**
     * 对于目录而言，以下属性不会由于子节点的创建修改删除而改变，这些属性只与该节点自己相关
     * czxid          --该节点创建的事务id，是一个64位的数字，在只要创建就不会在便
     * ctime          --该节点创建的时间，只要创建就不会在便
     * ephemeralowner --如果当前节点是临时性节点，那么该节点的内容就是属于那个seesion id，创建了就不会修改
     * <p>
     * 数据修改就会变动
     * version        --该节点的数据的版本号，只要数据发生改变，那么版本好就会递增
     * mzxid          --该节点修改的事务id，是一个64位的数字，每次修改就会+1
     * mtime          --该节点数据修改的时间,在每次修改都会改成1970到现在的秒数
     * datalength     --该节点存放的数据
     * <p>
     * acl修改就会变动
     * aversion       --该结点acl变换的版本号，变化包括创建和修改
     * <p>
     * 下面的属性发生在子节点上面，只要子节点有变化（创建或在删除），目录就会相应的修改
     * NumChildren     --子节点的数量，创建或者删除，才会变动
     * cversion        --只要子节点有变化（创建或者删除），那么这个version就会增加一次
     * pzxid           --只要子节点有变化（创建或者删除），那么该事务id就是增加
     * <p>
     * <p>
     * 对于叶子节点而言
     * NumChildren = 0，cversion = 0，pzxid = czxid ， ctime， ephemeralowner都不会在变动
     * 其他属性和目录一样，发生了变化就会修改
     * <p>
     * 删除一个叶子节点，那么该叶子节点的父节点变成叶子节点
     **/

    //create 只能够节点一个一个创建，不能够连续创建
    //对于已经存在的节点如果在创建的话，就会报错误 -- NodeExists
    //创建的话，可以使用orSetData方法表示如果已经存在那么就不在创建了
    public void create(CuratorFramework curatorFramework) throws Exception {
        System.out.println(
                curatorFramework.create()
                        .orSetData()
//                        .orSetData(2) //带版本号，版本号必须和当前路径的版本号一致，否则出现BadVersionException
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        //如果不指定值，那么默认会使用ip地址来填充
                        .forPath("/com/lee/zk/dist")
        );


    }

    //可以使用该方法来创建一个路径，如果父路径不存在，那么会依次创建，并且该方法调用多次效果都是一样的，也就是说幂等性
    //而且该方法创建的是不带任何数据的一个路径,而且该方法创建的是persistent的路径
    public void createContainer(CuratorFramework curatorFramework) {
        try {
            curatorFramework.createContainers("/com/lee/zook/dist");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //checkExists() 不会返回异常，哪怕不存在也不会返回异常
    //注意path里面不能够有.或者..或者//这样的形式，因为znode不存在相对路径，他们都是据绝对路径
    //path不能够以/结尾。必须不能够以/结尾
    //如果指定的一个路径不存在，那么返回stat为null
    public void stat(CuratorFramework curatorFramework) throws Exception {
        Stat stat = curatorFramework.checkExists().forPath("/zookeeper/lee");
        System.out.println(stat);
        System.out.println("czxid : " + stat.getCzxid());
        System.out.println("mzxid : " + stat.getMzxid());
        System.out.println("pzxid : " + stat.getPzxid());
        System.out.println("version : " + stat.getVersion());
        System.out.println("aversion : " + stat.getAversion());
        System.out.println("cversion : " + stat.getCversion());
        System.out.println("ctime : " + stat.getCtime());
        System.out.println("mtime : " + stat.getMtime());
        System.out.println("ephemeralowner : " + stat.getEphemeralOwner());
        System.out.println("datalength : " + stat.getDataLength());
        System.out.println("NumChildren : " + stat.getNumChildren());
    }


    public void stat(Stat stat) throws Exception {
        System.out.println("czxid : " + stat.getCzxid());
        System.out.println("mzxid : " + stat.getMzxid());
        System.out.println("pzxid : " + stat.getPzxid());
        System.out.println("version : " + stat.getVersion());
        System.out.println("aversion : " + stat.getAversion());
        System.out.println("cversion : " + stat.getCversion());
        System.out.println("ctime : " + stat.getCtime());
        System.out.println("mtime : " + stat.getMtime());
        System.out.println("ephemeralowner : " + stat.getEphemeralOwner());
        System.out.println("datalength : " + stat.getDataLength());
        System.out.println("NumChildren : " + stat.getNumChildren());
    }

    public void createTemp(CuratorFramework curatorFramework) {
        try {
            Stat stat = new Stat();
            String s = curatorFramework.create()
                    .compressed()
                    .storingStatIn(stat)
                    .creatingParentsIfNeeded() //这个功能创建的父节点是持久化的，因为非持久化子节点是不能够存放子节点的
                    .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                    .forPath("/com/lee/zk/cp/hello", "力量双".getBytes(StandardCharsets.UTF_8));
            System.out.println(s);
            stat(stat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
