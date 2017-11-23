package xyz.thaihuynh.expandablerecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

class MyAdapter extends BaseExpandableRecyclerAdapter<MyAdapter.GroupViewHolder, MyAdapter.ChildViewHolder> {

    private final Context mContext;
    private final List<String> mListDataHeader;
    private final HashMap<String, List<String>> mListDataChild;

    MyAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
        mContext = context;
        mListDataHeader = listDataHeader;
        mListDataChild = listChildData;
    }

    @Override
    int getGroupCount() {
        return mListDataHeader.size();
    }

    @Override
    int getChildrenCount(int groupPosition) {
        return mListDataChild.get(mListDataHeader.get(groupPosition)).size();
    }

    @Override
    Object getGroup(int groupPosition) {
        return mListDataHeader.get(groupPosition);
    }

    @Override
    Object getChild(int groupPosition, int childPosition) {
        return mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    long getChildId(int groupPosition, int childPosition) {
        int result = 0;
        for (int i = 0; i < groupPosition; i++) {
            result += mListDataChild.get(mListDataHeader.get(groupPosition)).size() + 1;
        }
        return result + childPosition;
    }

    @Override
    GroupViewHolder onCreateGroupViewHolder(ViewGroup parent) {
        return new GroupViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recycler_group, parent, false));
    }

    @Override
    ChildViewHolder onCreateChildViewHolder(ViewGroup parent) {
        return new ChildViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recycler_child, parent, false));
    }

    @Override
    void onBindGroupViewHolder(GroupViewHolder holder, int groupPosition) {
        ((TextView) holder.itemView).setText((String) getGroup(groupPosition));
    }

    @Override
    void onBindChildViewHolder(ChildViewHolder holder, int groupPosition, int childPosition) {
        ((TextView) holder.itemView).setText((String) getChild(groupPosition, childPosition));
    }
//    @Override
//    View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        return null;
//    }
//
//    @Override
//    View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        return null;
//    }

    @Override
    boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        GroupViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {

        ChildViewHolder(View itemView) {
            super(itemView);
        }
    }
}
