variable "name" {}
variable "source_instance_id" {}
variable "source_region" {}

resource "aws_ami_from_instance" "ami" {
  name               = "${var.name}"
  source_instance_id = "${var.source_instance_id}"
}

# resource "aws_ami_copy" "copy" {
#   name              = "${var.name}"
#   description       = "A copy of ${aws_ami_from_instance.ami.id}"
#   source_ami_id     = "${aws_ami_from_instance.ami.id}"
#   source_ami_region = "${var.source_region}"
#   tags {
#     Name = "${var.name}"
#   }
# }

output "id" {
  value = "${aws_ami_from_instance.ami.id}"
}
