// Setup indivprivate_ipual instances
resource "aws_instance" "cassandra" {
  ami             = "${var.ami}"
  instance_type   = "${var.instance_type}"
  count           = "${var.num_nodes}"
  security_groups = ["${var.security_group_name}"]
  key_name        = "${var.key_name}"

  tags {
    Name = "${var.cluster_name}cass${count.index + 1}"
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
      "sudo apt-get install -y dos2unix",
      "dos2unix /tmp/scripts/*/*",
      "chmod a+x /tmp/scripts/*/*",
      "echo chmod-ed all scripts",
      "sudo /tmp/scripts/common/bootstrap.sh",
      "sudo /tmp/scripts/cassandra/bootstrap.sh"
    ]
  }
}

resource "null_resource" "cassandra-cluster-config" {
  count = "${var.ami_creation_mode ? 0 : var.num_nodes}" // Disable configuration if only creating AMIs
  triggers {
    seed_ip = "${aws_instance.cassandra.0.private_ip}"
    this_ip = "${element(aws_instance.cassandra.*.private_ip, count.index)}"
    // Change to this instance requires reprovisioning
    this_id = "${element(aws_instance.cassandra.*.id, count.index)}"
  }
  connection {
    host        = "${element(aws_instance.cassandra.*.public_ip, count.index)}"
    user        = "${var.user}"
    private_key = "${var.private_key}"
  }
  provisioner "file" {
    source = "scripts"
    destination = "/tmp/scripts"
  }
  provisioner "remote-exec" {
    inline = [
      "sudo /tmp/scripts/cassandra/config.sh ${aws_instance.cassandra.0.private_ip}"
    ]
  }
}

resource "null_resource" "cassandra-cluster-start" {
  count = "${var.ami_creation_mode ? 0 : 1}" // Disable configuration if only creating AMIs
  depends_on = ["null_resource.cassandra-cluster-config"]
  triggers {
    seed_ip = "${aws_instance.cassandra.0.private_ip}"
    this_ip = "${element(aws_instance.cassandra.*.private_ip, count.index)}"
    // Change to this instance requires reprovisioning
    this_id = "${element(aws_instance.cassandra.*.id, count.index)}"
  }
  provisioner "local-exec" {
    command = "bash scripts/cassandra/start-cluster.sh ${join(" ", aws_instance.cassandra.*.public_ip)}"
  }
}


