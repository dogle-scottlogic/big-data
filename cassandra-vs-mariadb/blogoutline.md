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

### Replication Factor
<!-- Think this needs to come first because the consistency level section references the replication factor -->
The replication factor is another important configuration to be considered.
This is set when the keyspace is created and it defines how many copies of the data are created on different nodes.
Increasing the replication factor naturally increases the availability of the data.
**Fig 3** shows the different response times for different replication strategies in a five node cluster.
There is a slight overhead for the higher replication factors.
Since this is set at the keyspace level, we can opt for greater replication of more important data.
Two kinds of replication strategy are available in Cassandra.
We use the `SimpleStrategy` which is rack and data centre unaware. In this strategy, replicas are stored on the next nodes on the ring.
Alternatively there is the `NetworkTopologyStrategy` in which replicas are stored in distinct racks.
 
**Fig 3**
![](https://drive.google.com/open?id=0B65w2mgTevTpYVV3bWl4VXFHY00 "Response times for different Cassandra replication factors in a 5 node cluster")

### Consistency Level  
<!-- Also talk about what happens when nodes disagree on the value -->

The consistency level is configured on a per query basis and specifies how many nodes need to respond successfully for the operation to be considered successful. 
This flexibility enables individual operations to determine how important getting the most up to date data is. 
A selection of the consistency levels are described below:
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
**Fig 4** show the performance of the different consistency levels in a 5 node cluster with a replication factor of 3. As expected, higher consistency levels lead to higher latency. 

**Fig 4**
![](https://drive.google.com/open?id=0B65w2mgTevTpcE9TcDJzS2I3elk "Response times for different Cassandra consistency levels")
<!-- Got up to here -->
## Clustering: the SQL
### Galera cluster

Though MariaDB does not by itself support clustering, the API for[Galera cluster](http://galeracluster.com/products/)is included with MariaDB.
Therefore this is the first approach we try for creating a SQL cluster. 
Galera cluster is a multi-master system which offers synchronous replication across all the nodes so that each one contains the same data. 
Galera cluster is not directly equivalent to a Cassandra cluster; the aim of Cassandra cluster is to provide horizontal scaling.
Conversely, Galera only provides mild read scaling but negative write scaling - 
the aim of Galera cluster is to improve availability without compromising the consistency of the data. 
<!-- Some chat about setting it up? -->

## Comparing Galera and Cassandra

<!-- Need some chat about Cassandra settings -->
Since all of the nodes in a Galera cluster have all the data and can handle requests, we use a simple round robin load balancing strategy in our tests. 
**Fig 5** shows what happens to response times as more nodes are added to Cassandra and Galera clusters.

**Fig 5**
![](https://drive.google.com/open?id=0B65w2mgTevTpbTg2bHN2ZXdoMG8 "Response times for different cluster sizes for Cassandra and Galera")

As more nodes are added the response times of Cassandra decreases whereas the MariaDB write times increases.
For Cassandra the update status is noticeably slower than the similar update operation.
This is because this query uses the `IF EXISTS` condition to ensure the item is present before it is updated.
By default Cassandra creates a new row with the updated data without any check to see if the row already exists.
The updated data is then combined with the previous record on read.
Adding this extra check adds a significant overhead to the operation.
The MariaDB read times stay roughly the same as they are effectively still returning the data off the node which receives the request - all the extra work to replicate the data across the nodes is handled at write time.

This comparison isn't particularly informative as we are comparing apples with oranges and top hats with fezes.
Galera cluster replicates data to every node, so as we add nodes we are increasing the cost of storing that data.
Conversely, Cassandra is configured to store only two copies of the data, irrespective of the number of ndoes.  
We can achieve a fair, yet artifical comparison by requiring Cassandra to replicate data to every node (up to a maximum of four nodes). **Fig 6** shows the resulting response times.

**Fig 6**
![](https://drive.google.com/open?id=0B65w2mgTevTpMHQ1R1ZtQi01aWM "Response times for a Cassandra cluster with a replication factor equal to the nodes compared with a Galera cluster")

With this configuration the performance difference is less striking, though Cassandra still scales better. 
Cassandra's performance remains fairly constant whilst Galera response times increase with the number of nodes.

Whilst Galera cluster might theoretically offer some[mild scaling](https://www.percona.com/blog/2014/11/17/typical-misconceptions-on-galera-for-mysql/)due to there being multiple nodes to handle requests it is not what we observed here and certainly not the primary function of a Galera cluster. 
Additionally as more nodes are added there is a greater chance of deadlocks where two nodes try to edit the same data at the same time (see **Fig 7**).

**Fig 7**
![](https://drive.google.com/open?id=0B65w2mgTevTpOHBLYmR1YU8wVnM "Number of deadlocks in a Galera cluster as more nodes were added")

Each of these failed requests would need to be retried by the application for them to be applied.
However with Cassandra if two conflicting writes occurred concurrently both would have succeeded.
On the next read, Cassandra will consider the one with the most recent timestamp as the correct version. 
So with Galera it is immediately clear when data has failed to be updated whereas in Cassandra there is a chance that the update will silently not be applied.

## Network Database (NDB): the Return of (SQL) Scaling

For a SQL clustering mechanism which is more comparable to Cassandra we look at [NDB](https://dev.mysql.com/doc/refman/5.7/en/mysql-cluster.html). 
Unfortunately this is not currently supported by MariaDB so in order to investigate this we use [MySQL](https://www.mysql.com/) instead. 
NDB consists of SQL frontend nodes, whose data is stored on NoSQL backend data nodes.
Cluster configuration is controlled by management nodes.
Scaling is achieved by automatically sharding the data across the NoSQL nodes.
The data nodes can then be replicated up to four times to protect against data loss.
Therefore the number of data nodes is equal to the desired number of fragments multipled by the number of replicas.

Though NDB provides an API to store data directly on the NoSQL data nodes, we do not use this - all our requests go via the SQL nodes.

Since an NDB cluster has both SQL nodes and NoSQL nodes which can be varied independently we look at both of these to see how they affect performance. 
However, we find that, for our small test client, varying the number of SQL nodes makes no noticeable impact on performance.

Unexpectedly, when we vary the number of data nodes, we see that the performance *degrades* with the number of nodes.
In seeking simple optimisations we make two changes to the configuration.
1. We double the connection pool on the SQL nodes from one to two, in line with the guideline of twice the number of cores on the node. This connection pool controls the maximum number of simultaneous connections to the data nodes.
1. The default partitioning for NDB is based on the primary key. For our data, this means operations affecting a single order could hit multiple data nodes. To fix this we alter the partitioning of the order items table to be by the order ID.
**Fig 8** shows the difference in response times these changes made.

**Fig 8**
![](https://drive.google.com/open?id=0B65w2mgTevTpQkd2NVdTTUd1R2M "NDB cluster response times before and after optimisation")

Having made these optimisations we then compare NDB to Cassandra.
**Fig 9** shows the response times for the two clusters.

**Fig 9**
![](https://drive.google.com/open?id=0B65w2mgTevTpVksyRDhfYlRuVEk "NDB and Cassandra cluster response times")

As can be seen in this graph the performance of the two clusters are reasonably similar but the NBD response times do look to be increasing as more nodes are added.

In this comparison we increased the number of data nodes by increasing the fragments.
Alternatively the number of replicas can be increased instead.
**Fig 10** shows how this affects response times.
         
**Fig 10**
![](https://drive.google.com/open?id=0B65w2mgTevTpLWZEWS0yRGxPX0U "Response times as more replicas are added")

As might be expected, like Cassandra the response times increase as more replicas are stored but for NDB the read times remain fairly consistently low.

## Conclusions

Cassandra seems to scale well out of the box.
It is highly flexible - the replication factor can be set on a per-keyspace basis.
Furthermore, the consistency level is set by the client in the query, allowing the balance between availability and consistency to be tuned differently for different types of data and different client needs.
Galera cluster offers a robust multi-master cluster that will replicate your data across nodes but it is not designed to provide horizontal scaling.
NDB provides a way to scale a SQL database horizontally, though without the flexibility of Cassandra.

A few thing need to be kept in mind whilst looking at any conclusions about performance.
1. We tested against small clusters with a small test client, so it is not clear how these results extrapolate to more realistic production scenarios.
1. We restricted our VMs to those available through the free tier of AWS, the resources of which are very limited. They only had single processors with 1GB of RAM. Further, there seemed to be contention for resources at the host level - the performance would sometimes slow dramatically.
1. Crucially, however, the differences in response times we observed were tiny; even the slowest data point was less than 15ms.

## Future Work

The queries we use in these tests are fairly simple, mostly using primary keys.
It would be interesting to see how we could make our analysis more realistic.
Firstly we could investigate how the different databases perform with more complex operations, ideally using real world data.
We would expect different use cases to require particular data models, and this would drive the choice of database technology. 
It'd be worth investigating this through a series of case studies.
 
We could also extend our analysis so we are not just looking at average response times. We could look specifically at the slow queries, the VM's resources as the tests run or the failed requests.

In the real world, nodes fail and so database resilience is an important characteristic.
Netflix's [Chaos Monkey](https://github.com/Netflix/SimianArmy) might be a useful tool for investigating this.

In reality, a large system is going to span multiple data centres.
Future testing should use more realistic deployment topologies and sizes.
Ideally, we would take inspiration from how large companies setup their databases in the real world.

Our investigations on scaling SQL were constrained by our starting point of using MariaDB.
In reality, there are a class of [NewSQL](https://en.wikipedia.org/wiki/NewSQL) scalable databases  to choose from.
