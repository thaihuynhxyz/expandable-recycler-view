package xyz.thaihuynh.expandablerecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

abstract class BaseExpandableRecyclerAdapter<GVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    @Override
    public int getItemViewType(int position) {
        int count = 0;
        for (int i = 0; count < position; i++) count += getChildrenCount(i) + 1;
        return count == position ? 0 : 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == 0 ? onCreateGroupViewHolder(parent) : onCreateChildViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int groupCount = 0;
        int childCount = 0;
        for (int i = 0; groupCount + childCount < position; i++) {
            groupCount++;
            childCount += getChildrenCount(i);
        }

        if (getItemViewType(position) == 0) {
            onBindGroupViewHolder((GVH) holder, groupCount);
        } else {
            int groupPosition = groupCount - 1;
            int childPosition = position - groupCount - childCount + getChildrenCount(groupPosition);
            onBindChildViewHolder((CVH) holder, groupPosition, childPosition);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (int i = 0; i < getGroupCount(); i++) count += getChildrenCount(i) + 1;
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

    abstract void onBindGroupViewHolder(GVH holder, int groupPosition);

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
