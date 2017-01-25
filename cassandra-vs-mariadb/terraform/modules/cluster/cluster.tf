module "cassandra" {
  source              = "cassandra"
  ami_creation_mode   = "${var.ami_creation_mode}"
  ami                 = "${var.cassandra_ami}"
  num_nodes           = "${var.num_nodes}"
  cluster_name        = "${var.cluster_name}"
  key_name            = "${var.key_name}"
  security_group_name = "${var.security_group_name}"
  private_key         = "${var.private_key}"
}

module "mariadb" {
  source              = "mariadb"
  ami_creation_mode   = "${var.ami_creation_mode}"
  ami                 = "${var.mariadb_ami}"
  num_nodes           = "${var.num_nodes}"
  cluster_name        = "${var.cluster_name}"
  key_name            = "${var.key_name}"
  security_group_name = "${var.security_group_name}"
  private_key         = "${var.private_key}"
}
