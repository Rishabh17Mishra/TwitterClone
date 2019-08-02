package com.example.twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import es.dmoral.toasty.Toasty;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText textEmailLogin, textPasswordLogin;
    private Button btnSignUpUserLogin, btnLogInUserLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_log_in );

        setTitle("LOGIN");

        textEmailLogin = findViewById(R.id.textEmailLogin);
        textPasswordLogin = findViewById(R.id.textPasswordLogin);
        btnSignUpUserLogin = findViewById(R.id.btnSignUpUserLogin);
        btnLogInUserLogin = findViewById(R.id.btnLogInUserLogin);

        btnSignUpUserLogin.setOnClickListener(this);
        btnLogInUserLogin.setOnClickListener(this);
        textPasswordLogin.setOnKeyListener( new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) { onClick( btnLogInUserLogin );}
                return false;
            }
        } );

        if (ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().logOut();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogInUserLogin :
                if (textEmailLogin.getText().toString().equals( "" ) || textPasswordLogin.getText().toString().equals( "" )) {
                    Toasty.info(LogInActivity.this, "Email & Password is required", Toasty.LENGTH_SHORT).show();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog( this );
                    progressDialog.setMessage( textEmailLogin.getText().toString() + " Log In Complete" );
                    progressDialog.show();

                    ParseUser.logInInBackground( textEmailLogin.getText().toString(), textPasswordLogin.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null && e == null) {
                                ParseUser.getCurrentUser().add( "fanOf", ParseUser.getCurrentUser().get( "username" ) );
                                Toasty.success( LogInActivity.this, user.getUsername() + " is Logged in Successfully ", Toasty.LENGTH_SHORT ).show();
                                transitionToSocialMediaActivity();
                            } else {
                                Toasty.error( LogInActivity.this, e.getMessage(), Toasty.LENGTH_SHORT ).show();
                            }
                            progressDialog.dismiss();
                        }
                    } );
                }
                break;
            case R.id.btnSignUpUserLogin :
                Intent intent = new Intent( LogInActivity.this, SignUpActivity.class );
                startActivity( intent );
                break;
        }
    }

    public void rootLayoutTapped(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService( INPUT_METHOD_SERVICE );
            inputMethodManager.hideSoftInputFromWindow( getCurrentFocus().getWindowToken(), 0 );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void transitionToSocialMediaActivity() {
        Intent intent = new Intent(LogInActivity.this, TwitterActivity.class);
        startActivity(intent);
        finish();
    }
}
