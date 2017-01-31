variable "num_replicas" {}
variable "cluster_name" {}
variable "security_group_name" {}
variable "key_name" {}
variable "private_key" {}
variable "ami" {}
variable "num_nodes" {}
variable "data_ips" {
  type = "list"
}
variable "sql_ips" {
  type = "list"
}

data "template_file" "mgmt_config" {
  template = "${file("scripts/ndb/config-mgmt.sh")}"

  vars {
    mgmt_ips        = "${join(" ", module.nodes.private_ips)}"
    data_ips        = "${join(" ", var.data_ips)}"
    mysql_ips       = "${join(" ", var.sql_ips)}"
    num_of_replicas = "${var.num_replicas}"
  }
}

module "nodes" {
  source              = "../../../instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "${var.cluster_name}ndb-mgmt"
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
  file_content     = "${data.template_file.mgmt_config.rendered}"
  file_destination = "/tmp/config-mgmt.sh"
  trigger          = "${join(",", var.data_ips)},${join(",", var.sql_ips)},${join(",", module.nodes.private_ips)}"
  commands         = [
    "dos2unix /tmp/config-mgmt.sh",
    "sudo bash /tmp/config-mgmt.sh",
    "sudo pkill ndb_mgmd || true",
    "sudo ndb_mgmd -f /var/lib/mysql-cluster/config.ini --ndb-nodeid=1 --reload",
    "sudo systemctl enable rc-local.service", # Ensure ndb_mgmd starts on boot
    "sudo sed -i -r \"s|(ndb_mgmd.*\\n)?exit 0|ndb_mgmd -f /var/lib/mysql-cluster/config.ini\\nexit 0|\" /etc/rc.local"
  ]
}

output "private_ip" {
  value = "${element(module.nodes.private_ips, 0)}"
}
output "private_ips" {
  value = "${module.nodes.private_ips}"
}
output "public_ips" {
  value = "${module.nodes.public_ips}"
}
