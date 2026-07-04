# Configuration Management Debugging Guide

## Ansible Debugging
```powershell
# Verbose output
ansible-playbook playbook.yml -v     # Verbose
ansible-playbook playbook.yml -vvv   # Very verbose
ansible-playbook playbook.yml -vvvv  # Connection debug

# Check syntax
ansible-playbook playbook.yml --syntax-check

# Dry run
ansible-playbook playbook.yml --check

# Step through tasks
ansible-playbook playbook.yml --step

# Debug module
- debug:
    msg: "The value is {{ my_variable }}"

# Check specific host
ansible all -i inventory.ini -m setup | Select-String "ansible_os_family"
```

## Common Issues
- **SSH connection refused**: Check ssh key, username, host reachability.
- **Permission denied**: Use `become: yes` for root tasks.
- **Module not found**: Update Ansible; module may be in collection.
- **Template error**: Check Jinja2 syntax; undefined variable.
- **Package not found**: Update package cache; check repository.
- **Service not starting**: Check service name; enable may be required.
