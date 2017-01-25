output "public_ips" {
  value = "${join(" ", aws_instance.mariadb.*.public_ip)}"
}
