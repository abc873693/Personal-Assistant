package rainvisitor.personal_assistant;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import rainvisitor.personal_assistant.Database.SP_Service;
import rainvisitor.personal_assistant.libs.Utils;

public class StartActivity extends AppCompatActivity {
    private static final String AUTH_TAG = "Auth";
    private static final int RC_SIGN_IN = 1;
    private Context context;
    private RelativeLayout relativeLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        context = getApplicationContext();
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_start);
        CheckNetwork();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void CheckNetwork() {
        if (!Utils.isNetworkConnected(context)) {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "無可用的網路", Snackbar.LENGTH_INDEFINITE)
                    .setAction("確定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckNetwork();
                        }
                    });
            snackbar.show();
        } else {
            new LoginTask().execute();
        }
    }

    private class LoginTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            signIn();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(AUTH_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(AUTH_TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        mGoogleApiClient = new GoogleApiClient.Builder(StartActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth.addAuthStateListener(mAuthListener);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(AUTH_TAG, "firebaseAuthWithGoogle:" + acct.getId() + "N:" + acct.getEmail() + acct.getFamilyName() + acct.getGivenName());
        SP_Service sp_service = new SP_Service(context);
        sp_service.username_set(acct.getFamilyName() + acct.getGivenName());
        sp_service.userEmail_set(acct.getEmail());
        sp_service.userPhotoURL_set(acct.getPhotoUrl() + "");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(AUTH_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(AUTH_TAG, "signInWithCredential", task.getException());
                            Snackbar snackbar = Snackbar
                                    .make(relativeLayout, "驗證錯誤", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("確定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CheckNetwork();
                                        }
                                    });
                            snackbar.show();
                        } else {
                            startActivity(new Intent().setClass(StartActivity.this, MainActivity.class));
                            finish();
                        }
                        // ...
                    }
                });
    }
}
