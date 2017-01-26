variable "security_group_name" {}
variable "key_name" {}
variable "private_key" {}
variable "mariadb_password" {
  default = "myfirstpassword"
}

module "cassandra" {
  source              = "../resources/instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "cass-ami"
  bootstrap_commands  = [
    "sudo /tmp/scripts/cassandra/bootstrap.sh",
    "mkdir -p /home/ubuntu/analysis/src",
    "mkdir -p /home/ubuntu/analysis/testLogs"
  ]
}

module "mariadb" {
  source              = "../resources/instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "maria-ami"
  bootstrap_commands  = [
    "sudo /tmp/scripts/mariadb/bootstrap.sh ${var.mariadb_password}"
  ]
}

module "test" {
  source              = "../resources/instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "test-client-ami"
  bootstrap_commands  = [
    "sudo /tmp/scripts/test-client/bootstrap.sh"
  ]
}

resource "aws_ami_from_instance" "cass_ami" {
  name               = "cassandra-ami"
  source_instance_id = "${element(module.cassandra.ids, 0)}"
}

resource "aws_ami_from_instance" "mariadb_ami" {
  name               = "mariadb-ami"
  source_instance_id = "${element(module.mariadb.ids, 0)}"
}

resource "aws_ami_from_instance" "test-client_ami" {
  name = "test-client-ami"
  source_instance_id = "${element(module.test.ids, 0)}"
}

output "cassandra_ami_id" {
  value = "${aws_ami_from_instance.cass_ami.id}"
}
output "mariadb_ami_id" {
  value = "${aws_ami_from_instance.mariadb_ami.id}"
}
output "test-client_ami_id" {
  value = "${aws_ami_from_instance.test-client_ami.id}"
}
output "mariadb_password" {
  value = "${var.mariadb_password}"
}
