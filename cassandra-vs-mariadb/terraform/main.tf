// Inputs
variable "access_key" {}
variable "secret_key" {}

// Shouldn't need to override these inputs. If you do, use terraform.tfvars.
variable "public_key_path" {
  description = "Path to an SSH public key to be used for authentication"
  default = "~/.ssh/id_rsa.pub"
}
variable "private_key_path" {
  description = "Path to an SSH private key to be used for authentication"
  default = "~/.ssh/id_rsa"
}
variable "key_prefix" {
  description = "Desired prefix of AWS key pair"
  default = "dev"
}
variable "region" {
  description = "Default region"
  default = "eu-west-2"
}

provider "aws" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region     = "${var.region}"
}

resource "aws_key_pair" "auth" {
  key_name_prefix = "${var.key_prefix}"
  public_key      = "${file(var.public_key_path)}"
}

// Setup security group
module "security_group" {
  source      = "modules/security"
  subnet_cidr = "172.31.0.0/16"
}

// For creating AMIs to spin up clusters from
module "ami-nodes" {
  source              = "modules/ami"
  key_name            = "${aws_key_pair.auth.key_name}"
  security_group_name = "${module.security_group.name}"
  private_key         = "${file(var.private_key_path)}"
}

// Add clusters here

# module "cluster_3" {
#   source              = "modules/galera-cassandra"
#   num_nodes           = 3
#   cassandra_ami       = "${module.ami-nodes.cassandra_ami_id}"
#   mariadb_ami         = "${module.ami-nodes.mariadb_ami_id}"
#   test-client_ami     = "${module.ami-nodes.test-client_ami_id}"
#   mariadb_password    = "${module.ami-nodes.mariadb_password}"
#   cluster_name        = "threeNode-"
#   key_name            = "${aws_key_pair.auth.key_name}"
#   security_group_name = "${module.security_group.name}"
#   private_key         = "${file(var.private_key_path)}"
# }

# // Outputs. Accessible with 'terraform output'
# output "cluster_3_cassandra_ips" {
#   value = "${join(",", module.cluster_3.cassandra_public_ips)}"
# }
# output "cluster_3_mariadb_ips" {
#   value = "${join(",", module.cluster_3.mariadb_public_ips)}"
# }
# output "cluster_3_test-client_ip" {
#   value = "${module.cluster_3.test-client_public_ip}"
# }

module "ndb_cluster" {
  source              = "modules/ndb-cassandra"
  num_ndb_replicas    = 1
  num_ndb_fragments   = 5
  num_sql_nodes       = 2
  num_cassandra_nodes = 5
  cluster_name        = "ndb-cassandra-"
  security_group_name = "${module.security_group.name}"
  key_name            = "${aws_key_pair.auth.key_name}"
  private_key         = "${file(var.private_key_path)}"
  ndb_node_ami        = "${module.ami-nodes.ndb_ami_id}"
  sql_node_ami        = "${module.ami-nodes.ndb_sql_ami_id}"
  cassandra_ami       = "${module.ami-nodes.cassandra_ami_id}"
  test-client_ami     = "${module.ami-nodes.test-client_ami_id}"
}

output "ndb_cluster_mgmt_public_ips" {
  value = "${join(",", module.ndb_cluster.ndb_mgmt_public_ips)}"
}
output "ndb_cluster_data_public_ips" {
  value = "${join(",", module.ndb_cluster.ndb_data_public_ips)}"
}
output "ndb_cluster_sql_public_ips" {
  value = "${join(",", module.ndb_cluster.ndb_sql_public_ips)}"
}
output "ndb_cluster_cassandra_ips" {
  value = "${join(",", module.ndb_cluster.ndb_cassandra_public_ips)}"
}
output "ndb_cluster_test-client_ip" {
  value = "${module.ndb_cluster.ndb_test_client_public_ip}"
}
output "ndb_cluster_test-client_private_ip" {
  value = "${module.ndb_cluster.ndb_test_client_private_ip}"
}
output "ndb_cluster_num_ndb_replicas" {
  value = "${module.ndb_cluster.num_ndb_replicas}"
}
output "ndb_cluster_num_ndb_fragments" {
  value = "${module.ndb_cluster.num_ndb_fragments}"
}
