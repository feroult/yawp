const chalk = require('chalk');
var shell = require('../shell');

exports.command = 'new <project>';

exports.describe = 'create a new yawp project';

exports.builder = function (yargs) {
    return yargs
        .usage('Usage: ' + chalk.bold('yawp') + ' new <project>')
        .demand(1, '')
        .option('package', {
            alias: 'p',
            describe: 'project java package',
            default: 'project name'
        })
        .option('version', {
            alias: 'v',
            describe: 'project pom.xml version',
            default: '1.0-SNAPSHOT'
        })
        .example('yawp new testapp');
};

exports.handler = function (argv) {
    var project = argv.project;
    var pkg = argv.package === 'project name' ? project : argv.package;
    var version = argv.version;

    console.log('Creating project: ' + chalk.bold(project) +
        ', package: ' + chalk.bold(pkg) +
        ', version: ' + chalk.bold(version));

    var cmd = 'mvn archetype:generate -B' +
        ' -DarchetypeGroupId=io.yawp' +
        ' -DarchetypeArtifactId=yawp' +
        ' -DarchetypeVersion=LATEST' +
        ' -DgroupId=' + pkg +
        ' -DartifactId=' + project +
        ' -Dversion=' + version

    shell.run(cmd);
};
