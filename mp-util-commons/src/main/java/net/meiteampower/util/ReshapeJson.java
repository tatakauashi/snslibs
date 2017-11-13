package net.meiteampower.util;

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
 * @author kie
 */
public class ReshapeJson {

    /** デフォルトのインデント文字列 */
    private static final String DEFAULT_INDENT_STRING = "\t";
    /** デフォルトの改行文字列 */
    private static final String DEFAULT_RETURN = "\r\n";

    private final String indentString;

    /**
     * コンストラクタ。
     * インデント文字列にタブを設定
     */
    public ReshapeJson() {
    	indentString = DEFAULT_INDENT_STRING;
    }

    /**
     * コンストラクタ。
     * @param indentString インデント文字列
     */
    public ReshapeJson(String indentString) {
    	this.indentString = indentString;
    }

    public void executeFromFile(String filePath) {

        // 整形した文字列をファイルに出力する
        String outFilePath = filePath + "." + new Date().getTime() + ".txt";

        executeFromFile(filePath, outFilePath);
    }

    public void executeFromFile(String filePath, String outFilePath) {

        String reshaped = reshapeFromFile(filePath);

        saveReshapedJson(reshaped, outFilePath);
    }

    /**
     * JSON文字列を整形する。
     *
     * @param filePath
     *            整形するJSON文字列が格納されたファイルのフルパス
     * @param indent
     *            インデント文字列
     */
    public String reshapeFromFile(String filePath) {

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
            reshaped = executeDetail(sb.toString());

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
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
            		new FileOutputStream(filePath), "UTF-8"))) {
                writer.write(reshaped);
            } catch (IOException e) {
                e.printStackTrace();
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
    public String executeDetail(String json) {

        StringBuilder sb = new StringBuilder();
        int indentLevel = 0;
        for (char c : json.toCharArray()) {
            switch (c) {
            case '{':
            case '[':
                sb.append(c);
                sb.append(DEFAULT_RETURN);
                writeIndent(++indentLevel, sb);
                break;
            case '}':
            case ']':
                sb.append(DEFAULT_RETURN);
                writeIndent(--indentLevel, sb);
                sb.append(c);
                break;
            case ',':
                sb.append(c);
                sb.append(DEFAULT_RETURN);
                writeIndent(indentLevel, sb);
                break;
            default:
                sb.append(c);
                break;
            }
        }

        return sb.toString();
    }

    private void writeIndent(int indentLevel, StringBuilder sb) {
        for (int i = 0; i < indentLevel; i++)
        	sb.append(indentString);
    }

    public static final void main(String[] args) {
    	new ReshapeJson().executeFromFile(args[0]);
    }
}
