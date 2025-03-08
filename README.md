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

The application uses an H2 in-memory database by default. You can access the H2 console at http://localhost:8080/h2-console with these credentials:

- JDBC URL: `jdbc:h2:mem:slackdb`
- Username: `sa`
- Password: `password`

## License

MIT 