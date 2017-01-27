variable "cassandra_primary_ip" {
  description = "The broadcast IP of the first cassandra node"
}
variable "mariadb_ips" {
  description = "Comma-separated list of all the MariaDB IPs"
}
variable "security_group_name" {}
variable "key_name" {}
variable "cluster_name" {}
variable "private_key" {}
variable "mariadb_password" {}
variable "ami" {}

module "test_client" {
  source              = "../../resources/instance"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  tag_name            = "${var.cluster_name}test-client"
  ami                 = "${var.ami}"
}

resource "null_resource" "config" {
  triggers = {
    this_id = "${module.test_client.id}"
  }
  connection = {
    host        = "${module.test_client.public_ip}"
    user        = "${module.test_client.user}"
    private_key = "${var.private_key}"
  }
  provisioner "remote-exec" {
    inline = [
      "mkdir -p /home/ubuntu/analysis/testLogs",
      "mkdir -p /home/ubuntu/analysis/build/test-results"
    ]
  }
  provisioner "file" {
    source      = "../src"
    destination = "/home/ubuntu/analysis/src"
  }
  provisioner "file" {
    source      = "../build.gradle"
    destination = "/home/ubuntu/analysis/build.gradle"
  }
  provisioner "remote-exec" {
    inline = [
      "dos2unix /home/ubuntu/analysis/*/*",
      "/tmp/scripts/test-client/config.sh ${var.cassandra_primary_ip} ${var.mariadb_ips}",
    ]
  }
}

output "public_ip" {
  value = "${module.test_client.public_ip}"
}
output "private_ip" {
  value = "${module.test_client.private_ip}"
}
