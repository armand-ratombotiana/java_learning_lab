# Terraform Internals

## Terraform Core Architecture
- **terraform CLI**: Entry point, parses arguments, initializes backend.
- **Backend**: Defines where state is stored (local, S3, remote).
- **Provider Registry**: Fetches provider binaries from registry.terraform.io.
- **Provisioner**: Runs scripts on resources after creation (remote-exec, local-exec).
- **Graph Builder**: Constructs dependency graph from configuration references.

## State File Format
```json
{
  "version": 4,
  "terraform_version": "1.7.0",
  "serial": 42,
  "lineage": "abc-...",
  "resources": [
    {
      "module": "root",
      "mode": "managed",
      "type": "aws_instance",
      "name": "web",
      "provider": "provider[\"registry.terraform.io/hashicorp/aws\"]",
      "instances": [
        {
          "schema_version": 1,
          "attributes": { "id": "i-123", "ami": "ami-abc" },
          "dependencies": ["aws_security_group.web"]
        }
      ]
    }
  ]
}
```

## Plan File
Binary file containing the planned changes. Can be saved with `terraform plan -out=tfplan` and applied with `terraform apply tfplan`.

## Resource Addressing
```
module.foo.module.bar.aws_instance.baz[0] (full address)
```
