package org.thezero.wifipv.list;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.thezero.wifipv.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by thezero on 08/04/15.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>
{

    private List<WiFiEntry> entry;

    private Context c;

    public CardAdapter(Context context , List<WiFiEntry> e)
    {
        this.c = context;
        if(e!=null)
            this.entry = e;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i )
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.entry_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i ) {
        final WiFiEntry p = entry.get(i);

        viewHolder.mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mPSK.setText(c.getString(R.string.pass)+" "+p.getPSK());
                viewHolder.mID.setText(c.getString(R.string.identity)+" "+p.getIdentity());
            }
        });

        viewHolder.mSSID.setText(c.getString(R.string.ssid) + " " + p.getSSID());
        if(p.getPSK().length()>1){
            String pass = p.getPSK().substring(0, 1);
            for (int j=0; j<p.getPSK().length()-1; j++) {
                pass = pass + "*";
            }
            viewHolder.mPSK.setText(c.getString(R.string.pass)+" "+pass);
        }else{
            viewHolder.mPSK.setText(c.getString(R.string.pass)+" "+p.getPSK());
        }
        viewHolder.mKeyType.setText(c.getString(R.string.key)+" "+p.getKeyType());
        if(p.getKeyType().equals("NONE")){
            viewHolder.mPSK.setVisibility(View.GONE);
        }else{
            viewHolder.mPSK.setVisibility(View.VISIBLE);
        }
        if(p.getIdentity()==null){
            viewHolder.mID.setVisibility(View.GONE);
        }else{
            viewHolder.mID.setVisibility(View.VISIBLE);
            if(p.getIdentity().length()>1){
                String idn = p.getIdentity().substring(0, 1);
                for (int j=0; j<p.getIdentity().length()-1; j++) {
                    idn = idn + "*";
                }
                viewHolder.mID.setText(c.getString(R.string.identity)+" "+idn);
                Log.v("TAG",p.getIdentity().length()+" "+idn.length());
            }else{
                viewHolder.mID.setText(c.getString(R.string.identity)+" "+p.getIdentity());
            }

        }

        viewHolder.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action3:
                        QRCodeWriter writer = new QRCodeWriter();
                        try {
                            BitMatrix matrix = writer.encode(p.toString(), BarcodeFormat.QR_CODE, 700, 700, null);
                            int height = matrix.getHeight();
                            int width = matrix.getWidth();
                            final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                            for (int x = 0; x < width; x++){
                                for (int y = 0; y < height; y++){
                                    bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
                                }
                            }
                            final Dialog dialog = new Dialog(c, R.style.FullHeightDialog);
                            dialog.setContentView(R.layout.qrdialog);
                            ImageView qr_image = (ImageView) dialog.findViewById(R.id.qrimage);
                            qr_image.setImageBitmap(bmp);

                            Button dialogButton = (Button) dialog.findViewById(R.id.ok);
                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    File sdcard = Environment.getExternalStorageDirectory();
                                    FileOutputStream out = null;
                                    File check = new File(sdcard, "/wifipv/");
                                    if(!(check.exists()))
                                        check.mkdir();

                                    try {
                                        File myFile = new File(sdcard, "/wifipv/"+p.getSSID()+".jpg");
                                        out = new FileOutputStream(myFile);
                                        boolean success = bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                        if (success){
                                            MediaScannerConnection.scanFile(c, new String[]{myFile.getAbsolutePath()}, null,
                                                    new MediaScannerConnection.OnScanCompletedListener() {
                                                        @Override
                                                        public void onScanCompleted(String path, Uri uri) {
                                                            Log.v("TAG","file "+path+" was scanned seccessfully: "+uri);
                                                            Intent shareIntent = new Intent();
                                                            shareIntent.setAction(Intent.ACTION_SEND);
                                                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                                            shareIntent.setType("image/jpeg");
                                                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                            c.startActivity(Intent.createChooser(shareIntent, c.getText(R.string.send_to)));
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(c, c.getString(R.string.error_share), Toast.LENGTH_LONG).show();
                                        }
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } finally {
                                        if (out != null) try {
                                            out.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        } catch (WriterException e) {
                            Log.e("Tag",e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    case R.id.action4:
                        setClipboard(p.toString());
                        break;
                }
                return true;
            }
        });
    }

    private void setClipboard(String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(c, c.getString(R.string.copied), Toast.LENGTH_LONG).show();
    }

    public void add(WiFiEntry item, int position) {
        entry.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(WiFiEntry item) {
        int position = entry.indexOf(item);
        entry.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        entry.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return entry == null ? 0 : entry.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder
    {
        public TextView mSSID;
        public TextView mID;
        public TextView mPSK;
        public TextView mKeyType;
        public Toolbar mToolbar;
        public CardView mCard;

        public ViewHolder( View v )
        {
            super(v);
            mSSID = (TextView) v.findViewById(R.id.ssid);
            mID = (TextView) v.findViewById(R.id.id);
            mPSK = (TextView) v.findViewById(R.id.psk);
            mKeyType = (TextView) v.findViewById(R.id.keytype);
            mToolbar = (Toolbar) v.findViewById(R.id.toolbar2);
            mCard = (CardView)v.findViewById(R.id.cardview);

            mToolbar.inflateMenu(R.menu.popup);

        }
    }
}
