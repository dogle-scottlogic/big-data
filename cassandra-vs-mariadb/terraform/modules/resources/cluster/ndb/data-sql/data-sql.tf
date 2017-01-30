variable "cluster_name" {}
variable "security_group_name" {}
variable "key_name" {}
variable "private_key" {}
variable "ami" {}
variable "num_nodes" {}
variable "mgmt_private_ips" {
  type = "list"
}
variable "startup_commands" {
  type = "list"
}
variable "tag_name" {}

data "template_file" "config" {
  template = "${file("scripts/ndb/my.cnf")}"

  vars {
    mgmt_ips = "${join(" ", var.mgmt_private_ips)}"
  }
}

module "nodes" {
  source              = "../../../instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "${var.cluster_name}${var.tag_name}"
  num_nodes           = "${var.num_nodes}"
  ami                 = "${var.ami}"
}

module "config" {
  source           = "../../../template-remote"
  num_nodes        = "${var.num_nodes}"
  ids              = "${module.nodes.ids}"
  public_ips       = "${module.nodes.public_ips}"
  user             = "${module.nodes.user}"
  private_key      = "${var.private_key}"
  file_content     = "${data.template_file.config.rendered}"
  file_destination = "/tmp/my.cnf"
  commands         = "${var.startup_commands}"
}

output "private_ips" {
  value = "${module.nodes.private_ips}"
}
output "public_ips" {
  value = "${module.nodes.public_ips}"
}
output "ids" {
  value = "${module.nodes.ids}"
}
