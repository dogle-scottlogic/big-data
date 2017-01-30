variable "num_nodes" {}
variable "cluster_name" {}
variable "security_group_name" {}
variable "key_name" {}
variable "private_key" {}
variable "ami" {}

module "instances" {
  source              = "../../instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "${var.cluster_name}cass"
  num_nodes           = "${var.num_nodes}"
  ami                 = "${var.ami}"
}

module "config" {
  source      = "../../remote_commands"
  num_nodes   = "${var.num_nodes}"
  ids         = "${module.instances.ids}"
  public_ips  = "${module.instances.public_ips}"
  user        = "${module.instances.user}"
  private_key = "${var.private_key}"
  commands    = [
    "sudo /tmp/scripts/cassandra/config.sh ${element(module.instances.private_ips, 0)}"
  ]
}

resource "null_resource" "start" {
  depends_on = ["module.config"]
  triggers {
    ids = "${join(",", module.instances.ids)}"
  }
  provisioner "local-exec" {
    command = "bash scripts/cassandra/start-cluster.sh ${join(" ", module.instances.public_ips)}"
  }
}

output "public_ips" {
  value = "${module.instances.public_ips}"
}
output "primary_ip" {
  value = "${element(module.instances.private_ips, 0)}"
}
