package com.example.danilo.leiturabyte;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements DownloadListener {

    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/arquivo";
    EditText editText;
    DownloadManager downloadManager ;
    File dir;
    String file_name = "sx";
    EnviarRequisicaoHttp enviarRequisicaoHttp =  new EnviarRequisicaoHttp(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dir = new  File(path);
        dir.mkdir();
        editText = (EditText) findViewById(R.id.editText);


    }

    public  void acessar(View view) throws IOException
    {
        // Baixando , armazenando ele em um diretório especifico e fazendo a leitura dos status da câmera
        File del_file = new File (path + "/"+file_name);

        if(del_file.exists())
            del_file.delete();

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        //Uri uri = Uri.parse("http://mestredossites.com.br/wp-content/uploads/2012/01/google-imagens-.jpg");
        Uri uri = Uri.parse("http://10.5.5.9/camera/sx?t=terzaghi");
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setVisibleInDownloadsUi(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN); //Escondendo a notificação


        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        request.setDestinationInExternalPublicDir("/arquivo", file_name);

        Long reference = downloadManager.enqueue(request);


    }


    public byte[] getBytes(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public String toBinary( byte[] bytes )
    {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    public static int integerfrmbinary(String str){
        double j=0;
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)== '1'){
                j=j+ Math.pow(2,str.length()-1-i);
            }

        }
        return (int) j;
    }

    BroadcastReceiver onComplete=new BroadcastReceiver()
    {
        public void onReceive(Context ctxt, Intent intent)
        {

            File file = new File (path + "/"+file_name);
            int size = (int) file.length();

            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] buffer = new byte[size];
            int bytesRead = 0;
            try
            {
                bytesRead = in.read(buffer, 0, buffer.length);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            String binario = toBinary(buffer);
            int break_line=0;
            int j=0;
            String numero;
            int decimalValue;
            int[] Status = new int[bytesRead];
            int pos = 0;
            editText.setText("");
            while(j<binario.length())
            {
                numero =  binario.subSequence(j,j+8).toString();
                decimalValue = integerfrmbinary(numero);
                Status[pos] = decimalValue;
                editText.setText(editText.getText()+"\n"+pos+" - "+Status[pos]+" - "+binario.subSequence(j,j+8));
                j=j+8;

                pos++;

            }
            editText.setFocusable(false);
            Log.d("bytes",binario);
        }
    };

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

    }

    public void ler(View view) throws IOException
    {
        String url="http://10.5.5.9/camera/se?t=terzaghi";

        enviarRequisicaoHttp.GetResponse(url, new VolleyCallback()
        {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccessResponse(String result)
            {
                int size = (int) result.length();
                InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.ISO_8859_1));

                byte[] buffer = new byte[size];
                int bytesRead = 0;
                try {
                    bytesRead = stream.read(buffer, 0, buffer.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String binario = toBinary(buffer);
                int break_line=0;
                int j=0;
                String numero;
                int decimalValue;
                int[] Status = new int[bytesRead];
                int pos = 0;
                editText.setText("");
                while(j<binario.length())
                {
                    numero =  binario.subSequence(j,j+8).toString();
                    decimalValue = integerfrmbinary(numero);
                    Status[pos] = decimalValue;
                    editText.setText(editText.getText()+"\n"+pos+" - "+Status[pos]+" - "+binario.subSequence(j,j+8));
                    j=j+8;

                    pos++;

                }
                editText.setFocusable(false);
                Log.d("bytes",binario);

            }
        });

    }


}
