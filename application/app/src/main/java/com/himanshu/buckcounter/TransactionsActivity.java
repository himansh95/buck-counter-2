package com.himanshu.buckcounter;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.himanshu.buckcounter.business.Constants;
import com.himanshu.buckcounter.business.Util;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;

public class TransactionsActivity extends AppCompatActivity {
    String accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountName = getIntent().getExtras() != null ?
                getIntent().getExtras().getString(Constants.BUNDLE_ACCOUNTS_NAME, null) :
                null;
        setContentView(R.layout.activity_transactions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (accountName != null) {
            getSupportActionBar().setTitle(Util.capitalizeString(accountName));
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TransactionsActivity.this, AddTransaction.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed
                // in the Action Bar.
                Intent parentActivityIntent;

                if (accountName == null) {
                    parentActivityIntent = new Intent(this, MainActivity.class);
                } else {
                    parentActivityIntent = new Intent(this, AccountsActivity.class);
                }
                parentActivityIntent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(parentActivityIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
