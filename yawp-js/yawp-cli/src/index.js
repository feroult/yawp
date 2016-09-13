#!/usr/bin/env node

const chalk = require('chalk');

var yargs = require('yargs');

var argv = require('yargs')
    .usage('Usage: ' + chalk.bold('yawp') + ' <command> [options]')
    .commandDir('cmds')
    .demand(1, '').argv;
