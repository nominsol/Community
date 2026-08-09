package com.example.Community.util;

import com.example.Community.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class FileUtil {

    private static String staticBaseUrl;

    @Value("${cloud.aws.s3.public-base-url}")
    public void setStaticBaseUrl(String value) {
        FileUtil.staticBaseUrl = value;
    }

    public static String extractPathFromUrl(String url) {
        if (url == null || url.isBlank()) return null;
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            URI uri = new URI(url);
            String path = uri.getPath();
            return path != null ? path : url;
        } catch (URISyntaxException e) {
            return url;
        }
    }

    public static String toFullUrl(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return null;
        if (relativePath.startsWith("http")) return relativePath;
        return staticBaseUrl + relativePath;
    }

    public static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int z = (63 - Long.numberOfLeadingZeros(bytes)) / 10;
        return String.format("%.1f %sB", (double)bytes / (1L << (z * 10)), " KMGTPE".charAt(z));
    }
}