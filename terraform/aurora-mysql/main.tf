provider "aws" {
  region = "us-east-1"
}

resource "aws_rds_cluster" "database" {
  cluster_identifier      = "twitter-db"
  engine                  = "aurora-mysql"
  engine_version          = "8.0.mysql_aurora.3.04.1"
  availability_zones      = ["us-east-1a"]
  database_name           = "twitter_db"
  master_username         = var.admin
  master_password         = var.pwd
  backup_retention_period = 5
  preferred_backup_window = "07:00-09:00"
  skip_final_snapshot     = true

  tags = {
    Project        = var.project_name
  }
}

resource "aws_rds_cluster_instance" "cluster_instances" {
  identifier         = "twitter1"
  cluster_identifier = aws_rds_cluster.database.cluster_identifier
  instance_class     = "db.r5.large"
  engine             = aws_rds_cluster.database.engine
  engine_version     = aws_rds_cluster.database.engine_version
  publicly_accessible = true

  tags = {
    Project        = var.project_name
  }
}