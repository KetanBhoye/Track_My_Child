package com.example.trackmychild;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class Child_login extends Activity {

   ImageView Childsignin;
    int RC_SIGN_IN = 65;
    FirebaseAuth Auth;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();

            // Check if the user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = Auth.getCurrentUser();
            // do your stuff
            if (currentUser != null) {
                // go to MainActivity
                Intent intent = new Intent(Child_login.this,Parent_FetchLocation.class);
                startActivity(intent);
                finish();

            } else {
                // user doesn't exist
                signIn();
            }

        }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_login);
        Auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Childsignin = findViewById(R.id.child_signin);
        Childsignin.setOnClickListener(v -> signIn());

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = Auth.getCurrentUser();

                        Intent intent = new Intent(Child_login.this,Parent_FetchLocation.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        Toast.makeText(Child_login.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
