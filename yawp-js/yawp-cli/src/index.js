#!/usr/bin/env node

require('shelljs/global');

var yargs = require('yargs');

var argv = require('yargs')
    .usage('Usage: yawp <command> [options]')
    .commandDir('cmds')
    .demand(1, '').argv;
