# Configuration Management Exercises

## Exercise 1: Ansible Ad-Hoc
Use ad-hoc commands to check disk space (`ansible all -m shell -a "df -h"`).

## Exercise 2: First Playbook
Write a playbook that installs nginx, copies a custom index.html, and starts the service.

## Exercise 3: Variables
Use group_vars and host_vars. Demonstrate variable precedence.

## Exercise 4: Templates
Create a Jinja2 template for nginx config. Use variables for port and server_name.

## Exercise 5: Roles
Create an nginx role with tasks, handlers, templates, and default variables.

## Exercise 6: Ansible Vault
Encrypt a sensitive variable file. Use it in a playbook.

## Exercise 7: Dynamic Inventory
Set up dynamic inventory for AWS EC2 using the aws_ec2 plugin.

## Exercise 8: Playbook Tags
Add tags to tasks. Run specific tagged tasks with `--tags`.

## Exercise 9: Puppet/Chef (optional)
Write a Puppet manifest or Chef cookbook that installs and configures Apache.
