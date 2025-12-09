package service;

import model.UjianProgress;
import repository.UjianProgressRepository;
import utils.LoggerUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Supplier;

public class AutoSaveService {
    
    private Timer autoSaveTimer;
    private UjianProgressRepository progressRepo;
    private Supplier<UjianProgress> progressSupplier;
    private JLabel statusLabel;
    private boolean isRunning = false;
    
    private static final int DEFAULT_INTERVAL_SECONDS = 30;
    private int saveIntervalSeconds;
    
    public AutoSaveService(UjianProgressRepository progressRepo, 
                          Supplier<UjianProgress> progressSupplier,
                          JLabel statusLabel) {
        this(progressRepo, progressSupplier, statusLabel, DEFAULT_INTERVAL_SECONDS);
    }
    
    public AutoSaveService(UjianProgressRepository progressRepo, 
                          Supplier<UjianProgress> progressSupplier,
                          JLabel statusLabel,
                          int saveIntervalSeconds) {
        this.progressRepo = progressRepo;
        this.progressSupplier = progressSupplier;
        this.statusLabel = statusLabel;
        this.saveIntervalSeconds = saveIntervalSeconds;
        
        initializeTimer();
    }
    
    private void initializeTimer() {
        int intervalMillis = saveIntervalSeconds * 1000;
        
        autoSaveTimer = new Timer(intervalMillis, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAutoSave();
            }
        });
        
        autoSaveTimer.setRepeats(true);
    }
    
    public void start() {
        if (!isRunning) {
            autoSaveTimer.start();
            isRunning = true;
            updateStatusLabel("Auto-save aktif");
            
            LoggerUtil.logInfo("AutoSaveService", "start", 
                "Auto-save started with interval: " + saveIntervalSeconds + "s");
        }
    }
    
    public void stop() {
        if (isRunning) {
            autoSaveTimer.stop();
            isRunning = false;
            updateStatusLabel("Auto-save dihentikan");
            
            LoggerUtil.logInfo("AutoSaveService", "stop", "Auto-save stopped");
        }
    }
    
    public void saveNow() {
        performAutoSave();
    }
    
    private void performAutoSave() {
        try {
            UjianProgress currentProgress = progressSupplier.get();
            
            if (currentProgress == null) {
                LoggerUtil.logWarning("AutoSaveService", "performAutoSave", 
                    "Progress is null, skipping auto-save");
                return;
            }
            
            progressRepo.saveOrUpdate(currentProgress);
            
            updateStatusLabel("✓ Tersimpan otomatis");
            
            LoggerUtil.logInfo("AutoSaveService", "performAutoSave", 
                String.format("Auto-saved progress for siswa: %s, ujian: %s", 
                    currentProgress.getIdSiswa(), 
                    currentProgress.getIdUjian()));
            
            Timer resetTimer = new Timer(2000, e -> {
                if (isRunning) {
                    updateStatusLabel("Auto-save aktif");
                }
            });
            resetTimer.setRepeats(false);
            resetTimer.start();
            
        } catch (Exception e) {
            LoggerUtil.logError("AutoSaveService", "performAutoSave", 
                "Failed to auto-save progress", e);
            
            updateStatusLabel("⚠ Gagal menyimpan");
            
            Timer resetTimer = new Timer(3000, ev -> {
                if (isRunning) {
                    updateStatusLabel("Auto-save aktif");
                }
            });
            resetTimer.setRepeats(false);
            resetTimer.start();
        }
    }
    
    private void updateStatusLabel(String text) {
        if (statusLabel != null) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(text);
            });
        }
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public void setInterval(int seconds) {
        boolean wasRunning = isRunning;
        
        if (wasRunning) {
            stop();
        }
        
        this.saveIntervalSeconds = seconds;
        initializeTimer();
        
        if (wasRunning) {
            start();
        }
        
        LoggerUtil.logInfo("AutoSaveService", "setInterval", 
            "Auto-save interval changed to: " + seconds + "s");
    }
    
    public int getInterval() {
        return saveIntervalSeconds;
    }
    
    public void dispose() {
        stop();
        if (autoSaveTimer != null) {
            autoSaveTimer = null;
        }
        LoggerUtil.logInfo("AutoSaveService", "dispose", "Auto-save service disposed");
    }
}