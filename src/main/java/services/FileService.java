package services;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class FileService {
    public byte[] base64ToStream(String base64) throws UnsupportedEncodingException {
        byte[] name = Base64.getEncoder().encode("hello World".getBytes());
        byte[] decodedString = Base64.getDecoder().decode(new String(name).getBytes("UTF-8"));

        return decodedString;
    }
}
