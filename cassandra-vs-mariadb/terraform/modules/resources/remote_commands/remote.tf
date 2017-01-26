// For running remote commands on a cluster
variable "num_nodes" {
  default = 1
}
variable "ids" {
  type = "list"
}
variable "public_ips" {
  type = "list"
}
variable "user" {}
variable "private_key" {}
variable "commands" {
  type = "list"
}
variable "trigger" {
  default = ""
}

resource "null_resource" "remote" {
  count = "${var.num_nodes}"
  triggers {
    ids   = "${join(",", var.ids)}"
    other = "${var.trigger}"
  }
  connection {
    host        = "${element(var.public_ips, count.index)}"
    user        = "${var.user}"
    private_key = "${var.private_key}"
  }
  provisioner "remote-exec" {
    inline = "${var.commands}"
  }
}
