output "cassandra_ami_id" {
  value = "${aws_ami_from_instance.cass_ami.id}"
}
output "mariadb_ami_id" {
  value = "${aws_ami_from_instance.mariadb_ami.id}"
}
output "mariadb_password" {
  value = "${var.mariadb_password}"
}
