package com.himanshu.buckcounter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.himanshu.buckcounter.beans.Transaction;
import com.himanshu.buckcounter.business.DatabaseHelper;
import com.himanshu.buckcounter.business.Util;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import static com.himanshu.buckcounter.business.Constants.DATE_FORMAT;
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

    public void setUpParticularsTextView() {
        AutoCompleteTextView particulars = findViewById(R.id.add_transaction_particulars);
        particulars.setThreshold(1);
        HashSet<String> particularsSet = DatabaseHelper.getInstance(this).getAllTransactionParticulars();
        String[] staticList = getResources().getStringArray(R.array.particulars_array);
        particularsSet.addAll(Arrays.asList(staticList));
        String[] particularsList = new String[particularsSet.size()];
        particulars.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, particularsSet.toArray(particularsList)));
    }

    public void doSetup() {
        ChipGroup selectTransactionType = findViewById(R.id.add_transaction_type);
        selectTransactionType.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            int lastCheckedId = R.id.transaction_type_credit;
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == View.NO_ID) { // user tried to uncheck; keep the chip checked
                    group.check(lastCheckedId);
                    return;
                }
                lastCheckedId = checkedId;
                switch (checkedId) {
                    case R.id.transaction_type_contra:
                        findViewById(R.id.select_credit_account_container).setVisibility(View.VISIBLE);
                        findViewById(R.id.select_debit_account_container).setVisibility(View.VISIBLE);
                        break;
                    case R.id.transaction_type_debit:
                        findViewById(R.id.select_credit_account_container).setVisibility(View.GONE);
                        findViewById(R.id.select_debit_account_container).setVisibility(View.VISIBLE);
                        break;
                    case R.id.transaction_type_credit:
                        findViewById(R.id.select_credit_account_container).setVisibility(View.VISIBLE);
                        findViewById(R.id.select_debit_account_container).setVisibility(View.GONE);
                }
            }
        });
        String[] accountNames = DatabaseHelper.getInstance(this).getAllAccountNames();

        setUpAutoCompleteTextView((AutoCompleteTextView)findViewById(R.id.select_credit_account), accountNames);
        setUpAutoCompleteTextView((AutoCompleteTextView)findViewById(R.id.select_debit_account), accountNames);
        TextInputEditText addTransactionDate = findViewById(R.id.add_transaction_date);
        addTransactionDate.setText(DATE_FORMAT.format(new Date()));
        addTransactionDate.setKeyListener(null);
        addTransactionDate.setFocusable(false);
        addTransactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setFocusable(true);
                DialogFragment newFragment = new DatePickerFragment(AddTransaction.this);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        setUpParticularsTextView();
        findViewById(R.id.add_transaction_particulars).requestFocus();
    }

    public void addTransactionClicked(View view) {
        view.setEnabled(false);
        view.setAlpha(0.5f);
        int selectedTransactionType = ((ChipGroup)findViewById(R.id.add_transaction_type)).getCheckedChipId();
        AutoCompleteTextView selectCreditAccount = findViewById(R.id.select_credit_account);
        AutoCompleteTextView selectDebitAccount = findViewById(R.id.select_debit_account);
        TextInputEditText transactionDate = findViewById(R.id.add_transaction_date);
        AutoCompleteTextView transactionParticulars = findViewById(R.id.add_transaction_particulars);
        TextInputEditText transactionAmount = findViewById(R.id.add_transaction_amount);
        boolean validationFailed = false;

        if (selectedTransactionType == R.id.transaction_type_contra || selectedTransactionType == R.id.transaction_type_debit) {
            if (selectDebitAccount.getText().toString().isEmpty()) {
                validationFailed = true;
                ((TextInputLayout)findViewById(R.id.select_debit_account_container)).setError(getText(R.string.select_debit_account_error));
            } else {
                ((TextInputLayout)findViewById(R.id.select_debit_account_container)).setErrorEnabled(false);
            }
        }
        if (selectedTransactionType == R.id.transaction_type_contra || selectedTransactionType == R.id.transaction_type_credit) {
            if (selectCreditAccount.getText().toString().isEmpty()) {
                validationFailed = true;
                ((TextInputLayout)findViewById(R.id.select_credit_account_container)).setError(getText(R.string.select_credit_account_error));
            } else {
                ((TextInputLayout)findViewById(R.id.select_credit_account_container)).setErrorEnabled(false);
            }
        }
        if (selectedTransactionType == R.id.transaction_type_contra) {
            if (!(selectCreditAccount.getText().toString().isEmpty() || selectDebitAccount.getText().toString().isEmpty())) {
                if (selectCreditAccount.getText().toString().equals(selectDebitAccount.getText().toString())) {
                    validationFailed = true;
                    ((TextInputLayout)findViewById(R.id.select_debit_account_container)).setError(getText(R.string.select_contra_account_error));
                } else {
                    ((TextInputLayout)findViewById(R.id.select_debit_account_container)).setErrorEnabled(false);
                }
            }
        }
        if (transactionParticulars.getText() == null || transactionParticulars.getText().toString().isEmpty() || !transactionParticulars.getText().toString().matches(VALID_TEXT_REGEX)) {
            validationFailed = true;
            ((TextInputLayout)findViewById(R.id.add_transaction_particulars_container)).setError(getText(R.string.add_transaction_particulars_error));
        } else {
            ((TextInputLayout)findViewById(R.id.add_transaction_particulars_container)).setErrorEnabled(false);
        }
        if (transactionAmount.getText() == null || transactionAmount.getText().toString().isEmpty() || !transactionAmount.getText().toString().matches(VALID_AMOUNT_REGEX)) {
            validationFailed = true;
            ((TextInputLayout)findViewById(R.id.add_transaction_amount_container)).setError(getText(R.string.add_transaction_amount_error));
        } else {
            ((TextInputLayout)findViewById(R.id.add_transaction_amount_container)).setErrorEnabled(false);
        }
        if (validationFailed) {
            view.setEnabled(true);
            view.setAlpha(1f);
            return;
        }
        boolean transactionAddedSuccessfully = false;
        try {
            switch (selectedTransactionType) {
                case R.id.transaction_type_contra:
                    transactionAddedSuccessfully = DatabaseHelper.getInstance(this).insertTransaction(new Transaction(
                            Transaction.TransactionType.CONTRA,
                            transactionParticulars.getText().toString().trim().toLowerCase(),
                            Double.valueOf(transactionAmount.getText().toString().trim()),
                            DATE_FORMAT.parse(transactionDate.getText().toString().trim()),
                            selectDebitAccount.getText().toString().toLowerCase(),
                            selectCreditAccount.getText().toString().toLowerCase()
                    ));
                    break;
                case R.id.transaction_type_debit:
                    transactionAddedSuccessfully = DatabaseHelper.getInstance(this).insertTransaction(new Transaction(
                            Transaction.TransactionType.DR,
                            transactionParticulars.getText().toString().trim().toLowerCase(),
                            Double.valueOf(transactionAmount.getText().toString().trim()),
                            DATE_FORMAT.parse(transactionDate.getText().toString().trim()),
                            selectDebitAccount.getText().toString().toLowerCase()
                    ));
                    break;
                case R.id.transaction_type_credit:
                    transactionAddedSuccessfully = DatabaseHelper.getInstance(this).insertTransaction(new Transaction(
                            Transaction.TransactionType.CR,
                            transactionParticulars.getText().toString().trim().toLowerCase(),
                            Double.valueOf(transactionAmount.getText().toString().trim()),
                            DATE_FORMAT.parse(transactionDate.getText().toString().trim()),
                            selectCreditAccount.getText().toString().toLowerCase()
                    ));
                    break;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        CharSequence responseText = getText(R.string.add_transaction_success);
        if(!transactionAddedSuccessfully) {
            responseText = getText(R.string.add_transaction_failure);
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

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        Activity activity;

        DatePickerFragment(Activity activity){
            this.activity = activity;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = new GregorianCalendar(year, month, day);
            String date = DATE_FORMAT.format(calendar.getTime());
            ((TextInputEditText)activity.findViewById(R.id.add_transaction_date)).setText(date);
        }
    }
}
