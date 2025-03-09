-- Insert sample data only if the table is empty
INSERT INTO slack_notification (channel, message, username, icon_emoji, sent, response_message)
SELECT '#general', 'Welcome to Slack Integration!', 'System Bot', ':robot_face:', TRUE, 'Success'
WHERE NOT EXISTS (SELECT 1 FROM slack_notification); 