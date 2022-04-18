package com.test;

import ai.onnxruntime.reactnative.OnnxruntimeModule;
import ai.onnxruntime.reactnative.TensorHelper;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DataHandler extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    @NonNull
    @Override
    public String getName() {
        return "DataHandler";
    }

    DataHandler(ReactApplicationContext context) throws Exception {
        super(context);
        reactContext = context;
    }

    @ReactMethod
    public void getLocalModelPath(Promise promise) {
        try {
            //promise.resolve(reactContext.getExternalFilesDir(null).toString());
            String modelPath = copyFile(reactContext, "mobilenet.ort");
            promise.resolve(modelPath);
        } catch (Exception e) {
            promise.resolve( e.toString());
        }
    }

    private static String copyFile(Context context, String filename) throws Exception {
        File file = new File(context.getExternalFilesDir(null), filename);
        if (!file.exists()) {
            try (InputStream in = context.getAssets().open(filename)) {
                try (OutputStream out = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int read = in.read(buffer);
                    while (read != -1) {
                        out.write(buffer, 0, read);
                        read = in.read(buffer);
                    }
                }
            }
        }

        return file.toURI().toString();
    }
}