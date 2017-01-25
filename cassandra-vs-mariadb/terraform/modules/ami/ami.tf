// Setup individual instances
resource "aws_instance" "cassandra" {
  ami             = "${var.ami}"
  instance_type   = "${var.instance_type}"
  security_groups = ["${var.security_group_name}"]
  key_name        = "${var.key_name}"

  tags {
    Name = "cass-ami"
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

resource "aws_instance" "mariadb" {
  ami             = "${var.ami}"
  instance_type   = "${var.instance_type}"
  security_groups = ["${var.security_group_name}"]
  key_name        = "${var.key_name}"

  tags {
    Name = "maria-ami"
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

resource "aws_ami_from_instance" "cass_ami" {
  name               = "cassandra-ami"
  source_instance_id = "${aws_instance.cassandra.id}"
}

resource "aws_ami_from_instance" "mariadb_ami" {
  name               = "mariadb-ami"
  source_instance_id = "${aws_instance.mariadb.id}"
}
