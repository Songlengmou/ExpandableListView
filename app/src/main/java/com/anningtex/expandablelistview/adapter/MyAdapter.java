package com.anningtex.expandablelistview.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.anningtex.expandablelistview.R;
import com.anningtex.expandablelistview.entity.MainGoodsEntity;
import com.anningtex.expandablelistview.utils.DoubleUtil;
import com.anningtex.expandablelistview.weight.ExtendedEditText;
import com.anningtex.expandablelistview.weight.RiseNumberTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author Song
 * @Desc:
 */
public class MyAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<MainGoodsEntity.DataBean> dataBeans;
    private HashMap<String, String> map = new HashMap<>();
    private OnCheckListener onCheckListener;

    public MyAdapter(Context context, List<MainGoodsEntity.DataBean> dataBeans) {
        this.context = context;
        this.dataBeans = dataBeans;
    }

    @Override
    public int getGroupCount() {
        return dataBeans == null ? 0 : dataBeans.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return dataBeans == null ? 0 : dataBeans.get(i).getContainerList().size();
    }

    @Override
    public Object getGroup(int i) {
        return dataBeans.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return dataBeans.get(i).getContainerList().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        final GroupViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new GroupViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_remaining_parent, null);
            viewHolder.tvContainerNo = convertView.findViewById(R.id.tv_containerNo);
            viewHolder.tv_Str_CountryID_ChangeTo = convertView.findViewById(R.id.tv_Str_CountryID_ChangeTo);
            viewHolder.tvSl = convertView.findViewById(R.id.tv_sl);
            viewHolder.tvTj = convertView.findViewById(R.id.tv_tj);
            viewHolder.tvZl = convertView.findViewById(R.id.tv_zl);
            viewHolder.tvDate = convertView.findViewById(R.id.tv_date);
            viewHolder.line = convertView.findViewById(R.id.view);
            viewHolder.idGroupCheckbox = convertView.findViewById(R.id.id_group_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }

        if (i == 0) {
            viewHolder.line.setVisibility(View.GONE);
        } else {
            viewHolder.line.setVisibility(View.VISIBLE);
        }

        MainGoodsEntity.DataBean dataBean = dataBeans.get(i);
        viewHolder.tvContainerNo.setText(dataBean.getContainerNo());
        viewHolder.tvDate.setText(dataBean.getInDate());

        //获取所有的子集
        List<MainGoodsEntity.DataBean.ContainerListBean> containerList = dataBean.getContainerList();
        //默认所有子集都是未选中状态
        boolean containerListState = false;
        if (containerList != null && containerList.size() > 0) {
            //所有子集中有一个为选中状态，则父级选中状态
            for (MainGoodsEntity.DataBean.ContainerListBean goodsListBean : containerList) {
                if (goodsListBean.isChecked()) {
                    containerListState = true;
                    break;
                }
            }
        }

        //计算
        double allQBale = 0;
        double allVolumeUnit = 0;
        double allWeightUnit = 0;
        for (MainGoodsEntity.DataBean.ContainerListBean listBean : containerList) {
            if (!listBean.isChecked()) {
                int curQBale = listBean.getQBales();//包数
                double curVolumeUnit = listBean.getVolumeUnit();//体积
                double curWeightUnit = listBean.getWeightUnit();//重量
                allQBale = DoubleUtil.add(Double.valueOf(curQBale), allQBale);
                allVolumeUnit = DoubleUtil.add(DoubleUtil.mul(Double.valueOf(curQBale), curVolumeUnit), allVolumeUnit);
                allWeightUnit = DoubleUtil.add(DoubleUtil.mul(Double.valueOf(curQBale), curWeightUnit), allWeightUnit);
            }
        }

        //设置计算结果
        dataBean.setCheckAllQBale(allQBale + "");
        dataBean.setCheckAllVolumeUnit(allVolumeUnit + "");
        dataBean.setCheckAllWeightUnit(allWeightUnit + "");
        //设置父级选中状态
        viewHolder.idGroupCheckbox.setChecked(dataBeans.get(i).isGroupisChecked() || containerListState);
        //设置计算数值
        viewHolder.tvSl.setText(TextUtils.isEmpty(dataBean.getCheckAllQBale()) ? "0.0" : dataBean.getCheckAllQBale());
        viewHolder.tvTj.setText(TextUtils.isEmpty(dataBean.getCheckAllVolumeUnit()) ? "0.0" : dataBean.getCheckAllVolumeUnit());
        viewHolder.tvZl.setText(TextUtils.isEmpty(dataBean.getCheckAllWeightUnit()) ? "0.0" : dataBean.getCheckAllWeightUnit());

        final boolean nowBeanChecked = dataBeans.get(i).isGroupisChecked();
        viewHolder.idGroupCheckbox.setOnClickListener(v -> {
            setupOneParentAllChildChecked(!nowBeanChecked, i);
            checkTotalAll();
        });

        if (dataBeans.get(i).isShowCheckBox()) {
            viewHolder.idGroupCheckbox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.idGroupCheckbox.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int position, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        final ChildViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ChildViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_remaining_child, null);
            viewHolder.tvOrderNo = convertView.findViewById(R.id.tv_orderNo);
            viewHolder.tvAdd = convertView.findViewById(R.id.tv_add);
            viewHolder.tvSubtract = convertView.findViewById(R.id.tv_subtract);
            viewHolder.etBales = convertView.findViewById(R.id.et_bales);
            viewHolder.tvVolumeUnit = convertView.findViewById(R.id.tv_volumeUnit);
            viewHolder.tvWeightUnit = convertView.findViewById(R.id.tv_weightUnit);
            viewHolder.idCheckbox = convertView.findViewById(R.id.id_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        viewHolder.etBales.clearTextChangedListeners();
        MainGoodsEntity.DataBean goodsListBondedBean = dataBeans.get(position);
        List<MainGoodsEntity.DataBean.ContainerListBean> listBeans = goodsListBondedBean.getContainerList();

        if (listBeans != null && listBeans.size() > 0) {
            MainGoodsEntity.DataBean.ContainerListBean bean = listBeans.get(i1);
            viewHolder.tvOrderNo.setText(bean.getOrderNo());
            viewHolder.etBales.setText(bean.getQBales() + "");
            viewHolder.tvVolumeUnit.setText(bean.getVolumeUnit() + "");
            viewHolder.tvWeightUnit.setText(bean.getWeightUnit() + "");
            viewHolder.idCheckbox.setChecked(bean.isChecked());

            viewHolder.idCheckbox.setOnClickListener(v -> {
                //修改二级状态
                bean.setChecked(!bean.isChecked());
                boolean goodsListState = false;
                for (MainGoodsEntity.DataBean.ContainerListBean containerListBean : listBeans) {
                    if (containerListBean.isChecked()) {
                        goodsListState = true;
                        break;
                    }
                }
                //修改一级状态
                goodsListBondedBean.setGroupisChecked(goodsListState);
                checkTotalAll();
                notifyDataSetChanged();
            });

            if (bean.isShowCheckBox()) {
                viewHolder.idCheckbox.setVisibility(View.VISIBLE);
                viewHolder.tvAdd.setVisibility(View.VISIBLE);
                viewHolder.tvSubtract.setVisibility(View.VISIBLE);
            } else {
                viewHolder.idCheckbox.setVisibility(View.GONE);
                viewHolder.tvAdd.setVisibility(View.GONE);
                viewHolder.tvSubtract.setVisibility(View.GONE);
            }

            int max = bean.getQBale_MAX();
            viewHolder.tvAdd.setOnClickListener(v -> {
                int qBale = bean.getQBales();
                if (max > Integer.parseInt(viewHolder.etBales.getText().toString())) {
                    qBale++;
                    viewHolder.etBales.setText(qBale + "");
                    bean.setQBales(qBale);
                    checkTotalAll();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.big), Toast.LENGTH_SHORT).show();
                }
            });

            viewHolder.tvSubtract.setOnClickListener(v -> {
                int qBale = bean.getQBales();
                if (qBale > 0) {
                    qBale--;
                    viewHolder.etBales.setText(qBale + "");
                    bean.setQBales(qBale);
                    checkTotalAll();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.small), Toast.LENGTH_SHORT).show();
                }
            });

            viewHolder.etBales.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        int intPutNum = Integer.parseInt(s.toString());
                        if (intPutNum <= bean.getQBale_MAX()) {
                            bean.setQBales(intPutNum);
                        } else {
                            bean.setQBales(bean.getQBale_MAX());
                            Toast.makeText(context, context.getResources().getString(R.string.big), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        bean.setQBales(0);
                    }
                    String result = String.valueOf(bean.getQBales());
                    if (!result.equals(s.toString())) { //增加条件
                        viewHolder.etBales.setText(bean.getQBales() + "");
                        viewHolder.etBales.setSelection(viewHolder.etBales.getText().length());
                    }
                    checkTotalAll();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        return convertView;
    }

    static class GroupViewHolder {
        TextView tvContainerNo, tv_Str_CountryID_ChangeTo, tvDate;
        CheckBox idGroupCheckbox;
        RiseNumberTextView tvSl, tvTj, tvZl;
        View line;
    }

    static class ChildViewHolder {
        TextView tvOrderNo, tvVolumeUnit, tvWeightUnit, tvAdd, tvSubtract;
        ExtendedEditText etBales;
        CheckBox idCheckbox;
    }

    private void setupOneParentAllChildChecked(boolean b, int i) {
        dataBeans.get(i).setGroupisChecked(b);
        List<MainGoodsEntity.DataBean.ContainerListBean> containerList = dataBeans.get(i).getContainerList();
        for (int j = 0; j < containerList.size(); j++) {
            containerList.get(j).setChecked(b);
        }
        notifyDataSetChanged();
    }

    public void showCheckBox(int type) {
        if (dataBeans == null) {
            return;
        }
        if (type == 0) {
            for (int i = 0; i < dataBeans.size(); i++) {
                dataBeans.get(i).setShowCheckBox(true);
                List<MainGoodsEntity.DataBean.ContainerListBean> listBeans = dataBeans.get(i).getContainerList();
                for (int j = 0; j < listBeans.size(); j++) {
                    listBeans.get(j).setShowCheckBox(true);
                }
            }
        } else {
            for (int i = 0; i < dataBeans.size(); i++) {
                MainGoodsEntity.DataBean dataBean = dataBeans.get(i);
                dataBean.setShowCheckBox(false);
                dataBean.setGroupisChecked(false);
                List<MainGoodsEntity.DataBean.ContainerListBean> containerList = dataBeans.get(i).getContainerList();
                for (int j = 0; j < containerList.size(); j++) {
                    MainGoodsEntity.DataBean.ContainerListBean containerListBean = containerList.get(j);
                    containerListBean.setShowCheckBox(false);
                    containerListBean.setChecked(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<MainGoodsEntity.DataBean> getData() {
        return dataBeans == null ? new ArrayList<>() : dataBeans;
    }

    /**
     * 清空数据列表
     */
    public void clear() {
        dataBeans.clear();
        notifyDataSetChanged();
    }

    public void setData(List<MainGoodsEntity.DataBean> beans, boolean flag) {
        this.dataBeans = beans;
        setQMax(flag);
        notifyDataSetChanged();
    }

    private void setQMax(boolean flag) {
        if (dataBeans != null && flag) {
            for (int i = 0; i < dataBeans.size(); i++) {
                MainGoodsEntity.DataBean bondedBean = dataBeans.get(i);
                if (bondedBean != null) {
                    List<MainGoodsEntity.DataBean.ContainerListBean> containerList = bondedBean.getContainerList();
                    if (containerList != null) {
                        for (MainGoodsEntity.DataBean.ContainerListBean bean : containerList) {
                            bean.setQBale_MAX(bean.getQBales());
                        }
                    }
                }
            }
        }
    }

    public void checkTotalAll() {
        List<MainGoodsEntity.DataBean> beans = new ArrayList<>();
        map.clear();
        for (MainGoodsEntity.DataBean dataBean : dataBeans) {
            List<MainGoodsEntity.DataBean.ContainerListBean> containerList = dataBean.getContainerList();
            if (dataBean.isGroupisChecked()) {
                //拼装bean
                MainGoodsEntity.DataBean bean = new MainGoodsEntity.DataBean();
                bean.setGroupisChecked(dataBean.isGroupisChecked());
                bean.setContainerNo(dataBean.getContainerNo());
                bean.setInDate(dataBean.getInDate());
                bean.setShowCheckBox(dataBean.isShowCheckBox());
                //拼装containerList
                List<MainGoodsEntity.DataBean.ContainerListBean> listBeans = new ArrayList<>();
                for (MainGoodsEntity.DataBean.ContainerListBean curListBean : containerList) {
                    if (curListBean.isChecked()) {
                        //如果当前listBean已选中
                        listBeans.add(curListBean);
                    }
                }
                bean.setContainerList(listBeans);
                beans.add(bean);
            }
        }
        if (onCheckListener != null) {
            onCheckListener.totalAll(beans);
        }
    }

    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    public interface OnCheckListener {
        void totalAll(List<MainGoodsEntity.DataBean> dataBeans);
    }
}