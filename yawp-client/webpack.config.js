var yargs = require('yargs');
var webpack = require('webpack');
var path = require('path');

var mode = yargs.argv.mode;
var index = yargs.argv.index;

var plugins = [], outputFile;

var libraryName = index;

if (mode === 'build') {
    //plugins.push(new webpack.optimize.DedupePlugin());
    //plugins.push(new webpack.optimize.OccurrenceOrderPlugin(true));
    plugins.push(new webpack.optimize.UglifyJsPlugin({
        compress: {
            warnings: false
        }
    }));

    outputFile = libraryName + '.min.js';
} else {
    outputFile = libraryName + '.js';
}


var configBabel = {
    babel: {
        presets: ["es2015"],
        plugins: ["babel-plugin-add-module-exports"]
    }
};

var config = {
    entry: __dirname + '/src/web/index' + (index === 'yawp' ? '' : '.fixtures' ) + '.js',
    devtool: 'source-map',
    output: {
        path: __dirname + '/lib/web/',
        filename: outputFile,
        library: index,
        libraryTarget: 'umd',
        umdNamedDefine: true
    },
    module: {
        loaders: [
            {
                babelrc: false,
                test: /(\.jsx|\.js)$/,
                loader: 'babel',
                exclude: /(node_modules|bower_components)/,
                query: configBabel.babel
            },
            {
                babelrc: false,
                test: /(\.json)$/,
                loader: 'json'
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
