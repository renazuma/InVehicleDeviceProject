#!/bin/sh

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
bundle exec rake db:migrate:reset
#bundle exec rake db:migrate
bundle exec rake "java:model:create[true]"
bundle exec rails server --daemon --environment=development --port=3334
cd ..

cp -fr InVehicleDeviceApiClient/tests/* tests
d=src/com/kogasoftware/odt/invehicledevice/apiclient/model/base
cp -fr InVehicleDeviceApiClient/$d/* $d
d=src/com/kogasoftware/odt/invehicledevice/apiclient/model
yes n | cp -ip InVehicleDeviceApiClient/$d/* $d/ 2> /dev/null

cd InVehicleDeviceTestApiServer
cp ../database.yml config/database.yml
bundle install
bundle exec rails server --daemon --environment=development --port=3333
cd ..
