const chalk = require('chalk');
const repl = require('repl');
const yawp = require('yawp');

exports.command = 'console';

exports.describe = 'Launch the yawp interactive console';

exports.builder = function (yargs) {
    return yargs
        .usage('Usage: ' + chalk.bold('yawp') + ' console [options]')
        .option('base', {
            alias: 'b',
            describe: 'Base API url',
            default: 'http://localhost:8080/api'
        })
        .example('yawp console -h http://myapp.appspot.com/api');
};

exports.handler = function (argv) {
    var base = argv.base;

    yawp.config(function (c) {
        c.baseUrl(base);
    });

    var r = repl.start('> ');
    r.context.yawp = yawp;
};
