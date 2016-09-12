#!/usr/bin/env node

var yargs = require('yargs');

require('yargs')
    .usage('Usage: yawp <command> [options]')
    .commandDir('cmds')
    .demand(1, '')
    .argv