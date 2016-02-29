package edu.dartmouth.cs.together;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by di on 2/27/2016.
 */
public abstract class EventArrayAdapter<T> extends ArrayAdapter<T> {
private int mListItemLayoutResId;
        Context context;
        ImageView imgView;
public EventArrayAdapter(Context context, List<T> ts) {
        this(context, R.layout.event_list, ts);
        }

public EventArrayAdapter(
        Context context,
        int listItemLayoutResourceId,
        List<T> ts) {
        super(context, listItemLayoutResourceId, ts);
        this.context=context;
        mListItemLayoutResId = listItemLayoutResourceId;
        }

@Override
public android.view.View getView(
        int position,
        View convertView,
        ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater)getContext()
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listItemView = convertView;
        if (null == convertView) {
        listItemView = inflater.inflate(
        mListItemLayoutResId,
        parent,
        false);
        }

        // The ListItemLayout must use the standard text item IDs.
        TextView lineOneView = (TextView)listItemView.findViewById(
        R.id.etext1);
        TextView lineTwoView = (TextView)listItemView.findViewById(
        R.id.etext2);
        TextView lineTreView = (TextView)listItemView.findViewById(
        R.id.etext3);
        TextView lineFouView = (TextView)listItemView.findViewById(
        R.id.etext4);
        TextView lineFivView = (TextView)listItemView.findViewById(
        R.id.etext5);
        TextView lineSixView = (TextView)listItemView.findViewById(
        R.id.etext6);

        imgView=(ImageView)listItemView.findViewById(
                R.id.imageView2);
        imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        // you just put your Logic here And use this custom adapter to
                        // load your Data By using this particular custom adapter to
                        // your listview
                        Intent i=new Intent(getContext(),EventDetailActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(i);

                }
        });


        T t = (T)getItem(position);
        lineOneView.setText(lineOneText(t));
        lineTwoView.setText(lineTwoText(t));
        lineTreView.setText(lineTreText(t));
        lineFouView.setText(lineFouText(t));
        lineFivView.setText(lineFivText(t));
        lineSixView.setText(lineSixText(t));

        return listItemView;
        }

public abstract String lineTreText(T t);

public abstract String lineFouText(T t);

public abstract String lineFivText(T t);

public abstract String lineSixText(T t);

public abstract String lineOneText(T t);

public abstract String lineTwoText(T t);
        }
