variable "ids" {
  default = []
}
variable "command" {}

resource "null_resource" "local" {
  triggers {
    ids = "${var.ids}"
  }
  provisioner "local-exec" {
    command = "${var.command}"
  }
}
