output "cassandra_public_ips" {
  value = "${module.cassandra.public_ips}"
}
output "mariadb_public_ips" {
  value = "${module.mariadb.public_ips}"
}
output "test-client_public_ip" {
  value = "${module.test-client.public_ip}"
}
