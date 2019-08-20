package edu.buu.daowe.dialogue;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.baidubce.services.bos.BosClientConfiguration;

import edu.buu.daowe.R;
import edu.buu.daowe.Util.BosUtils;

public class ModifyPhotoBottomDialog extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        dialog.setContentView(R.layout.dialogue_photo_select);
        dialog.setCanceledOnTouchOutside(false); // 外部点击取消
        dialog.setCancelable(false);

        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);
        dialog.findViewById(R.id.btn_selectcancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        dialog.findViewById(R.id.btn_selectfromku).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BosClientConfiguration config = BosUtils.initBosClientConfiguration();
                choosePhoto();
            }
        });

        return dialog;

    }


    /**
     * 打开相册
     */
    private void choosePhoto() {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,首先展示分类列表)
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        picture.setType("image/*");
        getActivity().startActivityForResult(picture, 0x666);
        dismiss();
    }
}
