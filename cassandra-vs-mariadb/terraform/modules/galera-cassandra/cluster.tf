variable "num_nodes" {}
variable "cluster_name" {}
variable "security_group_name" {}
variable "key_name" {}
variable "private_key" {}
variable "cassandra_ami" {}
variable "mariadb_ami" {}
variable "test-client_ami" {}
variable "mariadb_password" {
  description = "Root password for mariadb"
  default     = "myfirstpassword"
}

module "cassandra" {
  source              = "../resources/cluster/cassandra"
  ami                 = "${var.cassandra_ami}"
  num_nodes           = "${var.num_nodes}"
  cluster_name        = "${var.cluster_name}"
  key_name            = "${var.key_name}"
  security_group_name = "${var.security_group_name}"
  private_key         = "${var.private_key}"
}

module "mariadb" {
  source              = "../resources/cluster/galera"
  ami                 = "${var.mariadb_ami}"
  mariadb_password    = "${var.mariadb_password}"
  num_nodes           = "${var.num_nodes}"
  cluster_name        = "${var.cluster_name}"
  key_name            = "${var.key_name}"
  security_group_name = "${var.security_group_name}"
  private_key         = "${var.private_key}"
  test_client_ip      = "${module.test-client.private_ip}"
}

module "test-client" {
  source               = "../resources/cluster/test-client"
  ami                  = "${var.test-client_ami}"
  key_name             = "${var.key_name}"
  mariadb_password     = "${var.mariadb_password}"
  security_group_name  = "${var.security_group_name}"
  private_key          = "${var.private_key}"
  cluster_name         = "${var.cluster_name}"
  cassandra_primary_ip = "${module.cassandra.primary_ip}"
  mariadb_ips          = "${join(",", module.mariadb.private_ips)}"
}

output "cassandra_public_ips" {
  value = "${module.cassandra.public_ips}"
}
output "mariadb_public_ips" {
  value = "${module.mariadb.public_ips}"
}
output "test-client_public_ip" {
  value = "${module.test-client.public_ip}"
}
