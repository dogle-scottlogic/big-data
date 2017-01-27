variable "num_ndb_replicas" {}
variable "num_ndb_fragments" {}
variable "num_sql_nodes" {}
variable "cluster_name" {}
variable "security_group_name" {}
variable "key_name" {}
variable "private_key" {}
variable "ndb_node_ami" {}
variable "sql_node_ami" {}

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
