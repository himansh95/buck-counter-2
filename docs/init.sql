create table ACCOUNTS(
_id integer PRIMARY KEY AUTOINCREMENT,
NAME text NOT NULL UNIQUE,
BALANCE real NOT  NULL
)

create table TRANSACTIONS(
_id integer PRIMARY KEY AUTOINCREMENT,
TYPE text NOT NULL,
PARTICULARS text,
AMOUNT real NOT NULL,
TIMESTAMP datetime NOT NULL,
DR_ACCOUNT_ID integer,
CR_ACCOUNT_ID integer,
FOREIGN KEY(DR_ACCOUNT_ID) REFERENCES ACCOUNTS(_id),
FOREIGN KEY(CR_ACCOUNT_ID) REFERENCES ACCOUNTS(_id)
)

create trigger if not exists TRIGGER_TRANSACTIONS_INSERT
 after insert on TRANSACTIONS for each row
 begin
 update ACCOUNTS set BALANCE = BALANCE + NEW.AMOUNT
 where _id = NEW.DR_ACCOUNT_ID
 and (NEW.TYPE = 'debit' or NEW.TYPE = 'contra');
 update ACCOUNTS set BALANCE = BALANCE - NEW.AMOUNT
 where _id = NEW.CR_ACCOUNT_ID
 and (NEW.TYPE = 'credit' or NEW.TYPE = 'contra');
 end
 
create trigger if not exists TRIGGER_TRANSACTIONS_DELETE
 after delete on TRANSACTIONS for each row
 begin
 update ACCOUNTS set BALANCE = BALANCE - OLD.AMOUNT
 where _id = OLD.DR_ACCOUNT_ID
 and (OLD.TYPE = 'debit' or OLD.TYPE = 'contra');
 update ACCOUNTS set BALANCE = BALANCE + OLD.AMOUNT
 where _id = OLD.CR_ACCOUNT_ID
 and (OLD.TYPE = 'credit' or OLD.TYPE = 'contra');
 end

create trigger if not exists TRIGGER_TRANSACTIONS_UPDATE_AMOUNT
 after update of AMOUNT on TRANSACTIONS for each row
 begin
 update ACCOUNTS set BALANCE = BALANCE - OLD.AMOUNT + NEW.AMOUNT
 where _id = OLD.DR_ACCOUNT_ID
 and (OLD.TYPE = 'debit' or OLD.TYPE = 'contra');
 update ACCOUNTS set BALANCE = BALANCE + OLD.AMOUNT - NEW.AMOUNT
 where _id = OLD.CR_ACCOUNT_ID
 and (OLD.TYPE = 'credit' or OLD.TYPE = 'contra');
 end