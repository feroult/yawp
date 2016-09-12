const chalk = require('chalk');
var shell = require('../shell');

exports.command = 'new <project>';

exports.describe = 'create a new yawp project';

exports.builder = function (yargs) {
    return yargs
        .usage('Usage: yawp new <project>')
        .example('yawp new testapp')
        .demand(1, '');
};

exports.handler = function (argv) {
    var project = argv.project;

    console.log('Creating project:', project);

    var cmd = 'mvn archetype:generate -B' +
        ' -DarchetypeGroupId=io.yawp' +
        ' -DarchetypeArtifactId=yawp' +
        ' -DarchetypeVersion=LATEST' +
        ' -DgroupId=' + project +
        ' -DartifactId=' + project +
        ' -Dversion=1.0-SNAPSHOT'

    shell.run(cmd);
};
