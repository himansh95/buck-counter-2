package com.himanshu.buckcounter;

import android.os.Bundle;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.himanshu.buckcounter.beans.Account;
import com.himanshu.buckcounter.beans.Transaction;
import com.himanshu.buckcounter.business.DatabaseHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.text.ParseException;
import java.util.Date;

import static com.himanshu.buckcounter.business.Constants.DATE_FORMAT;
import static com.himanshu.buckcounter.business.Constants.VALID_AMOUNT_REGEX;
import static com.himanshu.buckcounter.business.Constants.VALID_TEXT_REGEX;

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
        CheckBox isCreditCard = findViewById(R.id.is_credit_card);
        isCreditCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                TextInputLayout textInputLayout = findViewById(R.id.add_credit_limit_container);
                if (b) {
                    textInputLayout.setVisibility(View.VISIBLE);
                } else {
                    textInputLayout.setVisibility(View.GONE);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addActivityClicked(View view) {
        view.setEnabled(false);
        view.setAlpha(0.5f);
        TextInputEditText accountName = findViewById(R.id.add_account_name);
        TextInputEditText accountBalance = findViewById(R.id.add_account_balance);
        CheckBox initWithZero = findViewById(R.id.init_with_zero);
        CheckBox isCreditCard = findViewById(R.id.is_credit_card);
        TextInputEditText creditLimit = findViewById(R.id.add_credit_limit);
        boolean validationFailed = false;

        if (accountName.getText() == null || accountName.getText().toString().isEmpty() || !accountName.getText().toString().matches(VALID_TEXT_REGEX)) {
            validationFailed = true;
            ((TextInputLayout) findViewById(R.id.add_account_name_container)).setError(getText(R.string.add_account_name_error));
        } else {
            ((TextInputLayout) findViewById(R.id.add_account_name_container)).setErrorEnabled(false);
        }
        if (!initWithZero.isChecked() && (accountBalance.getText() == null || accountBalance.getText().toString().isEmpty() || !accountBalance.getText().toString().trim().matches(VALID_AMOUNT_REGEX))) {
            validationFailed = true;
            ((TextInputLayout) findViewById(R.id.add_account_balance_container)).setError(getText(R.string.add_account_balance_error));
        } else {
            ((TextInputLayout) findViewById(R.id.add_account_balance_container)).setErrorEnabled(false);
        }
        if (isCreditCard.isChecked() && (creditLimit.getText() == null || creditLimit.getText().toString().isEmpty() || !creditLimit.getText().toString().trim().matches(VALID_AMOUNT_REGEX))) {
            validationFailed = true;
            ((TextInputLayout) findViewById(R.id.add_credit_limit_container)).setError(getText(R.string.add_credit_limit_error));
        } else {
            ((TextInputLayout) findViewById(R.id.add_credit_limit_container)).setErrorEnabled(false);
        }
        if (validationFailed) {
            return;
        }
        boolean accountAddedSuccessfully;

        if (isCreditCard.isChecked()) {
            accountAddedSuccessfully = DatabaseHelper.getInstance(this).insertAccount(new Account(accountName.getText().toString().trim().toLowerCase(), true, Double.valueOf(creditLimit.getText().toString().trim())));
        } else {
            accountAddedSuccessfully = DatabaseHelper.getInstance(this).insertAccount(new Account(accountName.getText().toString().trim().toLowerCase()));
        }

        if (!initWithZero.isChecked()) {
            double initialBalance = Double.valueOf(accountBalance.getText().toString().trim());
            Transaction.TransactionType transactionType = initialBalance > 0 ? Transaction.TransactionType.DR : Transaction.TransactionType.CR;
            try {
                accountAddedSuccessfully = accountAddedSuccessfully &&
                        DatabaseHelper.getInstance(this).insertTransaction(new Transaction(
                                transactionType,
                                getString(R.string.initial_transaction_particulars),
                                initialBalance > 0 ? initialBalance : -1 * initialBalance,
                                DATE_FORMAT.parse(DATE_FORMAT.format(new Date())),
                                accountName.getText().toString().trim().toLowerCase()
                        ));
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
