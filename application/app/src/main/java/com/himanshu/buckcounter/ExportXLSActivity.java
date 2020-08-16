package com.himanshu.buckcounter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.himanshu.buckcounter.beans.Account;
import com.himanshu.buckcounter.beans.Transaction;
import com.himanshu.buckcounter.business.DatabaseHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.view.View;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.himanshu.buckcounter.business.Constants.DATE_FORMAT;
import static com.himanshu.buckcounter.business.Constants.DECIMAL_FORMAT;

public class ExportXLSActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_STORAGE = 404;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_xls);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(ExportXLSActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    exportToExcel();
                } else {
                    // Permission is missing and must be requested.
                    requestStoragePermission();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void exportToExcel() {
        new ExportToExcelTask().execute();
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(findViewById(R.id.main_layout), R.string.storage_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(ExportXLSActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_STORAGE);
                }
            }).show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportToExcel();
            } else {
                // Permission request was denied.
                Snackbar.make(findViewById(R.id.main_layout), R.string.storage_access_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public class ExportToExcelTask extends AsyncTask<Void, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(ExportXLSActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getText(R.string.export_progress_message));
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Workbook workbook = new HSSFWorkbook();
            Cell cell = null;

            //Cell style for header row
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.CORNFLOWER_BLUE.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

            Sheet sheet = workbook.createSheet(getString(R.string.export_sheet_name));
            sheet.setDefaultColumnWidth(15);

            int currentRowIndex = 0;
            String headerDate = getString(R.string.export_sheet_header_date);
            String headerParticulars = getString(R.string.export_sheet_header_particulars);

            // Generate column headings
            List<Account> accounts = DatabaseHelper.getInstance(ExportXLSActivity.this).getAllAccounts();

            Map<String, Integer> debitColumns = new HashMap<>();
            Map<String, Integer> creditColumns = new HashMap<>();

            int currentDebitColumn = 0;
            int currentCreditColumn = accounts.size() + 2;

            debitColumns.put(headerDate, currentDebitColumn++);
            debitColumns.put(headerParticulars, currentDebitColumn++);
            creditColumns.put(headerDate, currentCreditColumn++);
            creditColumns.put(headerParticulars, currentCreditColumn++);

            for (Account account: accounts) {
                debitColumns.put(account.getName().toUpperCase(), currentDebitColumn++);
                creditColumns.put(account.getName().toUpperCase(), currentCreditColumn++);
            }

            Row row = sheet.createRow(currentRowIndex++);
            cell = row.createCell(0);
            sheet.addMergedRegion(new CellRangeAddress(0,0,0,debitColumns.size() - 1));
            cell.setCellValue(getString(R.string.export_sheet_header_debit));
            cell.setCellStyle(cellStyle);
            cell = row.createCell(debitColumns.size());
            sheet.addMergedRegion(new CellRangeAddress(0, 0, debitColumns.size(), debitColumns.size() + creditColumns.size() - 1));
            cell.setCellValue(getString(R.string.export_sheet_header_credit));
            cell.setCellStyle(cellStyle);

            cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.LIME.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            row = sheet.createRow(currentRowIndex++);

            for (Map.Entry entry : debitColumns.entrySet()) {
                cell = row.createCell((Integer) entry.getValue());
                cell.setCellValue((String) entry.getKey());
                cell.setCellStyle(cellStyle);
            }
            for (Map.Entry entry : creditColumns.entrySet()) {
                cell = row.createCell((Integer) entry.getValue());
                cell.setCellValue((String) entry.getKey());
                cell.setCellStyle(cellStyle);
            }

            List<Transaction> transactions = DatabaseHelper.getInstance(ExportXLSActivity.this).getAllTransactions();
            Collections.reverse(transactions);

            int currentDebitRow = currentRowIndex;
            int currentCreditRow = currentRowIndex;

            for (Transaction transaction: transactions) {
                if (transaction.getTransactionType() == Transaction.TransactionType.DR || transaction.getTransactionType() == Transaction.TransactionType.CONTRA) {
                    row = sheet.getRow(currentDebitRow);

                    if (row == null) {
                        row = sheet.createRow(currentDebitRow);
                    }
                    cell = row.createCell(debitColumns.get(headerDate));
                    cell.setCellValue(DATE_FORMAT.format(transaction.getTimestamp()));
                    cell = row.createCell(debitColumns.get(headerParticulars));
                    cell.setCellValue(transaction.getParticulars());
                    cell = row.createCell(debitColumns.get(transaction.getDebitAccount().toUpperCase()));
                    cell.setCellValue(DECIMAL_FORMAT.format(transaction.getAmount()));

                    ++currentDebitRow;
                }
                if (transaction.getTransactionType() == Transaction.TransactionType.CR || transaction.getTransactionType() == Transaction.TransactionType.CONTRA) {
                    row = sheet.getRow(currentCreditRow);

                    if (row == null) {
                        row = sheet.createRow(currentCreditRow);
                    }
                    cell = row.createCell(creditColumns.get(headerDate));
                    cell.setCellValue(DATE_FORMAT.format(transaction.getTimestamp()));
                    cell = row.createCell(creditColumns.get(headerParticulars));
                    cell.setCellValue(transaction.getParticulars());
                    cell = row.createCell(creditColumns.get(transaction.getCreditAccount().toUpperCase()));
                    cell.setCellValue(DECIMAL_FORMAT.format(transaction.getAmount()));

                    ++currentCreditRow;
                }
            }
            cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.CORAL.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            currentRowIndex = currentDebitRow >= currentCreditRow ? currentDebitRow : currentCreditRow;

            row = sheet.createRow(currentRowIndex++);

            cell = row.createCell(creditColumns.get(headerDate));
            cell.setCellStyle(cellStyle);

            cell = row.createCell(creditColumns.get(headerParticulars));
            cell.setCellValue(getString(R.string.export_sheet_header_balance));
            cell.setCellStyle(cellStyle);

            for(Account account: accounts) {
                cell = row.createCell(creditColumns.get(account.getName().toUpperCase()));
                cell.setCellValue(DECIMAL_FORMAT.format(account.getBalance()));
                cell.setCellStyle(cellStyle);
            }

            row = sheet.createRow(currentRowIndex++);

            cell = row.createCell(creditColumns.get(headerParticulars));
            cell.setCellValue(getString(R.string.export_sheet_credit_limit));

            for(Account account: accounts) {
                if (account.isCreditCard()) {
                    cell = row.createCell(creditColumns.get(account.getName().toUpperCase()));
                    cell.setCellValue(DECIMAL_FORMAT.format(account.getCreditLimit()));
                }
            }

            row = sheet.createRow(currentRowIndex++);

            cell = row.createCell(creditColumns.get(headerParticulars));
            cell.setCellValue(getString(R.string.export_sheet_remaining_credit_limit));

            for(Account account: accounts) {
                if (account.isCreditCard()) {
                    cell = row.createCell(creditColumns.get(account.getName().toUpperCase()));
                    cell.setCellValue(DECIMAL_FORMAT.format(account.getCreditLimit() + account.getBalance()));
                }
            }

            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), getString(R.string.export_directory_name));

            if (! directory.exists()) {
                directory.mkdir();
            }
            String fileName = String.format(getString(R.string.export_file_name), new Date().getTime());
            File file = new File(directory, fileName);

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                workbook.write(fileOutputStream);
                fileOutputStream.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (this.dialog.isShowing()) {

                this.dialog.dismiss();
            }
            Snackbar.make(findViewById(R.id.main_layout),
                    aBoolean ? R.string.export_success_message : R.string.export_failure_message,
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}
