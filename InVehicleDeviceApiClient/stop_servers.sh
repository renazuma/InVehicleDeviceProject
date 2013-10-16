#!/bin/sh
# -*- coding: utf-8-unix -*-

cd OperatorWeb
(kill -INT $(cat tmp/pids/server.pid)) || true
cd ..

cd TestApiServer
(kill -INT $(cat tmp/pids/server.pid)) || true
cd ..
