output "cassandra_public_ips" {
  value = "${module.cassandra.public_ips}"
}
output "mariadb_public_ips" {
  value = "${module.mariadb.public_ips}"
}
