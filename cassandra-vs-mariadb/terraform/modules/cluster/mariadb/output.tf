output "id" {
  value = "${aws_instance.mariadb.0.id}"
}
output "public_ips" {
  value = "${join(",", aws_instance.mariadb.*.public_ip)}"
}
