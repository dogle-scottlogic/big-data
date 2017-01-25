output "cassandra_id" {
  value = "${module.cassandra.id}"
}
output "mariadb_id" {
  value = "${module.mariadb.id}"
}
output "cassandra_public_ips" {
  value = "${module.cassandra.public_ips}"
}
output "mariadb_public_ips" {
  value = "${module.mariadb.public_ips}"
}
