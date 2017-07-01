package net.meiteampower.twitterapi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import twitter4j.HttpParameter;
import twitter4j.HttpRequest;
import twitter4j.RequestMethod;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

//import jp.kielabo.utils.conf.AdsApiProperties;
//import jp.kielabo.utils.conf.NetworkProperties;

/**
 * Twitter Ads APIを実行する。
 *
 * @author K.SATOH
 */
public class TwitterApiAccessor {

    /** シングルトンインスタンス。シングルトンにしている意味は得にはありません。 */
    private static TwitterApiAccessor singleton;

    private static final Logger logger = Logger.getLogger(TwitterApiAccessor.class);

//    private static Configuration configuration;

    /**
     * 非公開コンストラクタ。 シングルトンのため。
     */
    private TwitterApiAccessor() {
    }

    /**
     * インスタンスを取得する。
     *
     * @return シングルトンインスタンス
     */
    public static TwitterApiAccessor getInstance() {
        if (singleton == null) {
            singleton = new TwitterApiAccessor();
        }
        return singleton;
    }

//    /**
//     * Ads APIを使用し、データを取得する。アクセストークンはアプリケーションのものを使用する。
//     *
//     * @param method
//     *            HTTP method
//     * @param resourceUrl
//     *            データを取得するURL
//     * @param apiParams
//     *            Ads APIを実行するために必要なパラメータ
//     * @param apiData
//     *            Ads APIの結果を格納するデータ
//     * @return 200 OKの場合のみtrue
//     * @throws Exception
//     *             取得した結果、401 や 403 が発生した場合など。
//     */
//    public boolean execute(String method, String resourceUrl, AdsApiParams apiParams, AdsApiData apiData)
//            throws Exception {
//
//        return execute(method, resourceUrl, apiParams, apiData, null, null);
//    }

    /**
     * Ads APIを使用し、データを取得する。
     *
     * @param method
     *            http method.
     * @param resourceUrl
     *            データを取得するURL
     * @param apiParams
     *            Ads APIを実行するために必要なパラメータ
     * @param apiData
     *            Ads APIの結果を格納するデータ
     * @return 200 OKの場合のみtrue
     * @throws IOException
     *             取得した結果、401 や 403 が発生した場合など。
     */
    public boolean execute(String method, String resourceUrl, TwParams apiParams, TwData apiData)
            throws IOException {

        // OAuthにおいて利用する変数宣言
        String urlStr = resourceUrl;

        // URLにaccount_idを埋め込む箇所があれば、指定したアカウントIDで置換する。
        if (urlStr.indexOf(":account_id") >= 0) {
            urlStr = urlStr.replace(":account_id", apiParams.getAccountId());
        }

        String oauthAccessToken = apiParams.getOauthAccessToken();
        String oauthAccessTokenSecret = apiParams.getOauthAccessTokenSecret();

//        // Authorizationヘッダを生成する
//        String authorizationHeader = generateAuthorizationHeader(method, resourceUrl, apiParams.getAccountId(),
//                apiParams.getQueryParams(), oauthAccessToken, oauthAccessTokenSecret);

        boolean isPost = "post".equals(method.toLowerCase());

        // クエリパラメータ設定
        String appendQeuries = "";
        if (apiParams.hasQueryParams()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : apiParams.getQueryParams().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                key = key == null ? "" : HttpParameter.encode(key);
                value = value == null ? "" : HttpParameter.encode(value);

                sb.append("&");
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            appendQeuries = sb.toString();
            if (appendQeuries.length() > 0) {
                appendQeuries = appendQeuries.substring(1);
            }

            // URLの末尾に結合する
            if (!isPost) {
                if (urlStr.indexOf("?") >= 0) {
                    if (!urlStr.endsWith("&")) {
                        urlStr += "&" + appendQeuries;
                    } else {
                        urlStr += appendQeuries;
                    }

                } else {
                    urlStr += "?" + appendQeuries;
                }
            }
        }

        // APIにアクセス
        URL url = new URL(urlStr);
        HttpURLConnection connection = null;
//        if ("1".equals(APIConfigurationConstants.getString(HTTP_PROXY_USE))) {
//            // プロキシを使用する場合
//
//            Proxy proxy = new Proxy(Proxy.Type.HTTP,
//                    new InetSocketAddress(APIConfigurationConstants.getString(HTTP_PROXY_SERVER),
//                            Integer.parseInt(APIConfigurationConstants.getString(HTTP_PROXY_PORT))));
//            connection = (HttpURLConnection) url.openConnection(proxy);
//        } else {
//            // プロキシを使用しない場合
//            connection = (HttpURLConnection) url.openConnection();
//        }
        connection = (HttpURLConnection) url.openConnection();

        if (appendQeuries.length() > 0 && isPost) {
            connection.setDoOutput(true);
        }

        // Authorizationヘッダを生成する
        String authorizationHeader = generateAuthorizationHeader(method, urlStr, apiParams.getAccountId(),
                isPost ? apiParams.getQueryParams() : null, oauthAccessToken, oauthAccessTokenSecret);

        logger.debug(String.format("authorizationHeader=[%s]", authorizationHeader));

        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", authorizationHeader);

        if (apiParams.hasRequestHeaders()) {
            for (Map.Entry<String, String> entry : apiParams.getRequestHeaders().entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        apiData.setMethod(method);
        apiData.setResourceUrl(urlStr);

        // リクエストヘッダー
        for (Map.Entry<String, List<String>> entry : connection.getRequestProperties().entrySet()) {
            apiData.addRequestHeader(entry.getKey(), entry.getValue());
        }

        // リクエストを送る
        if (connection.getDoOutput()) {
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(appendQeuries);

            apiData.setPostContent(appendQeuries);
        } else {
            connection.connect();
        }

        // レスポンスを受け取る
        // レスポンスコード
        int responseCode = connection.getResponseCode();
        apiData.setResponseCode(responseCode);

        // レスポンスヘッダー
        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            apiData.addResponseHeader(entry.getKey(), entry.getValue());
        }
        // Rate Limit
        if (apiData.getResponseHeaders().containsKey("x-rate-limit-limit")) {
            apiData.setRateLimitLimit(Integer.parseInt(apiData.getResponseHeaders().get("x-rate-limit-limit").get(0)));
        }
        if (apiData.getResponseHeaders().containsKey("x-rate-limit-remaining")) {
            apiData.setRateLimitRemaining(
                    Integer.parseInt(apiData.getResponseHeaders().get("x-rate-limit-remaining").get(0)));
        }
        if (apiData.getResponseHeaders().containsKey("x-rate-limit-reset")) {
            long l = Integer.parseInt(apiData.getResponseHeaders().get("x-rate-limit-reset").get(0));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(l * 1000);
            apiData.setRateLimitReset(calendar.getTime());
        }

        // コンテント
        boolean result = (responseCode == 200);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response;
            StringBuilder sb = new StringBuilder();
            while ((response = reader.readLine()) != null) {
                sb.append(response);
            }
            apiData.setContent(sb.toString());
        } catch (Exception e) {
            apiData.setException(e);
            logger.error(String.format("Twitter APIの実行に失敗しました。method=[%s], resourceUrl=[%s], params=[%s]",
            		method, resourceUrl, apiParams.toString()), e);
            result = false;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    // /**
    // * Basicの認可ヘッダー（Authorization: Basic）を作成する。
    // * @param consumerKey コンシューマキー
    // * @param consumerSecret コンシューマシークレット
    // * @return Authorizationヘッダー
    // */
    // private String makeBasicHeader(String consumerKey, String consumerSecret)
    // {
    // String key = consumerKey == null ? "" : consumerKey;
    // String secret = consumerSecret == null ? "" : consumerSecret;
    // return "Basic " + AdsApiCommon.encodeBase64String((key + ":" +
    // secret).getBytes());
    // }
    //
    // /**
    // * Bearerの認可ヘッダー（Authorization: Bearer）を作成する。
    // * @param consumerKey コンシューマキー
    // * @param consumerSecret コンシューマシークレット
    // * @return Authorizationヘッダー
    // */
    // private String makeBearerHeader(String accessToken) {
    // String key = accessToken == null ? "" : accessToken;
    // return "Bearer " + key;
    // }
    //
    // /**
    // * OAuthの認可ヘッダー（Authorization: OAuth）を作成する。
    // * @param method 使用するHTTP Method
    // * @param apiParams APIパラメータ
    // * @param consumerkey コンシューマキー
    // * @param consumerSecret コンシューマシークレット
    // * @param lOAuthToken OAuthトークン
    // * @param lOAuthTokenSecret OAuthトークンシークレット
    // * @param resourceUrl リクエストURL
    // * @return Authorizationヘッダー
    // * @throws NoSuchAlgorithmException
    // * @throws InvalidKeyException
    // */
    // private String execute(String method, AdsApiParams apiParams, String
    // consumerkey, String consumerSecret,
    // String lOAuthToken, String lOAuthTokenSecret, String resourceUrl)
    // throws NoSuchAlgorithmException, InvalidKeyException {
    //
    // // OAuthにおいて利用する共通パラメーター
    // // パラメーターはソートする必要があるためSortedMapを利用
    // SortedMap<String, String> params = new TreeMap<String, String>();
    // params.put("oauth_consumer_key", consumerkey);
    // params.put("oauth_signature_method", "HMAC-SHA1");
    // params.put("oauth_timestamp",
    // String.valueOf(AdsApiCommon.getUnixTime()));
    // params.put("oauth_nonce", String.valueOf(Math.random()));
    // params.put("oauth_version", "1.0");
    // if (lOAuthToken != null)
    // {
    // params.put("oauth_token", lOAuthToken);
    // }
    //
    // // コールバックURLが指定されている場合はパラメータに追加する
    // if (apiParams.getCallbackUrl() != null)
    // {
    // params.put("oauth_callback", apiParams.getCallbackUrl());
    // }
    //
    // // アクセストークン取得時にのみ利用するパラメーター
    // // アプリケーションの許可をした場合に表示される暗証番号を設定する
    // if (apiParams.getOauthVerifier() != null)
    // {
    // params.put("oauth_verifier", apiParams.getOauthVerifier());
    // }
    //
    // // 追加パラメータ
    // if (apiParams.hasQueryParams())
    // {
    // for (Map.Entry<String, String> entry :
    // apiParams.getQueryParams().entrySet())
    // {
    // String key = entry.getKey();
    // String value = entry.getValue();
    // params.put(key, value);
    // }
    // }
    //
    // {
    // /*
    // * 署名（oauth_signature）の生成
    // */
    // // パラメーターを連結する
    // String paramStr = "";
    // for (Entry<String, String> param : params.entrySet()) {
    // String key = param.getKey();
    // String value = param.getValue();
    //// if (!key.equals("oauth_callback"))
    // {
    // value = AdsApiCommon.urlEncode(value);
    // }
    //// paramStr += "&" + param.getKey() + "=" + param.getValue();
    // paramStr += "&" + key + "=" + value;
    // }
    // paramStr = paramStr.substring(1);
    //
    // // 署名対象テキスト（signature base string）の作成
    // String text = method + "&" + AdsApiCommon.urlEncode(resourceUrl) + "&"
    // + AdsApiCommon.urlEncode(paramStr);
    //
    // // 署名キーの作成
    // String key = AdsApiCommon.urlEncode(consumerSecret) + "&"
    // + AdsApiCommon.urlEncode(lOAuthTokenSecret);
    //
    // // HMAC-SHA1で署名を生成
    // SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
    // "HmacSHA1");
    // Mac mac = Mac.getInstance(signingKey.getAlgorithm());
    // mac.init(signingKey);
    // byte[] rawHmac = mac.doFinal(text.getBytes());
    // String signature = AdsApiCommon.encodeBase64String(rawHmac);
    //
    // // 署名をパラメータに追加
    // params.put("oauth_signature", signature);
    // }
    //
    // // Authorizationヘッダの作成
    // String paramStr = "";
    // for (Entry<String, String> param : params.entrySet()) {
    // paramStr += ", " + param.getKey() + "=\""
    // + AdsApiCommon.urlEncode(param.getValue()) + "\"";
    // }
    // paramStr = paramStr.substring(2);
    // String authorizationHeader = "OAuth " + paramStr;
    // return authorizationHeader;
    // }

    /**
     * Authorizationヘッダの値部分を生成する。
     * @param httpMethod
     *            method
     * @param resourceUrl データを取得するURL
     * @param accountId アカウントID
     * @param parameters APIアクセスに必要なパラメータ
     * @param oauthAccessToken アクセストークン。アプリのアクセストークンを使用しない場合は指定する。
     * @param oauthAccessTokenSecret アクセストークンシークレット。アプリのアクセストークンを使用しない場合は指定する。
     * @return Authorizationヘッダの値部分。「OAuth 」で始まる。
     */
    private String generateAuthorizationHeader(String httpMethod, String resourceUrl, String accountId,
            Map<String, String> parameters, String oauthAccessToken, String oauthAccessTokenSecret) {

        String authorizationHeader = null;
        try {
            OAuthAuthorization auth = null;
            if (oauthAccessToken != null && oauthAccessTokenSecret != null) {
                auth = new OAuthAuthorization(getConfiguration(oauthAccessToken, oauthAccessTokenSecret));
            } else {
                auth = new OAuthAuthorization(getConfiguration());
            }

            // リクエストメソッドオブジェクトを取得する
            RequestMethod method = getRequestMethod(httpMethod);

            // url
            String url = resourceUrl;
            if (accountId != null) {
                url = url.replaceAll(":account_id", accountId);
            }

            // parameters
            HttpParameter[] params = new HttpParameter[] {};
            List<HttpParameter> parameterList = new ArrayList<HttpParameter>();
            if (parameters != null) {
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    parameterList.add(new HttpParameter(entry.getKey(), entry.getValue()));
                }
                params = parameterList.toArray(new HttpParameter[] {});
            }

            // adding request headeres (no need yet.)
            Map<String, String> requestHeaders = new HashMap<String, String>();

            // http request object. auth is not necessary
            HttpRequest req = new HttpRequest(method, url, params, auth, requestHeaders);

            // Authorizationヘッダの値を取得
            authorizationHeader = auth.getAuthorizationHeader(req);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return authorizationHeader;
    }

    /**
     * リクエストメソッドオブジェクトを取得する。
     *
     * @param httpMethod
     *            HTTPメソッドの文字列
     * @return リクエストメソッドオブジェクト
     */
    private RequestMethod getRequestMethod(String httpMethod) {

        // request method
        RequestMethod method = null;
        if (httpMethod != null) {
            String methodString = httpMethod.toLowerCase();
            if (methodString.equals("post")) {
                method = RequestMethod.POST;
            } else if (methodString.equals("get")) {
                method = RequestMethod.GET;
            } else if (methodString.equals("put")) {
                method = RequestMethod.PUT;
            } else if (methodString.equals("delete")) {
                method = RequestMethod.DELETE;
            } else {
                throw new IllegalArgumentException("http method is not valid. httpMethod=" + httpMethod);
            }
        } else {
            throw new IllegalArgumentException("http method is null");
        }
        return method;
    }

    private Configuration getConfiguration(String oauthAccessToken, String oauthAccessTokenSecret) {
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true)
    		.setOAuthConsumerKey("1tTPxO6JAGbDyi4zZDI6ZlwQ3")
    		.setOAuthConsumerSecret("QYKYIgvhuJpAFKcP56u7wKQgjbyqFO2I9KFtJAOXvDtgwO1ard")
    		.setOAuthAccessToken(oauthAccessToken)
    		.setOAuthAccessTokenSecret(oauthAccessTokenSecret);
    	return cb.build();
    }

    private Configuration getConfiguration() {
    	return getConfiguration(
    			"796313650621317120-QLM5A6TVUbAS0OB1lO5GxTA5QblRfE4",
    			"fSX6OQTvCs0bRC3saZdrzxjWyhZCKRafxC61P8OXctZLV");
    }

}
