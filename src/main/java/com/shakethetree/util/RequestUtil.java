package com.shakethetree.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author xubing
 * @description //TODO 设计说明
 * @date 19-10-28
 * @copyright 中网易企秀
 */
public class RequestUtil {

    public static String readRequestBody(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        try(InputStream inputStream = request.getInputStream()) {
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                int ch;
                while((ch = reader.read()) != -1) {
                    stringBuilder.append((char) ch);
                }
                return stringBuilder.toString();
            }
        }catch(Exception e) {
            System.out.println("request body xml transfer to map failed!" + e);
            return stringBuilder.toString();
        }
    }
}
