#!/usr/bin/env node

const chalk = require('chalk');

var yargs = require('yargs');

var argv = require('yargs')
    .usage('Usage: ' + chalk.bold('yawp') + ' <command> [options]')
    .commandDir('cmds')
    .help('help')
    .alias('help', 'h')
    .wrap(null)
    .updateStrings({
        'Commands:': chalk.bold('Commands:'),
        'Options:': chalk.bold('Options:'),
        'Examples:': chalk.bold('Examples:'),
        'boolean': chalk.bold('boolean'),
        'default:': chalk.bold('default:')
    })
    .demand(1, '').argv;
