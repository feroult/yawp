#!/bin/bash
mkdir phantomjs-2.0.0
cd phantomjs-2.0.0
wget https://s3.amazonaws.com/travis-phantomjs/phantomjs-2.0.0-ubuntu-12.04.tar.bz2
tar -xjf phantomjs-2.0.0-ubuntu-12.04.tar.bz2
cd ..
export PATH=$PWD/phantomjs-2.0.0:$PATH
