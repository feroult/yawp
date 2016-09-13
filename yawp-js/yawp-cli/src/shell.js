const chalk = require('chalk');
const Spinner = require('cli-spinner').Spinner;

function info(s) {
    process.stdout.write(s);
}

function error(s) {
    process.stderr.write(s);
}

function clearLine() {
    process.stdout.write('\r\x1b[K');
}

function createSpinner() {
    var spinner = new Spinner('%s');
    spinner.setSpinnerString(0);
    spinner.start();
    return spinner;
}

export function run(cmd) {
    var spinner = createSpinner();

    var child = exec(cmd, {async: true, silent: true});

    var hasError = false;
    var output = '';

    child.stdout.on('data', function (data) {
        var line = '' + data;

        if (/ERROR/.test(line)) {
            hasError = true;
        }

        output += line;
    });

    child.on('exit', function () {
        spinner.stop();
        clearLine();
        if (!hasError) {
            info(chalk.bold('done!\n'));
        } else {
            error(chalk.red.bold('Error:\n'));
            error(output);
        }
    });
}
