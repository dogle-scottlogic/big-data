resource "aws_instance" "test-client" {
  ami             = "${var.ami}"
  instance_type   = "${var.instance_type}"
  security_groups = ["${var.security_group_name}"]
  key_name        = "${var.key_name}"

  tags {
    Name = "${var.cluster_name}test-client"
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
    inline = [
      "dos2unix /tmp/scripts/*/*",
      "chmod a+x /tmp/scripts/*/*",
      "echo chmod-ed all scripts",
      "sudo /tmp/scripts/test-client/config.sh"
    ]
  }
}
