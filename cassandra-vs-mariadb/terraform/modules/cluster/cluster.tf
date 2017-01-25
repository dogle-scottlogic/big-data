module "cassandra" {
  source              = "cassandra"
  ami                 = "${var.cassandra_ami}"
  num_nodes           = "${var.num_nodes}"
  cluster_name        = "${var.cluster_name}"
  key_name            = "${var.key_name}"
  security_group_name = "${var.security_group_name}"
  private_key         = "${var.private_key}"
}

module "mariadb" {
  source              = "mariadb"
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
  source               = "test-client"
  ami                  = "${var.test-client_ami}"
  key_name             = "${var.key_name}"
  mariadb_password    = "${var.mariadb_password}"
  security_group_name  = "${var.security_group_name}"
  private_key          = "${var.private_key}"
  cluster_name         = "${var.cluster_name}"
  cassandra_primary_ip = "${module.cassandra.primary_ip}"
  mariadb_ips          = "${module.mariadb.private_ips}"
}
