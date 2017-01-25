output "id" {
  value = "${aws_instance.cassandra.0.id}"
}
output "public_ips" {
  value = "${join(",", aws_instance.cassandra.*.public_ip)}"
}
