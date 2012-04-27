#!/bin/sh

PATH=$PATH:/usr/local/bin

for repo in AndroidCommon AndroidWebTestAPI InVehicleDeviceTestWeb OperatorWeb ; do
    rm -fr $repo
    git clone -b master git@github.com:odt/$repo.git
done

cat <<EOF >> database.yml
development:
  adapter: mysql2
  encoding: utf8
  reconnect: false
  database: odt_webapi_development
  pool: 5
  username: root
  socket: /var/lib/mysql/mysql.sock
EOF

cd OperatorWeb
git checkout add_invehicledevices_api
cp ../database.yml config/database.yml
bundle install
bundle exec rake db:drop db:create db:migrate
bundle exec rails server -d -p 3334
cd ..

cd InVehicleDeviceTestWeb
cp ../database.yml config/database.yml
bundle install
#bundle exec rake db:drop db:create db:migrate
bundle exec rails server -d -p 3333
cd ..

sed 's%^\(.*/AndroidWebTestAPI\)$%#\1%' --in-place tests/project.properties
cat tests/project.properties
cp -ru AndroidWebTestAPI/src tests/src

sed 's%=\.\./AndroidWebAPI$%=..%' --in-place AndroidWebTestAPI/project.properties
cat AndroidWebTestAPI/project.properties

# workaround
sed 's/192\.168\.104\.63:3000/10.0.2.2:3334/' --in-place tests/src/com/kogasoftware/odt/webapi/test/WebAPITestCase.java
sed 's/192\.168\.104\.63:3333/10.0.2.2:3333/' --in-place tests/src/com/kogasoftware/odt/webapi/test/WebAPITestCase.java
sed 's/192\.168\.104\.63:3333/10.0.2.2:3333/' --in-place AndroidWebTestAPI/tests/src/com/kogasoftware/odt/webtestapi/test/WebTestAPITestCase.java

. AndroidCommon/ci_setup.sh $*

cd `dirname $0`
ant $ant_arg lib-ci

cd OperatorWeb
(kill -INT $(cat tmp/pids/server.pid)) || true
cd ..

cd InVehicleDeviceTestWeb
(kill -INT $(cat tmp/pids/server.pid)) || true
cd ..

