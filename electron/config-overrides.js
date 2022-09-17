const NodePolyfillPlugin = require('node-polyfill-webpack-plugin');
const path = require('path');

module.exports = function override(config) {
  config.plugins = (config.plugins || []).concat([
    new NodePolyfillPlugin()
  ]);
  config.resolve.alias['@'] = path.join(__dirname, 'src');
  return config;
};