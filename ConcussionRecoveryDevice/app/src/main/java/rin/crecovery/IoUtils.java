package rin.crecovery;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class IoUtils {

    /**
     *
     * @return
     */
    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param context
     * @param fileDir
     * @param fileName
     * @param out
     */
    public static void writeBytestoFile(Context context, String fileDir, String fileName,
                                        byte[] out) {
        try {
            String path = getStorageDir(context, fileDir).getPath()
                    + File.separator + fileName;

            FileOutputStream fos = new FileOutputStream(path);
            fos.write(out);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param context
     * @param fileDir
     * @param fileName
     * @return
     */
    public static String readStringFromFile(Context context, String fileDir, String fileName) {
        String string = "";
        String line;

        try {
            String path = getStorageDir(context, fileDir).getPath()
                    + File.separator + fileName;

            FileInputStream fin = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            while ((line = reader.readLine()) != null) {
                string += line + System.getProperty("line.separator");
            }
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return string;
    }

    /**
     *
     * @param context
     * @param fileDir
     * @param fileName
     * @return
     */
    public static boolean isFileExisting(Context context, String fileDir, String fileName) {
        String path = getStorageDir(context, fileDir).getPath()
                + File.separator + fileName;
        File file = new File(path);
        return file.exists();
    }

    /**
     *
     * @param context
     * @param fileDir
     * @return
     */
    private static File getStorageDir(Context context, String fileDir) {
        // Get the directory for the user's public pictures directory.
        File file = new File(context.getExternalFilesDir(null),
                fileDir);
        file.mkdirs();

        return file;
    }

}

