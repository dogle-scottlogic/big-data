# Cassandra vs MariaDB: Scaling

## Keeping Ahead of the Demand
<!--Add real link when previous blog post is up -->
Before we start we must tip our hats to Dave and Laurie for their [previous blog post](http://blog.scottlogic.com/) where they evaluated single node deployments of [Cassandra](http://cassandra.apache.org/) and [MariaDB](https://mariadb.org/) using the example of a theoretical hat store. They found that in a single node set up the databases were fairly equivalent in terms of performance. In this blog post we have looked at what happens when a single node instance is no longer able to keep up with the demand on the database and you want to horizontally scale the database. In the previous post they said that it was a foregone conclusion that Cassandra would scale horizontally better so we have investigated this assumption further to see how well Cassandra scales horizontally and what options are available to try and scale MariaDB.

## Assessing Performance of Node Clusters

To assess the performance of the different clusters we made use of the database event generation and logging mechanism created by Dave and Laurie. The Java client for Cassandra by default supports load balancing so it was relatively straight forward to start up multiple docker containers and run some tests. However it quickly became apparent that this approach was unrealistic, **Fig1** shows a slow down in write response times as the number of nodes increases and **Fig2** shows a drastic slow down in read response times. As more nodes were started the more they were competing for the same limited hardware resources of the machine, in order to make an kind of realistic assessment of the scaling we needed to run the nodes on multiple machines.   

**Fig 1**
![](https://drive.google.com/a/scottlogic.co.uk/file/d/0B65w2mgTevTpN1VjUkEyWXVDaGM/view?usp=sharing "Write operation response times for multiple Cassandra nodes running on the same machine")

**Fig 2**
![](https://drive.google.com/open?id=0B65w2mgTevTpeFZlQWpYdmJ4a0k "Read response times for multiple Cassandra nodes running on the same machine")

The decision was made to use Amazon Web Services as this enabled us to quickly and easily create and destroy different virtual machines with new nodes to run our different tests. Since we had moved the database instances to remote machines the possibility that network latency would muddy the results became a concern so we created an additional VM that would run the tests thereby keeping network latency to a minimum.

## Tradeoffs Whilst Selecting Scaling Approaches
### CAP Theorem

As soon as you have multiple nodes there is the possibility that communication between the nodes will be interrupted, in this scenario the CAP theorem states it becomes impossible to guarantee more that two of the following:
 * Consistency - reads return the most recent write data or an error
 * Availability - request return non-error responses
 * Partition tolerance - the database continues to operate when communication between the nodes is interrupted.
 
This effectively means that when node communication is interrupted you can either guarantee that data will continue to be returned but it might be out of date or you can guarantee that any data returned is up to date but you might return an error if the up to date data is unavailable. The nature of your data and application will determine which of these options is appropriate.

### ACID and BASE
Another thing to consider when selecting the database to use is the importance or not of guaranteeing ACID properties of the database operations. These are:
* Atomicity - If part of the transaction fails then none of it is applied and the database is left unchanged.
* Consistency - The transaction will leave the database in a valid state as defined by rules such as constraints.
* Isolation - Concurrent transactions do not interfere with each other. So the result wil be the same if they are executed concurrently to if they were executed sequentially.
* Durability - Once the transaction is committed the change will remain. i.e. the change is stored in non-volatile memory .

SQL databases like MariaDB guarantee these but Cassandra does not. Cassandra has limited atomicity and isolation, within a partition. It also does not have the same concept of constraints as a SQL database so it does not offer any consistency guarantees. It does guarantee durability though. Cassandra offers BASE (Basically Available, Soft state, Eventual consistency) as an alternative to ACID. This means that the data will not always be consistent but if no further updates are made eventually all reads will return the most recent data.  

## Configuring Cassandra Cluster 

Cassandra has two important configuration options to consider whilst creating your cluster, the replication factor and the consistency level.

The replication factor is set when the keyspace is created and it defines how many copies of the data are created on different nodes. **Fig 3** shows the different response times for different replication strategies in a five node cluster. There is a slight overhead for the higher replication factors but there is the benefit of not having a single point of failure for the specific items of data. Since this is set at the keyspace level different data can be replicated onto different numbers of nodes. The cluster can also be configured with different replication strategies. We just used the simple strategy which put the replicas on the next nodes in a ring but there are more sophisticated strategies which are aware of the datacentres and racks which will place the replicas on nodes that are less likely to fail together.    
 
 **Fig 3**
 ![](https://drive.google.com/open?id=0B65w2mgTevTpNDFVblJhOHZXOXM "Response times for different Cassandra replication factors in a 5 node cluster")
 
 The consistency level is configured on a per query basis and specifies how many nodes need to respond successfully for the operation to be considered successful. A selection of the consistency levels are described bellow:
  
  | Consistency level | Write effect | Read effect|
  | ----------------- | ------------ | ---------- |
  | ONE, TWO, THREE | The write must be written to the specified number of replicas | Returns the response from the specified number of closest replicas |
  | ANY | This allows the write to succeed even if none of the replica nodes are available. The operation will be stored on the coordinator node and replayed to the correct node when it comes online again | Not applicable for reads | 
  | QUORUM | Must be written to (replication factor/2)+1 replica nodes | Returns the response when (replication factor/2)+1 nodes have responded |
  | ALL | Must be written to all replica nodes | Returns the response once all replica nodes have responded |
  
  There are other consistency levels that are data centre aware which we did not look at as we just had a single cluster. The less strict the consistency level is the higher the availability of the system will be but there will be a greater chance of stale data being read. For example a write operation with consistency of ANY can succeed even if none of the replica nodes are available but it will not be possible to read that data until the nodes are available again. The higher consistency levels provide a higher likelihood of the data being up to date but they have lower availability. For example with the consistency of ALL if any of the replica nodes do not respond then the operation will not succeed. **Fig 4** show the performance of the different consitency levels in a 5 node cluster with a replication factor of 3. As might be expected the general trend is that the higher consistency levels take slightly longer to be processed.   
 
 **Fig 4**
 ![](https://drive.google.com/open?id=0B65w2mgTevTpU181ZmFwWFhVQXc "Response times for different Cassandra consistency levels")

## Configuring MariaDB Cluster
### Galera cluster
MariaDB does not by itself support clustering but the API for[Galera cluster](http://galeracluster.com/products/)is included with MariaDB so this was the first approach we tried for creating a MariaDB cluster. Galera cluster is a multi-master system which offers synchronous replication across all the nodes so each one contains the same data. Galera cluster is not directly equivalent to a Cassandra cluster; the aim of Cassandra cluster is to provide horizontal scaling whereas the aim of the Galera cluster is to reduce downtime of the database without compromising consistency of the data. Since all of the nodes in a Galera cluster have all the data and can handle requests, we implemented simple round robin load balancing in our tests to fire requests to all of the nodes. **Fig 5** shows what happens to response times as more nodes are added to both a Cassandra cluster and a Galera cluster.

**Fig 5**
![](https://drive.google.com/open?id=0B65w2mgTevTpWThKeGIyUjNHdHM "Response times for different cluster sizes for Cassandra and Galera")

As more nodes were added the response times of Cassandra clearly decreased whereas the MariaDB write times increased. The MariaDB read times stayed roughly the same as they are effectively still returning the data off the node which receives the request, all the extra work to replicate the data across the nodes is handled at write time. Whilst Galera cluster might theoretically offer some[mild scaling](https://www.percona.com/blog/2014/11/17/typical-misconceptions-on-galera-for-mysql/)due to there being multiple nodes to handle requests it is not what we observed here and certainly not the primary function of a Galera cluster. Additionally as more nodes are added there is a greater chance of deadlocks where two nodes try to edit the same data at the same time, this is shown in **Fig 6**.

**Fig 6**
![](https://drive.google.com/open?id=0B65w2mgTevTpVmFNcExnUnNjWTg "Number of deadlocks in a Galera cluster as more nodes were added")

Each of these failed requests would need to be retried by the application for them to be applied. However with Cassandra if two conflicting writes occurred concurrently both would have succeeded but Cassandra will consider the one with the most recent timestamp as the correct version. So with MariaDB it is immediately clear when data has failed to be updated whereas in Cassandra there is a small chance that the update will not be applied.

### Network Database (NDB) cluster

[NDB](https://dev.mysql.com/doc/refman/5.7/en/mysql-cluster.html)is not currently supported by MariaDB so in order to investigate this we had to use MySQL instead. NDB works by having the data sharded and stored across various NoSQL data nodes, this is then accessed through SQL nodes. Each SQL node can access all the data across the different data nodes. The data is replicated synchronously across multiple nodes and can also replicate asynchronously between data centres. The data is partitioned across all the data nodes based upon the table and primary key so the SQL nodes can consistently calculate which data nodes the required data will be on.
<!-- Add in what we actually tried -->

<!--Caching maybe -->

## Conclusions

## Stuff for future?

* NewSQL databases?
* memcached + Galera? cf [This article](https://gigaom.com/2011/12/06/facebook-shares-some-secrets-on-making-mysql-scale/)