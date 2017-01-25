output "public_ips" {
  value = "${join(" ", aws_instance.cassandra.*.public_ip)}"
}
