-- Add tags column to slack_notification table
ALTER TABLE slack_notification ADD COLUMN tags VARCHAR(255); 