---
author: jwhite
contributors: 
 - dketley
title: Cassandra vs. MariaDB: Scaling
summary: Comparison of Horizontal Scaling of Cassandra and MariaDB
layout: post
---
# Cassandra vs MariaDB: Scaling

## Keeping Ahead of the Demand
<!--Add real link when previous blog post is up -->
Welcome [back](http://blog.scottlogic.com/) to the world's most popular hat shop, now under new management!
Our database remains creaky, but our quest for a modern, scalable solution continues.
The previous management, Dave and Laurie, [compared](http://blog.scottlogic.com/) running our database on single node instances of [Cassandra](http://cassandra.apache.org/) and [MariaDB](https://mariadb.org/).
In that scenario, the unoptimised performance of the two databases is fairly equivalent.
But, what shall we do as our hat shop grows more popular? 
How do we scale out our new database, or is our shop's size doomed to be... *capped*?
[Previously](http://blog.scottlogic.com) Dave and Laurie claimed that it's a foregone conclusion that Cassandra should outscale a traditional relational database.
In this post, we test that assumption.
Do we have to sacrifice the [ACID](https://en.wikipedia.org/wiki/ACID) properties of SQL databases?
What other options are available to us?
And how well does Cassandra scale in reality?
<!-- Need some spiel about Galera cluster -->

## We're Gonna Need a Bigger Host

For assessing the performance of the different clusters we make use of the database event generation and logging mechanism created by Dave and Laurie. 
The Java client for Cassandra by default supports load balancing so it is relatively straightforward to start up multiple docker containers on a development PC and run some tests.
It quickly becomes apparent that this approach is unrealistic, **Fig1** shows a slow down in write response times as the number of nodes increases and **Fig2** shows a drastic slow down in read response times. 
As more nodes are started the more they are competing for the same limited hardware resources of the host. 
In order to make any kind of realistic assessment of the scaling we need to run the nodes on separate machines.   

<!--Add real links to images-->
**Fig 1**
![](https://drive.google.com/open?id=0B65w2mgTevTpS1NZTU1MQjZZR0U "Write operation response times for multiple Cassandra nodes running on the same machine")

**Fig 2**
![](https://drive.google.com/open?id=0B65w2mgTevTpSmJfdEEwdVcwbTA "Read response times for multiple Cassandra nodes running on the same machine")

Since we don't have multiple unused PCs to hand, we opt to build the clusters on [Amazon Web Services](https://aws.amazon.com/).
AWS is well established and numerous tools integrate with its EC2 VM service.
We use [Vagrant](https://www.vagrantup.com/) to automate spinning up and provisioning our VMs.
In addition to the cluster hosts, we now spin up a separate test client host in the same region and subnet (previously we were running the tests locally) thus minimising the effect of network latency on our results.

<!-- I don't think this is necessary
## Tradeoffs Whilst Selecting Scaling Approaches
### ACID and BASE
When selecting the database to use it is necessary to consider the importance or not of guaranteeing ACID properties of the database operations. These are:
* Atomicity - If part of the transaction fails then none of it is applied and the database is left unchanged.
* Consistency - The transaction will leave the database in a valid state as defined by rules such as constraints.
* Isolation - Concurrent transactions do not interfere with each other. So the result wil be the same if they are executed concurrently to if they were executed sequentially.
* Durability - Once the transaction is committed the change will remain. i.e. the change is stored in non-volatile memory .

SQL databases like MariaDB guarantee these but Cassandra does not. Cassandra has limited atomicity and isolation, within a partition. Cassandra does not have the same concept of constraints as a SQL database so it does not offer any consistency guarantees. It does guarantee durability though. Instead of ACID Cassandra offers BASE (Basically Available, Soft state, Eventual consistency). This means that after an update the data returned may be different from different nodes but if no further updates are made eventually all reads will return the most recent data.
-->

## Clustering and Network Outages - 'hat's a Problem...

Scaling out a database into a cluster introduces extra complexity.
As soon as you have multiple nodes there is the possibility that communication between the nodes will be interrupted. 
In this scenario the CAP theorem states it becomes impossible to guarantee more that two of the following:
 * Consistency - valid reads return the most recent write data
 * Availability - valid requests return non-error responses
 * Partition tolerance - the database continues to operate when communication between the nodes is interrupted.

For our clusters this gives us the following tradeoffs
 * Galera cluster emphasies data safety and consistency. Its configuration has a tradeoff between cluster availability and partition tolerance. Low partition tolerance implies longer cluster outages in the event of node failure.
 * In contast, Cassandra generally emphasises availability and partition (AP) tolerance over consistency, although this can be tuned. 
 <!-- Greater subtlety here, e.g. see https://www.infoq.com/articles/cap-twelve-years-later-how-the-rules-have-changed -->
 <!-- Maybe add a discussion on ACID vs BASE -->
 <!-- Maybe talk about Cassandra guarantees? eventual consistency -->

## 'APpy Times - Configuring Cassandra Cluster 

As previously mentioned, in terms of the CAP theorem Cassandra values **A**vailability and **P**artition tolerance. 
In practice this gives us a tradeoff between **C**onsistency and latency. 
It *is* possible to get strong consistency in Cassandra... if you're willing to accept high latency.
This tradeoff is controlled via the *consistency level* parameter.

The consistency level is configured on a per query basis and specifies how many nodes need to respond successfully for the operation to be considered successful. 
This flexibility enables individual operations to determine how important getting the most up to date data is. 
A selection of the consistency levels are described below:
<!-- First need some chat about replicas and replication factors -->
<!-- Also talk about what happens when nodes disagree on the value -->
  
| Consistency level | Write effect | Read effect|
| ----------------- | ------------ | ---------- |
| `ONE`, `TWO`, `THREE`   | The write must be successfully written to the specified number of replicas | Returns the response from the specified number of closest replicas |
| `ANY`               | This allows the write to succeed even if none of the replica nodes are available. The operation will be stored on the coordinator node and replayed to the correct node when it comes online again | Not applicable for reads | 
| `QUORUM`            | Must be successfully written to (replication factor/2)+1 replica nodes | Returns the response when (replication factor/2)+1 nodes have responded |
| `ALL`               | Must be written to all replica nodes | Returns the response once all replica nodes have responded |
  
There are other consistency levels that are data centre aware which we do not look at as we our database is restricted to a single data centre. 
The less strict the consistency level is the higher the availability of the system will be, and the lower the latency, but there will be a greater chance of stale data being read. 
For example a write operation with consistency of `ANY` can succeed even if none of the replica nodes are available but it will not be possible to read that data until the nodes are available again. 
<!-- This is confusing - we need some chat about how writes are only written on READ -->
Conversely, higher consistency levels give a higher likelihood of the data being up to date at a cost of lower availability and higher latency. 
The extreme example of `ALL` is instructive. 
The latency of the query will be determined by the slowest node in the cluster. 
If *any* nodes are down then the operation will not succeed.
**Fig 3** show the performance of the different consistency levels in a 5 node cluster with a replication factor of 3. As expected, higher consistency levels lead to higher latency. 
 <!-- Got up to here -->
**Fig 3**
![](https://drive.google.com/open?id=0B65w2mgTevTpcE9TcDJzS2I3elk "Response times for different Cassandra consistency levels")

The replication factor is another important configuration to be considered.
This is set when the keyspace is created and it defines how many copies of the data are created on different nodes and as such increasing this increases the availability of the data.
**Fig 4** shows the different response times for different replication strategies in a five node cluster.
There is a slight overhead for the higher replication factors.
Since this is set at the keyspace level different data can be replicated onto different numbers of nodes.
The cluster can also be configured with different replication strategies. We just used the simple strategy which put the replicas on the next nodes in a ring but there are more sophisticated strategies which are aware of the datacentres and racks which will place the replicas on nodes that are less likely to fail together.    
 
**Fig 4**
![](https://drive.google.com/open?id=0B65w2mgTevTpYVV3bWl4VXFHY00 "Response times for different Cassandra replication factors in a 5 node cluster")
 
## Configuring MariaDB Cluster
### Galera cluster
MariaDB does not by itself support clustering but the API for[Galera cluster](http://galeracluster.com/products/)is included with MariaDB so this was the first approach we tried for creating a MariaDB cluster. Galera cluster is a multi-master system which offers synchronous replication across all the nodes so each one contains the same data. Galera cluster is not directly equivalent to a Cassandra cluster; the aim of Cassandra cluster is to provide horizontal scaling whereas the aim of the Galera cluster is to improve availability without compromising the consistency of the data. Since all of the nodes in a Galera cluster have all the data and can handle requests, we implemented simple round robin load balancing in our tests to fire requests to all of the nodes. **Fig 5** shows what happens to response times as more nodes are added to both a Cassandra cluster and a Galera cluster.

**Fig 5**
![](https://drive.google.com/open?id=0B65w2mgTevTpbTg2bHN2ZXdoMG8 "Response times for different cluster sizes for Cassandra and Galera")

As more nodes were added the response times of Cassandra clearly decreased whereas the MariaDB write times increased.
For Cassandra the update status is noticeably slower than the similar update operation.
This is because this query uses the `IF EXISTS` condition to ensure the item is present before it is updated.
By default Cassandra would just create a new row with the updated data without any check to see if the row already existed.
The updated data would be combined with the previous record when the row is read.
Adding this extra check adds a significant overhead to the operation.
The MariaDB read times stayed roughly the same as they are effectively still returning the data off the node which receives the request, all the extra work to replicate the data across the nodes is handled at write time.
However this is not an entirely fair comparison as the Galera cluster replicates the data to all nodes but Cassandra only has 2 copies.
**Fig 6** shows what happens when we configure Cassandra to replicate across all nodes.

**Fig 6**
![](https://drive.google.com/open?id=0B65w2mgTevTpMHQ1R1ZtQi01aWM "Response times for a Cassandra cluster with a replication factor equal to the nodes compared with a Galera cluster")

With this configuration the performance difference is less striking but but Cassandra's performance is remaining fairly constant whilst Galera response times are getting longer as more nodes are added.
This comparison is somewhat artificial as we are configuring Cassandra to behave like Galera but it is not likely to be how you would actually configure Cassandra.

Whilst Galera cluster might theoretically offer some[mild scaling](https://www.percona.com/blog/2014/11/17/typical-misconceptions-on-galera-for-mysql/)due to there being multiple nodes to handle requests it is not what we observed here and certainly not the primary function of a Galera cluster. Additionally as more nodes are added there is a greater chance of deadlocks where two nodes try to edit the same data at the same time, this is shown in **Fig 7**.

**Fig 7**
![](https://drive.google.com/open?id=0B65w2mgTevTpOHBLYmR1YU8wVnM "Number of deadlocks in a Galera cluster as more nodes were added")

Each of these failed requests would need to be retried by the application for them to be applied. However with Cassandra if two conflicting writes occurred concurrently both would have succeeded but Cassandra will consider the one with the most recent timestamp as the correct version. So with MariaDB it is immediately clear when data has failed to be updated whereas in Cassandra there is a small chance that the update will not be applied.

### Network Database (NDB) Cluster

For a SQL clustering mechanism which is more comparable to Cassandra we looked at [NDB](https://dev.mysql.com/doc/refman/5.7/en/mysql-cluster.html). Unfortunately this is not currently supported by MariaDB so in order to investigate this we had to use MySQL instead. NDB works by having the data partitioned into shards and stored across various NoSQL data nodes, this is then accessed through SQL nodes. Each SQL node can access all the data across the different data nodes. The data is replicated synchronously across multiple nodes. The data is partitioned across the data nodes based upon the table and primary key so the SQL nodes can consistently calculate which data nodes the required data will be on. NDB does not have the same flexibility as Cassandra, the number of replicas and the number of nodes the data is split across is configured for the cluster as a whole and the number of nodes must equal the number of replicas times the number of fragments.

Since an NDB cluster has both SQL nodes and NoSQL nodes which can be varied independently we looked at both of these to see how they affected the performance. Firstly we looked at the the SQL nodes and found for our relatively small clusters that adding more SQL nodes made no noticeable impact on performance.

When we initially looked at the NoSQL nodes we saw that their performance was relatively slow as we added more nodes.
We then looked if there were any simple optimisations we could make to the configuration.
We made two changes, firstly we increased the connection pool on the SQL nodes so they could communicate with multiple data nodes at once.
Secondly the default partitioning for NDB is based on the primary key of the row so this means for the data we were using (orders and order lines) the items we were performing the operations on could be spread across multiple nodes.
We changed the partitioning of the order items to be by the order id rather than the item id meaning all the data we were using in any single transaction should be on the same NoSQL node.
**Fig 8** shows the difference in response times these changes made.

**Fig 8**
![](https://drive.google.com/open?id=0B65w2mgTevTpQkd2NVdTTUd1R2M "NDB cluster response times before and after optimisation")

Having made these optimisations we then compared NDB to Cassandra.
**Fig 9** shows the response times for the two clusters.

**Fig 9**
![](https://drive.google.com/open?id=0B65w2mgTevTpVksyRDhfYlRuVEk "NDB and Cassandra cluster response times")

As can be seen in this graph the performance of the two clusters are reasonably similar but the NBD response times do look to be increasing as more nodes are added.

In this comparison we increased the number of data nodes by increasing the fragments.
Alternatively the number of replicas can be increased instead.
**Fig 10** shows how this affects response times.
         
**Fig 10**
![](https://drive.google.com/open?id=0B65w2mgTevTpLWZEWS0yRGxPX0U "Response times as more replicas are added")

As might be expected like Cassandra the response times increase as more replicas are stored but for NDB the read times remain fairly consistently low.

## Conclusions

From our investigations have seen that Cassandra is very easy to horizontally scale and it is highly configurable through replication factor and consistency level to get the balance between availability and consistency for the different items of data.
Galera cluster offers a robust multi-master cluster that will replicate your data across nodes but it is not designed to provide horizontal scaling.
NDB does offer a way to scale an SQL database horizontally although it is not as configurable as Cassandra.

A few thing need to be kept in mind whilst looking at any conclusions about performance.
Firstly these are still rather small clusters and it is difficult to extrapolate how this will perform with hundreds of nodes.
Secondly we were using the free tier of AWS so the virtual machines were very limited.
They only had single virtual processors with 1GB of ram and we observed the performance would occasionally slow down dramatically.
This was particularly restrictive on the test VM as it mean we could not have multiple threads firing requests to the database.
Crucially overall the differences we observed were tiny; even the slowest data point was less than 15ms.

## Thoughts for Future Investigations
The queries we have used in the tests have been fairly simple, most using primary keys.
It would be interesting to see how we could make our analysis more realistic.
Firstly we could investigate how the different databases perform with more complex operations, ideally it would be nice if we could use some real world data to know how representative our test data is.
We would expect different data models to perform very differently for different databases, we could see how correct these expectations are.
For example it would be interesting how NDB performs when it has to fetch data across multiple nodes or even scan the whole table.
 
We could also extend our analysis so we are not just looking at average response times. We could look specifically at the slow queries, the VM's resources as the tests run, the failed requests or the impact on performance when nodes are added and removed (Netflix's [Chaos Monkey](https://github.com/Netflix/SimianArmy) might be worth looking into for this). 

In reality for a large system is going to have multiple clusters in different data centers.
We could investigate how the observed trends carry on when extra clusters are added.

We only did very superficial optimisation, we could spend more time looking at how to best configure the databases.
Perhaps looking at how large companies use these databases in reality could help. e.g. look at how Netflix use Cassandra and Facebook use MySQL.
 
We have so far looked at a limited selection of databases.
We could consider other databases such as NewSQL databases.
