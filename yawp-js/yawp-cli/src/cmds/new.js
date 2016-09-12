exports.command = 'new <project>';

exports.describe = 'create a new yawp project';

exports.builder = function (yargs) {
    return yargs
        .usage('Usage: yawp new <project>')
        .example('yawp new my-app')
        .demand(1, '');
};

exports.handler = function (argv) {
    console.log('creating project:', argv.project);
};