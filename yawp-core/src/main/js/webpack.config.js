var path = require('path');
var webpack = require('webpack');

module.exports = {
	devtool: 'source-map',
	entry: [
		'./yawp-cli'
	],
	output: {
		path: path.join(__dirname, 'dist'),
		library: "yawp",
		filename: 'yawp.min.js',
		libraryTarget: "umd"
	},
	plugins: [
		new webpack.optimize.OccurenceOrderPlugin(),
		new webpack.DefinePlugin({
				'process.env': {
					'NODE_ENV': JSON.stringify('production')
				}
			}),
		new webpack.optimize.UglifyJsPlugin({
				compressor: {
					warnings: false
				}
			})
	],
	module: {}
};
