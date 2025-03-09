# Slack Integration Spring Boot Application

A Spring Boot application for sending and managing Slack notifications.

## Features

- Send messages to Slack channels or users
- Customize message username and emoji
- Track message delivery status
- View message history
- RESTful API for programmatic access
- Web interface for manual sending

## Prerequisites

- Java 17 or higher
- Gradle
- Slack workspace with Bot Token

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/slack-integration.git
cd slack-integration
```

### 2. Configure Slack API credentials

Edit `src/main/resources/application.properties` and update the following properties:

```properties
slack.bot-token=xoxb-your-bot-token
slack.signing-secret=your-signing-secret
slack.client-id=your-client-id
slack.client-secret=your-client-secret
```

To get these credentials:

1. Go to [Slack API Apps](https://api.slack.com/apps)
2. Create a new app or select an existing one
3. Under "OAuth & Permissions", add the following scopes:
   - `chat:write`
   - `chat:write.public`
   - `channels:read`
   - `users:read`
4. Install the app to your workspace
5. Copy the Bot User OAuth Token (starts with `xoxb-`)
6. Under "Basic Information", copy the Signing Secret

### 3. Build the application

```bash
./gradlew build
```

### 4. Run the application

```bash
./gradlew bootRun
```

The application will be available at http://localhost:8080

## Usage

### Web Interface

1. Open http://localhost:8080 in your browser
2. Fill in the channel (e.g., `#general` or `@username`) and message
3. Optionally set a custom username and emoji
4. Click "Send Message"
5. View message history at http://localhost:8080/history

### REST API

#### Send a message

```bash
curl -X POST http://localhost:8080/api/slack/send \
  -H "Content-Type: application/json" \
  -d '{
    "channel": "#general",
    "message": "Hello from API!",
    "username": "API Bot",
    "iconEmoji": ":robot_face:"
  }'
```

#### Get all notifications

```bash
curl -X GET http://localhost:8080/api/slack
```

#### Get a specific notification

```bash
curl -X GET http://localhost:8080/api/slack/1
```

#### Save a notification (without sending)

```bash
curl -X POST http://localhost:8080/api/slack \
  -H "Content-Type: application/json" \
  -d '{
    "channel": "#general",
    "message": "This will be saved but not sent"
  }'
```

#### Delete a notification

```bash
curl -X DELETE http://localhost:8080/api/slack/1
```

## Database

The application uses Flyway for database migrations. Migration scripts are located in `src/main/resources/db/migration`.

### PostgreSQL

By default, the application is configured to use PostgreSQL. Make sure you have PostgreSQL installed and running, and create a database named `slackdb`:

```bash
sudo -u postgres psql -c "CREATE DATABASE slackdb;"
```

### H2 (Development)

For development, you can switch to H2 in-memory database by updating the `application.properties` file:

```properties
# H2 Database configuration (for development)
spring.datasource.url=jdbc:h2:mem:slackdb
spring.datasource.username=sa
spring.datasource.password=password
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Comment out PostgreSQL configuration
# spring.datasource.url=jdbc:postgresql://localhost:5432/slackdb
# spring.datasource.username=postgres
# spring.datasource.password=postgres
# spring.datasource.driver-class-name=org.postgresql.Driver

# Update dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

You can access the H2 console at http://localhost:8090/h2-console with these credentials:

- JDBC URL: `jdbc:h2:mem:slackdb`
- Username: `sa`
- Password: `password`

### Creating New Migrations

To create a new migration:

1. Create a new SQL file in `src/main/resources/db/migration`
2. Name it following the Flyway naming convention: `V{version}__{description}.sql`
   - Example: `V3__Add_user_column.sql`
3. Write your SQL migration script
4. Run the application, and Flyway will automatically apply the migration

## License

MIT 