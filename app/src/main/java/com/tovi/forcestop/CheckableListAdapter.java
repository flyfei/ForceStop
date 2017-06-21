package com.tovi.forcestop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author <a href='mailto:zhaotengfei9@gmail.com'>Tengfei Zhao</a>
 */

public class CheckableListAdapter extends BaseAdapter {


    private List<AppInfo> items;
    private LayoutInflater layoutInflater;
    private SharedPrefs sharedPrefs;

    public CheckableListAdapter(Context context, List<AppInfo> items) {
        super();
        this.items = items;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.sharedPrefs = new SharedPrefs(context.getApplicationContext());
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public Object getItem(int position) {
        return items == null ? null : items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        AppInfo app = items.get(position);
        ViewHolder viewHolder;

        if (null == convertView) {
            convertView = this.layoutInflater.inflate(R.layout.checlist_item, null);

            viewHolder = new ViewHolder();
            viewHolder.logo = (ImageView) convertView.findViewById(R.id.app_logo);
            viewHolder.name = (TextView) convertView.findViewById(R.id.app_name);
            viewHolder.check = (CheckBox) convertView.findViewById(R.id.app_check);

            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AppInfo a = (AppInfo) finalViewHolder.check.getTag();
                    sharedPrefs.setBlackApp(a.appPName, isChecked);
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalViewHolder.check.setChecked(!finalViewHolder.check.isChecked());
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.check.setTag(app);


        viewHolder.logo.setImageDrawable(app.appLogo);
        viewHolder.name.setText(app.appName);
        viewHolder.check.setChecked(sharedPrefs.isBlackApp(app.appPName));
        return convertView;
    }

    public class ViewHolder {
        ImageView logo;
        TextView name;
        CheckBox check;
    }
}
