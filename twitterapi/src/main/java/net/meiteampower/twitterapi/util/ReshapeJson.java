package net.meiteampower.twitterapi.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 * JSON文字列を整形する。
 *
 * @author K.SATOH
 */
public class ReshapeJson {

    /** デフォルトのインデント文字列 */
    private static final String DEFAULT_INDENT_STRING = "\t";
    /** デフォルトの改行文字列 */
    private static final String DEFAULT_RETURN = "\r\n";

    public void executeFromFile(String filePath) {
        executeFromFile(filePath, DEFAULT_INDENT_STRING);
    }

    public void executeFromFile(String filePath, String indent) {

        String reshaped = reshapeFromFile(filePath, indent);

        // 整形した文字列をファイルに出力する
        String outFilePath = filePath + "." + new Date().getTime() + ".txt";

        saveReshapedJson(reshaped, outFilePath);
    }

    /**
     * JSON文字列を整形する。インデントはデフォルトを使用する。
     *
     * @param filePath
     *            整形するJSON文字列が格納されたファイルのフルパス
     */
    public String reshapeFromFile(String filePath) {
        return reshapeFromFile(filePath, DEFAULT_INDENT_STRING);
    }

    /**
     * JSON文字列を整形する。
     *
     * @param filePath
     *            整形するJSON文字列が格納されたファイルのフルパス
     * @param indent
     *            インデント文字列
     */
    public String reshapeFromFile(String filePath, String indent) {

        String reshaped = null;
        BufferedReader reader = null;
        try {
            // ファイルからJSON文字列を取得する
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));

            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // JSON文字列を整形する
            reshaped = executeDetail(sb.toString(), indent);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return reshaped;
    }

    private void saveReshapedJson(String reshaped, String filePath) {

        if (reshaped != null) {

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
                writer.write(reshaped);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * JSON文字列を整形する。
     *
     * @param filePath
     *            整形するJSON文字列が格納されたファイルのフルパス
     * @param indent
     *            インデント文字列
     */
    public String executeDetail(String json, String indent) {

        StringBuilder sb = new StringBuilder();
        int indentLevel = 0;
        for (char c : json.toCharArray()) {
            switch (c) {
            case '{':
            case '[':
                sb.append(c);
                sb.append(DEFAULT_RETURN);
                writeIndent(indent, ++indentLevel, sb);
                break;
            case '}':
            case ']':
                sb.append(DEFAULT_RETURN);
                writeIndent(indent, --indentLevel, sb);
                sb.append(c);
                break;
            case ',':
                sb.append(c);
                sb.append(DEFAULT_RETURN);
                writeIndent(indent, indentLevel, sb);
                break;
            default:
                sb.append(c);
                break;
            }
        }

        return sb.toString();
    }

    private void writeIndent(String indent, int indentLevel, StringBuilder sb) {
        for (int i = 0; i < indentLevel; i++, sb.append(indent))
            ;
    }
}