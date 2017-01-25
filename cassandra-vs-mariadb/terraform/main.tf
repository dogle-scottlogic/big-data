// AWS access details
provider "aws" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region     = "${var.region}"
}

resource "aws_key_pair" "auth" {
  key_name_prefix = "${var.key_prefix}"
  public_key      = "${file(var.public_key_path)}"
}

module "security_group" {
  source      = "modules/security"
  subnet_cidr = "172.31.0.0/16"
}

module "cluster_3" {
  source              = "modules/cluster"
  num_nodes           = 3
  cassandra_ami       = "${module.ami-nodes.cassandra_ami_id}"
  mariadb_ami         = "${module.ami-nodes.mariadb_ami_id}"
  cluster_name        = "threeNode-"
  key_name            = "${aws_key_pair.auth.key_name}"
  security_group_name = "${module.security_group.name}"
  private_key         = "${file(var.private_key_path)}"
}

// For creating AMIs
module "ami-nodes" {
  source              = "modules/ami"
  key_name            = "${aws_key_pair.auth.key_name}"
  security_group_name = "${module.security_group.name}"
  private_key         = "${file(var.private_key_path)}"
}
