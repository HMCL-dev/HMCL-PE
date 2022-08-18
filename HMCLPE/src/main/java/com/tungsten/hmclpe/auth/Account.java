package com.tungsten.hmclpe.auth;

import com.tungsten.hmclpe.auth.offline.OfflineSkinSetting;

public class Account {
    public int loginType;
    public String email;
    public String password;
    public String user_type;
    public String auth_session;
    public String auth_player_name;
    public String auth_uuid;
    public String auth_access_token;
    public String auth_client_token;
    public String refresh_token;
    public String loginServer;
    public String texture;
    public OfflineSkinSetting offlineSkinSetting;

    public Account (int loginType,String email,String password,String user_type,String auth_session,String auth_player_name,String auth_uuid,String auth_access_token,String auth_client_token,String refresh_token,String loginServer,String texture){
        this.loginType = loginType;
        this.email = email;
        this.password = password;
        this.user_type = user_type;
        this.auth_session = auth_session;
        this.auth_player_name = auth_player_name;
        this.auth_uuid = auth_uuid;
        this.auth_access_token = auth_access_token;
        this.auth_client_token = auth_client_token;
        this.refresh_token = refresh_token;
        this.loginServer = loginServer;
        this.texture = texture;
    }

    public void refresh(Account account) {
        this.loginType = account.loginType;
        this.email = account.email;
        this.password = account.password;
        this.user_type = account.user_type;
        this.auth_session = account.auth_session;
        this.auth_player_name = account.auth_player_name;
        this.auth_uuid = account.auth_uuid;
        this.auth_access_token = account.auth_access_token;
        this.auth_client_token = account.auth_client_token;
        this.refresh_token = account.refresh_token;
        this.loginServer = account.loginServer;
        this.texture = account.texture;
        this.offlineSkinSetting = account.offlineSkinSetting;
    }
}
