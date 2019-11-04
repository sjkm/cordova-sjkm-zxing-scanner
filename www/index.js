var exec = require('cordova/exec');

exports.coolMethod = function (config, success, error) {
    exec(success, error, 'ZxingScanner', 'decodeQrCode', [config]);
};
