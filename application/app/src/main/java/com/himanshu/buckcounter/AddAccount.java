package com.himanshu.buckcounter;

import android.os.Bundle;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.himanshu.buckcounter.beans.Account;
import com.himanshu.buckcounter.business.DatabaseHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class AddAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CheckBox initWithZero = findViewById(R.id.init_with_zero);
        initWithZero.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                TextInputLayout textInputLayout = findViewById(R.id.add_account_balance_container);
                if (b) {
                    textInputLayout.setVisibility(View.GONE);
                } else {
                    textInputLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addActivityClicked(View view) {
        TextInputEditText accountName = findViewById(R.id.add_account_name);
        TextInputEditText accountBalance = findViewById(R.id.add_account_balance);
        CheckBox initWithZero = findViewById(R.id.init_with_zero);
        boolean validationFailed = false;

        if (accountName.getText() == null || accountName.getText().toString().isEmpty() || !accountName.getText().toString().matches("^[\\w\\s\\d]+$")) {
            validationFailed = true;
            ((TextInputLayout)findViewById(R.id.add_account_name_container)).setError(getText(R.string.add_account_name_error));
        } else {
            ((TextInputLayout)findViewById(R.id.add_account_name_container)).setErrorEnabled(false);
        }
        if(!initWithZero.isChecked() && (accountBalance.getText() == null || accountBalance.getText().toString().isEmpty() || !accountBalance.getText().toString().matches("^[0-9]+(\\.[0-9]+)?$"))) {
            validationFailed = true;
            ((TextInputLayout)findViewById(R.id.add_account_balance_container)).setError(getText(R.string.add_account_balance_error));
        } else {
            ((TextInputLayout)findViewById(R.id.add_account_balance_container)).setErrorEnabled(false);
        }
        if(validationFailed) {
            return;
        }
        boolean accountAddedSuccessfully;
        if (initWithZero.isChecked()) {
            accountAddedSuccessfully = DatabaseHelper.getInstance(this).insertAccount(new Account(accountName.getText().toString().trim().toLowerCase()));
        } else {
            accountAddedSuccessfully = DatabaseHelper.getInstance(this).insertAccount(new Account(accountName.getText().toString().trim().toLowerCase(), Double.valueOf(accountBalance.getText().toString().trim())));
        }
        CharSequence responseText = getText(R.string.add_account_success);
        if (!accountAddedSuccessfully) {
            responseText = getText(R.string.add_account_failure);
        }
        Snackbar response = Snackbar.make(view, responseText, Snackbar.LENGTH_SHORT);
        response.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                finish();
            }
        });
        response.show();
    }
}
