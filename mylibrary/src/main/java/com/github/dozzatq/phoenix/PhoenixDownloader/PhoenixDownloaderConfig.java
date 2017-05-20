package com.github.dozzatq.phoenix.PhoenixDownloader;

/**
 * Created by RondailP on 10.10.2016.
 */
public class PhoenixDownloaderConfig {
    public final static String PARAM_FILENAME = ":PhoenixDownloader:FILENAME";
    public final static String PARAM_URL = ":PhoenixDownloader:URL";
    public final static String PARAM_RECEIVER = ":PhoenixDownloader:RECEIVER";
    public final static String PARAM_TOTAL_BYTES = ":PhoenixDownloader:TOTALBYTES";
    public final static String PARAM_RECEIVED_BYTES = ":PhoenixDownloader:RECEIVED";
    public final static String SEND_PARAMS_FORMAT = ".PhoenixDownloader:SEND_ID:%d";

    public final static String PARAM_STATUS = ".PhoenixDownloaderService:STATUS";

    public final static String PARAM_PROGRESS_PERCENTAGE = ".PhoenixDownloaderService:PERCENTAGE";

    public final static String PARAM_PROGRESS_EXCEPTION = ".PhoenixDownloaderService:EXCEPTION";

    public final static String PARAM_DOWNLOAD_RESULT = ".PhoenixDownloaderService:RESULT";

    public final static int STATUS_PRE_DOWNLOADING = 4;
    public final static int STASUS_DOWNLOADING = 1;
    public final static int STATUS_FAILED = 2;
    public final static int STATUS_DOWNLOADED = 3;
}
