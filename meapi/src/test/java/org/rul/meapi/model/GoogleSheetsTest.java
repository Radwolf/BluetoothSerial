package org.rul.meapi.model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;

import junit.framework.Assert;

import org.junit.Test;
import org.rul.meapi.Utils;
import org.rul.meapi.service.GoogleSheetsService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rgonzalez on 24/05/2016.
 */
public class GoogleSheetsTest {

    @Test
    public void pruebaBasicaTest(){
        ByteBuffer faceSheet = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        faceSheet.put(new byte[]{
                0, 0, 0, 0, 48, 72, 68, 34,
                68, 72, 48, 0, 0, 0, 0, 0});
        ByteBuffer cadenaFace = GoogleSheetsService.getFaceSheets("Corazon");
        System.out.println(Utils.bytesToHexString(faceSheet.array()));
        System.out.println(Utils.bytesToHexString(cadenaFace.array()));
        int position;
        if(faceSheet.limit() == cadenaFace.limit()) {
            for (position = 0; position < faceSheet.limit()
                    && cadenaFace.get(position) == faceSheet.get(position); position++) {
            }
        }else{
            position = -1;
        }
        Assert.assertEquals(cadenaFace.limit(), position);
    }
}
