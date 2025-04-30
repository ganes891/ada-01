resource "aws_s3_bucket" "terraform_state" {
  bucket = "ada-01-bucket"

  # Prevent accidental deletion of this S3 bucket
  lifecycle {
    prevent_destroy = true
  }
}

  backend "s3" {
    bucket = "<your unique bucket name>"
    key    = "my_lambda/terraform.tfstate"
    region = "eu-central-1"
  }
