// Setup individual instances
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
      "dos2unix /tmp/scripts/*/*",
      "chmod a+x /tmp/scripts/*/*",
      "echo chmod-ed all scripts"
    ]
  }
}

resource "null_resource" "cassandra-cluster-config" {
  count = "${var.num_nodes}"
  triggers {
    seed_ip = "${aws_instance.cassandra.0.private_ip}"
    // Change to this instance requires reprovisioning
    this_id = "${element(aws_instance.cassandra.*.id, count.index)}"
  }
  connection {
    host        = "${element(aws_instance.cassandra.*.public_ip, count.index)}"
    user        = "${var.user}"
    private_key = "${var.private_key}"
  }
  provisioner "remote-exec" {
    inline = [
      "sudo /tmp/scripts/cassandra/config.sh ${aws_instance.cassandra.0.private_ip}"
    ]
  }
}

resource "null_resource" "cassandra-cluster-start" {
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


