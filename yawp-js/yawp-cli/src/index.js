#!/usr/bin/env node
const chalk = require('chalk');
const yargs = require('yargs');

const cmds = [
    {path: './cmds/new', alias: 'n'},
    {path: './cmds/console', alias: 'c'}
];

var args = require('yargs')
    .usage('Usage: ' + chalk.bold('yawp') + ' <command> [options]')
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
    .demand(1, '');

cmds.forEach((cmd) => {
    var cmdModule = require(cmd.path);
    args.command(cmdModule);
    if (cmd.alias) {
        args.command(cmd.alias, false, cmdModule);
    }
});

var argv = args.argv;
