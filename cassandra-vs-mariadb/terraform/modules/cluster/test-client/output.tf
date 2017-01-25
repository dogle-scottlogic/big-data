output "public_ip" {
  value = "${aws_instance.test-client.public_ip}"
}
