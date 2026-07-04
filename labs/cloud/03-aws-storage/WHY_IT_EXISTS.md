# Why AWS Storage Exists

## The Problem
Applications need persistent, durable, and scalable storage. Traditional on-premise storage requires capacity planning, hardware procurement, RAID configuration, backup strategies, and disaster recovery planning. AWS Storage eliminates this operational burden.

## Three Storage Types for Three Needs
- **Block (EBS)**: Low-latency, single-instance attachment — database volumes, OS disks
- **File (EFS)**: Shared file system across instances — web server content, home directories
- **Object (S3)**: Internet-scale, API-accessible — data lakes, backups, media, static websites

## Java Context
- S3 → Asset storage, file upload/download via presigned URLs
- EBS → Database persistence, application logs
- EFS → Shared config across multiple Java app instances
- S3 lifecycle → Archive old logs, expire temporary files
