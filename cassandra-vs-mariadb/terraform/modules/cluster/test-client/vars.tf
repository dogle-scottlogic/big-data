variable "cassandra_primary_ip" {
  description = "The broadcast IP of the first cassandra node"
}
variable "mariadb_ips" {
  description = "Comma-separated list of all the MariaDB IPs"
}
variable "security_group_name" {
  description = "The name of the security group to attach to the instances"
}
variable "key_name" {
  description = "AWS key name to use to connect"
}
variable "cluster_name" {
  description = "The name of this specific cluster"
}
variable "private_key" {
  description = "Private key to use when connecting"
}
variable "mariadb_password" {}
variable "ami" {
}
variable "gradle_debug" {
  description = "Whether to run gradle in debug mode"
  default     = "false"
}
variable "instance_type" {
  description = "The type of VM to use"
  default     = "t2.micro"
}
variable "user" {
  default = "ubuntu"
}
