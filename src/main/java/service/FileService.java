package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class FileService {

    private static final String UPLOAD_DIR = "data/uploads/";
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".pdf", ".docx", ".ppt", ".pptx", ".jpg", ".png");

    public FileService() {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    public String uploadFile(File sourceFile, String newFileName) throws IOException {
        if (!isValidExtension(sourceFile.getName())) {
            throw new IOException("Format file tidak didukung. Gunakan: PDF, DOCX, PPT, atau Gambar.");
        }

        File destFile = new File(UPLOAD_DIR + newFileName);
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return newFileName;
    }

    public boolean isValidExtension(String filename) {
        String lower = filename.toLowerCase();
        return ALLOWED_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }

    public File getFile(String filename) {
        return new File(UPLOAD_DIR + filename);
    }

    public void deleteFile(String filename) {
        File f = new File(UPLOAD_DIR + filename);
        if (f.exists()) f.delete();
    }
}