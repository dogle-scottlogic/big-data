// Define a security group allows SSH traffic and nothing else from outside.
resource "aws_security_group" "clusterSecurityGroup" {
  name = "Cluster Security Group"
  description = "For Cassandra, MariaDB and test nodes"

  // SSH from anywhere
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  // HTTP from anywhere for apt-get
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  // Any inbound traffic in the subnet
  ingress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["${var.subnet_cidr}"]
  }

  // Any outbound traffic
  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags {
    Name = "Cassandra / MariaDB"
  }
}
