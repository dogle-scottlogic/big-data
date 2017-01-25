output "cluster_3_cassandra_ips" {
  value = "${module.cluster_3.cassandra_public_ips}"
}
output "cluster_3_mariadb_ips" {
  value = "${module.cluster_3.mariadb_public_ips}"
}
