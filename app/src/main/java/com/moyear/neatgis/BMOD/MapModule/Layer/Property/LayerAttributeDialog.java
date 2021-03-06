package com.moyear.neatgis.BMOD.MapModule.Layer.Property;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.moyear.neatgis.R;
import com.moyear.neatgis.BMOD.MapModule.Layer.Fragment.LayerDisplayFragment;
import com.moyear.neatgis.BMOD.MapModule.Layer.Fragment.LayerFieldFragment;
import com.moyear.neatgis.BMOD.MapModule.Layer.Fragment.LayerGeneralFragment;
import com.moyear.neatgis.BMOD.MapModule.Layer.Fragment.LayerInfoFragment;
import com.moyear.neatgis.BMOD.MapModule.Layer.Fragment.LayerTagFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.moyear.neatgis.Common.Adapter.CommonFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import gisluq.lib.Util.ScreenUtils;

public class LayerAttributeDialog extends DialogFragment {

    String TAG = "LayerAttributeDialog";

    private boolean isFullScreen = false;

    public static final String TARGETLAYER = "TargetLayer";

    private Context mContext;

    Layer mTargetLayer;

    private List<String> tabTtileList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<Fragment>();


    public LayerAttributeDialog(Context context,Layer targetLayer) {
        this.mContext = context;
        this.mTargetLayer = targetLayer;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_layer_property, container, false);

        initToolBar(rootView);
        initFragment(rootView);

        return rootView;


    }

    private void initFragment(View rootView) {

        tabTtileList.add("基本");
        fragmentList.add(new LayerGeneralFragment(mTargetLayer));
        tabTtileList.add("信息");
        fragmentList.add(new LayerInfoFragment(mTargetLayer));

        if (mTargetLayer instanceof FeatureLayer) {
            tabTtileList.add("字段");
            fragmentList.add(new LayerFieldFragment(mTargetLayer));
            tabTtileList.add("展示");
            fragmentList.add(new LayerDisplayFragment(mTargetLayer));
            tabTtileList.add("标签");
            fragmentList.add(new LayerTagFragment(mTargetLayer));
        }

        CommonFragmentPagerAdapter sectionsPagerAdapter =
                new CommonFragmentPagerAdapter(mContext, getChildFragmentManager(), tabTtileList, fragmentList);

        ViewPager viewPager = rootView.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = rootView.findViewById(R.id.tabs_layer_properties);
        tabs.setupWithViewPager(viewPager);
    }

    private void initToolBar(View rootView) {
        RelativeLayout rlToolbar = rootView.findViewById(R.id.toolbar);
        ImageButton btnClose = rootView.findViewById(R.id.img_dialog_layer_attr_icon);
        ImageButton btnMenu = rootView.findViewById(R.id.btn_dialog_file_chooser_field_close);
        ImageButton btnFullScreen = rootView.findViewById(R.id.btn_dialog_layer_attr_fullscreen);
        TextView txtLayerAttr = rootView.findViewById(R.id.txt_dialog_layer_attr);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Window window = getDialog().getWindow();
                if (window != null) {
                    // 一定要设置Background，如果不设置，window属性设置无效
                    window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
                    DisplayMetrics dm = new DisplayMetrics();
                    if (getActivity() != null) {
                        WindowManager windowManager = getActivity().getWindowManager();
                        if (windowManager != null) {
                            windowManager.getDefaultDisplay().getMetrics(dm);
                            WindowManager.LayoutParams params = window.getAttributes();
                            params.gravity = Gravity.CENTER;

                            if (isFullScreen) {
                                int height = (int) (ScreenUtils.getScreenHeight(getContext()) * 0.8);
                                int width = (int) (ScreenUtils.getScreenWidth(getContext()) * 0.9);

                                params.width = width;
                                params.height = height;

                                isFullScreen = false;
                            } else {
                                // 动态获取屏幕高度（不含状态栏），以避免状态栏变黑
                                params.height = ScreenUtils.getScreenHeight(getContext()) - ScreenUtils.getStatusHeight(getContext());
                                params.width = ViewGroup.LayoutParams.MATCH_PARENT;

                                isFullScreen = true;
                            }
                            window.setAttributes(params);
                        }
                    }
                }

            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu pm = new PopupMenu(getContext(), v);
                pm.getMenuInflater().inflate(R.menu.menu_dialog_layer_attr, pm.getMenu());

                //根据对话框是否可点击背景取消切换menu的文本
                String strFixable = isCancelable() ? "固定":"不固定";
                pm.getMenu().findItem(R.id.menu_dialog_layer_attr_item_fix).setTitle(strFixable);

                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // TODO Auto-generated method stub
                        switch (item.getItemId()) {
                            case R.id.menu_dialog_layer_attr_item_fix://切换对话框是否固
                                setCancelable(!isCancelable());
                                break;
                            case R.id.menu_dialog_layer_attr_item_setting:
                                Toast.makeText(getContext(), "设置，代码待写!!!", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                pm.show();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        setCancelable(false);//设置不可单击取消

        Window window = getDialog().getWindow();
        if (window != null) {
            // 一定要设置Background，如果不设置，window属性设置无效
            window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
            DisplayMetrics dm = new DisplayMetrics();
            if (getActivity() != null) {
                WindowManager windowManager = getActivity().getWindowManager();
                if (windowManager != null) {
                    windowManager.getDefaultDisplay().getMetrics(dm);
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.gravity = Gravity.CENTER;

                    int height = (int) (ScreenUtils.getScreenHeight(getContext()) * 0.8);
                    int width = (int) (ScreenUtils.getScreenWidth(getContext()) * 0.9);

                    // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
                    //params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.width = width;
                    params.height = height;
                    window.setAttributes(params);
                }
            }
        }
    }



}