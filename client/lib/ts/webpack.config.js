path = require('path');

module.exports = {
  entry: './src/tunnel/tunnel.ts',

  resolve: {
    extensions: ['.ts', '.tsx', '.js']
  },

  devtool: 'source-map',

  module: {
    rules: [
      { test: /\.tsx?$/, loader: "ts-loader" }
    ]
  },

  output: {
    path: path.resolve(__dirname, 'dist/tunnel'),
    filename: 'tunnel.js',
    library: 'RemoteWebAuthnClient',
    libraryTarget: 'umd'
  }
}
