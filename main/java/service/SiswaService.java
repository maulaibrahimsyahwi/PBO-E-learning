package service;

import model.*;
import java.util.*;

public class SiswaService {

    private List<Materi> materiList;
    private List<Tugas> tugasList;
    private List<Ujian> ujianList;
    private List<Jawaban> jawabanList = new ArrayList<>();

    public SiswaService(List<Materi> materiList, List<Tugas> tugasList,
                        List<Ujian> ujianList) {
        this.materiList = materiList;
        this.tugasList = tugasList;
        this.ujianList = ujianList;
    }

    public void submitJawaban(Jawaban j) {
        jawabanList.add(j);
        System.out.println("Jawaban berhasil dikirim!\n");
    }

    public List<Materi> getMateri() { return materiList; }
    public List<Tugas> getTugas() { return tugasList; }
    public List<Ujian> getUjian() { return ujianList; }
}
