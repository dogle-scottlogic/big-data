variable "num_nodes" {}
variable "cluster_name" {}
variable "security_group_name" {}
variable "key_name" {}
variable "private_key" {}
variable "ami" {}
variable "mariadb_password" {}
variable "test_client_ip" {}

module "instances" {
  source              = "../../resources/instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "${var.cluster_name}maria"
  num_nodes           = "${var.num_nodes}"
  ami                 = "${var.ami}"
}

module "config" {
  source      = "../../resources/remote_commands"
  num_nodes   = "${var.num_nodes}"
  ids         = "${module.instances.ids}"
  public_ips  = "${module.instances.public_ips}"
  user        = "${module.instances.user}"
  private_key = "${var.private_key}"
  commands    = [
    "sudo /tmp/scripts/mariadb/config.sh ${join(",", module.instances.private_ips)}"
  ]
}

resource "null_resource" "primary" {
  depends_on = ["module.config"]
  triggers {
    ids   = "${join(",", module.instances.ids)}"
  }
  connection {
    host        = "${element(module.instances.public_ips, 0)}"
    user        = "${module.instances.user}"
    private_key = "${var.private_key}"
  }
  provisioner "remote-exec" {
    inline = ["/tmp/scripts/mariadb/start-primary.sh ${var.mariadb_password}"]
  }
}

resource "null_resource" "start" {
  depends_on = ["null_resource.primary"]
  triggers {
    ids   = "${join(",", module.instances.ids)}"
  }
  provisioner "local-exec" {
  command = "bash scripts/mariadb/start-cluster.sh ${join(" ", module.instances.public_ips)}"
  }
}

resource "null_resource" "setup_access" {
  depends_on = ["null_resource.start"]
  triggers {
    ids   = "${join(",", module.instances.ids)}"
  }
  connection {
    host        = "${element(module.instances.public_ips, 0)}"
    user        = "${module.instances.user}"
    private_key = "${var.private_key}"
  }
  provisioner "remote-exec" {
    inline = [
      "/tmp/scripts/mariadb/allow-access.sh ${var.test_client_ip} ${var.mariadb_password}"
    ]
  }
}

output "public_ips" {
  value = "${module.instances.public_ips}"
}
output "private_ips" {
  value = "${module.instances.private_ips}"
}

