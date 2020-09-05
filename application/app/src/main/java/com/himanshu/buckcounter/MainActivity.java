package com.himanshu.buckcounter;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.himanshu.buckcounter.business.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity 
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "MainActivityLog";
    private final int RC_SIGN_IN = 1001;
    private GoogleSignInClient mGoogleSignInClient;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        GoogleSignInOptions gso = Util.getGoogleSignInOptions();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.google_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        findViewById(R.id.google_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            updateUI(account);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        View header = mNavigationView.getHeaderView(0);
        ImageView profilePicture = header.findViewById(R.id.nav_image);
        TextView profileName = header.findViewById(R.id.nav_name);
        TextView profileEmail = header.findViewById(R.id.nav_email);
        SignInButton signIn = mNavigationView.findViewById(R.id.google_sign_in);
        Button signOut = mNavigationView.findViewById(R.id.google_sign_out);

        if (account == null) { // signed out state
            profilePicture.setImageResource(R.mipmap.ic_launcher_round);
            profileName.setText(R.string.nav_header_title);
            profileEmail.setText(R.string.nav_header_subtitle);
            signIn.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.GONE);
        } else {
            if (account.getPhotoUrl() != null) {
                Picasso.get()
                        .load(account.getPhotoUrl())
                        .transform(new Transformation() {
                            @Override
                            public Bitmap transform(Bitmap source) {
                                int size = Math.min(source.getWidth(), source.getHeight());

                                int x = (source.getWidth() - size) / 2;
                                int y = (source.getHeight() - size) / 2;

                                Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                                if (squaredBitmap != source) {
                                    source.recycle();
                                }

                                Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

                                Canvas canvas = new Canvas(bitmap);
                                Paint paint = new Paint();
                                BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                                paint.setShader(shader);
                                paint.setAntiAlias(true);

                                float r = size/2f;
                                canvas.drawCircle(r, r, r, paint);

                                squaredBitmap.recycle();
                                return bitmap;
                            }

                            @Override
                            public String key() {
                                return "circle";
                            }
                        })
                        .resize(150, 150)
                        .centerInside()
                        .into(profilePicture);
            }
            profileName.setText(account.getDisplayName());
            profileEmail.setText(account.getEmail());
            signIn.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_accounts) {
            startActivity(new Intent(this, AccountsActivity.class));
        } else if (id == R.id.nav_transactions) {
            startActivity(new Intent(this, TransactionsActivity.class));
        } else if (id == R.id.nav_export) {
            startActivity(new Intent(this, ExportXLSActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            String emailData = "mailto:buck.counter.care@gmail.com" +
                    "?cc=" + "buck.counter.care@gmail.com" +
                    "&subject=" + Uri.encode("Buck Counter Feedback");
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse(emailData));

            try {
                startActivity(email);
            } catch (ActivityNotFoundException e) {
                Snackbar.make(mNavigationView, "No Email client found", Snackbar.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void homeCardClicked(View view) {
        Class targetClass;
        switch (view.getId()) {
            case R.id.accounts:
                targetClass = AccountsActivity.class;
                break;
            case R.id.add_account:
                targetClass = AddAccount.class;
                break;
            case R.id.transactions:
                targetClass = TransactionsActivity.class;
                break;
            case R.id.add_transaction:
                targetClass = AddTransaction.class;
                break;
            default:
                targetClass = MainActivity.class;
        }
        startActivity(new Intent(this, targetClass));
    }
}
