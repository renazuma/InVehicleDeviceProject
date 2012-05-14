#!/bin/sh

cd OperatorWeb
(kill -INT $(cat tmp/pids/server.pid)) || true
cd ..

cd InVehicleDeviceTestWeb
(kill -INT $(cat tmp/pids/server.pid)) || true
cd ..

