# Visual Guide to Actor Model

## Actor Structure
```
+------------------------------------------+
|               Actor                       |
|                                           |
|  +----------------------------------+     |
|  |            Mailbox                |     |
|  |  [Msg1][Msg2][Msg3]...[MsgN]     |     |
|  +----------------------------------+     |
|                                           |
|  +----------------------------------+     |
|  |            State                  |     |
|  |  (private, encapsulated)         |     |
|  +----------------------------------+     |
|                                           |
|  +----------------------------------+     |
|  |           Behavior               |     |
|  |  (message handlers)              |     |
|  +----------------------------------+     |
|                                           |
|  Can: Send Msg | Create Actor | Change Beh|
+------------------------------------------+
```

## Actor Hierarchy
```
                    +--------+
                    | System |  (Root Guardian)
                    +----+---+
                         |
            +------------+------------+
            |            |            |
       +----v---+  +----v---+  +-----v-----+
       | User   |  | System |  | DeadLetter |
       | Guardn |  | Guardn |  |   Actor    |
       +----+---+  +--------+  +------------+
            |
       +----v---+
       | /user  |
       +----+---+
            |
     +------+------+
     |      |      |
  +--v-+ +--v-+ +--v-+
  | A  | | B  | | C  |
  +----+ +----+ +----+
     |
  +--v-+ +--v-+
  | A1 | | A2 |
  +----+ +----+
```

## Message Flow
```
Sender                    Receiver
  |                          |
  |-- Message ------------>  |
  |                          |-- Process message
  |                          |-- (Optional) Send response
  |<-- Reply -------------   |
  |                          |
  |-- Message to child --->  |
  |    (create actor)        |
  |                          |
```

## Supervision Hierarchy
```
Supervisor (watches children)
    |
    |-- Child A (if fails -> Supervisor decides: resume/restart/stop)
    |-- Child B
    |
    |-- Supervisor Strategy:
    |   - Resume: Continue (error handled)
    |   - Restart: Create new actor instance
    |   - Stop: Terminate child permanently
    |   - Escalate: Pass decision to parent
```
