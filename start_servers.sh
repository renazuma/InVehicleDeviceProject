#!/bin/sh
# -*- coding: utf-8-unix -*-

for mysql_socket in \
  /var/lib/mysql/mysql.sock \
  /var/run/mysqld/mysqld.sock \
  /tmp/mysql.sock \
  /var/mysql/mysql.sock \
  ; do
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

(gem list --local | grep bundler) || gem install bundler
(gem list --local | grep debugger-linecache) || gem install debugger-linecache -v '1.1.2' -- --with-ruby-include=$rvm_path/src/$RVM_RUBY_STRING/

cd operatorweb
cp ../database.yml config/database.yml
bundle install
bundle exec rake db:migrate:reset
bundle exec rails server --daemon --environment=development --port=3334
cd ..

cd invehicledevice-testapiserver
cp ../database.yml config/database.yml
bundle install
bundle exec rails server --daemon --environment=development --port=3333
cd ..
