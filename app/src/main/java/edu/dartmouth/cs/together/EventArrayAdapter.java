package edu.dartmouth.cs.together;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by di on 2/27/2016.
 */
public abstract class EventArrayAdapter<T> extends ArrayAdapter<T> {
private int mListItemLayoutResId;

public EventArrayAdapter(Context context, List<T> ts) {
        this(context, R.layout.event_list, ts);
        }

public EventArrayAdapter(
        Context context,
        int listItemLayoutResourceId,
        List<T> ts) {
        super(context, listItemLayoutResourceId, ts);
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


        T t = (T)getItem(position);
        lineOneView.setText(lineOneText(t));
        lineTwoView.setText(lineTwoText(t));
        lineOneView.setText(lineTreText(t));
        lineTwoView.setText(lineFouText(t));
        lineOneView.setText(lineFivText(t));
        lineTwoView.setText(lineSixText(t));

        return listItemView;
        }

public abstract String lineTreText(T t);

public abstract String lineFouText(T t);

public abstract String lineFivText(T t);

public abstract String lineSixText(T t);

public abstract String lineOneText(T t);

public abstract String lineTwoText(T t);
        }
