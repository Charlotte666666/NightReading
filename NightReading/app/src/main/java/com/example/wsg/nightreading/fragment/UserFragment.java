package com.example.wsg.nightreading.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wsg.nightreading.R;
import com.example.wsg.nightreading.entity.MyUser;
import com.example.wsg.nightreading.ui.AboutActivity;
import com.example.wsg.nightreading.ui.DonateActivity;
import com.example.wsg.nightreading.ui.LoginActivity;
import com.example.wsg.nightreading.ui.ShareActivity;
import com.example.wsg.nightreading.utils.L;
import com.example.wsg.nightreading.utils.UtilTools;
import com.example.wsg.nightreading.view.CustomDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.wsg.nightreading.R.id.btn_update_ok;
import static com.example.wsg.nightreading.R.id.et_age;
import static com.example.wsg.nightreading.R.id.et_desc;
import static com.example.wsg.nightreading.R.id.et_sex;
import static com.example.wsg.nightreading.R.id.et_username;
import static com.example.wsg.nightreading.R.id.profile_image;

/**
 * 项目名：NightReading
 * 包名：com.example.wsg.nightreading.fragment
 * 文件名：UserFragment
 * 创建者：wsg
 * 创建时间：2017/9/6  19:39
 * 描述：个人中心
 */

public class UserFragment extends Fragment {

    @BindView(R.id.btn_camera)
    Button btnCamera;
    @BindView(R.id.btn_picture)
    Button btnPicture;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    private CustomDialog dialog;

    private UserFragment() {

    }

    private static UserFragment i = new UserFragment();

    //3.提供一个用于获取实例的方法，使用public static修饰
    public static UserFragment getInstance() {
        return i;
    }


    @BindView(profile_image)
    CircleImageView profileImage;
    @BindView(R.id.edit_user)
    TextView editUser;
    @BindView(et_username)
    EditText etUsername;
    @BindView(et_sex)
    EditText etSex;
    @BindView(et_age)
    EditText etAge;
    @BindView(et_desc)
    EditText etDesc;
    @BindView(btn_update_ok)
    Button btnUpdateOk;
    TextView tvNight;
    @BindView(R.id.tv_about)
    TextView tvAbout;
    @BindView(R.id.btn_exit_user)
    Button btnExitUser;
    Unbinder unbinder;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.tv_donate)
    TextView tvDonate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, null);
        unbinder = ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    private void initView() {
        //初始化dialog
        dialog = new CustomDialog(getActivity(), 0, 0,
                R.layout.dialog_photo, R.style.pop_anim_style, Gravity.BOTTOM, 0);
        //提示框以外点击无效
        dialog.setCancelable(false);
    }


    private void initData() {
//        etUsername.setText();
//        etSex.setText();
        //默认是不可点击的/不可输入
        setEnabled(false);


        //设置具体的值
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        etUsername.setText(userInfo.getUsername());
        etAge.setText(userInfo.getAge() + "");
        etSex.setText(userInfo.isSex() ? getString(R.string.text_boy) : getString(R.string.text_girl_f));
        etDesc.setText(userInfo.getDesc());
    }

    private void setEnabled(boolean b) {

        etUsername.setEnabled(b);
        etSex.setEnabled(b);
        etAge.setEnabled(b);
        etDesc.setEnabled(b);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({profile_image, R.id.edit_user, btn_update_ok, R.id.tv_about, R.id.btn_exit_user, R.id.tv_share, R.id.tv_donate,R.id.btn_camera, R.id.btn_picture, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case profile_image:
                break;
            case R.id.edit_user:
                setEnabled(true);
                btnUpdateOk.setVisibility(View.VISIBLE);

                break;
            case btn_update_ok:
                //1.拿到输入框的值
                String username = etUsername.getText().toString();
                String age = etAge.getText().toString();
                String sex = etSex.getText().toString();
                String desc = etDesc.getText().toString();


                //2.判断是否为空
                if (!TextUtils.isEmpty(username) & !TextUtils.isEmpty(age) & !TextUtils.isEmpty(sex)) {
                    //3.更新属性
                    MyUser user = new MyUser();
                    user.setUsername(username);
                    user.setAge(Integer.parseInt(age));
                    //性别
                    if (sex.equals(getString(R.string.text_boy))) {
                        user.setSex(true);
                    } else {
                        user.setSex(false);
                    }
                    //简介
                    if (!TextUtils.isEmpty(desc)) {
                        user.setDesc(desc);
                    } else {
                        user.setDesc(getString(R.string.text_nothing));
                    }
                    BmobUser bmobUser = BmobUser.getCurrentUser();
                    user.update(bmobUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //修改成功
                                setEnabled(false);
                                btnUpdateOk.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), R.string.text_editor_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.text_editor_failure, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), getString(R.string.text_tost_empty), Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.tv_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;


            case R.id.btn_exit_user:
                //清除缓存用户对象
                MyUser.logOut();
                // 现在的currentUser是null了
                BmobUser currentUser = MyUser.getCurrentUser();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            case R.id.tv_share:
                startActivity(new Intent(getActivity(), ShareActivity.class));
                break;
            case R.id.tv_donate:
                startActivity(new Intent(getActivity(), DonateActivity.class));
                break;
            case R.id.btn_camera:
                break;
            case R.id.btn_picture:
                toCamera();
                break;
            case R.id.btn_cancel:
                dialog.dismiss();
                break;





        }
    }


    public static final String PHOTO_IMAGE_FILE_NAME = "fileImg.jpg";
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int IMAGE_REQUEST_CODE = 101;
    public static final int RESULT_REQUEST_CODE = 102;
    private File tempFile = null;

    //跳转相机
    private void toCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断内存卡是否可用，可用的话就进行储存
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME)));
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        dialog.dismiss();
    }

    //跳转相册
    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
        dialog.dismiss();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != getActivity().RESULT_CANCELED) {
            switch (requestCode) {
                //相册数据
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                //相机数据
                case CAMERA_REQUEST_CODE:
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(tempFile));
                    break;
                case RESULT_REQUEST_CODE:
                    //有可能点击舍弃
                    if (data != null) {
                        //拿到图片设置
                        setImageToView(data);
                        //既然已经设置了图片，我们原先的就应该删除
                        if (tempFile != null) {
                            tempFile.delete();
                        }
                    }
                    break;
            }
        }
    }

    //裁剪
    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
            L.e("uri == null");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", "true");
        //裁剪宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪图片的质量
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        //发送数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    //设置图片
    private void setImageToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("data");
            profileImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //保存
        UtilTools.putImageToShare(getActivity(),profileImage);
    }


}
