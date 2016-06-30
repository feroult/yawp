var yargs = require('yargs');
var webpack = require('webpack');
var path = require('path');

var mode = yargs.argv.mode;
var env = yargs.argv.env || 'node';

var plugins = [], outputFile;

var libraryName = env === 'node' ? 'node' : 'yawp';

if (mode === 'build') {
    //plugins.push(new webpack.optimize.DedupePlugin());
    //plugins.push(new webpack.optimize.OccurrenceOrderPlugin(true));
    plugins.push(new webpack.optimize.UglifyJsPlugin({
        compress: {
            warnings: false
        }
    }));

    outputFile = libraryName + (env === 'node' ? '.js' : '.min.js');
} else {
    outputFile = libraryName + '-dev.js';
}

var configNode = {
    babel: {
        presets: ["es2015", "stage-0"],
        plugins: ["babel-plugin-add-module-exports", "syntax-async-functions", ["transform-runtime", {
            polyfill: false,
            regenerator: true
        }]]
    }
};

var configBrowser = {
    babel: {
        presets: ["es2015"],
        plugins: ["babel-plugin-add-module-exports"]
    }
};

var config = {
    entry: __dirname + '/src/' + env + '/index.js',
    devtool: 'source-map',
    output: {
        path: __dirname + '/',
        filename: outputFile,
        library: 'yawp',
        libraryTarget: 'umd',
        umdNamedDefine: true
    },
    module: {
        loaders: [
            {
                test: /(\.jsx|\.js)$/,
                loader: 'babel',
                exclude: /(node_modules|bower_components)/,
                query: env === 'node' ? configNode.babel : configBrowser.babel
            }
            //,
            //{
            //    test: /(\.jsx|\.js)$/,
            //    loader: "eslint-loader",
            //    exclude: /node_modules/
            //}
        ]
    },
    resolve: {
        root: path.resolve('./src'),
        extensions: ['', '.js']
    },
    plugins: plugins
};

module.exports = config;
