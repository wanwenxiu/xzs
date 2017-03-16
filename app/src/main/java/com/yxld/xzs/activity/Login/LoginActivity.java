package com.yxld.xzs.activity.Login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.marlonmafra.android.widget.EditTextPassword;
import com.yxld.xzs.MainActivity;
import com.yxld.xzs.R;
import com.yxld.xzs.base.BaseActivity;
import com.yxld.xzs.contain.Contains;
import com.yxld.xzs.entity.AppLogin;
import com.yxld.xzs.http.ServiceFactory;
import com.yxld.xzs.subscribers.RxSubscriber;
import com.yxld.xzs.transformer.DefaultTransformer;
import com.yxld.xzs.utils.StringUitls;
import com.yxld.xzs.view.DeleteEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import tyrantgit.explosionfield.ExplosionField;

/**
 * 登陆界面
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.phone)
    DeleteEditText phone;
    @BindView(R.id.password)
    EditTextPassword password;
    @BindView(R.id.login_button)
    Button loginButton;

    private SharedPreferences sp;
    private String NameValue, PwdValue;
    private ExplosionField mExplosionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("登录");
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mExplosionField = ExplosionField.attach2Window(LoginActivity.this);
        addListener(findViewById(R.id.login_button));
        sp = this.getSharedPreferences("userInfo", MODE_PRIVATE);
        //判断记住密码多选框的状态
        if (sp.getBoolean("ISCHECK", true)) {
            //设置默认是记录密码状态
            phone.setText(sp.getString("NAME", ""));
            password.setText(sp.getString("PASSWORD", ""));
            phone.setSelection(phone.getText().length());
            //判断自动登陆多选框状态
            if (sp.getBoolean("AUTO_ISCHECK", false)) {
                //保存用户名和密码
                NameValue = phone.getText().toString();
                PwdValue = password.getText().toString();
                Login();
            }
        }

    }


    private void Login() {
        ServiceFactory.httpService()
                .login(phone.getText().toString(), StringUitls.getMD5(password.getText().toString()))
                .compose(new DefaultTransformer<AppLogin>())
                .subscribe(new RxSubscriber<AppLogin>(this) {
                    @Override
                    public void onNext(AppLogin login) {
                        Contains.appLogin = login;
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("NAME", NameValue);
                        editor.putString("PASSWORD", PwdValue);
                        editor.putBoolean("ISCHECK", true);
                        editor.putBoolean("AUTO_ISCHECK", true);
                        editor.commit();
                        mExplosionField.explode(loginButton);
                        loginButton.setOnClickListener(null);
                        new Thread() {
                            public void run() {
                                try {
                                    sleep(1000);
                                    startActivity(MainActivity.class);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }

                });
    }

    private void addListener(final View loginButton) {
        if (loginButton instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) loginButton;
            for (int i = 0; i < parent.getChildCount(); i++) {
                addListener(parent.getChildAt(i));
            }
        } else {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tel = phone.getText().toString().trim();
                    String pwd = password.getText().toString().trim();
                    if (tel == null || tel.equals("")) {
                        phone.requestFocus();
                        Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    } else if (pwd == null || pwd.equals("")) {
                        password.requestFocus();
                        Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    } else {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(loginButton, InputMethodManager.SHOW_FORCED);
                        imm.hideSoftInputFromWindow(loginButton.getWindowToken(), 0); //强制隐藏键盘
                        //保存用户名和密码
                        NameValue = phone.getText().toString();
                        PwdValue = password.getText().toString();
                        Login();
                    }
                }
            });
        }
    }
}

