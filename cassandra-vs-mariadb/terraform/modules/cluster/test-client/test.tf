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
    source      = "scripts"
    destination = "/tmp/scripts"
  }
  provisioner "remote-exec" {
    inline = [
      "dos2unix /tmp/scripts/*/*",
      "chmod a+x /tmp/scripts/*/*",
      "echo chmod-ed all scripts",
    ]
  }
}

resource "null_resource" "config" {
  triggers = {
    this_id = "${aws_instance.test-client.id}" // Reprovision if instance changes!
  }
  connection = {
    host        = "${aws_instance.test-client.public_ip}"
    user        = "${var.user}"
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

