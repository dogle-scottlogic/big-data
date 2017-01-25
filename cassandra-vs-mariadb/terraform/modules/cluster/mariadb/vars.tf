variable "ami_creation_mode" {
  description = "If set to true, only bootstrap nodes, ready for saving as AMIs"
  default     = "false"
}
variable "num_nodes" {
  description = "The number of nodes each database should have"
}
variable "cluster_name" {
  description = "The name of this specific cluster"
}
variable "security_group_name" {
  description = "The name of the security group to attach to the instances"
}
variable "key_name" {
  description = "AWS key name to use to connect"
}
variable "private_key" {
  description = "Private key to use when connecting"
}
variable "ami" {
}
variable "mariadb_password" {
  description = "Root password for mariadb"
  default     = "myfirstpassword"
}
variable "user" {
  description = "User to connect as"
  default     = "ubuntu"
}
variable "instance_type" {
  description = "The type of VM to use"
  default     = "t2.micro"
}
variable "region" {
  default = "eu-west-2"
}
