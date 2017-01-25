output "public_ips" {
  value = "${join(" ", aws_instance.cassandra.*.public_ip)}"
}
output "primary_ip" {
  value = "${aws_instance.cassandra.0.private_ip}"
}
