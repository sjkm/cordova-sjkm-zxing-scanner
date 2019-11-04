package cordova.sjkm.zxingscanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ZxingScanner extends CordovaPlugin {

    private static final String DECODE_ERROR_MESSAGE = "Could not decode image!";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("decodeQrCode")) {
            JSONObject config = args.getJSONObject(0);
            String filePath = config.optString("filePath", null);

            if(filePath != null) {
                this.decodeQrCode(filePath, callbackContext);
            }
            return true;
        }
        return false;
    }

    private void decodeQrCode(String filePath, CallbackContext callbackContext) {
        File file = new File(filePath);

        Log.d("ZxingScanner", "Start decoding QR Code image: " + file.getAbsolutePath() + "...");

        new ReadQrCodeAsyncTask(callbackContext, file.getAbsolutePath()).execute();
    }

    class ReadQrCodeAsyncTask extends AsyncTask<Void, Void, String> {

        private CallbackContext callbackContext;
        private String filePath;

        public ReadQrCodeAsyncTask(CallbackContext callbackContext, String filePath) {
            this.callbackContext = callbackContext;
            this.filePath = filePath;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Bitmap bMap = BitmapFactory.decodeFile(this.filePath);
            String decoded = null;

            int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Reader reader = new QRCodeReader();
            try {
                Result result = reader.decode(bitmap);
                decoded = result.getText();
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (ChecksumException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }
            return decoded;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null) {
                this.callbackContext.success(result);
            } else {
                this.callbackContext.error(DECODE_ERROR_MESSAGE);
            }
        }

    }

}
