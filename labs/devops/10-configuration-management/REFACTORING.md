# Configuration Management Refactoring

## Before (Flat Playbook)
```yaml
- name: Configure webserver
  hosts: webservers
  tasks:
    - name: Install nginx
      apt:
        name: nginx
        state: present
    - name: Install PHP
      apt:
        name: php-fpm
        state: present
    - name: Copy nginx config
      template:
        src: nginx.conf.j2
        dest: /etc/nginx/nginx.conf
      notify: restart nginx
    - name: Copy PHP config
      template:
        src: php.ini.j2
        dest: /etc/php/8.1/fpm/php.ini
      notify: restart php
  handlers:
    - name: restart nginx
      service:
        name: nginx
        state: restarted
    - name: restart php
      service:
        name: php8.1-fpm
        state: restarted
```

## After (Modular with Roles)
```yaml
- name: Configure webserver
  hosts: webservers
  roles:
    - common
    - nginx
    - php-fpm
    - monitoring
```

## Gains
- Each role independently testable
- Roles reusable across different playbooks
- Separation of concerns (nginx vs php vs monitoring)
- Variables organized per role in defaults/main.yml
- Community roles available via Ansible Galaxy
