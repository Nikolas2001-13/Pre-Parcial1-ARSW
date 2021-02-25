package edu.eci.arsw.moneylaundering;

import sun.awt.windows.ThemeReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyLaundering implements Runnable
{
    private TransactionAnalyzer transactionAnalyzer;
    private TransactionReader transactionReader;
    private int amountOfFilesTotal;
    private AtomicInteger amountOfFilesProcessed;
    private ConcurrentLinkedDeque <ProcessThreads> threads;
    private static final int ThreadNumber = 5;
    private boolean pause = false;

    public MoneyLaundering()
    {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger();
        threads = new ConcurrentLinkedDeque<>();
        pause = false;
        amountOfFilesTotal = -1;


    }

    public void processTransactionData()
    {
        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        int range = amountOfFilesTotal/ThreadNumber;

        for (int i = 0; i<ThreadNumber; i++){
            if(i==ThreadNumber-1){
                threads.add(new ProcessThreads(transactionFiles, i*range, amountOfFilesTotal-1, transactionAnalyzer, amountOfFilesProcessed, transactionReader));
            } else {
                threads.add(new ProcessThreads(transactionFiles, i*range, (i*range)+range-1, transactionAnalyzer, amountOfFilesProcessed, transactionReader));
            }
            threads.getLast().start();
        }

        for(File transactionFile : transactionFiles)
        {            
            List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
            for(Transaction transaction : transactions)
            {
                transactionAnalyzer.addTransaction(transaction);
            }
            amountOfFilesProcessed.incrementAndGet();
        }
    }

    public List<String> getOffendingAccounts()
    {
        return transactionAnalyzer.listOffendingAccounts();
    }

    private List<File> getTransactionFileList()
    {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/")).filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }

    public void resumeThread() {
        for (ProcessThreads thread:threads) {
            thread.resume();
        }
    }

    public void pauseThread() {
        pause = true;

        for (ProcessThreads thread:threads) {
            thread.pauseThread();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Scanner scanner;
        Thread thread = new Thread(() -> processTransactionData());

        thread.start();

        while (amountOfFilesTotal==-1 || amountOfFilesProcessed.get()<amountOfFilesTotal) {
            scanner = new Scanner(System.in);
            String line = scanner.nextLine();

            if (line.contains("end")) {
                break;
            } else if (line.isEmpty()) {
                if (pause) {
                    resumeThread();
                } else {
                    pauseThread();
                }
            } else if (!pause && !line.isEmpty()) {
                showReport();
            }
        }
    }

    private void showReport() {
        String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
        List<String> offendingAccounts = getOffendingAccounts();
        String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
        message = String.format(message, amountOfFilesProcessed.get(), amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
        System.out.println(message);
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new MoneyLaundering());

        thread.start();
    }


}
