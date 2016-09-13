const chalk = require('chalk');
const repl = require('repl');
const yawp = require('yawp');

exports.command = 'shell';

exports.describe = 'Launch the yawp interactive shell';

exports.builder = function (yargs) {
    return yargs
        .usage('Usage: ' + chalk.bold('yawp') + ' shell [options]')
        .option('base', {
            alias: 'b',
            describe: 'Base API url',
            default: 'http://localhost:8080/api'
        })
        .example('yawp shell -h http://myapp.appspot.com/api');
};

exports.handler = function (argv) {
    var base = argv.base;

    yawp.config(function (c) {
        c.baseUrl(base);
    });

    var r = repl.start('> ');
    r.context.yawp = yawp;
};
