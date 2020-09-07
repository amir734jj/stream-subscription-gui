package services;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class FileService {
    public byte[] base64ToStream(String base64) throws UnsupportedEncodingException {
        byte[] decodedString = Base64.getDecoder().decode(base64.getBytes("UTF-16"));

        return decodedString;
    }
}
