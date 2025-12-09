package utils;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

public class LoadingUtil {
    
    public static <T> T executeWithLoading(
            Component parent, 
            String title, 
            String message, 
            Callable<T> task) {
        
        LoadingDialog dialog = new LoadingDialog(
            SwingUtilities.getWindowAncestor(parent), 
            title, 
            message
        );
        
        TaskResult<T> result = new TaskResult<>();
        
        SwingWorker<T, Void> worker = new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                try {
                    return task.call();
                } catch (Exception e) {
                    result.exception = e;
                    throw e;
                }
            }
            
            @Override
            protected void done() {
                dialog.dispose();
                try {
                    result.value = get();
                    result.success = true;
                } catch (Exception e) {
                    result.success = false;
                    result.exception = e;
                    LoggerUtil.logError("LoadingUtil", "executeWithLoading", 
                        "Task execution failed", e);
                }
            }
        };
        
        worker.execute();
        dialog.setVisible(true);
        
        if (!result.success && result.exception != null) {
            if (result.exception instanceof RuntimeException) {
                throw (RuntimeException) result.exception;
            } else {
                throw new RuntimeException(result.exception);
            }
        }
        
        return result.value;
    }
    
    public static void executeWithLoading(
            Component parent, 
            String title, 
            String message, 
            Runnable task) {
        
        executeWithLoading(parent, title, message, () -> {
            task.run();
            return null;
        });
    }
    
    public static <T> void executeWithLoadingAsync(
            Component parent, 
            String title, 
            String message, 
            Callable<T> task,
            LoadingCallback<T> callback) {
        
        LoadingDialog dialog = new LoadingDialog(
            SwingUtilities.getWindowAncestor(parent), 
            title, 
            message
        );
        
        SwingWorker<T, Integer> worker = new SwingWorker<T, Integer>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.call();
            }
            
            @Override
            protected void done() {
                dialog.dispose();
                try {
                    T result = get();
                    callback.onSuccess(result);
                } catch (Exception e) {
                    LoggerUtil.logError("LoadingUtil", "executeWithLoadingAsync", 
                        "Async task failed", e);
                    callback.onError(e);
                }
            }
        };
        
        worker.execute();
        dialog.setVisible(true);
    }
    
    public static <T> void executeWithProgress(
            Component parent, 
            String title, 
            String message, 
            ProgressTask<T> task,
            LoadingCallback<T> callback) {
        
        ProgressDialog dialog = new ProgressDialog(
            SwingUtilities.getWindowAncestor(parent), 
            title, 
            message
        );
        
        SwingWorker<T, Integer> worker = new SwingWorker<T, Integer>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.execute((progress, statusMessage) -> {
                    publish(progress);
                    if (statusMessage != null) {
                        SwingUtilities.invokeLater(() -> 
                            dialog.setStatusMessage(statusMessage));
                    }
                });
            }
            
            @Override
            protected void process(java.util.List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    int latestProgress = chunks.get(chunks.size() - 1);
                    dialog.setProgress(latestProgress);
                }
            }
            
            @Override
            protected void done() {
                dialog.dispose();
                try {
                    T result = get();
                    callback.onSuccess(result);
                } catch (Exception e) {
                    LoggerUtil.logError("LoadingUtil", "executeWithProgress", 
                        "Progress task failed", e);
                    callback.onError(e);
                }
            }
        };
        
        worker.execute();
        dialog.setVisible(true);
    }
    
    private static class TaskResult<T> {
        T value;
        boolean success = false;
        Exception exception;
    }
    
    public interface LoadingCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
    
    public interface ProgressTask<T> {
        T execute(ProgressUpdater updater) throws Exception;
    }
    
    public interface ProgressUpdater {
        void updateProgress(int percent, String message);
    }
    
    private static class LoadingDialog extends JDialog {
        private JProgressBar progressBar;
        private JLabel messageLabel;
        
        public LoadingDialog(Window parent, String title, String message) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            setResizable(false);
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            
            messageLabel = new JLabel(message);
            messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setPreferredSize(new Dimension(300, 25));
            progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            panel.add(messageLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            panel.add(progressBar);
            
            add(panel);
            pack();
            setLocationRelativeTo(parent);
        }
    }
    
    private static class ProgressDialog extends JDialog {
        private JProgressBar progressBar;
        private JLabel messageLabel;
        private JLabel statusLabel;
        
        public ProgressDialog(Window parent, String title, String message) {
            super(parent, title, ModalityType.APPLICATION_MODAL);
            
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            setResizable(false);
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            
            messageLabel = new JLabel(message);
            messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            messageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            
            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            progressBar.setPreferredSize(new Dimension(350, 30));
            progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            statusLabel = new JLabel("Memulai...");
            statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            statusLabel.setForeground(Color.GRAY);
            
            panel.add(messageLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            panel.add(progressBar);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(statusLabel);
            
            add(panel);
            pack();
            setLocationRelativeTo(parent);
        }
        
        public void setProgress(int percent) {
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(percent);
            });
        }
        
        public void setStatusMessage(String message) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(message);
            });
        }
    }
}