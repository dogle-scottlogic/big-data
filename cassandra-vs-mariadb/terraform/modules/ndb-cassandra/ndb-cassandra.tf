variable "num_ndb_replicas" {}
variable "num_ndb_fragments" {}
variable "num_sql_nodes" {}
variable "cluster_name" {}
variable "security_group_name" {}
variable "key_name" {}
variable "private_key" {}
variable "ndb_node_ami" {}
variable "sql_node_ami" {}
variable "cassandra_ami" {}
variable "test-client_ami" {}
variable "num_cassandra_nodes" {}
variable "sql_password" {
  default = "myfirstpassword"
}

module "cassandra" {
  source              = "../resources/cluster/cassandra"
  ami                 = "${var.cassandra_ami}"
  num_nodes           = "${var.num_cassandra_nodes}"
  cluster_name        = "${var.cluster_name}"
  key_name            = "${var.key_name}"
  security_group_name = "${var.security_group_name}"
  private_key         = "${var.private_key}"
}

module "ndb" {
  source              = "../resources/cluster/ndb"
  num_ndb_replicas    = "${var.num_ndb_replicas}"
  num_ndb_fragments   = "${var.num_ndb_fragments}"
  num_sql_nodes       = "${var.num_sql_nodes}"
  cluster_name        = "${var.cluster_name}"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  ndb_node_ami        = "${var.ndb_node_ami}"
  sql_node_ami        = "${var.sql_node_ami}"
  sql_password        = "${var.sql_password}"
  test-client_ip      = "${module.test-client.private_ip}"
}

module "test-client" {
  source               = "../resources/cluster/test-client"
  ami                  = "${var.test-client_ami}"
  key_name             = "${var.key_name}"
  mariadb_password     = "${var.sql_password}"
  security_group_name  = "${var.security_group_name}"
  private_key          = "${var.private_key}"
  cluster_name         = "${var.cluster_name}"
  cassandra_primary_ip = "${module.cassandra.primary_ip}"
  mariadb_ips          = "${join(",", module.ndb.sql_private_ips)}"
}

output "ndb_mgmt_public_ips" {
  value = "${module.ndb.mgmt_public_ips}"
}
output "ndb_data_public_ips" {
  value = "${module.ndb.data_public_ips}"
}
output "ndb_sql_public_ips" {
  value = "${module.ndb.sql_public_ips}"
}
output "ndb_cassandra_public_ips" {
  value = "${module.cassandra.public_ips}"
}
output "ndb_test_client_public_ip" {
  value  ="${module.test-client.public_ip}"
}
