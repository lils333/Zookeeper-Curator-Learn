
# zookeeper
### zookeeper 是什么？

zookeeper是一个分布式的，开放源码的分布式应用程序协调服务(再加一点)		

### zookeeper使用那些算法来保证可靠性和数据一致性?

Paxos，zab	

3. zookeeper提供的几个主要组成部分?

- leader -- 所有的数据写入和同步都是leader在决定。
- follower --	接受leader发送过来的数据，和leader维持心跳，转发客户端的写入请求。
- observer -- 自己google，一般不使用。		

4. zookeeper直接提供分布式功能么?

本身并不提供分布式的功能，他实际上是一个类似于文件系统管理的程序，但是由于他提供了一些保证，所以我们可以使用这些保证来完成分布式编程。也就是说zookeeper只是一个分布式编程的工具。

5. zookeeper提供以下特性：
- 顺序一致性：也就是说每一个客服端发送的修改动作都是按照客户端发送的顺序来执行的
- 原子性：也就是说客户端发送的修改动作，要么成功，要么失败，他们是一个原子的动作
- 同一视图：也就是说不管是哪个客户端，他们看到的zookeeper集群最终都是一个视图
- 可靠性: 也就是说一个客户端写入成功，那么数据就会被存放到事务日志里面去，不会丢失数据
- 及时性：在一个确定的时间段之内，保证客户端的更新会同步到所有集群里面去

6. zookeeper可以不使用集群吗?

   可以，但是最好的使用方式是集群，一般奇数个就可以了，但是也不能够太多，如果过多的话，会导致集群里面的各个server同步花费过多的时间
   这样的话允许(n - 1)/2个宕机

7. 可以存储应用数据?

   最好不要，zookeeper把所有的数据存放到内存里面，所以不能够用来存储应用数据，应用数据一般都会比较大，这些应用相关的数据可以存放在ActiveMQ这些broker里面去

8. 单个数据能够存放多大?

   默认最大存放的数据为1M，但是实际上存储的数据应该远远小于1M最好就是在几byte到几KB之间的数据数据，原因在于zookeeper集群之间需要同步这些数据，
   如果数据过大，大部分时间都会花在同步上面，所以性能会非常低，而且有时会出现一些非预期的问题

9. 什么是wacth?

     zookeeper提供的核心功能之一，watch机制原理就是一种回调机制，把当前关注的znode添加了一个watch以后。那么当该znode发生了变化，就会触发服务器发送watch事件给客户端

10. 那些方法可以注册watch?

    所有查询方法（包括状态和数据）exist, getData, getChildren

11. watch能够多次触发？

     不能，zookeer的watch是由zookeeper服务保存，只能够触发一次，触发了以后如果客户端不在attach，那么以后就不会再触发watch
     并且zookeeper不能够保证每一个事件都可以接受到，但是它保证的是最终数据是一致的( 一个watch在被发送给客户端，但是客户端还没有接受到这个watch，这个时候该watch的node又发生了变化，比如数据被修改了，那么这个时候，由于没有
      watch所以这个时候的event是接受不到。所以当我们可以通过查询最新的值，再一次注册上watch
   ）

12. 事务日志存放位置

     建议单独一个磁盘用来存放事务日志，让该zookeeper程序独占磁盘的IO，数据快照文件不要求和日志存放在一起	
       
## Curator
Curator框架本身就会监控对zookeeper的连接，每一个zookeeper的操作都会被包装在一个retry机制里面去执行，所以
	- 每一个curaotr的操作都会在连接建立以后执行
	- 每一个curator框架的操作都会根据当前设置的retry机制来执行操作
		-- 阻塞直到连接重新建立或者超时,如果已经连接
		-- 获取我们在创建CuratorFramowrk的时候传入的重试机制，然后开始重试执行curator的操作（SUSPEND LOST）
	- 如果一个连接临时丢失掉了，那么curator会根据设置的重试策略恢复
	- 所有的curtor提供的recipes都已经对连接问题有很好处理了

### curator
  1. share
	   - SharedCount
	   - SharedValue
	2. nodes
	   - PersistentNode
	   - GroupMember
	3. lock
	   - InterProcessMutex
	   - InterProcessMultiLock
	   - InterProcessReadWriteLock
	   - InterProcessSemaphoreMutex
	   - InterProcessSemaphoreV2
	4. leader election
	   - LeaderLatch
	   - LeaderSelector
	5. cache
	   - NodeCache
	   - PathChildrenCache
	   - TreeCache
	6. barrier
	   - DistributedBarrier
	   - DistributedDoubleBarrier
	7. atomic
	   - DistributedAtomicInteger
	   - DistributedAtomicLong
	   - DistributedAtomicNumber
	8. service discovery
	   - ServiceDiscovery
	   - ServiceProvider
	9. queue（不建议使用，如果要使用可以使用ActiveMQ/RabbitMQ这些消息中间件）
