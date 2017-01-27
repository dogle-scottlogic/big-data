# Cassandra vs MariaDB: Scaling

## Keeping Ahead of the Demand
<!--Add real link when previous blog post is up -->
Before we start we must tip our hats to Dave and Laurie for their [previous blog post](http://blog.scottlogic.com/) where they evaluated single node deployments of [Cassandra](http://cassandra.apache.org/) and [MariaDB](https://mariadb.org/) using the example of a theoretical hat store. They found that in a single node set up the databases were fairly equivalent in therms of performance. In this blog post we have looked at what happens when a single node instance is no longer able to keep up with the demand on the database and you want to horizontally scale the database. In the previous post they said that it was a foregone conclusion that Cassandra would scale horizontally better so we have investigated this assumption further to see how well cassandra scales horizontally and what options are available to try and scale a MariaDB.

## Assessing Performance of Node Clusters

To assess the performance of the different clusters we made use of the database event generation and logging mechanism created by Dave and Laurie. The Java client for Cassandra by default supports load balancing so it was relatively straight forward to start up multiple docker containers and run some tests. However it quickly became apparent that this approach was unrealistic, **Fig1** shows a slow down in write response times as the number of nodes increases and **Fig2** shows a drastic slow down in read response times. The more nodes started the more they were competing for the same limited hardware resources of the machine, in order to make an kind of realistic assessment of the scaling we needed to run the nodes on multiple machines.   

**Fig 1**
![](https://drive.google.com/a/scottlogic.co.uk/file/d/0B65w2mgTevTpN1VjUkEyWXVDaGM/view?usp=sharing "Write operation response times for multiple Cassandra nodes running on the same machine")

**Fig 2**
![](https://drive.google.com/open?id=0B65w2mgTevTpeFZlQWpYdmJ4a0k "Read response times for multiple Cassandra nodes running on the same machine")

The decision was made to use Amazon Web Services as this enabled us to quickly and easily create and destroy different virtual machines with new nodes to run our different tests. Since we had moved the database instances to remote machines the possibility that network latency would muddy the results became a concern so we created an additional VM that would run the tests thereby keeping network latency to a minimum.

## Configuring Cassandra Cluster 

Cassandra has two important configuration options to consider whilst creating your cluster, the replication factor and the consistency level.

The replication factor is set when the keyspace is created and it defines how many copies of the data are created on different nodes. **Fig 3** shows the different response times for different replication strategies in a five node cluster. There is a slight overhead for the higher replication factors but there is the benefit of not having a single point of failure for the specific items of data. Since this is set at the keyspace level different data can be replicated onto different numbers of nodes. The cluster can also be configured with different replication strategies. We just used the simple strategy which put the replicas on the next nodes in a ring but there are more sophisticated strategies which are aware of the datacentres and racks which place the replicas on nodes that are less likely to fail together.    
 
 **Fig 3**
 ![](https://drive.google.com/open?id=0B65w2mgTevTpNDFVblJhOHZXOXM "Response times for different Cassandra replication factors in a 5 node cluster")
 
 The consistency level is configured on a per query basis and specifies how many nodes need to respond successfully for the operation to be considered successful. A selection of the consistency levels are described bellow:
  
  | Consistency level | Write effect | Read effect|
  |---|---|---|
  |ONE, TWO, THREE| The  | |
 
 **Fig 4**
 ![](https://drive.google.com/open?id=0B65w2mgTevTpU181ZmFwWFhVQXc "Response times for different Cassandra consistency levels")

## Configuring MariaDB Cluster

   
## Tradeoffs Whilst Selecting Approaches
### CAP Theorem

As soon as you have multiple nodes there is the possibility that communication between the nodes will be interrupted, in this scenario the CAP theorem states it becomes impossible to guarantee more that two of the following:
 * Consistency - reads return the most recent write data or an error
 * Availability - request return non-error responses
 * Partition tolerance - the database continues to operate when communication between the nodes is interrupted.
 
This effectively means that when node communication is interrupted you can either guarantee that data will continue to be returned but it might be out of date or you can guarantee that any data returned is up to date but you might return an error if the up to date data is unavailable. The nature of your data and application will determine which of these options is appropriate.
### SQL  vs no SQL ACID

### MySQL

Key strategies
* Sharding
* Caching (memcached?)

#### Galera cluster

Master - master - every node identical.
[Mild scaling](https://www.percona.com/blog/2014/11/17/typical-misconceptions-on-galera-for-mysql/). Specifically
* Write sets can be applied in parallel on remote nodes
* Row-based replication. Replicated events can be faster than original write.

#### NDB cluster

["NDB Cluster integrates the standard MySQL server with an in-memory clustered storage engine called NDB (Network DataBase)"](https://dev.mysql.com/doc/refman/5.5/en/mysql-cluster-overview.html).

* SQL mysqld nodes run on top of ndbd data nodes.
* Each node can access all tables.

#### Stuff for future?

* NewSQL databases?
* memcached + Galera? cf [This article](https://gigaom.com/2011/12/06/facebook-shares-some-secrets-on-making-mysql-scale/)
