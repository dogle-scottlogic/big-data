module "ami-nodes" {
  source              = "../cluster"
  ami_creation_mode   = "true"
  num_nodes           = 1
  cluster_name        = "ami-"
  key_name            = "${var.key_name}"
  security_group_name = "${var.security_group_name}"
  private_key         = "${var.private_key}"
}

resource "aws_ami_from_instance" "cass_ami" {
  name               = "cassandra-ami"
  source_instance_id = "${module.ami-nodes.cassandra_id}"
}

resource "aws_ami_from_instance" "mariadb_ami" {
  name               = "mariadb-ami"
  source_instance_id = "${module.ami-nodes.mariadb_id}"
}
