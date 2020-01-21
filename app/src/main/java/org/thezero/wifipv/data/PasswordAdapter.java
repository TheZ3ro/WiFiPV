package org.thezero.wifipv.data;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import net.glxn.qrgen.android.QRCode;

import org.thezero.wifipv.R;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder>
{
    private List<Password> entry;
    private Context c;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView mSSID;
        TextView mID;
        TextView mPSK;
        TextView mKeyType;
        TextView mIDLabel;
        TextView mPSKLabel;
        ImageView mImg;
        Toolbar mToolbar;

        ViewHolder(View v)
        {
            super(v);
            mSSID = v.findViewById(R.id.ssid);
            mID = v.findViewById(R.id.id);
            mPSK = v.findViewById(R.id.psk);
            mKeyType = v.findViewById(R.id.keytype);
            mPSKLabel = v.findViewById(R.id.psk_label);
            mIDLabel = v.findViewById(R.id.id_label);
            mImg = v.findViewById(R.id.imageView);
            mToolbar = v.findViewById(R.id.toolbar);
            mToolbar.inflateMenu(R.menu.menu_popup);
        }
    }

    public PasswordAdapter(Context context , List<Password> e)
    {
        this.c = context;
        if (e != null)
            this.entry = e;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.entry_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Password p = entry.get(i);

        viewHolder.mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mPSK.setText(p.getPSK());
                viewHolder.mID.setText(p.getIdentity());
            }
        });

        viewHolder.mSSID.setText(p.getSSID());
        if (p.getPSK().length() > 1) {
            String pass = p.getPSK().substring(0, 1);
            pass = pass + "*****";
            viewHolder.mPSK.setText(pass);
        } else {
            viewHolder.mPSK.setText(p.getPSK());
        }

        viewHolder.mKeyType.setText(p.getKeyType());
        if (p.getKeyType().equals("NONE")) {
            viewHolder.mPSK.setVisibility(View.GONE);
            viewHolder.mPSKLabel.setVisibility(View.GONE);
            viewHolder.mImg.setImageResource(R.drawable.wifi_open);
        } else {
            viewHolder.mPSK.setVisibility(View.VISIBLE);
            viewHolder.mPSKLabel.setVisibility(View.VISIBLE);
            viewHolder.mImg.setImageResource(R.drawable.wifi_lock);
        }

        if (p.getIdentity() == null) {
            viewHolder.mID.setVisibility(View.GONE);
            viewHolder.mIDLabel.setVisibility(View.GONE);
        } else {
            viewHolder.mID.setVisibility(View.VISIBLE);
            viewHolder.mIDLabel.setVisibility(View.VISIBLE);
            if (p.getIdentity().length() > 1) {
                String idn = p.getIdentity().substring(0, 1);
                idn = idn + "*****";
                viewHolder.mID.setText(idn);
            } else {
                viewHolder.mID.setText(p.getIdentity());
            }
        }

        viewHolder.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.show_qr:
                        showQRCode(p.toString());
                        break;
                    case R.id.copy:
                        setClipboard(p.toString());
                        break;
                    case R.id.copy_password:
                        setClipboard(p.getPSK());
                        break;
                }
                return true;
            }
        });
    }

    private void setClipboard(String password) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", password);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(c, c.getString(R.string.copied), Toast.LENGTH_LONG).show();
        }
    }

    private void showQRCode(String password) {
        Bitmap bmp = QRCode.from(password).withSize(700, 700).bitmap();

        final Dialog dialog = new Dialog(this.c, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.qrdialog);

        ImageView qr_image =  dialog.findViewById(R.id.qrimage);
        qr_image.setImageBitmap(bmp);

        Button dialogButton = dialog.findViewById(R.id.close);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void add(Password item) {
        entry.add(item);
        notifyDataSetChanged();
    }

    public void remove(Password item) {
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
}
