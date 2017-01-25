variable "security_group_name" {
  description = "The name of the security group to attach to the instances"
}
variable "key_name" {
  description = "AWS key name to use to connect"
}
variable "private_key" {
  description = "Private key to use when connecting"
}
variable "mariadb_password" {
  description = "Root password for mariadb"
  default     = "myfirstpassword"
}
variable "instance_type" {
  description = "The type of VM to use"
  default     = "t2.micro"
}
variable "region" {
  default = "eu-west-2"
}
variable "ami" {
  default = "ami-57eae033" // Ubuntu 16.04
}
variable "user" {
  default = "ubuntu"
}
