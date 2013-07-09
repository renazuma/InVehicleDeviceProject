#!/bin/sh
# -*- coding: utf-8-unix -*-

cd operatorweb
(kill -INT $(cat tmp/pids/server.pid)) || true
cd ..

cd invehicledevice-testapiserver
(kill -INT $(cat tmp/pids/server.pid)) || true
cd ..
