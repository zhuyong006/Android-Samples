package com.dzt.btcommunication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * implementation getCount,getItem,getItemId methods
 *
 * @param <T>
 * @author
 */
public abstract class AbsListAdapter<T> extends BaseAdapter {

	protected Context context;
	protected LayoutInflater layoutInflater = null;
	protected List<T> datas = null;

	public AbsListAdapter(Context context, List<T> datas) {
		this.context = context;
		this.datas = datas;
		layoutInflater = LayoutInflater.from(this.context);
	}

	/**
	 * The refresh data
	 *
	 * @param datas
	 */
	public void refreshDatas(List<T> datas) {
		this.datas = datas;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas != null ? datas.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 刷新单个item
	 *
	 * @param listView
	 * @param position
	 */
	public void refreshListView(AbsListView listView, int position) {
		int start = listView.getFirstVisiblePosition();
		int last = listView.getLastVisiblePosition();
		for (int i = start, j = last; i <= j; i++) {
			if (position == i) {
				View convertView = listView.getChildAt(position - start);
				if (convertView != null) {
					getView(position, convertView, listView);
					break;
				}
			}
		}
	}

	@Override
	public abstract View getView(int position, View convertView,
								 ViewGroup parent);

}
