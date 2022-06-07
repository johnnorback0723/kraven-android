package com.kraven.utils;


import android.content.Context;
import android.net.Uri;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.kraven.application.KravenCustomer;
import com.kraven.data.URLFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AWSUtil {

    public static final String MY_BUCKET_LOCAL = "kravendev";
    public static final String MY_BUCKET = "kravenprod.s3.01";
    public static final String FOLDER_USER = "user/";
    public static AmazonS3Client sS3Client;
    private static TransferUtility sTransferUtility;
    private static Regions local = Regions.EU_WEST_1;
    private static Regions live = Regions.US_EAST_1;


    private static AWSCredentials awsCredentials;

    public static TransferUtility getTransferUtility() {
        if (sTransferUtility == null) {
            sS3Client = new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider());
            sS3Client.setRegion(Region.getRegion(URLFactory.IS_LOCAL ? local : live));

            sTransferUtility = TransferUtility.builder()
                    .context(KravenCustomer.appContext)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .s3Client(sS3Client)
                    .build();
        }

        return sTransferUtility;
    }

    public static void deleteImage(String fileName) {
        try {
            if (sS3Client == null) {
                sS3Client = new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider());
                sS3Client.setRegion(Region.getRegion(Regions.EU_WEST_1));
            }
            sS3Client.deleteObject(new DeleteObjectRequest(URLFactory.IS_LOCAL ? MY_BUCKET_LOCAL : MY_BUCKET, AWSUtil.FOLDER_USER + fileName));
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        }
    }


    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[]{
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0; ; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format(Locale.US, "%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

    public static File copyContentUriToFile(Context context, Uri uri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(uri);
        File copiedData = new File(context.getDir("SampleImagesDir", Context.MODE_PRIVATE), UUID
                .randomUUID().toString());
        copiedData.createNewFile();

        FileOutputStream fos = new FileOutputStream(copiedData);
        byte[] buf = new byte[2046];
        int read = -1;
        while ((read = is.read(buf)) != -1) {
            fos.write(buf, 0, read);
        }

        fos.flush();
        fos.close();

        return copiedData;
    }


    public static void fillMap(Map<String, Object> map, TransferObserver observer, boolean isChecked) {
        int progress = (int) ((double) observer.getBytesTransferred() * 100 / observer
                .getBytesTotal());
        map.put("id", observer.getId());
        map.put("checked", isChecked);
        map.put("fileName", observer.getAbsoluteFilePath());
        map.put("progress", progress);
        map.put("bytes",
                getBytesString(observer.getBytesTransferred()) + "/"
                        + getBytesString(observer.getBytesTotal()));
        map.put("state", observer.getState());
        map.put("percentage", progress + "%");
    }
}