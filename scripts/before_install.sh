#!/bin/bash

# postgresql
psql -c 'create database yawp_test;' -U postgres

# phantomjs
mkdir phantomjs-2.0.0
cd phantomjs-2.0.0
wget https://s3.amazonaws.com/travis-phantomjs/phantomjs-2.0.0-ubuntu-12.04.tar.bz2
tar -xjf phantomjs-2.0.0-ubuntu-12.04.tar.bz2
cd ..
export PATH=$PWD/phantomjs-2.0.0:$PATH

# node
sudo apt-get remove -y nodejs
curl -sL https://deb.nodesource.com/setup_6.x | sudo -E bash -
sudo apt-get install -y nodejs
which node
node --version
export PATH=/usr/local/bin/node:$PATH
which node
node --version
