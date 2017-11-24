package xyz.thaihuynh.expandablerecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

abstract class BaseExpandableRecyclerAdapter<GVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    /**
     * This data type represents a child position
     */
    private final static int CHILD = 1;

    /**
     * This data type represents a group position
     */
    private final static int GROUP = 2;

    private Map<Object, Boolean> mGroupState = new HashMap<>();

    @Override
    public int getItemViewType(int position) {
        int count = 0;
        for (int i = 0; count < position; i++) count += (mGroupState.get(getGroup(i)) ? getChildrenCount(i) : 0) + 1;
        return count == position ? GROUP : CHILD;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == GROUP) {
            final GVH groupViewHolder = onCreateGroupViewHolder(parent);
            groupViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int groupCount = 0;
                    int allChildCount = 0;
                    final int adapterPosition = groupViewHolder.getAdapterPosition();
                    for (int i = 0; groupCount + allChildCount < adapterPosition; i++) {
                        groupCount++;
                        allChildCount += (mGroupState.get(getGroup(i)) ? getChildrenCount(i) : 0);
                    }
                    final Object group = getGroup(groupCount);
                    boolean isExpanded = mGroupState.get(group);
                    mGroupState.put(group, !isExpanded);
                    final int childCount = getChildrenCount(groupCount);
                    if (isExpanded) {
                        notifyItemChanged(adapterPosition);
                        notifyItemRangeRemoved(adapterPosition + 1, childCount);
                    } else {
                        notifyItemChanged(adapterPosition);
                        notifyItemRangeInserted(adapterPosition + 1, childCount);
                    }
                }
            });
            return groupViewHolder;
        } else {
            return onCreateChildViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int groupCount = 0;
        int childCount = 0;
        for (int i = 0; groupCount + childCount < position; i++) {
            groupCount++;
            childCount += (mGroupState.get(getGroup(i)) ? getChildrenCount(i) : 0);
        }

        if (getItemViewType(position) == GROUP) {
            onBindGroupViewHolder((GVH) holder, groupCount, mGroupState.get(getGroup(groupCount)));
        } else {
            int groupPosition = groupCount - 1;
            int childPosition = position - groupCount - childCount + (mGroupState.get(getGroup(groupPosition)) ? getChildrenCount(groupPosition) : 0);
            onBindChildViewHolder((CVH) holder, groupPosition, childPosition);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        final int groupCount = getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            Object group = getGroup(i);
            if (!mGroupState.containsKey(group)) {
                mGroupState.put(group, false);
            }
            count += (mGroupState.get(group) ? getChildrenCount(i) : 0) + 1;
        }
        return count;
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    abstract int getGroupCount();

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *                      count should be returned
     * @return the children count in the specified group
     */
    abstract int getChildrenCount(int groupPosition);

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */
    abstract Object getGroup(int groupPosition);

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */
    abstract Object getChild(int groupPosition, int childPosition);

    /**
     * Gets the ID for the group at the given position. This group ID must be
     * unique across groups. The combined ID (see
     * {@link #getCombinedGroupId(long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */
    abstract long getGroupId(int groupPosition);

    /**
     * Gets the ID for the given child within the given group. This ID must be
     * unique across all children within the group. The combined ID (see
     * {@link #getCombinedChildId(long, long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *                      the ID is wanted
     * @return the ID associated with the child
     */
    abstract long getChildId(int groupPosition, int childPosition);

    abstract GVH onCreateGroupViewHolder(ViewGroup parent);

    abstract CVH onCreateChildViewHolder(ViewGroup parent);

    abstract void onBindGroupViewHolder(GVH holder, int groupPosition, boolean isExpanded);

    abstract void onBindChildViewHolder(CVH holder, int groupPosition, int childPosition);

    /**
     * Whether the child at the specified position is selectable.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return whether the child is selectable.
     */
    abstract boolean isChildSelectable(int groupPosition, int childPosition);

    /**
     * Override this method if you foresee a clash in IDs based on this scheme:
     * <p>
     * Base implementation returns a long:
     * <li> bit 0: Whether this ID points to a child (unset) or group (set), so for this method
     * this bit will be 0.
     * <li> bit 1-31: Lower 31 bits of the groupId
     * <li> bit 32-63: Lower 32 bits of the childId.
     * <p>
     * {@inheritDoc}
     */
    public long getCombinedGroupId(long groupId) {
        return (groupId & 0x7FFFFFFF) << 32;
    }

    /**
     * Override this method if you foresee a clash in IDs based on this scheme:
     * <p>
     * Base implementation returns a long:
     * <li> bit 0: Whether this ID points to a child (unset) or group (set), so for this method
     * this bit will be 1.
     * <li> bit 1-31: Lower 31 bits of the groupId
     * <li> bit 32-63: Lower 32 bits of the childId.
     * <p>
     * {@inheritDoc}
     */
    public long getCombinedChildId(long groupId, long childId) {
        return 0x8000000000000000L | ((groupId & 0x7FFFFFFF) << 32) | (childId & 0xFFFFFFFF);
    }
}
