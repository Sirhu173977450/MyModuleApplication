package com.lei.diydialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements View.OnClickListener{
    private Dialog dialog;
    private Window window;
    private Button btnDialogConfirm,btnDialogCancle,btnShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        btnShow=(Button)findViewById(R.id.btnShow);
        btnShow.setOnClickListener(this);
    }

    private void showDialog(){
        View view = getLayoutInflater().inflate(R.layout.dialog_common,null);
        dialog=new Dialog(MainActivity.this,R.style.transparentFrameWindowStyle);//透明
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        window =dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        //点击事件的注册
//        btnDialogConfirm=(Button)window.findViewById(R.id.btnConfirm);
//        btnDialogConfirm.setOnClickListener(this);
//        btnDialogCancle=(Button)window.findViewById(R.id.btnCancle);
//        btnDialogCancle.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.btnConfirm:
//
//                break;
//            case R.id.btnCancle:
//                dialog.dismiss();
//                break;
            case R.id.btnShow:
                showDialog();
                break;
            default:

                break;
        }
    }
}
