variable "num_ndb_replicas" {}
variable "num_ndb_fragments" {}
variable "num_sql_nodes" {}
variable "cluster_name" {}
variable "security_group_name" {}
variable "key_name" {}
variable "private_key" {}
variable "ndb_node_ami" {}
variable "sql_node_ami" {}
variable "sql_password" {}
variable "test-client_ip" {}
variable "num_ndb_mgmt_nodes" {
  default = 1
}
variable "sql_start_cmd" {
  default = "nohup mysqld_safe --ndbcluster --ndb-connectstring=${} --datadir=/var/lib/mysql --pid-file=/var/lib/mysql/mysql.pid --syslog --user=ubuntu >/dev/null 2>&1 &"
}

module "ndb_mgmt" {
  source              = "mgmt"
  num_nodes           = "${var.num_ndb_mgmt_nodes}"
  num_replicas        = "${var.num_ndb_replicas}"
  cluster_name        = "${var.cluster_name}"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  ami                 = "${var.ndb_node_ami}"
  data_ips            = "${module.ndb_data.private_ips}"
  sql_ips             = "${module.ndb_sql.private_ips}"
}

module "ndb_data" {
  source              = "data-sql"
  num_nodes           = "${var.num_ndb_replicas * var.num_ndb_fragments}"
  tag_name            = "ndb-data"
  cluster_name        = "${var.cluster_name}"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  ami                 = "${var.ndb_node_ami}"
  mgmt_private_ips    = "${module.ndb_mgmt.private_ips}"
  startup_commands    = [
    "sudo mv /tmp/my.cnf /etc/my.cnf",
    "sudo mkdir -p /usr/local/mysql/data",
    "sudo ndbd",
    "sudo systemctl enable rc-local.service", # Ensure ndbd starts on boot
    "sudo sed -i 's/(ndbd\\n)?exit 0/ndbd\\nexit 0/' /etc/rc.local"
  ]
}

module "ndb_sql" {
  source              = "data-sql"
  num_nodes           = "${var.num_sql_nodes}"
  tag_name            = "ndb-sql"
  cluster_name        = "${var.cluster_name}"
  security_group_name = "${var.security_group_name}"
  key_name            = "${var.key_name}"
  private_key         = "${var.private_key}"
  ami                 = "${var.sql_node_ami}"
  mgmt_private_ips    = "${module.ndb_mgmt.private_ips}"
  startup_commands    = [
    "sudo mv /tmp/my.cnf /etc/my.cnf",
    "sudo pkill -f mysql",
    "sudo rm -rf /var/lib/mysql /var/lib/mysql-files",
    "sudo mysqld --initialize-insecure --user=ubuntu --datadir=/var/lib/mysql",
    "sudo cp /opt/mysql/server-*/support-files/mysql.server /etc/init.d/mysqld",
    "sudo mkdir /var/lib/mysql-files",
    "sudo chown -R ubuntu /var/lib/mysql-files",
  ]
}

# module "ndb_sql_config" {
#   source      = "../../remote_commands"
#   num_nodes   = "${var.num_sql_nodes}"
#   ids         = "${module.ndb_sql.ids}"
#   public_ips  = "${module.ndb_sql.public_ips}"
#   user        = "${module.ndb_sql.user}"
#   private_key = "${var.private_key}"
#   commands    = [
#     # "nohup mysqld_safe --ndbcluster --ndb-connectstring=${module.ndb_mgmt.private_ip} --datadir=/var/lib/mysql --pid-file=/var/lib/mysql/mysql.pid --syslog --user=ubuntu >/dev/null 2>&1 &",
#     "mysqladmin -u root password \"${var.sql_password}\"",
#     "mysql -u root --password=${var.sql_password} -e \"GRANT ALL PRIVILEGES ON *.* TO 'root'@'${var.test-client_ip}' IDENTIFIED BY '${var.sql_password}' WITH GRANT OPTION\""
#   ]
# }

output "mgmt_public_ips" {
  value = "${module.ndb_mgmt.public_ips}"
}
output "data_public_ips" {
  value = "${module.ndb_data.public_ips}"
}
output "sql_public_ips" {
  value = "${module.ndb_sql.public_ips}"
}
output "sql_private_ips" {
  value = "${module.ndb_sql.private_ips}"
}
