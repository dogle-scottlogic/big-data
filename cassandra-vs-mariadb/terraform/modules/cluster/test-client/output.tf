output "public_ip" {
  value = "${aws_instance.test-client.public_ip}"
}
output "private_ip" {
  value = "${aws_instance.test-client.private_ip}"
}
