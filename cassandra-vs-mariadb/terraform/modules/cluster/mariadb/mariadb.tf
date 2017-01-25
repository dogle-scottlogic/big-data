// Setup indivprivate_ipual instances
resource "aws_instance" "mariadb" {
  ami             = "${var.ami}"
  instance_type   = "${var.instance_type}"
  count           = "${var.num_nodes}"
  security_groups = ["${var.security_group_name}"]
  key_name        = "${var.key_name}"

  tags {
    Name = "${var.cluster_name}maria${count.index + 1}"
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
      "sudo /tmp/scripts/mariadb/bootstrap.sh ${var.mariadb_password}"
    ]
  }
}

resource "null_resource" "mariadb-cluster-config" {
  count = "${var.ami_creation_mode ? 0 : var.num_nodes}" // Disable configuration if only creating AMIs
  triggers {
    password  = "${var.mariadb_password}"
    maria_ips = "${join(",", aws_instance.mariadb.*.private_ip)}"
    // Change to any instance of the cluster requires reprovisioning
    cluster_instance_ids = "${join(",", aws_instance.mariadb.*.id)}"
  }
  connection {
    host        = "${element(aws_instance.mariadb.*.public_ip, count.index)}"
    user        = "${var.user}"
    private_key = "${var.private_key}"
  }
  provisioner "file" {
    source = "scripts"
    destination = "/tmp/scripts"
  }
  provisioner "remote-exec" {
    inline = [
      "sudo /tmp/scripts/mariadb/config.sh maria${count.index} ${join(",", aws_instance.mariadb.*.private_ip)}"
    ]
  }
}

resource "null_resource" "mariadb-cluster-primary" {
  depends_on  = ["null_resource.mariadb-cluster-config"]
  count = "${var.ami_creation_mode ? 0 : 1}" // Disable configuration if only creating AMIs
  triggers {
    password  = "${var.mariadb_password}"
    maria_ips = "${join(",", aws_instance.mariadb.*.private_ip)}"
    // Change to any instance of the cluster requires reprovisioning
    cluster_instance_ids = "${join(",", aws_instance.mariadb.*.id)}"
  }
  connection {
    host        = "${aws_instance.mariadb.0.public_ip}"
    user        = "${var.user}"
    private_key = "${var.private_key}"
  }
  provisioner "remote-exec" {
    inline = [
      "/tmp/scripts/mariadb/start-primary.sh ${var.mariadb_password}"
    ]
  }
}

resource "null_resource" "mariadb-cluster-start" {
  depends_on = ["null_resource.mariadb-cluster-primary"]
  count = "${var.ami_creation_mode ? 0 : 1}" // Disable configuration if only creating AMIs
  triggers {
    password  = "${var.mariadb_password}"
    maria_ips = "${join(",", aws_instance.mariadb.*.private_ip)}"
    // Change to any instance of the cluster requires reprovisioning
    cluster_instance_ids = "${join(",", aws_instance.mariadb.*.id)}"
  }
  provisioner "local-exec" {
    command = "bash scripts/mariadb/start-cluster.sh ${join(" ", aws_instance.mariadb.*.public_ip)}"
  }
}

