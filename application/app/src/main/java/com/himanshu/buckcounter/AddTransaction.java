package com.himanshu.buckcounter;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.himanshu.buckcounter.beans.Account;
import com.himanshu.buckcounter.beans.Transaction;
import com.himanshu.buckcounter.business.DatabaseHelper;
import com.himanshu.buckcounter.business.Util;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;

import java.util.Date;
import java.util.List;

import static com.himanshu.buckcounter.business.Constants.VALID_AMOUNT_REGEX;
import static com.himanshu.buckcounter.business.Constants.VALID_TEXT_REGEX;

public class AddTransaction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        doSetup();
    }

    public void setUpAutoCompleteTextView(final AutoCompleteTextView autoCompleteTextView, String[] array){
        autoCompleteTextView.setKeyListener(null);
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array));
        autoCompleteTextView.setFocusable(true);
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    Util.hideSoftKeyboard(AddTransaction.this, view);
                    autoCompleteTextView.showDropDown();
                }
            }
        });
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.showDropDown();
            }
        });
    }

    public void doSetup() {
        RadioGroup selectTransactionType = findViewById(R.id.add_transaction_type);
        selectTransactionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId() == R.id.transaction_type_contra){
                    findViewById(R.id.select_credit_account_container).setVisibility(View.VISIBLE);
                    findViewById(R.id.select_debit_account_container).setVisibility(View.VISIBLE);
                } else if(radioGroup.getCheckedRadioButtonId() == R.id.transaction_type_debit) {
                    findViewById(R.id.select_credit_account_container).setVisibility(View.GONE);
                    findViewById(R.id.select_debit_account_container).setVisibility(View.VISIBLE);
                } if(radioGroup.getCheckedRadioButtonId() == R.id.transaction_type_credit) {
                    findViewById(R.id.select_credit_account_container).setVisibility(View.VISIBLE);
                    findViewById(R.id.select_debit_account_container).setVisibility(View.GONE);
                }
            }
        });
        List<Account> accounts = DatabaseHelper.getInstance(this).getAllAccounts();
        String[] accountNames = new String[accounts.size()];
        int i = 0;
        for(Account account : accounts) {
            accountNames[i++] = account.getName().toUpperCase();
        }
        setUpAutoCompleteTextView((AutoCompleteTextView)findViewById(R.id.select_credit_account), accountNames);
        setUpAutoCompleteTextView((AutoCompleteTextView)findViewById(R.id.select_debit_account), accountNames);
    }

    public void addTransactionClicked(View view) {
        int selectedTransactionType = ((RadioGroup)findViewById(R.id.add_transaction_type)).getCheckedRadioButtonId();
        AutoCompleteTextView selectCreditAccount = findViewById(R.id.select_credit_account);
        AutoCompleteTextView selectDebitAccount = findViewById(R.id.select_debit_account);
        TextInputEditText transactionParticulars = findViewById(R.id.add_transaction_particulars);
        TextInputEditText transactionAmount = findViewById(R.id.add_transaction_amount);
        boolean validationFailed = false;

        if (selectedTransactionType == R.id.transaction_type_contra || selectedTransactionType == R.id.transaction_type_debit) {
            if (selectDebitAccount.getText().toString().isEmpty()) {
                validationFailed = true;
                ((TextInputLayout)findViewById(R.id.select_debit_account_container)).setError("Please select a debit account");
            } else {
                ((TextInputLayout)findViewById(R.id.select_debit_account_container)).setErrorEnabled(false);
            }
        }
        if (selectedTransactionType == R.id.transaction_type_contra || selectedTransactionType == R.id.transaction_type_credit) {
            if (selectCreditAccount.getText().toString().isEmpty()) {
                validationFailed = true;
                ((TextInputLayout)findViewById(R.id.select_credit_account_container)).setError("Please select a credit account");
            } else {
                ((TextInputLayout)findViewById(R.id.select_credit_account_container)).setErrorEnabled(false);
            }
        }
        if (selectedTransactionType == R.id.transaction_type_contra) {
            if (!(selectCreditAccount.getText().toString().isEmpty() || selectDebitAccount.getText().toString().isEmpty())) {
                if (selectCreditAccount.getText().toString().equals(selectDebitAccount.getText().toString())) {
                    validationFailed = true;
                    ((TextInputLayout)findViewById(R.id.select_debit_account_container)).setError("Credit and Debit accounts cannot be the same");
                } else {
                    ((TextInputLayout)findViewById(R.id.select_debit_account_container)).setErrorEnabled(false);
                }
            }
        }
        if (transactionParticulars.getText() == null || transactionParticulars.getText().toString().isEmpty() || !transactionParticulars.getText().toString().matches(VALID_TEXT_REGEX)) {
            validationFailed = true;
            ((TextInputLayout)findViewById(R.id.add_transaction_particulars_container)).setError("Please enter a valid transaction particulars");
        } else {
            ((TextInputLayout)findViewById(R.id.add_transaction_particulars_container)).setErrorEnabled(false);
        }
        if (transactionAmount.getText() == null || transactionAmount.getText().toString().isEmpty() || !transactionAmount.getText().toString().matches(VALID_AMOUNT_REGEX)) {
            validationFailed = true;
            ((TextInputLayout)findViewById(R.id.add_transaction_amount_container)).setError("Please enter a valid transaction amount");
        } else {
            ((TextInputLayout)findViewById(R.id.add_transaction_amount_container)).setErrorEnabled(false);
        }
        if (validationFailed) {
            return;
        }
        boolean transactionAddedSuccessfully = false;
        switch (selectedTransactionType) {
            case R.id.transaction_type_contra:
                transactionAddedSuccessfully = DatabaseHelper.getInstance(this).insertTransaction(new Transaction(
                        Transaction.TransactionType.CONTRA,
                        transactionParticulars.getText().toString().trim().toLowerCase(),
                        Double.valueOf(transactionAmount.getText().toString().trim()),
                        new Date(),
                        selectDebitAccount.getText().toString().toLowerCase(),
                        selectCreditAccount.getText().toString().toLowerCase()
                ));
                break;
            case R.id.transaction_type_debit:
                transactionAddedSuccessfully = DatabaseHelper.getInstance(this).insertTransaction(new Transaction(
                        Transaction.TransactionType.DR,
                        transactionParticulars.getText().toString().trim().toLowerCase(),
                        Double.valueOf(transactionAmount.getText().toString().trim()),
                        new Date(),
                        selectDebitAccount.getText().toString().toLowerCase()
                ));
                break;
            case R.id.transaction_type_credit:
                transactionAddedSuccessfully = DatabaseHelper.getInstance(this).insertTransaction(new Transaction(
                        Transaction.TransactionType.CR,
                        transactionParticulars.getText().toString().trim().toLowerCase(),
                        Double.valueOf(transactionAmount.getText().toString().trim()),
                        new Date(),
                        selectCreditAccount.getText().toString().toLowerCase()
                ));
                break;
        }
        CharSequence responseText = "Transaction added successfully";
        if(!transactionAddedSuccessfully) {
            responseText = "Transaction could not be added";
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
