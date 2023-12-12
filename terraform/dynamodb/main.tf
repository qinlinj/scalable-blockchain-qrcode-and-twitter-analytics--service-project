provider "aws" {
  region = "us-east-1"
}

resource "aws_dynamodb_table" "tweets-table" {
  name           = "Tweets"
  billing_mode   = "PROVISIONED"
  read_capacity  = 20
  write_capacity = 20
  hash_key       = "id"

  attribute {
    name = "id"
    type = "N"
  }

  attribute {
    name = "user_id"
    type = "N"
  }

  attribute {
    name = "in_reply_to_user_id"
    type = "N"
  }

  attribute {
    name = "retweeted_status_user_id"
    type = "N"
  }

  global_secondary_index {
    name               = "userIdIdx"
    hash_key           = "user_id"
    write_capacity     = 10
    read_capacity      = 20
    projection_type    = "ALL"
  }

  global_secondary_index {
    name               = "replyIdx"
    hash_key           = "in_reply_to_user_id"
    write_capacity     = 10
    read_capacity      = 20
    projection_type    = "ALL"
  }

  global_secondary_index {
    name               = "retweetIdx"
    hash_key           = "retweeted_status_user_id"
    write_capacity     = 10
    read_capacity      = 20
    projection_type    = "ALL"
  }

  tags = {
    Name        = "Tweets"
    Project = var.project_name
  }
}

resource "aws_appautoscaling_target" "tweets-table_read_target" {
  max_capacity       = 200
  min_capacity       = 5
  resource_id        = "table/${aws_dynamodb_table.tweets-table.name}"
  scalable_dimension = "dynamodb:table:ReadCapacityUnits"
  service_namespace  = "dynamodb"
}

resource "aws_appautoscaling_policy" "tweets-table_read_policy" {
  name               = "dynamodb-read-capacity-utilization-${aws_appautoscaling_target.tweets-table_read_target.resource_id}"
  policy_type        = "TargetTrackingScaling"
  resource_id        = "${aws_appautoscaling_target.tweets-table_read_target.resource_id}"
  scalable_dimension = "${aws_appautoscaling_target.tweets-table_read_target.scalable_dimension}"
  service_namespace  = "${aws_appautoscaling_target.tweets-table_read_target.service_namespace}"

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "DynamoDBReadCapacityUtilization"
    }
    target_value = 70
  }
}


resource "aws_dynamodb_table" "users-table" {
  name           = "Users"
  billing_mode   = "PROVISIONED"
  read_capacity  = 20
  write_capacity = 20
  hash_key       = "id"

  attribute {
    name = "id"
    type = "N"
  }

  tags = {
    Name        = "Users"
    Project = var.project_name
  }
}

resource "aws_appautoscaling_target" "users-table_read_target" {
  max_capacity       = 200
  min_capacity       = 5
  resource_id        = "table/${aws_dynamodb_table.users-table.name}"
  scalable_dimension = "dynamodb:table:ReadCapacityUnits"
  service_namespace  = "dynamodb"
}

resource "aws_appautoscaling_policy" "users-table_read_policy" {
  name               = "dynamodb-read-capacity-utilization-${aws_appautoscaling_target.users-table_read_target.resource_id}"
  policy_type        = "TargetTrackingScaling"
  resource_id        = "${aws_appautoscaling_target.users-table_read_target.resource_id}"
  scalable_dimension = "${aws_appautoscaling_target.users-table_read_target.scalable_dimension}"
  service_namespace  = "${aws_appautoscaling_target.users-table_read_target.service_namespace}"

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "DynamoDBReadCapacityUtilization"
    }
    target_value = 70
  }
}