#!/usr/bin/with-contenv bash
source /common.sh

# Generate appropriate crontab file
sed -e "s/\${game}/$GAME/" /etc/crontabs/husky.template > /etc/crontabs/husky
chown husky:husky /etc/crontabs/husky

# Make sure that 'husky' has ownership over the log file
touch /data/cronjobs.log
chown husky:husky /data/cronjobs.log

msg "Created crontab file"