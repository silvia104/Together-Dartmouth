package edu.dartmouth.cs.together.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by foxmac on 16/2/27.
 */
public class FilterExpandableListAdapter extends BaseExpandableListAdapter {
    LayoutInflater inflater = null;
    JSONObject filterModel = null;

    class ViewHolder {
        public TextView vhTextView;
        public CheckBox vhCheckBox;

    }


    public FilterExpandableListAdapter (LayoutInflater inflater, JSONObject model ){
        this.inflater = inflater;
        this.filterModel = model;
    }


    @Override
    public int getGroupCount() {
        return filterModel.length();
    }


    @Override
    public Object getGroup(int groupPosition) {
        Iterator i = filterModel.keys();
        while (groupPosition > 0){
            i.next();
            groupPosition--;
        }
        return (i.next());
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) { convertView=
                inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }
        TextView tv=
                ((TextView)convertView.findViewById(android.R.id.text1));
        tv.setText(getGroup(groupPosition).toString());

        return(convertView);

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            JSONArray children= getChildren(groupPosition);
            return(children.length());
        }
        catch (JSONException e) {
            Log.e(getClass().getSimpleName(), "Exception getting children", e);
        }
        return (0);
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        try{

            JSONArray children = getChildren(groupPosition);
            return (children.get(childPosition));

        }catch (JSONException e){
            Log.e(getClass().getSimpleName(), "Exception getting item from JSON array", e);
        }

        return (null);
    }



    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 1024 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView= inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        }





        TextView tv=(TextView)convertView;
        tv.setText(getChild(groupPosition, childPosition).toString());
        return(convertView);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private JSONArray getChildren(int groupPosition) throws JSONException {
        String key=getGroup(groupPosition).toString();
        return(filterModel.getJSONArray(key)); }
}
