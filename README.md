# Connected Social Media Site API

This is a social media application API that provides blog like functionality. It allows users to post messages that have
captions and can have either an image or video attached. Users can also like and comment on other posts.

The application uses MySQL for database needs and RabbitMQ for handling posts, likes, and comments. AWS S3 is also used to
store images and videos from posts. Okta is used as the identity service. A Docker file is included to Dockerize the
application.

## Required Environment Variables

- RABBITMQ_URL
- RABBITMQ_USERNAME
- RABBITMQ_PASSWORD
- AWS_ACCESS_KEY
- AWS_SECRET
- AWS_BUCKET_NAME
- DB_URL
- DB_USERNAME
- DB_PASSWORD
- OKTA_ISSUER
- OKTA_CLIENT
- OKTA_SECRET (Custom value)

## Dependencies Needed to Run the Application

1. MySQL
2. RabbitMQ
3. AWS S3
4. Okta
