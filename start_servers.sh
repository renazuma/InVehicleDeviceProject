#!/bin/sh

git submodule update --init
git submodule foreach 'git checkout master; git pull'

for mysql_socket in \
  /var/lib/mysql/mysql.sock /var/run/mysqld/mysqld.sock; do
  if [ -e $mysql_socket ]; then
    break
  fi
done

cat <<EOF >> database.yml
development:
  adapter: mysql2
  encoding: utf8
  reconnect: false
  database: odt_webapi_development
  pool: 5
  username: root
  socket: $mysql_socket
EOF

cd OperatorWeb
cp ../database.yml config/database.yml
bundle install
#bundle exec rake db:migrate:reset
bundle exec rake db:migrate
bundle exec rails server -d -p 3334
cd ..

cd InVehicleDeviceTestWeb
cp ../database.yml config/database.yml
bundle install
bundle exec rails server -d -p 3333
cd ..
