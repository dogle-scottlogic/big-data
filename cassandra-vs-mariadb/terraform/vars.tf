variable "access_key" {
  description = "Your AWS user's access key"
}
variable "secret_key" {
  description = "Your AWS user's secret key"
}
variable "public_key_path" {
  description = "Path to an SSH public key to be used for authentication"
  default = "~/.ssh/id_rsa.pub"
}
variable "private_key_path" {
  description = "Path to an SSH private key to be used for authentication"
  default = "~/.ssh/id_rsa"
}
variable "key_prefix" {
  description = "Desired prefix of AWS key pair"
  default = "dev"
}
variable "region" {
  description = "Default region"
  default = "eu-west-2"
}
