# Step-by-Step Configuration Management Guide

## 1. Install Ansible
```powershell
# On control node (Linux/WSL)
pip install ansible
# Verify
ansible --version
```

## 2. Create Inventory File
```ini
; inventory.ini
[webservers]
web1.example.com
web2.example.com

[dbservers]
db1.example.com

[all:vars]
ansible_user=ubuntu
ansible_ssh_private_key_file=~/.ssh/id_rsa
```

## 3. Test Connectivity
```powershell
ansible all -i inventory.ini -m ping
```

## 4. Create Your First Playbook
```yaml
- name: First playbook
  hosts: all
  tasks:
    - name: Ensure nginx is installed
      apt:
        name: nginx
        state: present
```

## 5. Run Playbook
```powershell
ansible-playbook -i inventory.ini playbook.yml
```

## 6. Add Variables and Templates
```yaml
- name: Configurable webserver
  hosts: webservers
  vars:
    server_name: "{{ inventory_hostname }}"
  tasks:
    - name: Template config
      template:
        src: site.conf.j2
        dest: /etc/nginx/sites-available/site.conf
```

## 7. Create a Role
```powershell
ansible-galaxy init webserver
# Edit files in roles/webserver/
```

## 8. Use Community Roles
```powershell
ansible-galaxy install geerlingguy.nginx
# In playbook:
# roles:
#   - geerlingguy.nginx
```
