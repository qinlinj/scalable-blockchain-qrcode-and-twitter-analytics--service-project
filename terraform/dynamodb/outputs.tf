output "tweets-table_arn" {
  description = "ARN of the DynamoDB table"
  value       = aws_dynamodb_table.tweets-table.arn
}

output "tweets-table_id" {
  description = "ID of the DynamoDB table"
  value       = aws_dynamodb_table.tweets-table.id
}

output "users-table_arn" {
  description = "ARN of the DynamoDB table"
  value       = aws_dynamodb_table.users-table.arn
}

output "users-table_id" {
  description = "ID of the DynamoDB table"
  value       = aws_dynamodb_table.users-table.id
}