# Mental Models for Messaging

## The Postal Service Model
- **Producer**: Person sending a letter
- **Message**: The letter
- **Broker**: Postal service sorting facility
- **Topic/Queue**: Address on the envelope
- **Consumer**: Person receiving the letter
- **Routing**: Mail carrier delivering to correct address

## The Newspaper Model (Pub/Sub)
- **Publisher**: Newspaper company
- **Topic**: Newspaper section (Sports, Business)
- **Subscriber**: Reader who receives that section
- **Broker**: Distribution system

## The Assembly Line Model (Streams)
- Messages flow through processing stages
- Each stage transforms and forwards
- Multiple workers at each stage for scaling
- Buffers between stages handle speed differences
