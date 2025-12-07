package service;

import model.*;
import repository.*;
import utils.IdUtil;
import utils.UjianHelper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class UjianService {
    private final UjianRepository ujianRepo;
    private final UjianProgressRepository progressRepo;
    private final SoalRepository soalRepo;
    private final NilaiRepository nilaiRepo;
    private final JawabanRepository jawabanRepo;

    public UjianService(UjianRepository ur, UjianProgressRepository pr, SoalRepository sr, NilaiRepository nr, JawabanRepository jr) {
        this.ujianRepo = ur;
        this.progressRepo = pr;
        this.soalRepo = sr;
        this.nilaiRepo = nr;
        this.jawabanRepo = jr;
    }

    public UjianProgress startOrContinueUjian(Siswa siswa, Ujian ujian) {
        UjianProgress progress = progressRepo.getProgress(siswa.getIdUser(), ujian.getIdUjian());
        
        if (progress == null) {
            int durationSeconds = ujian.getTipeUjian().equals("KUIS") ? ujian.getWaktuPerSoal() * ujian.getMaxSoal() : ujian.getDurasiTotal() * 60;
            progress = new UjianProgress(IdUtil.generate(), siswa.getIdUser(), ujian.getIdUjian(), durationSeconds);
            progressRepo.saveOrUpdate(progress);
        } else {
            LocalDateTime start = LocalDateTime.parse(progress.getWaktuMulai());
            LocalDateTime now = LocalDateTime.now();
            long secondsPassed = Duration.between(start, now).getSeconds();
            
            int totalDurasi = ujian.getTipeUjian().equals("KUIS") ? ujian.getWaktuPerSoal() * ujian.getMaxSoal() : ujian.getDurasiTotal() * 60;
            int realSisaWaktu = (int) (totalDurasi - secondsPassed);
            
            progress.setSisaWaktu(Math.max(0, realSisaWaktu));
        }
        return progress;
    }

    public void submitUjian(Siswa siswa, Ujian ujian, List<Soal> soalList, List<Object> inputComponents) {
        UjianProgress progress = progressRepo.getProgress(siswa.getIdUser(), ujian.getIdUjian());
        
        int score = UjianEvaluationService.hitungNilai(soalList, inputComponents);
        boolean hasEssay = soalList.stream().anyMatch(s -> "ESSAY".equals(s.getTipeSoal()));
        
        String keterangan = "Selesai";
        if (progress != null && progress.getViolationCount() >= 3) {
            keterangan = "Diskualifikasi";
            score = 0;
        } else if (hasEssay) {
            keterangan = "Menunggu Koreksi";
        }

        Nilai n = new Nilai(IdUtil.generate(), siswa, ujian, score, keterangan);
        nilaiRepo.addNilai(n);

        Jawaban j = new Jawaban(IdUtil.generate(), siswa, ujian, "Digital (Auto-Score: " + score + ")");
        jawabanRepo.addJawaban(j, null);

        progressRepo.deleteProgress(siswa.getIdUser(), ujian.getIdUjian());
    }

    public List<Soal> getSoalUjian(Ujian u) {
        List<Soal> allSoal = soalRepo.getByUjian(u.getIdUjian());
        List<Soal> randomSoal = UjianHelper.randomizeSoal(allSoal, u.getMaxSoal());
        return randomSoal.stream().map(UjianHelper::randomizeOptions).collect(Collectors.toList());
    }
    
    public void saveProgress(UjianProgress p) {
        progressRepo.saveOrUpdate(p);
    }
}