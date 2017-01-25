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
variable "ami" {
}
variable "instance_type" {
  description = "The type of VM to use"
  default     = "t2.micro"
}
variable "user" {
  default = "ubuntu"
}
