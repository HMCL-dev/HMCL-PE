package com.tungsten.hmclpe.auth.microsoft;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import android.os.Build;
import android.util.*;

import androidx.annotation.RequiresApi;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.tungsten.hmclpe.auth.AuthenticationException;
import com.tungsten.hmclpe.auth.yggdrasil.Texture;
import com.tungsten.hmclpe.auth.yggdrasil.TextureType;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.gson.tools.TolerableValidationException;
import com.tungsten.hmclpe.utils.gson.tools.Validation;
import com.tungsten.hmclpe.utils.io.HttpRequest;
import com.tungsten.hmclpe.utils.io.NetworkUtils;
import com.tungsten.hmclpe.utils.io.ResponseCodeException;

import java.io.*;
import java.net.*;
import java.util.*;

import net.kdt.pojavlaunch.utils.Tools;

import org.json.*;

public class Msa {
    private static final String authTokenUrl = "https://login.live.com/oauth20_token.srf";
    private static final String xblAuthUrl = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String xstsAuthUrl = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String mcLoginUrl = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String mcProfileUrl = "https://api.minecraftservices.com/minecraft/profile";

    public String msRefreshToken;
    public String mcName;
    public String mcToken;
    public String mcUuid;
    public String tokenType;
    public boolean doesOwnGame;

    public Msa(boolean isRefresh, String authCode) throws IOException, JSONException {
        acquireAccessToken(isRefresh, authCode);
    }

    public void acquireAccessToken(boolean isRefresh, String authcode) throws IOException, JSONException {

        URL url = new URL(authTokenUrl);
        Log.i("MicroAuth", "isRefresh=" + isRefresh + ", authCode= "+authcode);
        Map<Object, Object> data = new HashMap<>();

        data.put("client_id", "00000000402b5328");
        data.put(isRefresh ? "refresh_token" : "code", authcode);
        data.put("grant_type", isRefresh ? "refresh_token" : "authorization_code");
        data.put("redirect_url", "https://login.live.com/oauth20_desktop.srf");
        data.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");

        String req = ofFormData(data);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(req.getBytes("UTF-8").length));
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        try(OutputStream wr = conn.getOutputStream()) {
            wr.write(req.getBytes("UTF-8"));
        }
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(Tools.read(conn.getInputStream()));
            msRefreshToken = jo.getString("refresh_token");
            Log.i("MicroAuth","Acess Token = "+jo.getString("access_token"));
            acquireXBLToken(jo.getString("access_token"));
        }else{
            throwResponseError(conn);
        }

    }

    private void acquireXBLToken(String accessToken) throws IOException, JSONException {

        URL url = new URL(xblAuthUrl);

        Map<Object, Object> data = new HashMap<>();
        Map<Object, Object> properties = new HashMap<>();
        properties.put("AuthMethod", "RPS");
        properties.put("SiteName", "user.auth.xboxlive.com");
        properties.put("RpsTicket", accessToken);
        data.put("Properties",properties);
        data.put("RelyingParty", "http://auth.xboxlive.com");
        data.put("TokenType", "JWT");
        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(req.getBytes("UTF-8").length));
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        try(OutputStream wr = conn.getOutputStream()) {
            wr.write(req.getBytes("UTF-8"));
        }
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(Tools.read(conn.getInputStream()));
            Log.i("MicroAuth","Xbl Token = "+jo.getString("Token"));
            acquireXsts(jo.getString("Token"));
        }else{
            throwResponseError(conn);
        }
    }

    private void acquireXsts(String xblToken) throws IOException, JSONException {

        URL url = new URL(xstsAuthUrl);
        Map<Object, Object> data = new HashMap<>();
        Map<Object, Object> properties = new HashMap<>();
        properties.put("SandboxId", "RETAIL");
        properties.put("UserTokens",Collections.singleton(xblToken));
        data.put("Properties",properties);
        data.put("RelyingParty", "rp://api.minecraftservices.com/");
        data.put("TokenType", "JWT");
        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(req.getBytes("UTF-8").length));
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        try(OutputStream wr = conn.getOutputStream()) {
            wr.write(req.getBytes("UTF-8"));
        }

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(Tools.read(conn.getInputStream()));
            String uhs = jo.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
            Log.i("MicroAuth","Xbl Xsts = "+jo.getString("Token")+"; Uhs = " + uhs);
            acquireMinecraftToken(uhs,jo.getString("Token"));
        }else{
            throwResponseError(conn);
        }
    }

    private void acquireMinecraftToken(String xblUhs, String xblXsts) throws IOException, JSONException {

        URL url = new URL(mcLoginUrl);

        Map<Object, Object> data = new HashMap<>();
        data.put("identityToken", "XBL3.0 x=" + xblUhs + ";" + xblXsts);

        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(req.getBytes("UTF-8").length));
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        try(OutputStream wr = conn.getOutputStream()) {
            wr.write(req.getBytes("UTF-8"));
        }

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(Tools.read(conn.getInputStream()));
            Log.i("MicroAuth","MC token: "+jo.getString("access_token"));
            mcToken = jo.getString("access_token");
            tokenType = jo.getString("token_type");
            checkMcProfile(jo.getString("access_token"));
        }else{
            throwResponseError(conn);
        }
    }

    private void checkMcProfile(String mcAccessToken) throws IOException, JSONException {

        URL url = new URL(mcProfileUrl);

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
        conn.setUseCaches(false);
        conn.connect();

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            String s= Tools.read(conn.getInputStream());
            Log.i("MicroAuth","profile:" + s);
            JSONObject jsonObject = new JSONObject(s);
            String name = (String) jsonObject.get("name");
            String uuid = (String) jsonObject.get("id");
            String uuidDashes = uuid .replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            );
            doesOwnGame = true;
            Log.i("MicroAuth","UserName = " + name);
            Log.i("MicroAuth","Uuid Minecraft = " + uuidDashes);
            mcName=name;
            mcUuid=uuidDashes;
        }else{
            Log.i("MicroAuth","It seems that this Microsoft Account does not own the game.");
            doesOwnGame = false;
            throwResponseError(conn);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Optional<Map<TextureType, Texture>> getTextures(MinecraftProfileResponse profile) {
        Objects.requireNonNull(profile);

        Map<TextureType, Texture> textures = new EnumMap<>(TextureType.class);

        if (!profile.skins.isEmpty()) {
            textures.put(TextureType.SKIN, new Texture(profile.skins.get(0).url, null));
        }
        // if (!profile.capes.isEmpty()) {
        // textures.put(TextureType.CAPE, new Texture(profile.capes.get(0).url, null);
        // }

        return Optional.of(textures);
    }

    public static MinecraftProfileResponse getMinecraftProfile(String tokenType, String accessToken)
            throws IOException, AuthenticationException {
        HttpURLConnection conn = HttpRequest.GET("https://api.minecraftservices.com/minecraft/profile")
                .authorization(tokenType, accessToken)
                .createConnection();
        int responseCode = conn.getResponseCode();
        if (responseCode == HTTP_NOT_FOUND) {
            throw new NoMinecraftJavaEditionProfileException();
        } else if (responseCode != 200) {
            throw new ResponseCodeException(new URL("https://api.minecraftservices.com/minecraft/profile"), responseCode);
        }

        String result = NetworkUtils.readData(conn);
        return JsonUtils.fromNonNullJson(result, MinecraftProfileResponse.class);
    }

    public static class MinecraftProfileResponseSkin implements Validation {
        public String id;
        public String state;
        public String url;
        public String variant; // CLASSIC, SLIM
        public String alias;

        @Override
        public void validate() throws JsonParseException, TolerableValidationException {
            Validation.requireNonNull(id, "id cannot be null");
            Validation.requireNonNull(state, "state cannot be null");
            Validation.requireNonNull(url, "url cannot be null");
            Validation.requireNonNull(variant, "variant cannot be null");
        }
    }

    public static class MinecraftProfileResponseCape {

    }

    public static class MinecraftProfileResponse extends MinecraftErrorResponse implements Validation {
        @SerializedName("id")
        UUID id;
        @SerializedName("name")
        String name;
        @SerializedName("skins")
        List<MinecraftProfileResponseSkin> skins;
        @SerializedName("capes")
        List<MinecraftProfileResponseCape> capes;

        @Override
        public void validate() throws JsonParseException, TolerableValidationException {
            Validation.requireNonNull(id, "id cannot be null");
            Validation.requireNonNull(name, "name cannot be null");
            Validation.requireNonNull(skins, "skins cannot be null");
            Validation.requireNonNull(capes, "capes cannot be null");
        }
    }

    private static class MinecraftErrorResponse {
        public String path;
        public String errorType;
        public String error;
        public String errorMessage;
        public String developerMessage;
    }

    public static class NoMinecraftJavaEditionProfileException extends AuthenticationException {
    }

    public static String ofJSONData(Map<Object, Object> data) {
        return new JSONObject(data).toString();
    }

    public static String ofFormData(Map<Object, Object> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            try {
                builder.append(URLEncoder.encode(entry.getKey().toString(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                //Should not happen
            }
        }
        return builder.toString();
    }

    private static void throwResponseError(HttpURLConnection conn) throws IOException {
        String otherErrStr = "";
        String errStr = Tools.read(conn.getErrorStream());
        Log.i("MicroAuth","Error code: " + conn.getResponseCode() + ": " + conn.getResponseMessage() + "\n" + errStr);
        
        if (errStr.contains("NOT_FOUND") &&
            errStr.contains("The server has not found anything matching the request URI"))
        {
            // TODO localize this
            otherErrStr = "It seems that this Microsoft Account does not own the game. Make sure that you have bought/migrated to your Microsoft account.";
        }
        
        throw new RuntimeException(otherErrStr + "\n\nMSA Error: " + conn.getResponseCode() + ": " + conn.getResponseMessage() + ", error stream:\n" + errStr);
    }
}

