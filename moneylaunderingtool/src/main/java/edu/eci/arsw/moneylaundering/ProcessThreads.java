package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessThreads extends Thread{

    private List<File> transactionFiles;
    private int a;
    private int b;
    private TransactionAnalyzer transactionAnalyzer;
    private AtomicInteger amountOfFilesProcessed;
    private TransactionReader transactionReader;
    private boolean pause;

    public ProcessThreads(List<File> transactionFiles, int a, int b, TransactionAnalyzer transactionAnalyzer, AtomicInteger amountOfFilesProcessed, TransactionReader transactionReader){
        this.transactionFiles = transactionFiles;
        this.a = a;
        this.b = b;
        this.transactionAnalyzer = transactionAnalyzer;
        this.amountOfFilesProcessed = amountOfFilesProcessed;
        this.transactionReader = transactionReader;
        this.pause = false;
    }

    public void run() {
        for (File file : transactionFiles) {
            synchronized (this) {
                while (pause) {
                    try {
                        wait();
                    } catch (InterruptedExceotion e) {
                        e.printStackTrace();
                    }

                }
            }
            List<Transaction> transactions = transactionReader.readTransactionsFromFile(file);

            for (Transaction transaction : transactions) {
                transactionAnalyzer.addTransaction(transaction);
            }

            amountOfFilesProcessed.incrementAndGet();

        }
    }

    public void pauseThread(){
        pause = true;
    }

    public void resumeThread(){
        pause = false;
        synchronized (this){
            notifyAll();
        }
    }

}