variable "security_group_name" {}
variable "key_name" {}
variable "private_key" {}
variable "tag_name" {}
variable "bootstrap_commands" {
  type = "list"
  default = []
}
variable "num_nodes" {
  default = 1
}
variable "instance_type" {
  description = "The type of VM to use"
  default     = "t2.micro"
}
variable "ami" {
  // Ubuntu 16.04
  default = "ami-57eae033"
  # default = "ami-6f587e1c"
}

variable "user" {
  default = "ubuntu"
}
variable "initial_commands" {
  default = [
    "sudo apt-get install -y dos2unix",
    "dos2unix /tmp/scripts/*/*",
    "chmod a+x /tmp/scripts/*/*",
    "echo chmod-ed all scripts",
  ]
}

resource "aws_instance" "instance" {
  ami             = "${var.ami}"
  instance_type   = "${var.instance_type}"
  security_groups = ["${var.security_group_name}"]
  key_name        = "${var.key_name}"
  count           = "${var.num_nodes}"

  tags {
    Name = "${var.tag_name}${count.index + 1}"
  }

  connection {
    user        = "${var.user}"
    private_key = "${var.private_key}"
  }

  provisioner "file" {
    source = "scripts"
    destination = "/tmp/scripts"
  }

  provisioner "remote-exec" {
    inline = "${concat(var.initial_commands, var.bootstrap_commands)}"
  }
}

output "ids" {
  value = ["${aws_instance.instance.*.id}"]
}
output "id" {
  value = "${element(aws_instance.instance.*.id, 0)}"
}
output "public_ips" {
  value = ["${aws_instance.instance.*.public_ip}"]
}
output "public_ip" {
  value = "${element(aws_instance.instance.*.public_ip, 0)}"
}
output "private_ips" {
  value = ["${aws_instance.instance.*.private_ip}"]
}
output "private_ip" {
  value = "${element(aws_instance.instance.*.private_ip, 0)}"
}
output "user" {
  value = "${var.user}"
}
