var exec = require('cordova/exec');

exports.decodeQrCode = function (config, success, error) {
    exec(success, error, 'ZxingScanner', 'decodeQrCode', [config]);
};
