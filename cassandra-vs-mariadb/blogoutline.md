# Cassandra vs MariaDB: Scaling

## Introduction

* Blather about advantages / disadvantages of NoSQL vs SQL
    * SQL - ACID guarantees
    * NoSQL - designed from start for horizontal scaling

## Quick recap of single node comparison

## CAP theorem - trade offs

### Cassandra 

Replication factor stuff.

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
