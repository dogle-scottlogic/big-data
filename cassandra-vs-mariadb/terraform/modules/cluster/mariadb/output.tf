output "public_ips" {
  value = "${join(" ", aws_instance.mariadb.*.public_ip)}"
}
output "private_ips" {
  value = "${join(",", aws_instance.mariadb.*.private_ip)}"
}
