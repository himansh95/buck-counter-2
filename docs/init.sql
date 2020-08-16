create table ACCOUNTS(
NAME text PRIMARY KEY,
BALANCE real NOT NULL,
IS_CREDIT_CARD integer DEFAULT 0,
CREDIT_LIMIT real
)

create table TRANSACTIONS(
_id integer PRIMARY KEY AUTOINCREMENT,
TYPE text NOT NULL,
PARTICULARS text,
AMOUNT real NOT NULL,
TIMESTAMP datetime NOT NULL,
DR_ACCOUNT text,
CR_ACCOUNT text,
FOREIGN KEY(DR_ACCOUNT) REFERENCES ACCOUNTS(NAME),
FOREIGN KEY(CR_ACCOUNT) REFERENCES ACCOUNTS(NAME)
)

create trigger if not exists TRIGGER_TRANSACTIONS_INSERT
 after insert on TRANSACTIONS for each row
 begin
 update ACCOUNTS set BALANCE = BALANCE + NEW.AMOUNT
 where NAME = NEW.DR_ACCOUNT
 and (NEW.TYPE = 'debit' or NEW.TYPE = 'contra');
 update ACCOUNTS set BALANCE = BALANCE - NEW.AMOUNT
 where NAME = NEW.CR_ACCOUNT
 and (NEW.TYPE = 'credit' or NEW.TYPE = 'contra');
 end

create trigger if not exists TRIGGER_TRANSACTIONS_DELETE
 after delete on TRANSACTIONS for each row
 begin
 update ACCOUNTS set BALANCE = BALANCE - OLD.AMOUNT
 where NAME = OLD.DR_ACCOUNT
 and (OLD.TYPE = 'debit' or OLD.TYPE = 'contra');
 update ACCOUNTS set BALANCE = BALANCE + OLD.AMOUNT
 where NAME = OLD.CR_ACCOUNT
 and (OLD.TYPE = 'credit' or OLD.TYPE = 'contra');
 end

create trigger if not exists TRIGGER_TRANSACTIONS_UPDATE_AMOUNT
 after update of AMOUNT on TRANSACTIONS for each row
 begin
 update ACCOUNTS set BALANCE = BALANCE - OLD.AMOUNT + NEW.AMOUNT
 where NAME = OLD.DR_ACCOUNT
 and (OLD.TYPE = 'debit' or OLD.TYPE = 'contra');
 update ACCOUNTS set BALANCE = BALANCE + OLD.AMOUNT - NEW.AMOUNT
 where NAME = OLD.CR_ACCOUNT
 and (OLD.TYPE = 'credit' or OLD.TYPE = 'contra');
 end
