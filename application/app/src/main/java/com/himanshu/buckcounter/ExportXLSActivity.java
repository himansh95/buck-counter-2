package com.himanshu.buckcounter;

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himanshu.buckcounter.beans.Account;
import com.himanshu.buckcounter.beans.Backup;
import com.himanshu.buckcounter.beans.Transaction;
import com.himanshu.buckcounter.business.DatabaseHelper;
import com.himanshu.buckcounter.business.DriveServiceHelper;

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
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportXLSActivity extends AppCompatActivity {
    private static final String TAG = "ExportXLSActivityTag";
    private static final int PERMISSION_REQUEST_STORAGE = 404;
    private static final int RC_SIGN_IN = 1008;
    private DriveServiceHelper mDriveServiceHelper;

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

        FloatingActionButton fab_backup = findViewById(R.id.fab_backup);
        fab_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ExportXLSActivity.this);

                if (account != null) {
                    handleBackup(account);
                } else {
                    Toast.makeText(ExportXLSActivity.this, "Sign in to make backup dude!", Toast.LENGTH_LONG).show();
                }
            }
        });

        FloatingActionButton fab_restore = findViewById(R.id.fab_restore);
        fab_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ExportXLSActivity.this);

                if (account != null) {
                    handleRestore(account);
                } else {
                    Toast.makeText(ExportXLSActivity.this, "Sign in to restore backup dude!", Toast.LENGTH_LONG).show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void handleRestore (GoogleSignInAccount googleAccount) {
        final FloatingActionButton fab_restore = findViewById(R.id.fab_restore);
        fab_restore.setEnabled(false);
        fab_restore.setAlpha(0.5f);
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        setUpDriveServiceHelper(googleAccount);

        try {
            mDriveServiceHelper.queryFiles()
                    .addOnSuccessListener(new OnSuccessListener<FileList>() {
                        @Override
                        public void onSuccess(FileList fileList) {
                            List<com.google.api.services.drive.model.File> list = fileList.getFiles();
                            com.google.api.services.drive.model.File file = list.get(0);
                            mDriveServiceHelper.readFile(file.getId())
                                    .addOnSuccessListener(new OnSuccessListener<Pair<String, String>>() {
                                        @Override
                                        public void onSuccess(Pair<String, String> stringStringPair) {
                                            Gson gson = new Gson();
                                            Backup backup = gson.fromJson(stringStringPair.second, Backup.class);
                                            DatabaseHelper dbHelper = DatabaseHelper.getInstance(ExportXLSActivity.this);
                                            dbHelper.deleteAllAccounts();
                                            for (Account account: backup.getAccountList()) {
                                                account.setBalance(0);
                                                dbHelper.insertAccount(account);
                                            }
                                            for (Transaction transaction: backup.getTransactionList()) {
                                                dbHelper.insertTransaction(transaction);
                                            }
                                            findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout),
                                                    "Data Restored",
                                                    Snackbar.LENGTH_SHORT);
                                            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                                @Override
                                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                                    super.onDismissed(transientBottomBar, event);
                                                    fab_restore.setEnabled(true);
                                                    fab_restore.setAlpha(1f);
                                                }
                                            });
                                            snackbar.show();
                                        }
                                    });
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleBackup (GoogleSignInAccount googleAccount) {
        final FloatingActionButton fab_backup = findViewById(R.id.fab_backup);
        fab_backup.setEnabled(false);
        fab_backup.setAlpha(0.5f);
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        setUpDriveServiceHelper(googleAccount);

        try {
            mDriveServiceHelper.createFile()
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            String fileName = String.format("Backup_%s.txt", new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US).format(new Date()));
                            List<Account> accounts = DatabaseHelper.getInstance(ExportXLSActivity.this).getAllAccounts();
                            List<Transaction> transactions = DatabaseHelper.getInstance(ExportXLSActivity.this).getAllTransactions();

                            Backup backup = new Backup(accounts, transactions);
                            Gson gson = new Gson();
                            Type type = new TypeToken<Backup>() {}.getType();
                            String jsonString = gson.toJson(backup, type);
                            mDriveServiceHelper.saveFile(s, fileName, jsonString)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout),
                                                    task.isSuccessful() ? "Data Backed Up" : "Data Not Backed Up",
                                                    Snackbar.LENGTH_SHORT);
                                            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                                @Override
                                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                                    super.onDismissed(transientBottomBar, event);
                                                    fab_backup.setEnabled(true);
                                                    fab_backup.setAlpha(1f);
                                                }
                                            });
                                            snackbar.show();
                                        }
                                    });
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void setUpDriveServiceHelper(GoogleSignInAccount googleAccount) {
        // Use the authenticated account to sign in to the Drive service.
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        ExportXLSActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(googleAccount.getAccount());
        Drive googleDriveService =
                new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName(getString(R.string.app_name))
                        .build();

        // The DriveServiceHelper encapsulates all REST API and SAF functionality.
        // Its instantiation is required before handling any onClick actions.
        mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
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
        private final ProgressBar progressBar = findViewById(R.id.progress_bar);
        private final FloatingActionButton fab = findViewById(R.id.fab);

        @Override
        protected void onPreExecute() {
            this.progressBar.setVisibility(View.VISIBLE);
            fab.setEnabled(false);
            fab.setAlpha(0.5f);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Workbook workbook = new HSSFWorkbook();
            Cell cell = null;

            // Cell style for dates
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy"));

            // Cell style for currency
            CellStyle currencyCellStyle = workbook.createCellStyle();
            currencyCellStyle.setDataFormat(workbook.createDataFormat().getFormat("₹ ##,##,##,##,##0.00"));

            // Cell style for header row
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
            cell = row.createCell(debitColumns.size() + creditColumns.size());
            cell.setCellValue(getString(R.string.export_sheet_header_total));
            cell.setCellStyle(cellStyle);

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
                    cell.setCellValue(transaction.getTimestamp());
                    cell.setCellStyle(dateCellStyle);
                    cell = row.createCell(debitColumns.get(headerParticulars));
                    cell.setCellValue(transaction.getParticulars());
                    cell = row.createCell(debitColumns.get(transaction.getDebitAccount().toUpperCase()));
                    cell.setCellValue(transaction.getAmount());
                    cell.setCellStyle(currencyCellStyle);

                    ++currentDebitRow;
                }
                if (transaction.getTransactionType() == Transaction.TransactionType.CR || transaction.getTransactionType() == Transaction.TransactionType.CONTRA) {
                    row = sheet.getRow(currentCreditRow);

                    if (row == null) {
                        row = sheet.createRow(currentCreditRow);
                    }
                    cell = row.createCell(creditColumns.get(headerDate));
                    cell.setCellValue(transaction.getTimestamp());
                    cell.setCellStyle(dateCellStyle);
                    cell = row.createCell(creditColumns.get(headerParticulars));
                    cell.setCellValue(transaction.getParticulars());
                    cell = row.createCell(creditColumns.get(transaction.getCreditAccount().toUpperCase()));
                    cell.setCellValue(transaction.getAmount());
                    cell.setCellStyle(currencyCellStyle);

                    ++currentCreditRow;
                }
            }
            cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.CORAL.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyle.setDataFormat(workbook.createDataFormat().getFormat("₹ ##,##,##,##,##0.00"));

            currentRowIndex = currentDebitRow >= currentCreditRow ? currentDebitRow : currentCreditRow;

            row = sheet.createRow(currentRowIndex++);

            cell = row.createCell(creditColumns.get(headerDate));
            cell.setCellStyle(cellStyle);

            cell = row.createCell(creditColumns.get(headerParticulars));
            cell.setCellValue(getString(R.string.export_sheet_header_balance));
            cell.setCellStyle(cellStyle);

            for(Account account: accounts) {
                cell = row.createCell(creditColumns.get(account.getName().toUpperCase()));
                cell.setCellValue(account.getBalance());
                cell.setCellStyle(cellStyle);
            }
            cell = row.createCell(debitColumns.size() + creditColumns.size());
            cell.setCellValue(DatabaseHelper.getInstance(ExportXLSActivity.this).getTotalAccountBalance(false));
            cell.setCellStyle(cellStyle);

            row = sheet.createRow(currentRowIndex++);

            cell = row.createCell(creditColumns.get(headerParticulars));
            cell.setCellValue(getString(R.string.export_sheet_credit_limit));

            for(Account account: accounts) {
                if (account.isCreditCard()) {
                    cell = row.createCell(creditColumns.get(account.getName().toUpperCase()));
                    cell.setCellValue(account.getCreditLimit());
                    cell.setCellStyle(currencyCellStyle);
                }
            }

            row = sheet.createRow(currentRowIndex++);

            cell = row.createCell(creditColumns.get(headerParticulars));
            cell.setCellValue(getString(R.string.export_sheet_remaining_credit_limit));

            for(Account account: accounts) {
                if (account.isCreditCard()) {
                    cell = row.createCell(creditColumns.get(account.getName().toUpperCase()));
                    cell.setCellValue(account.getCreditLimit() + account.getBalance());
                    cell.setCellStyle(currencyCellStyle);
                }
            }
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return false;
            }
            File root = ExportXLSActivity.this.getExternalFilesDir(null);
            File directory = new File(root, getString(R.string.export_directory_name));

            if (! directory.exists()) {
                directory.mkdir();
            }
            String fileName = String.format(getString(R.string.export_file_name), new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US).format(new Date()));
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

                // Using downloads manager to download from local file
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadManager.addCompletedDownload(fileName, fileName, true, MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileName.substring(fileName.lastIndexOf('.') + 1)), file.getAbsolutePath(), file.length(), true);

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
            this.progressBar.setVisibility(View.GONE);
            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout),
                    aBoolean ? R.string.export_success_message : R.string.export_failure_message,
                    Snackbar.LENGTH_SHORT);
            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    fab.setEnabled(true);
                    fab.setAlpha(1f);
                }
            });
            snackbar.show();
        }
    }
}
