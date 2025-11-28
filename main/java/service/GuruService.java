package service;

import model.*;
import java.util.*;

public class GuruService {

    private List<Materi> materiList = new ArrayList<>();
    private List<Tugas> tugasList = new ArrayList<>();
    private List<Ujian> ujianList = new ArrayList<>();
    private List<Jawaban> jawabanList = new ArrayList<>();

    public void tambahMateri(Materi m) {
        materiList.add(m);
        System.out.println("Materi berhasil ditambahkan.\n");
    }

    public void buatTugas(Tugas t) {
        tugasList.add(t);
        System.out.println("Tugas berhasil dibuat.\n");
    }

    public void buatUjian(Ujian u) {
        ujianList.add(u);
        System.out.println("Ujian berhasil dibuat.\n");
    }

    public void tambahJawaban(Jawaban j) {
        jawabanList.add(j);
    }

    public List<Jawaban> getJawaban() { return jawabanList; }
    public List<Tugas> getTugas() { return tugasList; }
    public List<Ujian> getUjian() { return ujianList; }
}
