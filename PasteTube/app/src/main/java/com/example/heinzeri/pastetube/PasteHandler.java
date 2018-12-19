package com.example.heinzeri.pastetube;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;

public class PasteHandler {
    public String TAG ="PasteTube";

    public ClipData clipData;
    public String online_data;
    public String online_data_old = null;
    public String offline_data_old = null;
    public ClipData offline_data_clip;
    public String offline_data;

    private ClipboardManager clipboardManager;
    private PasteTube client;
    private String userid;

    public PasteHandler(String p_userid, ClipboardManager p_clipboard, PasteTube p_client){
        clipboardManager = p_clipboard;
        client = p_client;
    }

    public void check(){
        online_data = client.Paste(userid);
        offline_data_clip = clipboardManager.getPrimaryClip();
        offline_data = offline_data_clip.getItemAt(0).getText().toString();


        if (!online_data.equals(online_data_old)){
            online_data_old = online_data;
            offline_data_old = online_data;

            clipData = ClipData.newPlainText("synced text",
                    online_data);

            clipboardManager.setPrimaryClip(clipData);

            Log.i(TAG, online_data);
        } else if (!offline_data.equals(offline_data_old)){
            online_data_old = offline_data;
            offline_data_old = offline_data;

            client.Copy(userid, offline_data);

            Log.i(TAG, offline_data);

        }



    /*

    // Examines the item on the clipboard. If getText() does not return null, the clip item contains the
    // text. Assumes that this application can only handle one item at a time.
    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

    // Gets the clipboard as text.
    String pasteData = item.getText().toString();

    // If the string contains data, then the paste operation is done
    if (pasteData != "") {
        return;

        // The clipboard does not contain text. If it contains a URI, attempts to get data from it
    } else {
        Log.v(TAG, item.getText().toString());
        client.Copy(userid, item.getText().toString());
    }
    */
    }
}
