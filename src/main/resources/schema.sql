-- Drop table if exists
DROP TABLE IF EXISTS slack_notification;

-- Create slack_notification table
CREATE TABLE IF NOT EXISTS slack_notification (
    id SERIAL PRIMARY KEY,
    channel VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    username VARCHAR(255),
    icon_emoji VARCHAR(255),
    sent_at TIMESTAMP,
    sent BOOLEAN DEFAULT FALSE,
    response_message TEXT
); 