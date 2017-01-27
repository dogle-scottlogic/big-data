variable "security_group_name" {}
variable "key_name" {}
variable "private_key" {}
variable "mariadb_password" {
  default = "myfirstpassword"
}

data "aws_region" "current" {
  current = true
}

module "cassandra" {
  source              = "../resources/ami-creation-instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "cass-ami"
  bootstrap_commands  = [
    "sudo /tmp/scripts/ami/common-bootstrap.sh",
    "sudo /tmp/scripts/ami/cassandra-bootstrap.sh",
    "mkdir -p /home/ubuntu/analysis/src",
    "mkdir -p /home/ubuntu/analysis/testLogs"
  ]
}

module "cass_ami" {
  source             = "../resources/ami-create"
  name               = "cassandra-ami"
  source_instance_id = "${element(module.cassandra.ids, 0)}"
  source_region      = "${data.aws_region.current.name}"
}

module "mariadb" {
  source              = "../resources/ami-creation-instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "maria-ami"
  bootstrap_commands  = [
    "sudo /tmp/scripts/ami/common-bootstrap.sh",
    "sudo /tmp/scripts/ami/mariadb-bootstrap.sh ${var.mariadb_password}"
  ]
}

module "mariadb_ami" {
  source             = "../resources/ami-create"
  name               = "mariadb-ami"
  source_instance_id = "${element(module.mariadb.ids, 0)}"
  source_region      = "${data.aws_region.current.name}"
}

module "ndb" {
  source              = "../resources/ami-creation-instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "ndb-ami"
  bootstrap_commands  = [
    "sudo /tmp/scripts/ami/common-bootstrap.sh",
    "sudo /tmp/scripts/ami/ndb-bootstrap.sh"
  ]
}

module "ndb_ami" {
  source             = "../resources/ami-create"
  name               = "ndb-ami"
  source_instance_id = "${element(module.ndb.ids, 0)}"
  source_region      = "${data.aws_region.current.name}"
}

module "ndb_sql" {
  source              = "../resources/ami-creation-instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "ndb-sql-ami"
  bootstrap_commands  = [
    "sudo /tmp/scripts/ami/common-bootstrap.sh",
    "sudo /tmp/scripts/ami/ndb-bootstrap-sql.sh",
    "sudo /tmp/scripts/ami/ndb-bootstrap.sh"
  ]
}

module "ndb_sql_ami" {
  source             = "../resources/ami-create"
  name               = "ndb_sql-ami"
  source_instance_id = "${element(module.ndb_sql.ids, 0)}"
  source_region      = "${data.aws_region.current.name}"
}

module "test" {
  source              = "../resources/ami-creation-instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "test-client-ami"
  bootstrap_commands  = [
    "sudo /tmp/scripts/ami/common-bootstrap.sh",
    "sudo /tmp/scripts/ami/test-client-bootstrap.sh"
  ]
}

module "test_ami" {
  source             = "../resources/ami-create"
  name               = "test-client-ami"
  source_instance_id = "${element(module.test.ids, 0)}"
  source_region      = "${data.aws_region.current.name}"
}

output "cassandra_ami_id" {
  value = "${module.cass_ami.id}"
}
output "mariadb_ami_id" {
  value = "${module.mariadb_ami.id}"
}
output "mariadb_password" {
  value = "${var.mariadb_password}"
}
output "ndb_ami_id" {
  value = "${module.ndb_ami.id}"
}
output "ndb_sql_ami_id" {
  value = "${module.ndb_sql_ami.id}"
}
output "test-client_ami_id" {
  value = "${module.test_ami.id}"
}
